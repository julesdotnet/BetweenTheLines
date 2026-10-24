package net.jules.core.scene;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

	private final int sceneID;
	private final List<? super GeoBody> sceneObjects = new ArrayList<>();

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
		ArrayList<Triangle3D> returnList = new ArrayList<>();
		
		for(Object o : sceneObjects) {
			GeoBody g = (GeoBody) o;
			
			for(Triangle3D t : g.getTriangles()) {
				returnList.add(t);
			}
			
		}
		return returnList.toArray(new Triangle3D[0]);
	}
	
	
	
}