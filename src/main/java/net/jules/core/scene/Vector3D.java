package net.jules.core.scene;

public class Vector3D {
    private float x, y, z;

    public Vector3D(float x, float y, float z) {
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
        float length = (float) Math.sqrt(x*x + y*y + z*z);

        if (length == 0) return new Vector3D(0, 0, 0);

        return new Vector3D(x / length, y / length, z / length);
    }

    public Vector3D multiply(float scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
    
    public float dot(Vector3D other) {
        return x * other.x
             + y * other.y
             + z * other.z;
    }

    public Vector3D cross(Vector3D other) {
        return new Vector3D(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    
    public float getX() {
    	return x;
    }
    
    public float getY() {
    	return y;
    }
    
    public float getZ() {
    	return z;
    }
}