package net.jules.core.content.weapon;

import java.awt.Color;

import net.jules.core.scene.Point3D;
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
}