package net.jules.graphics;

import java.awt.*;
import javax.swing.*;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.GeoBody;
import net.jules.core.scene.Point3D;

public class Window extends JFrame implements Runnable{
	
	private static final long serialVersionUID = 1L;
	public static final Dimension DEFAULT_SIZE = new Dimension(800, 800);
	
	Camera mainCam = new Camera(1, 10, (float) 0.8, Window.DEFAULT_SIZE);
	GeoBody testBody = new GeoBody(new Point3D(-400, 400, 3000, 0, 0),
									new Point3D(400, 400, 3000, 0, 0),
									new Point3D(-400, 400, 4500, 0, 0),
									new Point3D(400, 400, 4500, 0, 0),
									new Point3D(-400, -400, 3000, 0, 0),
									new Point3D(400, -400, 3000, 0, 0),
									new Point3D(-400, -400, 4500, 0, 0),
									new Point3D(400, -400, 4500, 0, 0));


	public Window() {
		setTitle("Between The Lines");
		setPreferredSize(DEFAULT_SIZE);
		setMinimumSize(new Dimension(600, 400));
		setResizable(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.BLACK);
		
		pack();
		
		Thread t = new Thread(this);
		
		t.start();
		
	}
	
	//update all sorts of game shi
	public void update(float delta) {
		testBody.move(30 * delta, delta, delta);
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		ScreenPoint[] screenTestPoint = testBody.getScreenPoints(mainCam);
		
		g2.setColor(Color.RED);
		for(ScreenPoint s : screenTestPoint) {
			g2.fillRect(s.getX(), s.getY(), 5, 5);
		}
	}
	@Override
	public void run() {
	    long lastTime = System.currentTimeMillis();

	    while (true) {
	        long currentTime = System.currentTimeMillis();

	        float delta = (currentTime - lastTime) / 1000.0f;
	        lastTime = currentTime;

	        update(delta);
	        repaint();
	    }
	}
 }
