package net.jules.core.content.entity;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.jules.core.camera.Camera;
import net.jules.core.content.weapon.*;
import net.jules.core.scene.Point3D;
import net.jules.graphics.HUDPanel;
import net.jules.graphics.RenderPanel;

public class FPSController extends Entity implements KeyListener, MouseMotionListener, MouseListener {

	private static final float MOVE_SPEED = 220f;
	private static final float MOUSE_SENSITIVITY = 0.1f;
	
	//used by several threads
	private final Set<Integer> keysPressed = ConcurrentHashMap.newKeySet();
	private volatile boolean mouseLocked = false;

	private final RenderPanel rp;
	private final Camera camera;
	
	//game related stuff
	Weapon weapon = new Scar(this);

	public FPSController(Point3D position, float yaw, float pitch, RenderPanel rp) {
		super(position, yaw, pitch);
		camera = new Camera(1, 10, 1f, RenderPanel.DEFAULT_SIZE);
		camera.setPosition(position);
		camera.setYaw(yaw);
		camera.setPitch(pitch);
		camera.setRoll(0);

		this.rp = rp;
	}

	public void update(float delta) {
		handleMovement(delta);
		
		HUDPanel.getInstance().addDebugText("yaw: " + (int) camera.getYaw() + " | pitch: " + (int) camera.getPitch(), 10, 40);
		HUDPanel.getInstance().addDebugText("x: " + (int) camera.getX() + "	y: " + (int) camera.getY() + "	z: " + (int) camera.getZ(), 10, 60);
	
		weapon.update();
	}
	
	private void handleMovement(float delta) {
		float forwardInput = 0f;
		float rightInput = 0f;
		
		if (keysPressed.contains(KeyEvent.VK_W)) forwardInput += 1f;
		if (keysPressed.contains(KeyEvent.VK_S)) forwardInput -= 1f;
		if (keysPressed.contains(KeyEvent.VK_D)) rightInput += 1f;
		if (keysPressed.contains(KeyEvent.VK_A)) rightInput -= 1f;

		if (forwardInput == 0f && rightInput == 0f) return;

		//norm
		float len = (float) Math.sqrt(forwardInput * forwardInput + rightInput * rightInput);
		forwardInput /= len;
		rightInput /= len;

		float yawRad = (float) Math.toRadians(camera.getYaw());
		float sin = (float) Math.sin(yawRad);
		float cos = (float) Math.cos(yawRad);

		float dx = forwardInput * sin + rightInput * cos;
		float dz = forwardInput * cos - rightInput * sin;

		float step = MOVE_SPEED * delta;
		position.modX(dx * step);
		position.modY(0);
		position.modZ(dz * step);
		
		weapon.getWeaponModel().move(dx * step, 0, dz * step);
	}
	
	public Weapon getWeapon() {
		return weapon;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!mouseLocked) return;

		int mX = e.getX() - rp.getWidth() / 2;
		int mY = e.getY() - rp.getHeight() / 2;

		rp.centerMouse();
		
		modYaw(mX * MOUSE_SENSITIVITY);
		modPitch(mY * MOUSE_SENSITIVITY);
		
		camera.setYaw(getYaw());
		camera.setPitch(getPitch());
		
		weapon.getWeaponModel().rotateAround(getPosition(), (float) Math.toRadians(mX * MOUSE_SENSITIVITY), (float) Math.toRadians(mY * MOUSE_SENSITIVITY));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int kc = e.getExtendedKeyCode();

		if (kc == KeyEvent.VK_ESCAPE) {
			mouseLocked = !mouseLocked;
			rp.setCursorVisible(!mouseLocked);
		} else {
			keysPressed.add(kc);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.remove(e.getExtendedKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public Camera getCamera() {
		return camera;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			weapon.onLeftClick();
		} else if(e.getButton() == MouseEvent.BUTTON3) {
			weapon.onRightClick();
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}