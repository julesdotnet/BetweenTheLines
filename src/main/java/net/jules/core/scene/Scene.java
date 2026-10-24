package net.jules.core.scene;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

	private final int sceneID;
	private final List<GeoBody> sceneObjects = new ArrayList<>();

	public Scene(int sceneID) {
		this.sceneID = sceneID;
		SceneHandler.registerScene(this);
	}
	
	abstract void update();

	public GeoBody[] getSceneObjects() {
		return sceneObjects.toArray(new GeoBody[0]);
	}

	//static funs
	/**
	 * Creates a scene from the given bodies and registers it under sceneID.
	 * Usage: Scene.buildScene(Scene.MAIN_SCENE, SceneParser.parseSceneFromFile(path));
	 */
	public void buildScene(List<GeoBody> bodies) {
		sceneObjects.addAll(bodies);
	}


	public int getSceneID() {
		return sceneID;
	}
	
	public Triangle3D[] getRenderEligibleTriangles() {
		ArrayList<Triangle3D> result = new ArrayList<>();
		for (GeoBody g : sceneObjects) {
			for (Triangle3D t : g.getTriangles()) {
				result.add(t);
			}
		}
		return result.toArray(new Triangle3D[0]);
	}
	
	
	
}