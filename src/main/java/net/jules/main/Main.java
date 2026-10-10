package net.jules.main;

import javax.swing.JFrame;

import net.jules.graphics.RenderPanel;

public class Main {

	
	public static void main(String[] args) {
		System.out.println("Launch at: " + System.currentTimeMillis());
		
		JFrame window = new JFrame();
		
		RenderPanel rp = new RenderPanel();
		
		window.setTitle("Between The Lines");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(true);
		
		window.add(rp);
		
		window.pack();
	}
}
