package net.jules.core.content.weapon;

import java.awt.Color;

import net.jules.core.content.entity.Entity;
import net.jules.core.content.weapon.Bullet.BulletFactory;
import net.jules.core.scene.ComposedGeoBody;
import net.jules.core.scene.Vector3D;
import net.jules.main.TinyLogger;

public class Scar extends RangedWeapon{
	
	private Entity e;
	private ComposedGeoBody weaponModel = WeaponModels.getScarModel();
	
	public Scar(Entity e) {
		super("Scar", 16, 0.1f, 1);
		
		this.e = e;
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void onLeftClick() {
		
		double yaw = Math.toRadians(e.getYaw());
		double pitch = -Math.toRadians(e.getPitch());

		Vector3D traj = new Vector3D(
		    (float) (Math.cos(pitch) * Math.sin(yaw)),
		    (float) (Math.sin(pitch)),
		    (float) (Math.cos(pitch) * Math.cos(yaw))
		);
		
		TinyLogger.log("traj.x: " + traj.getX() + " | traj.y: " + traj.getY() + " | traj.z: " + traj.getZ());
		
		BulletUpdater.getInstance().registerBullet(BulletFactory.create(e.getPosition(), traj, 2000, Color.CYAN, 40f, Color.LIGHT_GRAY));
		
		
	}

	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ComposedGeoBody getWeaponModel() {
		return weaponModel;
	}
}
