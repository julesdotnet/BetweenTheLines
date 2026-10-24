package net.jules.core.scene;

import java.io.IOException;
import java.util.ArrayList;

import net.jules.graphics.RenderPanel;
import net.jules.main.TinyLogger;

public class SceneHandler {
	//static scene identifier codes
	public static final int MAIN_SCENE = 1;

	//current scene
	private static int currentScene = MAIN_SCENE;
	private static final ArrayList<Scene> sceneList = new ArrayList<>();
	
	Scene mainScene = new Scene(MAIN_SCENE) {
		@Override
		void update() {
			
		}
	};
	
	public SceneHandler(RenderPanel rp) {
		try {
			mainScene.buildScene(SceneParser.parseSceneFromFile("/btlscenes/map1.btlscene"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TinyLogger.log("Existing scenes at launch: " + sceneList.size());
	}
	
	public static void registerScene(Scene s) {
		sceneList.add(s);
	}
	
	public static void setCurrentScene(int newCurrentScene) {
		currentScene = newCurrentScene;
	}

	public static Scene getCurrentScene() {
		return getSceneByID(currentScene);
	}

	private static Scene getSceneByID(int sceneID) {
		for (Scene s : sceneList) {
			if (s.getSceneID() == sceneID) {
				return s;
			}
		}
		return null;
	}
	
	public void updateCurrentScene() {
		mainScene.update();
	}
}
