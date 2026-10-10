package net.jules.core.camera;

import java.awt.Dimension;

import net.jules.core.scene.Point3D;
import net.jules.graphics.RenderPanel;

public class Camera {
	private float aspect;
	private float near;
	private float far;
	private float fov;
	private Dimension frustumSize;
	/**
	 * 
	 * @param near
	 * @param far
	 * @param f (focal length)
	 * @param fov (in radians)
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
		return new ClipSpacePoint(projectPointX(tp.getX()), projectPointY(tp.getY()), projectPointZ(tp.getZ()), projectPointW(tp.getZ()), tp.getU(), tp.getV());
	}
	
	public ScreenPoint projectToScreenSpace(Point3D point) {
		ClipSpacePoint c = projectToClipSpace(point);
		NDCPoint n = c.toNDC();
		ScreenPoint s = n.toScreenSpace(RenderPanel.DEFAULT_SIZE.width, RenderPanel.DEFAULT_SIZE.height);
		 
		return s;
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
}	
