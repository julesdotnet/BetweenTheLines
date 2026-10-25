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

	private static final boolean TEXT_MODE = true;
	
	private static final String TEXT = "wall";

	private static final int SX;
	private static final int SY;
	
	private static final int[] GLYPH_DX;
	private static final int[] GLYPH_DY;

	static {
		Font font = new Font("Arial", Font.PLAIN, 10);

		BufferedImage scratch = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = scratch.createGraphics();
		FontMetrics gridFm = sg.getFontMetrics();
		int xOffset = gridFm.stringWidth(TEXT) / 2;
		int yOffset = gridFm.getHeight() / 2;
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

	private static BufferedImage image;
	private static int[] imagePixels;

	private static float[] depth;
	private static int[] textColors; 

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

		newBackImage();
		Arrays.fill(depth, 0f);
		return out;
	}

	public void rasterizeTriangles(Camera cam, int w, int h, Triangle3D... triangles) {
		if (w <= 0 || h <= 0) {
			return;
		}
		ensureBuffers(w, h);

		final float near = Math.max(cam.getNear(), 1e-4f);
		final Point3D[] clipped = new Point3D[4];
		final ScreenPoint[] sp = new ScreenPoint[4];

		for (Triangle3D t : triangles) {
			Point3D[] pts = t.getPoints();

			Point3D p0 = cam.adjustPointForCameraParams(pts[0]);
			Point3D p1 = cam.adjustPointForCameraParams(pts[1]);
			Point3D p2 = cam.adjustPointForCameraParams(pts[2]);
			int rgb = t.getColor().getRGB();

			boolean in0 = p0.getZ() >= near;
			boolean in1 = p1.getZ() >= near;
			boolean in2 = p2.getZ() >= near;

			if (in0 && in1 && in2) {
				fillTriangle(cam.projectCameraSpaceToScreen(p0, w, h),
						cam.projectCameraSpaceToScreen(p1, w, h),
						cam.projectCameraSpaceToScreen(p2, w, h), rgb, w, h);
			} else if (in0 || in1 || in2) {
				int n = clipNear(p0, p1, p2, near, clipped);
				for (int i = 0; i < n; i++) {
					sp[i] = cam.projectCameraSpaceToScreen(clipped[i], w, h);
				}
				for (int i = 1; i + 1 < n; i++) {
					fillTriangle(sp[0], sp[i], sp[i + 1], rgb, w, h);
				}
			}
		}
	}

	/**
	 * Sutherland-Hodgman against the single plane z
	 */
	private static int clipNear(Point3D a, Point3D b, Point3D c, float near, Point3D[] out) {
		Point3D[] in = { a, b, c };
		int n = 0;
		for (int i = 0; i < 3; i++) {
			Point3D cur = in[i];
			Point3D nxt = in[(i + 1) % 3];
			boolean curIn = cur.getZ() >= near;
			boolean nxtIn = nxt.getZ() >= near;

			if (curIn) {
				out[n++] = cur;
			}
			if (curIn != nxtIn) {
				float t = (near - cur.getZ()) / (nxt.getZ() - cur.getZ());
				out[n++] = new Point3D(
						cur.getX() + t * (nxt.getX() - cur.getX()),
						cur.getY() + t * (nxt.getY() - cur.getY()),
						near,
						cur.getU() + t * (nxt.getU() - cur.getU()),
						cur.getV() + t * (nxt.getV() - cur.getV()));
			}
		}
		return n;
	}

	private static void fillTriangle(ScreenPoint a, ScreenPoint b, ScreenPoint c, int rgb, int w, int h) {
		final int[] target = TEXT_MODE ? textColors : imagePixels;
		final float[] zbuf = depth;
		final int lw = latW;

		float w0 = a.getW();
		float w1 = b.getW();
		float w2 = c.getW();

		if (w0 <= 0 || w1 <= 0 || w2 <= 0) {
			return;
		}

		float ax = a.getX(), ay = a.getY();
		float bx = b.getX(), by = b.getY();
		float cx = c.getX(), cy = c.getY();

		float ux = bx - ax, uy = by - ay;
		float vx = cx - ax, vy = cy - ay;
		float d = ux * vy - uy * vx;
		if (d == 0) {
			return;
		}
		float invD = 1f / d;

		int minX = Math.max(0, (int) Math.floor(Math.min(ax, Math.min(bx, cx))));
		int maxX = Math.min(w - 1, (int) Math.ceil(Math.max(ax, Math.max(bx, cx))));
		int minY = Math.max(0, (int) Math.floor(Math.min(ay, Math.min(by, cy))));
		int maxY = Math.min(h - 1, (int) Math.ceil(Math.max(ay, Math.max(by, cy))));
		if (minX > maxX || minY > maxY) {
			return;
		}

		int i0 = (minX + SX - 1) / SX;
		int i1 = maxX / SX;
		int j0 = (minY + SY - 1) / SY;
		int j1 = maxY / SY;
		if (i0 > i1 || j0 > j1) {
			return;
		}

		float sdx = vy * invD, sdy = -vx * invD;
		float tdx = -uy * invD, tdy = ux * invD;

		float z0 = 1f / w0;
		float dz1 = 1f / w1 - z0;
		float dz2 = 1f / w2 - z0;

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