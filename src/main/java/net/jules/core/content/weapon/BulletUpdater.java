package net.jules.core.content.weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.graphics.RenderPanel;

/**
 * Handles per-frame behavior for all bullets
 */
public class BulletUpdater {
	
	private static BulletUpdater INSTANCE = new BulletUpdater();
	
	private List<Bullet> bulletList = Collections.synchronizedList(new ArrayList<>());
	
	private BulletUpdater() {
		
	}
	
	public void update(float delta) {
		synchronized(bulletList) {
			for(Bullet b : bulletList) {
				b.modCurrentPosition(delta);
				
				//if(b.rayIntersectsTriangle());
			}
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
