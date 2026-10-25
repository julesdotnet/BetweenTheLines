package net.jules.core.content.weapon;

import java.awt.Color;

import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;
import net.jules.core.scene.Vector3D;

public class Bullet {
    private final Point3D origin;
    private final float speed;
    private Vector3D trajectory;
    private Color bulletColor;

    // Trail
    private float trailLength;
    private Color trailColor;

    // updatable
    private final Point3D currentPosition;

    private Bullet(
            Point3D origin,
            float speed,
            Vector3D trajectory,
            Color bulletColor,
            float trailLength,
            Color trailColor
    ) {
        this.origin = copy(origin);
        this.currentPosition = copy(origin);
        this.speed = speed;
        this.trajectory = trajectory;
        this.bulletColor = bulletColor;
        this.trailLength = trailLength;
        this.trailColor = trailColor;
    }

    private static Point3D copy(Point3D p) {
        return new Point3D(p.getX(), p.getY(), p.getZ(), p.getU(), p.getV());
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Point3D getCurrentPosition() {
        return currentPosition;
    }

    public void modCurrentPosition(float delta) {
        currentPosition.modX(delta * trajectory.getX() * speed);
        currentPosition.modY(delta * trajectory.getY() * speed);
        currentPosition.modZ(delta * trajectory.getZ() * speed);
    }

    public float getSpeed() {
        return speed;
    }

    public Vector3D getTrajectory() {
        return trajectory;
    }

    public Color getBulletColor() {
        return bulletColor;
    }

    public float getTrailLength() {
        return trailLength;
    }

    public Color getTrailColor() {
        return trailColor;
    }

    public static class BulletFactory {

        public static Bullet create(
                Point3D origin,
                Vector3D trajectory,
                float speed,
                Color bulletColor,
                float trailLength,
                Color trailColor) {

            trajectory.normalize();

            return new Bullet(
                origin,
                speed,
                trajectory,
                bulletColor,
                trailLength,
                trailColor
            );
        }
    }
    
    public boolean rayIntersectsTriangle(Triangle3D t) {
    	
    	Point3D v0 = t.getPoints()[0];
    	Point3D v1 = t.getPoints()[1];
    	Point3D v2 = t.getPoints()[2];

        final float EPSILON = 1e-6f;

        Vector3D edge1 = v1.subtract(v0);
        Vector3D edge2 = v2.subtract(v0);

        Vector3D h = trajectory.cross(edge2);
        float a = edge1.dot(h);

        // Ray parallel to the triangle plane
        if (Math.abs(a) < EPSILON) {
            return false;
        }

        float f = 1.0f / a;

        Vector3D s = origin.subtract(v0);
        float u = f * s.dot(h);

        if (u < 0.0f || u > 1.0f) {
            return false;
        }

        Vector3D q = s.cross(edge1);
        float v = f * trajectory.dot(q);

        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }

        return (f * edge2.dot(q)) >= EPSILON;
    }
}