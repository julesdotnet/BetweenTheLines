package net.jules.core.scene;

public class Vector3D {
    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(
            x - other.x,
            y - other.y,
            z - other.z
        );
    }

    public Vector3D normalize() {
        double length = Math.sqrt(x*x + y*y + z*z);

        if (length == 0) return new Vector3D(0, 0, 0);

        return new Vector3D(x / length, y / length, z / length);
    }

    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
}