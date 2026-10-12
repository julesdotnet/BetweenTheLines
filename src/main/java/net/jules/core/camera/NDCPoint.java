package net.jules.core.camera;

public class NDCPoint {
	private float x, y, z;
	private float w;
	private short u, v;

	public NDCPoint(float x, float y, float z, float w, float u, float v) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.u = (short)(u * 65535);
		this.v = (short)(v * 65535);
		this.w = w;
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

	public boolean isVisible() {
		return x >= -1 && x <= 1 && y >= -1 && y <= 1 && z >= -1 && z <= 1;
	}

	// NDCPoint.toScreenSpace
	public ScreenPoint toScreenSpace(int screenWidth, int screenHeight) {
		float screenX = (x + 1) * 0.5f * screenWidth;
		float screenY = (1 - y) * 0.5f * screenHeight;

		return new ScreenPoint(screenX, screenY, z, w, getU(), getV());
	}
}
