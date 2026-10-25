package net.jules.core.content.weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.Collision;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.SceneHandler;
import net.jules.core.scene.Triangle3D;
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
		// fetch once per frame, not once per bullet
		// TODO: this should be ALL solid triangles, not just the ones eligible for rendering
		Iterable<Triangle3D> world = Arrays.asList(SceneHandler.getCurrentScene().getRenderEligibleTriangles());
		
		synchronized(bulletList) {
			Iterator<Bullet> it = bulletList.iterator();
			while(it.hasNext()) {
				Bullet b = it.next();
				
				// copy the start position BEFORE moving (getCurrentPosition may return the live object)
				Point3D p = b.getCurrentPosition();
				float ox = p.getX(), oy = p.getY(), oz = p.getZ();
				
				b.modCurrentPosition(delta);
				
				// the step this bullet just took = the segment we test
				p = b.getCurrentPosition();
				float dx = p.getX() - ox;
				float dy = p.getY() - oy;
				float dz = p.getZ() - oz;
				
				if(Collision.sweep(ox, oy, oz, dx, dy, dz, world) != Collision.NO_HIT) {
					it.remove();
				}
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
		
		int width = rp.getWidth();
		int height = rp.getHeight();
		
		ArrayList<ScreenPoint> screenPoints = new ArrayList<>();
		// synchronizedList does NOT protect iteration; lock the same monitor update() uses
		synchronized(bulletList) {
			for(Bullet b : bulletList) {
				screenPoints.add(c.projectToScreenSpace(b.getCurrentPosition(), width, height));
			}
		}
		return screenPoints.toArray(new ScreenPoint[0]);
	}
	
}