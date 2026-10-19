package net.jules.core.content.weapon;

import java.util.ArrayList;

/**
 * Handles per-frame behavior for all bullets
 */
public class BulletUpdater {
	
	private static BulletUpdater INSTANCE = new BulletUpdater();
	
	private ArrayList<Bullet> bulletList = new ArrayList<>();
	
	private BulletUpdater() {
		
	}
	
	public void update() {
		
	}
	
	public void registerBullet(Bullet b) {
		bulletList.add(b);
	}
	
	public void deregisterBullet(Bullet b) {
		bulletList.remove(b);
	}
	
	public static BulletUpdater getInstance() {
		return INSTANCE;
	}
	
}
