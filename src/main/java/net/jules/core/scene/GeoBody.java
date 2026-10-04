package net.jules.core.scene;

import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;

public class GeoBody {
	public ArrayList<Point3D> points;
	
	public GeoBody(Point3D... p) {
		points = new ArrayList<>();
		for(Point3D pt : p ) {
			points.add(pt);
		}
	}
	
	public Point3D[] getPoints() {
		return points.toArray(new Point3D[0]);
	}
	
	public ScreenPoint[] getScreenPoints(Camera cam) {
		ArrayList<ScreenPoint> sp = new ArrayList<>();
		for(Point3D p : points) {
			sp.add(cam.projectToScreenSpace(p));
		}
		return sp.toArray(new ScreenPoint[0]);
	}
}
