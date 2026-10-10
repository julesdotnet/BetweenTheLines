package net.jules.core.scene;

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
}
