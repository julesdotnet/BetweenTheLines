package net.jules.core.scene;

import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;

public class GeoBody {
	private Point3D centroid;
	private ArrayList<Point3D> points;
	private ArrayList<Triangle3D> triangles;
	
	
	public GeoBody(Point3D... p) {
		points = new ArrayList<>();
		for(Point3D pt : p ) {
			points.add(pt);
		}
		
		setCentroid();
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
		setCentroid();
	}
	
	//centroid
	public Point3D getCentroid() {
		float sumX = 0, sumY = 0, sumZ = 0;
		int n = getPoints().length;
		if (n == 0) {
			return new Point3D(0, 0, 0, 0, 0);
		}

		for (Point3D p : getPoints()) {
			sumX += p.getX();
			sumY += p.getY();
			sumZ += p.getZ();
		}

		return new Point3D(sumX / n, sumY / n, sumZ / n, 0, 0);
	}
	
	public void setCentroid() {
		centroid = getCentroid();
	}
	
	//rotators
	/**
	 * @param angle, in radians
	 */
	public void rotateX(float angle) {
		for (Point3D p : getPoints()) {
			float y = (float) (p.getY() - centroid.getY());
			float z = (float) (p.getZ() - centroid.getZ());
			p.setY((float) ((y * Math.cos(angle)) - (z * Math.sin(angle))) + centroid.getY());
			p.setZ((float) ((y * Math.sin(angle)) + (z * Math.cos(angle))) + centroid.getZ());
		}
	}

	public void rotateY(float angle) {
		for (Point3D p : getPoints()) {
			float x = p.getX() - centroid.getX();
			float z = p.getZ() - centroid.getZ();

			p.setX((float) ((x * Math.cos(angle)) - (z * Math.sin(angle))) + centroid.getX());
			p.setZ((float) ((x * Math.sin(angle)) + (z * Math.cos(angle))) + centroid.getZ());
		}
	}

	public void rotateZ(float angle) {
		for (Point3D p : getPoints()) {
			float x = p.getX() - centroid.getX();
			float y = p.getY() - centroid.getY();

			p.setX((float) (x * Math.cos(angle) - y * Math.sin(angle)) + centroid.getX());
			p.setY((float) (x * Math.sin(angle) + y * Math.cos(angle)) + centroid.getY());

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
