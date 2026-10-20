package net.jules.core.content.weapon;

import java.awt.Color;

import net.jules.core.content.entity.Entity;
import net.jules.core.content.weapon.Bullet.BulletFactory;
import net.jules.core.scene.Vector3D;

public class Scar extends RangedWeapon{
	
	Entity e;
	
	public Scar(Entity e) {
		super("Scar", 16, 0.1f, 1);
		
		this.e = e;
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void onLeftClick() {
		
		double yaw = e.getYaw();
		double pitch = e.getPitch();

		Vector3D traj = new Vector3D(
		    (float) (Math.cos(pitch) * Math.sin(yaw)),
		    (float) (Math.sin(pitch)),
		    (float) (Math.cos(pitch) * Math.cos(yaw))
		);
		
		BulletUpdater.getInstance().registerBullet(BulletFactory.create(e.getPosition(), traj, 800, Color.CYAN, 40f, Color.LIGHT_GRAY));
		
		
	}

	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub
		
	}
}
