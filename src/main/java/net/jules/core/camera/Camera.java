package net.jules.core.camera;

import java.awt.Dimension;

import net.jules.core.scene.Point3D;

public class Camera {
	private float aspect;
	private float near;
	private float far;
	private float fov;
	private Dimension frustumSize;
	
	private float yaw = -30f;
	private float pitch = 10f;
	private float roll = 0f;
	
	private Point3D pos = new Point3D(-200,0,0,0,0);
	
	/**
	 * Implementation for converting 3D-Points into 2D-Screenspace points.
	 * 
	 * @param near
	 * @param far
	 * @param f (focal length)
	 * @param fov (field of view, in radians)
	 * @param frustumSize
	 */
	public Camera(float near, float far, float fov, Dimension frustumSize) {
		if(frustumSize != null) {
			this.aspect = frustumSize.width / frustumSize.height;
		} else {
			this.aspect = 1;
		}
		this.near = near;
		this.far = far;
		this.fov = fov;
		
	}
	/**
	 * @param x
	 * @return ClipSpace x component of full point
	 */
	private float projectPointX(float x) {
		return (float) (x * (1 / (aspect * Math.tan(getFov() / 2))));
	}
	/**
	 * @param y
	 * @return ClipSpace x component of full point
	 */
	private float projectPointY(float y) {
		return (float) (y * (1 / (Math.tan(getFov() / 2))));
	}
	/**
	 * @param z
	 * @return ClipSpace x component of full point
	 */
	private float projectPointZ(float z) {
		float n = near;
		float f = far;
		
		float a = -(n + f) / (f - n);
		float b = (2 * f * n) / (f - n);
		 
		return a * z + b;
	}
	/**
	 * @param w
	 * @return ClipSpace x component of full point
	 */
	private float projectPointW(float z) {
		return z;
	}
	
	/**
	 * @param tp Point toProject (tp)
	 * @return a new ClipSpacePoint which can be further turned into a normalized device coordinate point (NDC)
	 **/
	public ClipSpacePoint projectToClipSpace(Point3D tp) {
		
		Point3D p = adjustPointForCameraParams(tp);
		
		return new ClipSpacePoint(projectPointX(p.getX()), projectPointY(p.getY()), projectPointZ(p.getZ()), projectPointW(p.getZ()), p.getU(), p.getV());
	}
	
	public ScreenPoint projectToScreenSpace(Point3D point, int width, int height) {
		
		ClipSpacePoint c = projectToClipSpace(point);
		NDCPoint n = c.toNDC();
		ScreenPoint s = n.toScreenSpace(width, height);
		 
		return s;
	}
	
	//modify point according to camera rotation and position
	/*public Point3D adjustPointForCameraParams(Point3D p) {
		Point3D returnPoint = new Point3D(p.getX() - getX(), p.getY() - getY(), p.getZ() - getZ(), p.getU(), p.getV());
		
		returnPoint.rotate(yaw, pitch, roll, pos);
		
		return returnPoint;
	}*/
	
	public Point3D adjustPointForCameraParams(Point3D p) {
	    Point3D relative = new Point3D(
	        p.getX() - pos.getX(),
	        p.getY() - pos.getY(),
	        p.getZ() - pos.getZ(),
	        p.getU(),
	        p.getV()
	    );

	    Point3D origin = new Point3D(0, 0, 0, 0, 0);

	    Point3D rotated = relative.rotate(
	        yaw, pitch, roll, origin
	    );

	    /*System.out.println("Yaw: " + yaw);
	    System.out.println("Before rotation: " + relative.getX());
	    System.out.println("After rotation:  " + rotated.getX());*/

	    return rotated;
	}
	
	/**
	 * Recalculates the aspect ratio of the render view. Necessary for window resizing.
	 * @param width
	 * @param height
	 */
	public void recalculateAspect(int width, int height) {
		aspect = (float) width / (float) height;
	}
	
	/**
	 * modifies yaw (currentyaw += dY)
	 * @param dY
	 */
	public void modYaw(float dY) {
		yaw *= dY;
	}
	/**
	 * modifies pitch (currentpitch += dP)
	 * @param dP
	 */
	public void modPitch(float dP) {
		pitch *= dP;
	}
	
	/**
	 * modifies roll (currentroll += dR)
	 * @param dR
	 */
	public void modRoll(float dR) {
		roll *= dR;
	}
	
	//moving
	public void move(float x, float y, float z) {
		pos.setX(pos.getX() + x);
		pos.setY(pos.getY() + y);
		pos.setZ(pos.getZ() + z);
	}
	
	
	//getters/setters
		public float getAspect() {
			return aspect;
		}
		public float getNear() {
			return near;
		}
		public float getFar() {
			return far;
		}
		public float getFov() {
			return fov;
		}
		public Dimension getFrustumSize() {
			return frustumSize;
		}
		public float getYaw() {
			return yaw;
		}
		public float getRoll() {
			return roll;
		}
		public float getPitch() {
			return pitch;
		}
		public void setYaw(float y) {
			yaw = y;
		}
		public void setRoll(float r) {
			roll = r;
		}
		public void setPitch(float p) {
			pitch = p;
		}
		public float getX( ) {
			return pos.getX();
		}
		public float getY( ) {
			return pos.getY();
		}
		public float getZ( ) {
			return pos.getZ();
		}
}	
