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
	//getters
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
	//setters
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
	
	//modders
	public void modX(float x) {
		this.x += x;
	}

	public void modY(float y) {
		this.y += y;
	}

	public void modZ(float z) {
		this.z += z;
	}

	public void modW(float w) {
		this.w += w;
	}
	
	public void mod(float x, float y, float z) {
		modX(x);
		modY(y);
		modZ(z);
	}
	
	//rotation
	private Point3D rotateYaw(float dY, Point3D pivot) {
	    double angle = Math.toRadians(dY);

	    double px = getX() - pivot.getX();
	    double py = getY() - pivot.getY();
	    double pz = getZ() - pivot.getZ();

	    double cos = Math.cos(angle);
	    double sin = Math.sin(angle);

	    double x = px * cos + pz * sin;
	    double z = -px * sin + pz * cos;

	    return new Point3D(
	        (float) (x + pivot.getX()),
	        (float) (py + pivot.getY()),
	        (float) (z + pivot.getZ()),
	        u,
	        v
	        
	    );
	}
	
	private Point3D rotatePitch(float dP, Point3D pivot) {
	    double angle = Math.toRadians(dP);

	    double px = getX() - pivot.getX();
	    double py = getY() - pivot.getY();
	    double pz = getZ() - pivot.getZ();

	    double cos = Math.cos(angle);
	    double sin = Math.sin(angle);

	    double y = py * cos - pz * sin;
	    double z = py * sin + pz * cos;

	    return new Point3D(
	        (float) (px + pivot.getX()),
	        (float) (y + pivot.getY()),
	        (float) (z + pivot.getZ()),
	        u,
	        v
	    );
	}
	
	private Point3D rotateRoll(float dR, Point3D pivot) {
	    double angle = Math.toRadians(dR);

	    double px = getX() - pivot.getX();
	    double py = getY() - pivot.getY();
	    double pz = getZ() - pivot.getZ();

	    double cos = Math.cos(angle);
	    double sin = Math.sin(angle);

	    double x = px * cos - py * sin;
	    double y = px * sin + py * cos;

	    return new Point3D(
	        (float) (x + pivot.getX()),
	        (float) (y + pivot.getY()),
	        (float) (pz + pivot.getZ()),
	        u,
	        v
	    );
	}
	
	public Vector3D subtract(Point3D other) {
	    return new Vector3D(
	        x - other.x,
	        y - other.y,
	        z - other.z
	    );
	}
	
	//evil monolith of rotation
	
	/**
	 * Rotates a point around a pivot
	 * @param yaw
	 * @param pitch
	 * @param roll
	 * @param pivot
	 * @return
	 */
	public Point3D rotate(float yaw, float pitch, float roll, Point3D pivot) {

	    double yAngle = Math.toRadians(yaw);
	    double pAngle = Math.toRadians(pitch);
	    double rAngle = Math.toRadians(roll);

	    double x = getX() - pivot.getX();
	    double y = getY() - pivot.getY();
	    double z = getZ() - pivot.getZ();

	    double cos = Math.cos(yAngle);
	    double sin = Math.sin(yAngle);

	    double x1 = x * cos + z * sin;
	    double z1 = -x * sin + z * cos;

	    x = x1;
	    z = z1;

	    cos = Math.cos(pAngle);
	    sin = Math.sin(pAngle);

	    double y1 = y * cos - z * sin;
	    double z2 = y * sin + z * cos;

	    y = y1;
	    z = z2;

	    cos = Math.cos(rAngle);
	    sin = Math.sin(rAngle);

	    double x2 = x * cos - y * sin;
	    double y2 = x * sin + y * cos;

	    x = x2;
	    y = y2;

	    return new Point3D(
	        (float) (x + pivot.getX()),
	        (float) (y + pivot.getY()),
	        (float) (z + pivot.getZ()),
	        u,
	        v
	    );
	}
	
		
}
