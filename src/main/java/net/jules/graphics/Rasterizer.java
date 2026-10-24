package net.jules.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;

public class Rasterizer {

	/** true = "wall" text look, false = solid fill. */
	private static final boolean TEXT_MODE = true;

	/**
	 * Skip back-facing triangles. Only safe for closed meshes with consistent winding.
	 * If everything disappears when enabled, flip the sign test in rasterizeTriangles.
	 */
	private static final boolean CULL_BACKFACES = false;

	private static final String TEXT = "wall";

	// Sample lattice stride. Text mode only needs one sample per text cell, solid mode needs every pixel.
	private static final int SX;
	private static final int SY;
	// Pre-rendered glyph: pixel offsets (relative to the sample point) that are "on".
	private static final int[] GLYPH_DX;
	private static final int[] GLYPH_DY;

	static {
		Font font = new Font("Arial", Font.PLAIN, 10);

		BufferedImage scratch = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = scratch.createGraphics();
		// grid spacing/placement uses the DEFAULT font metrics, exactly like the original code
		FontMetrics gridFm = sg.getFontMetrics();
		int xOffset = gridFm.stringWidth(TEXT) / 2;
		int yOffset = gridFm.getHeight() / 2;
		// the glyph itself is drawn in Arial 10
		sg.setFont(font);
		FontMetrics fm = sg.getFontMetrics();
		int textW = fm.stringWidth(TEXT);
		int ascent = fm.getAscent();
		int gw = textW + 2;
		int gh = fm.getHeight() + 2;
		sg.dispose();

		BufferedImage mask = new BufferedImage(gw, gh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D mg = mask.createGraphics();
		mg.setFont(font);
		mg.setColor(Color.WHITE);
		mg.drawString(TEXT, 0, ascent);
		mg.dispose();

		int[] dx = new int[gw * gh];
		int[] dy = new int[gw * gh];
		int n = 0;
		for (int my = 0; my < gh; my++) {
			for (int mx = 0; mx < gw; mx++) {
				if ((mask.getRGB(mx, my) >>> 24) > 127) {
					// same placement as the old drawString(text, x - xOffset, y - yOffset)
					dx[n] = mx - xOffset;
					dy[n] = my - ascent - yOffset;
					n++;
				}
			}
		}
		GLYPH_DX = Arrays.copyOf(dx, n);
		GLYPH_DY = Arrays.copyOf(dy, n);

		SX = TEXT_MODE ? Math.max(1, (int) (xOffset * 2.2f)) : 1;
		SY = TEXT_MODE ? Math.max(1, yOffset) : 1;
	}

	private static int width;
	private static int height;
	private static int latW;
	private static int latH;

	private static BufferedImage image; // fresh image being drawn into this frame
	private static int[] imagePixels;

	private static float[] depth;     // one entry per lattice sample
	private static int[] textColors;  // one entry per lattice sample (text mode only)

	private static void ensureBuffers(int w, int h) {
		if (w <= 0 || h <= 0) {
			return;
		}
		if (w == width && h == height && image != null) {
			return;
		}
		width = w;
		height = h;
		latW = (w + SX - 1) / SX;
		latH = (h + SY - 1) / SY;

		newBackImage();
		depth = new float[latW * latH];
		textColors = TEXT_MODE ? new int[latW * latH] : null;
	}

	private static void newBackImage() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	/**
	 * Finishes the frame and returns it. The returned image is never reused or modified
	 * afterwards, so it is safe to paint from another thread.
	 */
	public static BufferedImage generateView(int w, int h) {
		if (w <= 0 || h <= 0) {
			return null;
		}
		ensureBuffers(w, h);

		BufferedImage out = image;
		int[] pixels = imagePixels;

		if (TEXT_MODE) {
			for (int j = 0; j < latH; j++) {
				int y = j * SY;
				int rowIdx = j * latW;
				for (int i = 0; i < latW; i++) {
					int idx = rowIdx + i;
					if (depth[idx] <= 0f) {
						continue;
					}
					int x = i * SX;
					int c = textColors[idx] | 0xFF000000;
					for (int k = 0; k < GLYPH_DX.length; k++) {
						int px = x + GLYPH_DX[k];
						int py = y + GLYPH_DY[k];
						if (px < 0 || px >= width || py < 0 || py >= height) {
							continue;
						}
						pixels[py * width + px] = c;
					}
				}
			}
		}

		// `out` now belongs to the caller and is never touched again (the EDT may still be painting it).
		// Start the next frame on a fresh, already-transparent image.
		newBackImage();
		Arrays.fill(depth, 0f);
		return out;
	}

	public void rasterizeTriangles(Camera cam, int w, int h, Triangle3D... triangles) {
		if (w <= 0 || h <= 0) {
			return; // panel not laid out yet
		}
		ensureBuffers(w, h);

		final int[] target = TEXT_MODE ? textColors : imagePixels;
		final float[] zbuf = depth;
		final int lw = latW;

		for (Triangle3D t : triangles) {
			Point3D[] pts = t.getPoints();

			ScreenPoint a = cam.projectToScreenSpace(pts[0], w, h);
			ScreenPoint b = cam.projectToScreenSpace(pts[1], w, h);
			ScreenPoint c = cam.projectToScreenSpace(pts[2], w, h);

			float w0 = a.getW();
			float w1 = b.getW();
			float w2 = c.getW();

			if (w0 <= 0 || w1 <= 0 || w2 <= 0) {
				continue; // no near-plane clipping yet
			}

			float ax = a.getX(), ay = a.getY();
			float bx = b.getX(), by = b.getY();
			float cx = c.getX(), cy = c.getY();

			// work relative to vertex a: smaller numbers, better float precision
			float ux = bx - ax, uy = by - ay;
			float vx = cx - ax, vy = cy - ay;
			float d = ux * vy - uy * vx;
			if (d == 0) {
				continue;
			}
			if (CULL_BACKFACES && d < 0) {
				continue;
			}
			float invD = 1f / d;

			int minX = Math.max(0, (int) Math.floor(Math.min(ax, Math.min(bx, cx))));
			int maxX = Math.min(w - 1, (int) Math.ceil(Math.max(ax, Math.max(bx, cx))));
			int minY = Math.max(0, (int) Math.floor(Math.min(ay, Math.min(by, cy))));
			int maxY = Math.min(h - 1, (int) Math.ceil(Math.max(ay, Math.max(by, cy))));
			if (minX > maxX || minY > maxY) {
				continue;
			}

			// bbox -> lattice range
			int i0 = (minX + SX - 1) / SX;
			int i1 = maxX / SX;
			int j0 = (minY + SY - 1) / SY;
			int j1 = maxY / SY;
			if (i0 > i1 || j0 > j1) {
				continue;
			}

			// p = a + s*u + t*v  ->  s,t linear in screen position, l0 = 1 - s - t
			float sdx = vy * invD, sdy = -vx * invD;
			float tdx = -uy * invD, tdy = ux * invD;

			// 1/w is affine in screen space -> depth = z0 + s*dz1 + t*dz2
			float z0 = 1f / w0;
			float dz1 = 1f / w1 - z0;
			float dz2 = 1f / w2 - z0;

			int rgb = t.getColor().getRGB();

			for (int j = j0; j <= j1; j++) {
				float qy = (j * SY + 0.5f) - ay;
				float sRow = qy * sdy;
				float tRow = qy * tdy;
				int rowIdx = j * lw;

				for (int i = i0; i <= i1; i++) {
					float qx = (i * SX + 0.5f) - ax;
					float s = sRow + qx * sdx;
					float tt = tRow + qx * tdx;

					if (s < 0f || tt < 0f || s + tt > 1f) {
						continue;
					}

					float z = z0 + s * dz1 + tt * dz2;
					int idx = rowIdx + i;
					if (z > zbuf[idx]) {
						zbuf[idx] = z;
						target[idx] = rgb;
					}
				}
			}
		}
	}
}