package net.jules.core.content.entity;

import net.jules.core.scene.Point3D;

public abstract class Entity {
	protected Point3D position;
	
	private float yaw;
	private float pitch;
	private boolean doClamp = true;
	
	public Entity(Point3D pos, float yaw, float pitch) {
		this.position = pos;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void modYaw(float mod) {
		yaw += mod;
	}
	
	public void modPitch(float mod) {
		pitch += mod;
		clamp(doClamp);
	}
	
	public void move(float x, float z) {
		position.modX(x);
		position.modZ(z);
	}
	
	private void clamp(boolean doClamp) {
		
		if(!doClamp) {
			return;
		}
		
		if(pitch < -90) {
			pitch = -90f;
		}
		if(pitch > 90) {
			pitch = 90;
		}
 	}
}
