package net.jules.core.content.weapon;

import java.util.ArrayList;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.graphics.RenderPanel;
import net.jules.main.TinyLogger;

/**
 * Handles per-frame behavior for all bullets
 */
public class BulletUpdater {
	
	private static BulletUpdater INSTANCE = new BulletUpdater();
	
	private ArrayList<Bullet> bulletList = new ArrayList<>();
	
	private BulletUpdater() {
		
	}
	
	public void update(float delta) {
		for(Bullet b : bulletList) {
			b.modCurrentPosition(delta);
		}
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
	
	public ScreenPoint[] getAllBulletScreenPoints(Camera c, RenderPanel rp) {
		
		ArrayList<ScreenPoint> screenPoints = new ArrayList<>();
		for(Bullet b : bulletList ) {
			screenPoints.add(c.projectToScreenSpace(b.getCurrentPosition(), rp.getWidth(), rp.getHeight()));
		}
		return screenPoints.toArray(new ScreenPoint[0]);
	}
	
}
