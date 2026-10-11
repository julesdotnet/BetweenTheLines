package net.jules.core.scene;

import java.awt.Color;
import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;

public class Triangle3D {
	//defining points
	private Point3D[] points = new Point3D[3];
	private Color color;
	
	public Triangle3D(Point3D p1, Point3D p2, Point3D p3, Color c) {
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
		color = c;
	}
	
	
	//getters
	public Point3D[] getPoints() {
		return points;
	}
	
	public float[] getAllScreenX(Camera c, int width, int height) {
		ScreenPoint[] screenPoints = getScreenPoints(c, width, height);
		return new float[] {screenPoints[0].getX(),
				screenPoints[1].getX(),
				screenPoints[2].getX()};
	}
	
	public float[] getAllScreenY(Camera c, int width, int height) {
		ScreenPoint[] screenPoints = getScreenPoints(c, width, height);
		return new float[] {screenPoints[0].getY(),
				screenPoints[1].getY(),
				screenPoints[2].getY()};
	}
	
	public float[] getAllScreenZ(Camera c, int width, int height) {
		ScreenPoint[] screenPoints = getScreenPoints(c, width, height);
		return new float[] {screenPoints[0].getZ(),
				screenPoints[1].getZ(),
				screenPoints[2].getZ()};
	}
	
	public ScreenPoint[] getScreenPoints(Camera cam, int width, int height) {
		ArrayList<ScreenPoint> sp = new ArrayList<>();
		for(Point3D p : points) {
			sp.add(cam.projectToScreenSpace(p, width, height));
		}
		return sp.toArray(new ScreenPoint[0]);
	}
	
	public Color getColor() {
		return color;
	}
}
