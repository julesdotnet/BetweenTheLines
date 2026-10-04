package net.jules.core.camera;

public class ScreenPoint {
	private int x, y;
	private float z;
	private float w;
	private short u, v;

	public ScreenPoint(int x, int y, float z, float w, float u, float v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.u = (short)(u * 65535);
		this.v = (short)(v * 65535);
	}

	public int getX() {
		return x;
	}
	public int getY() {
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
}
