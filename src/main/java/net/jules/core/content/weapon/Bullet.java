
package net.jules.core.content.weapon;

import java.awt.Color;

import net.jules.core.scene.Point3D;
import net.jules.core.scene.Vector3D;

public class Bullet {
    private Point3D origin;
    private float speed;
    private Vector3D trajectory;
    private Color bulletColor;

    // Trail
    private float trailLength;
    private Color trailColor;

    private Bullet(
            Point3D origin,
            float speed,
            Vector3D trajectory,
            Color bulletColor,
            float trailLength,
            Color trailColor
    ) {
        this.origin = origin;
        this.speed = speed;
        this.trajectory = trajectory;
        this.bulletColor = bulletColor;
        this.trailLength = trailLength;
        this.trailColor = trailColor;
    }
    
    //getters
    

    public Point3D getOrigin() {
		return origin;
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
                Point3D target,
                float speed,
                Color bulletColor,
                float trailLength,
                Color trailColor
        ) {
            Vector3D trajectory = new Vector3D(
                target.getX() - origin.getX(),
                target.getY() - origin.getY(),
                target.getZ() - origin.getZ()
            );

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
