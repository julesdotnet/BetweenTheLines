package net.jules.core.scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import net.jules.core.camera.Camera;
import net.jules.graphics.RenderPanel;
import net.jules.main.TinyLogger;

public class FPSController implements KeyListener, MouseMotionListener {
	
	private final Set<Integer> keysPressed = new HashSet<>();
	private boolean mouseLocked = false;
	
	private RenderPanel rp;
	
	Camera camera;
	
	public FPSController(Point3D position, float yaw, float pitch, RenderPanel rp) {
		camera = new Camera(1, 10, (float) 0.8, RenderPanel.DEFAULT_SIZE);
		camera.move(position.getX(), position.getY(), position.getZ());
		camera.setYaw(yaw);
		camera.setPitch(pitch);
		camera.setRoll(0);
		
		this.rp = rp;
	}
	
	public void update(float delta) {
		switch(getDirection()) {
		case "FORWARD":
			camera.move(0, 0, 200f * delta);
			break;
			
		case "BACKWARD":
			camera.move(0, 0, -200f * delta);
			break;
		
		case "LEFT":
			camera.move(-200f * delta, 0, 0);
			break;	
		
		case "RIGHT":
			camera.move(200f * delta, 0, 0);
			break;
			
		case "FORWARD_LEFT":
			camera.move(0, 0, 200f * delta);
			break;
			
		case "BACKWARD_LEFT":
			camera.move(0, 0, -200f * delta);
			break;
		
		case "FORWARD_RIGHT":
			camera.move(-200f * delta, 0, 0);
			break;	
		
		case "BACKWARD_RIGHT":
			camera.move(200f * delta, 0, 0);
			break;	
		
		} 
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		int mX = arg0.getX() - rp.getWidth() / 2;
		int mY = arg0.getY() - rp.getHeight() / 2;
		
		if(mouseLocked) {
			rp.centerMouse();
			camera.modYaw(mX * 0.1f);
			TinyLogger.log("mX?: " + mX);
			camera.modPitch(mY * 0.1f);
		
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int kc = e.getExtendedKeyCode();
		
		if(kc == KeyEvent.VK_ESCAPE) {
			mouseLocked = !mouseLocked;
			rp.setCursorVisible(!mouseLocked);
		} else {
			keysPressed.add(kc);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int kc = e.getExtendedKeyCode();
		
		keysPressed.remove(kc);
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * builds a direction based off the set of all the WASD-keys pressed <br>
	 * 
	 * FORMAT: "FORWARD/BACKWARD_LEFT/RIGHT" <br>
	 * 
	 * if movement is one-dimensional, there will be no underscore and only one word e.g. "LEFT" <br>
	 * 
	 * if there is no movement, output will be "NONE" <br>
	 * 
	 * @return string direction
	 */
	public String getDirection() {
		StringBuilder direction = new StringBuilder();
		//underscore applied?
		boolean ua = false;
		
		if(keysPressed.contains(KeyEvent.VK_W) && !keysPressed.contains(KeyEvent.VK_S))  {
			direction.append("FORWARD");
		} else if (keysPressed.contains(KeyEvent.VK_S)) {
			direction.append("BACKWARD");
		}
		
		if(direction.length() > 0) {
			direction.append("_");
			ua = true;
		}
		
		if(keysPressed.contains(KeyEvent.VK_A) && !keysPressed.contains(KeyEvent.VK_D))  {
			direction.append("LEFT");
		} else if (keysPressed.contains(KeyEvent.VK_D)) {
			direction.append("RIGHT");
		} else if (ua){
			direction.deleteCharAt(direction.indexOf("_"));
		}
		
		if(direction.length() == 0) {
			direction.append("NONE");
		}
		
		return direction.toString();
	}
	
	public Camera getCamera() {
		return camera;
	}
	

}
