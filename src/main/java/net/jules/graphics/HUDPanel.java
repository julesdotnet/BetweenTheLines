package net.jules.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HUDPanel {
	
	private List<DebugText> debugTextList = Collections.synchronizedList(new ArrayList<>());
	
	class DebugText {
		private String text;
		private int x = 0;
		private int y = 0;
		public DebugText(String text, int x, int y) {
			this.text = text;
			this.x = x;
			this.y = y;
		}
		
		public String getText() {
			return text;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
	private static HUDPanel INSTANCE = new HUDPanel();
	
	private HUDPanel() {
		
	}
	
	public void drawHUD(Graphics g) {
		
		g.setColor(Color.RED);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		
		synchronized (debugTextList) {
			for(DebugText d : debugTextList) {
				g.drawString(d.getText(), d.getX(), d.getY());
			}
			debugTextList.clear();
		}
		
	}
	
	public static HUDPanel getInstance() {
		return INSTANCE;
	}
	
	public void addDebugText(String text, int x, int y) {
		debugTextList.add(new DebugText(text, x ,y));
	}
}
