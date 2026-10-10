package net.jules.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import net.jules.core.camera.Camera;
import net.jules.core.camera.ScreenPoint;
import net.jules.core.scene.GeoBody;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.Triangle3D;

public class RenderPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 1L;
	public static final Dimension DEFAULT_SIZE = new Dimension(800, 800);
	
	Rasterizer r = new Rasterizer();
	
	BufferedImage view;
	
	Camera mainCam = new Camera(1, 10, (float) 0.8, RenderPanel.DEFAULT_SIZE);
	
	Point3D p1 = new Point3D(-400, 400, 3000, 0, 0);
	Point3D p2 = new Point3D(400, 400, 3000, 0, 0);
	Point3D p3 = new Point3D(-400, 400, 4500, 0, 0);
	Point3D p4 = new Point3D(400, 400, 4500, 0, 0);
	Point3D p5 = new Point3D(-400, -400, 3000, 0, 0);
	Point3D p6 = new Point3D(400, -400, 3000, 0, 0);
	Point3D p7 = new Point3D(-400, -400, 4500, 0, 0);
	Point3D p8 = new Point3D(400, -400, 4500, 0, 0);
	
	GeoBody testBody = new GeoBody(p1, p2, p3, p4, p5, p6, p7, p8);
									
	
	Triangle3D t1 = new Triangle3D(p1, p2, p3, Color.BLUE);
	Triangle3D t2 = new Triangle3D(p4, p2, p3, Color.PINK);
	Triangle3D t3 = new Triangle3D(p3, p4, p8, Color.RED);
	Triangle3D t4 = new Triangle3D(p1, p2, p5, Color.GREEN);
	


	public RenderPanel() {
		setPreferredSize(DEFAULT_SIZE);
		setMinimumSize(new Dimension(600, 400));
		setVisible(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		
		Thread t = new Thread(this);
		
		t.start();
		
	}
	
	//update all sorts of game shi
	public void update(float delta) {
		testBody.move(30 * delta, delta, delta);
		
		if (view != null) {
			blacken(view);
		}
		r.rasterizeTriangles(mainCam, t1, t2, t3, t4);
		view = Rasterizer.generateView(getWidth(), getHeight());
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(view, null, 0, 0);
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
	
	public void blacken(BufferedImage b) {
		for(int y = 0; y < b.getHeight(); y++) {
			for(int x = 0; x < b.getHeight(); x++) {
				b.setRGB(x, y, Color.black.getRGB());
			}
		}
	}
 }
