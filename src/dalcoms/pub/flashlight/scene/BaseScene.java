package dalcoms.pub.flashlight.scene;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import dalcoms.pub.flashlight.AppColor;
import dalcoms.pub.flashlight.ResourcesManager;
import dalcoms.pub.flashlight.ThisAppCommon;

import android.app.Activity;

public abstract class BaseScene extends Scene{
	protected Engine engine;
	protected Activity activity;
	protected VertexBufferObjectManager vbom;
	protected Camera camera;
	protected ResourcesManager resourcesManager;
	protected SceneManager sceneManager;
	protected AppColor appColor;
	protected ThisAppCommon appComm;
	
	public BaseScene(){
		this.resourcesManager = ResourcesManager.getInstance();
		this.engine = this.resourcesManager.getEngine();
		this.activity = this.resourcesManager.getActivity();
		this.vbom = this.resourcesManager.getVbom();
		this.camera = this.resourcesManager.getCamera();
		this.sceneManager = SceneManager.getInstance();
		this.appColor = AppColor.getInstance();
		this.appComm = new ThisAppCommon();
		
		this.createScene();
	}
	
	public abstract void createScene();
	public abstract void attachSprites();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	
	public abstract Engine getEngine();
	public abstract Activity getActivity();
	public abstract VertexBufferObjectManager getVbom();
	public abstract Camera getCamera();
	public abstract ResourcesManager getResourcesManager();
	public abstract SceneManager getSceneManager();
	
	public abstract void disposeScene();
}
