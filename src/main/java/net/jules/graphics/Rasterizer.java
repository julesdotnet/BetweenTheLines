package net.jules.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;
import net.jules.main.TinyLogger;

public class Rasterizer {
	private static class TriangleData {
		private final int x;
		private final int y;
		private final float depth;
		private final int value;
		private boolean textRendering;

		public TriangleData(int x, int y, float depth, int value, boolean textRendering) {
			this.x = x;
			this.y = y;
			this.depth = depth;
			this.value = value;
			this.textRendering = textRendering;
		}

		public int getX() { return x; }
		public int getY() { return y; }
		public float getDepth() { return depth; }
		public int getValue() { return value; }
		public boolean getTextRenderingEnabled() { return textRendering; }
	}

	private static final ArrayList<TriangleData> triangleData = new ArrayList<>();

	public static BufferedImage generateView(int width, int height) {
		if (width <= 0 || height <= 0) {
			TinyLogger.log("omitted");
			return null;
		}

		BufferedImage view = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		float[] depthMap = new float[width * height];

		for (TriangleData d : triangleData) {
			int x = d.getX();
			int y = d.getY();
			if (x < 0 || x >= width || y < 0 || y >= height) {
				continue;
			}

			int idx = width * y + x;
			if (d.getDepth() > depthMap[idx]) {
				if(d.getTextRenderingEnabled()) {
					paintText("wall", x, y, view, new Color(d.getValue()));
				} else {
					view.setRGB(x, y, d.getValue());
				}
				depthMap[idx] = d.getDepth();
			}
		}

		triangleData.clear();
		return view;
	}

	public void rasterizeTriangles(Camera cam, int width, int height, Triangle3D... triangles) {
		for (Triangle3D t : triangles) {
			Point3D[] pts = t.getPoints();

			ScreenPoint a = cam.projectToScreenSpace(pts[0], width, height);
			ScreenPoint b = cam.projectToScreenSpace(pts[1], width, height);
			ScreenPoint c = cam.projectToScreenSpace(pts[2], width, height);

			// clip-space w from the projection, NOT Point3D.getW() (always 1)
			float w0 = a.getW();
			float w1 = b.getW();
			float w2 = c.getW();

			if (w0 <= 0 || w1 <= 0 || w2 <= 0) {
				continue; // no near-plane clipping yet
			}

			float ax = a.getX(), ay = a.getY();
			float bx = b.getX(), by = b.getY();
			float cx = c.getX(), cy = c.getY();

			float d = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
			if (d == 0) {
				continue;
			}

			int minX = Math.max(0, (int) Math.floor(Math.min(ax, Math.min(bx, cx))));
			int maxX = Math.min(width - 1, (int) Math.ceil(Math.max(ax, Math.max(bx, cx))));
			int minY = Math.max(0, (int) Math.floor(Math.min(ay, Math.min(by, cy))));
			int maxY = Math.min(height - 1, (int) Math.ceil(Math.max(ay, Math.max(by, cy))));

			float invW0 = 1f / w0;
			float invW1 = 1f / w1;
			float invW2 = 1f / w2;
			int rgb = t.getColor().getRGB();

			for (int y = minY; y <= maxY; y++) {
				float py = y + 0.5f;
				for (int x = minX; x <= maxX; x++) {
					float px = x + 0.5f;

					float l0 = ((bx - px) * (cy - py) - (by - py) * (cx - px)) / d;
					float l1 = ((cx - px) * (ay - py) - (cy - py) * (ax - px)) / d;
					float l2 = 1f - l0 - l1;

					if (l0 < 0 || l1 < 0 || l2 < 0) {
						continue;
					}

					float depth = invW0 * l0 + invW1 * l1 + invW2 * l2;
					triangleData.add(new TriangleData(x, y, depth, rgb, true));
				}
			}
		}
	}
	
	/**
	 * prepares and paints text on a specific place on a bufferedimg
	 * @param text (e. g. wall, grass)
	 * @param x (x-loc)
	 * @param y (y-loc)
	 * @param b (bufferedimg to paint on)
	 * @param c (color of text, e. g. Color.RED)
	 */
	private static void paintText(String text, int x, int y, BufferedImage b, Color c) {
		
		Graphics2D g = (Graphics2D) b.getGraphics();
		FontMetrics fm = g.getFontMetrics();
	
		int xOffset =  (int) (fm.stringWidth(text) / 2);
		int yOffset = (int) (fm.getHeight() / 2);
		
		if((x % (int) (xOffset * 2.2f)) != 0) {
			return;
		}
		if((y % yOffset) != 0) {
			return;
		}
	
		
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.setColor(c);
		
		g.drawString(text, x - xOffset, y - yOffset);
		
		
	}
}