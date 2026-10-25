package net.jules.core.content.weapon;

import java.awt.Color;
import java.util.ArrayList;

import net.jules.core.scene.ComposedGeoBody;
import net.jules.core.scene.GeoBody;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;

public class WeaponModels {
	
	//scar
	//i am fully aware that this is some bs
	public static ComposedGeoBody getScarModel() {
	    ArrayList<GeoBody> scarParts = new ArrayList<>();
	    Color color = Color.LIGHT_GRAY;

	    Point3D p0 = new Point3D(10,   0,  90, 0, 0);
	    Point3D p1 = new Point3D(10, 10,  90, 0, 0);
	    Point3D p2 = new Point3D(20, 10,  90, 0, 0);
	    Point3D p3 = new Point3D(20,   0,  90, 0, 0);
	    Point3D p4 = new Point3D(10,   0,   0, 0, 0);
	    Point3D p5 = new Point3D(10, 10,   0, 0, 0);
	    Point3D p6 = new Point3D(20, 10,   0, 0, 0);
	    Point3D p7 = new Point3D(20,   0,   0, 0, 0);

	    GeoBody main = new GeoBody(p0, p1, p2, p3, p4, p5, p6, p7);

	    main.setTriangles(new Triangle3D[] {
	            new Triangle3D(p0, p1, p2, color), new Triangle3D(p0, p2, p3, color), // front
	            new Triangle3D(p4, p5, p6, color), new Triangle3D(p4, p6, p7, color), // back
	            new Triangle3D(p0, p1, p5, color), new Triangle3D(p0, p5, p4, color), // right
	            new Triangle3D(p3, p2, p6, color), new Triangle3D(p3, p6, p7, color), // left
	            new Triangle3D(p0, p3, p7, color), new Triangle3D(p0, p7, p4, color), // top
	            new Triangle3D(p1, p2, p6, color), new Triangle3D(p1, p6, p5, color)  // bottom
	    });

	    scarParts.add(main);

	    return new ComposedGeoBody(scarParts);
	}
}
