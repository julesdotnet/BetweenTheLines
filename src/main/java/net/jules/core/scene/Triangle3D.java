package net.jules.core.scene;

import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;

public class Triangle3D {
	//defining points
	private Point3D[] points = new Point3D[3];
	
	public Triangle3D(Point3D p1, Point3D p2, Point3D p3) {
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
	}
	
	
	//getters
	public Point3D[] getPoints() {
		return points;
	}
	
	public float[] getAllScreenX(Camera c) {
		ScreenPoint[] screenPoints = getScreenPoints(c);
		return new float[] {screenPoints[0].getX(),
				screenPoints[1].getX(),
				screenPoints[2].getX()};
	}
	
	public float[] getAllScreenY(Camera c) {
		ScreenPoint[] screenPoints = getScreenPoints(c);
		return new float[] {screenPoints[0].getY(),
				screenPoints[1].getY(),
				screenPoints[2].getY()};
	}
	
	public float[] getAllScreenZ(Camera c) {
		ScreenPoint[] screenPoints = getScreenPoints(c);
		return new float[] {screenPoints[0].getZ(),
				screenPoints[1].getZ(),
				screenPoints[2].getZ()};
	}
	
	public ScreenPoint[] getScreenPoints(Camera cam) {
		ArrayList<ScreenPoint> sp = new ArrayList<>();
		for(Point3D p : points) {
			sp.add(cam.projectToScreenSpace(p));
		}
		return sp.toArray(new ScreenPoint[0]);
	}
}
