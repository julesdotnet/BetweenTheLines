package net.jules.core.scene;

public class Point3D {
	private float x, y, z, w = 1;
	private short u, v;

	public Point3D(float x, float y, float z, float u, float v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = (short)(u * 65535);
		this.v = (short)(v * 65535);
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
	public float getW() {
		return w;
	}
	public float getU() {
		return (u & 0xFFFF) / 65535.0f;
	}
	public float getV() {
		return (v & 0xFFFF) / 65535.0f;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void setW(float w) {
		this.w = w;
	}
}
