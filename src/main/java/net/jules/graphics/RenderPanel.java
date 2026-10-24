package net.jules.graphics;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

import net.jules.core.camera.ScreenPoint;
import net.jules.core.content.entity.FPSController;
import net.jules.core.content.weapon.BulletUpdater;
import net.jules.core.scene.GeoBody;
import net.jules.core.scene.Point3D;
import net.jules.core.scene.SceneHandler;
import net.jules.core.scene.Triangle3D;

public class RenderPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 1L;
	public static final Dimension DEFAULT_SIZE = new Dimension(800, 800);
	
	Rasterizer r = new Rasterizer();
	
	BufferedImage view = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
	
	//Camera mainCam = new Camera(1, 10, (float) 0.8, RenderPanel.DEFAULT_SIZE);
	
	FPSController fpsc = new FPSController(new Point3D(0, 0, 0, 0, 0), 20 ,0, this);
	
	/*Point3D p1 = new Point3D(-400, 400, 3000, 0, 0);
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
	Triangle3D t4 = new Triangle3D(p1, p2, p5, Color.GREEN);*/
	
	SceneHandler sh = new SceneHandler(this);
	


	public RenderPanel() {
		setPreferredSize(DEFAULT_SIZE);
		setMinimumSize(new Dimension(600, 400));
		setVisible(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		setFocusable(true);
		addKeyListener(fpsc);
		addMouseMotionListener(fpsc);
		addMouseListener(fpsc);
		
		Thread t = new Thread(this);
		
		t.start();
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				
				if(width >= 0 && height >= 0) {
					view = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				}
			}
		});
		
	}
	
	//update all sorts of game shi
	public void update(float delta) {
		//testBody.move(0 * delta, -300f * delta, 0 * delta);
		//testBody.rotateY(0.4f * delta);
		
		BulletUpdater.getInstance().update(delta);
		
		fpsc.update(delta);
		fpsc.getCamera().recalculateAspect(getWidth(), getHeight());
		r.rasterizeTriangles(fpsc.getCamera(), getWidth(), getHeight(), SceneHandler.getCurrentScene().getRenderEligibleTriangles());
		view = Rasterizer.generateView(getWidth(), getHeight());
		
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(view, null, 0, 0);
		
		ScreenPoint[] pts = BulletUpdater.getInstance().getAllBulletScreenPoints(fpsc.getCamera(), this);

		
		for(ScreenPoint p : pts) {
			g2.setColor(Color.WHITE);
			g2.fillRect((int) (p.getX() - 10), (int) (p.getY() - 10), 20, 20);
		}
		
	}
	
	@Override
	public void run() {
	    long lastTime = System.nanoTime();

	    while (true) {
	        long currentTime = System.nanoTime();

	        float delta = (currentTime - lastTime) / 1_000_000_000.0f;
	        lastTime = currentTime;

	        update(delta);
	        repaint();
	    }
	}
	
	public void centerMouse() {
	    try {
	        Point center = new Point(
	            getWidth() / 2,
	            getHeight() / 2
	        );

	        SwingUtilities.convertPointToScreen(center, this);
	        
	        Robot robot = new Robot();
	        robot.mouseMove(center.x, center.y);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void setCursorVisible(boolean visible) {
	    if (visible) {
	        setCursor(Cursor.getDefaultCursor());
	    } else {
	        Toolkit toolkit = Toolkit.getDefaultToolkit();

	        BufferedImage cursorImage = new BufferedImage(
	            16, 16, BufferedImage.TYPE_INT_ARGB
	        );

	        Cursor invisibleCursor = toolkit.createCustomCursor(
	            cursorImage,
	            new Point(0, 0),
	            "invisibleCursor"
	        );

	        setCursor(invisibleCursor);
	    }
	}
	
 }
