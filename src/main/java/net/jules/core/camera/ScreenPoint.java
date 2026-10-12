package net.jules.core.camera;

public class ScreenPoint {
	private final float x, y;
	private final float z;
	private final float w;
	private final short u, v;

	public ScreenPoint(float x, float y, float z, float w, float u, float v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.u = (short)(u * 65535);
		this.v = (short)(v * 65535);
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getZ() { return z; }
	public float getW() { return w; }
	public float getU() { return (u & 0xFFFF) / 65535.0f; }
	public float getV() { return (v & 0xFFFF) / 65535.0f; }
}