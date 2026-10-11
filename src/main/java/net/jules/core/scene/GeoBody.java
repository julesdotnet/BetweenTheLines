package net.jules.core.scene;

import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;

public class GeoBody {
	private ArrayList<Point3D> points;
	private ArrayList<Triangle3D> triangles;
	
	
	public GeoBody(Point3D... p) {
		points = new ArrayList<>();
		for(Point3D pt : p ) {
			points.add(pt);
		}
	}
	
	public void setTriangles(Triangle3D... t)  {
		triangles = new ArrayList<>();
		for(Triangle3D tr : t ) {
			triangles.add(tr);
		}
	}
	
	
	public void move(float x, float y, float z) {
		for(Point3D p : points) {
			p.mod(x, y, z);
		}
	}
	
	//getters
	public Point3D[] getPoints() {
		return points.toArray(new Point3D[0]);
	}
	
	public ScreenPoint[] getScreenPoints(Camera cam, int width, int height) {
		ArrayList<ScreenPoint> sp = new ArrayList<>();
		for(Point3D p : points) {
			sp.add(cam.projectToScreenSpace(p, width, height));
		}
		return sp.toArray(new ScreenPoint[0]);
	}
}
