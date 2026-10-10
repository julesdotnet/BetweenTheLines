package net.jules.graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;

public class Rasterizer {
	class TriangleData {
		int[] triangleData = new int[4];
		
		public TriangleData(int x, int y, int depth, int value) {
			triangleData[0] = x;
			triangleData[1] = y;
			triangleData[2] = depth;
			triangleData[3] = value;
		}
		//getters
		public int getX() {
			return triangleData[0];
		}
		
		public int getY() {
			return triangleData[1];
		}
		
		public int getDepth() {
			return triangleData[2];
		}
		public int getValue() {
			return triangleData[3];
		}
	}
	
	private static ArrayList<TriangleData> triangleData = new ArrayList<>();
	
	public static BufferedImage generateView(int width, int height) {
		BufferedImage view = new BufferedImage(RenderPanel.DEFAULT_SIZE.width, RenderPanel.DEFAULT_SIZE.height, BufferedImage.TYPE_INT_ARGB);
		
		for(TriangleData d : triangleData) {
			view.setRGB(d.getX(), d.getY(), d.getValue());
		}
		
		triangleData.clear();
		return view;
	}
	
	public void rasterizeTriangles(Camera c, Triangle3D... triangles) {
		for(Triangle3D t : triangles) {
			Rectangle boundingBox = getBoundingBox(t, c);
			for(int y = boundingBox.y; y <= boundingBox.y + boundingBox.height; y++) {
				for(int x = boundingBox.x; x <= boundingBox.x + boundingBox.height; x++) {
					float[] bW = calculateBarycentricWeights(t, c, x, y);

					
					if(bW[0] < 0 || bW[1] < 0 || bW[2] < 0) {
						continue;
					}
					
					triangleData.add(new TriangleData(x, y, 0, Color.red.getRGB()));
					
				}
			}
		}
	}

	
	private float[] calculateBarycentricWeights(Triangle3D t, Camera cam, int px, int py) {
		
		float x = px + 0.5f;
		float y = py + 0.5f;
		
		Point3D[] points = t.getPoints();
		
		
		//a bit pointless ik
		ScreenPoint a = cam.projectToScreenSpace(points[0]);
		ScreenPoint b = cam.projectToScreenSpace(points[1]);
		ScreenPoint c = cam.projectToScreenSpace(points[2]);
		
		//calc weights, d
		float[] weights = new float[3];
		float d = (b.getX() - a.getX())  * (c.getY() - a.getY())
				- (b.getY() - a.getY()) * (c.getX() - a.getX());
		
		//0 = bad = omit = return bs values
		//maybe ill worry about it someday
		if(d == 0) {
			return new float[]{-1f, -1f, -1f};
		}
		
		//barycentric weights
		weights[0] = ((b.getX() - x) * (c.getY() - y)
		          - (b.getY() - y) * (c.getX() - x)) / d;

		weights[1] = ((c.getX() - x) * (a.getY() - y)
		          - (c.getY() - y) * (a.getX() - x)) / d;

		weights[2] = 1f - weights[0] - weights[1];
		
		return weights;
	}
	
	private Rectangle getBoundingBox(Triangle3D t, Camera c) {
		Integer minX = null;
		Integer maxX = null;
		Integer minY = null;
		Integer maxY = null;
		//x boundaries
		for(float x : t.getAllScreenX(c)) {
			if(minX == null || x < minX) {
				minX = (int)x;
			}
			
			if(maxX == null || x > maxX) {
				maxX = (int)x;
			}
		}
		//y boundaries
		for(float y : t.getAllScreenY(c)) {
			if(minY == null || y < minY) {
				minY = (int)y;
			}
			
			if(maxY == null || y > maxY) {
				maxY = (int)y;
			}
		}
		
		return new Rectangle(minX, minY, maxX, maxY);
	}
}
