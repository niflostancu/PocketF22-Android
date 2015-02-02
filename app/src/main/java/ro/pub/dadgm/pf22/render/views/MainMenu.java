package ro.pub.dadgm.pf22.render.views;

import android.opengl.GLES20;
import android.opengl.Matrix;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.render.Camera;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.ShaderManager;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.objects.ObjectsManager;
import ro.pub.dadgm.pf22.render.objects.hud.CenteredContainer;
import ro.pub.dadgm.pf22.render.objects.hud.HUDObject;
import ro.pub.dadgm.pf22.render.objects.hud.MenuBackground;
import ro.pub.dadgm.pf22.render.objects.hud.MenuTitle;
import ro.pub.dadgm.pf22.render.utils.DrawText;
import ro.pub.dadgm.pf22.render.utils.ShaderLoader;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;

/**
 * The view for the game's main menu.
 * 
 * <p>The view is a 2D scene that contains a background image, and several menu buttons and texts.</p>
 * 
 * <p>The world space is x=[0, 10], y=[0, 10]. Its scene will be scaled so that the value of the 
 * height is always 10 and the aspect ratio is maintained (only width will vary).</p>
 */
public class MainMenu implements View, Scene3D {
	
	
	/**
	 * The list of shaders to register (used by this view).
	 */
	protected static final Object[][] REGISTER_SHADERS = {
			{ "simple_tex", R.raw.simple_tex_v, R.raw.simple_tex_f }, 
			{ "draw_text", R.raw.draw_text_v, R.raw.draw_text_f }
	};
	
	/**
	 * The camera used for the menu.
	 */
	protected Camera camera;
	
	/**
	 * The list of objects to be rendered.
	 */
	protected ObjectsManager<HUDObject> objects;
	
	/**
	 * The shader manager used for this view.
	 */
	protected ShaderManager shaderManager;

	/**
	 * The text drawing library.
	 */
	protected DrawText drawText;
	
	
	/**
	 * Constructs the main menu of the game.
	 */
	public MainMenu() {
		// initialize the objects manager
		objects = new ObjectsManager<>();
		
		// initialize the camera object with the identity view and projection
		camera = new Camera();
		
		// initialize the shader manager
		shaderManager = new ShaderManager();
		
		// draw text library
		drawText = new DrawText(this);
	}
	
	/**
	 * Build the main menu scene.
	 */
	@Override
	public void onActivate() {
		// clean up structures first
		ShaderLoader.clear();
		TextureLoader.clear();
		objects.clear();
		shaderManager.clear();
		drawText.destroy();
		
		// initialize the shaders
		for (Object[] shaderProps: REGISTER_SHADERS) {
			String name = (String)shaderProps[0];
			Integer vertexRes = (Integer)shaderProps[1];
			Integer fragmentRes = (Integer)shaderProps[2];
			
			shaderManager.registerShader(name, vertexRes, fragmentRes);
		}
		
		// initialize the scene objects
		
		MenuBackground background = new MenuBackground(this, "fullsize", -9);
		background.setDimensions(10, 10);
		background.position().setCoordinates(0, 0, 9.9f);
		objects.add(background);
		
		CenteredContainer centeredContainer = new CenteredContainer(this, "fullsize", -5);
		centeredContainer.position().setCoordinates(0, 0, -1);
		centeredContainer.setDimensions(10, 10);
		objects.add(centeredContainer);
		
		final Object[][] centeredObjects = new Object[][]{
				// { object, position, size }
				{ new MenuTitle(this, "text", 3), new float[]{ 0f, 9.5f, 5f }, new float[]{ 0, 1 } }, 
		};
		
		for (Object[] objProps: centeredObjects) {
			HUDObject hudObject = (HUDObject)objProps[0];
			if (objProps.length > 1) {
				float[] position = (float[]) objProps[1];
				hudObject.position().setCoordinates(position[0], position[1], position[2]);
			}
			if (objProps.length > 2) {
				float[] size = (float[]) objProps[2];
				hudObject.setDimensions(size[0], size[1]);
			}
			
			centeredContainer.getObjects().add(hudObject);
		}
		
		centeredContainer.repositionObjects();
	}
	
	@Override
	public void onClose() {
		// destroy the objects
		for (HUDObject obj: objects) {
			obj.destroy();
		}
		objects.clear();
		
		// destroy the DrawText instance.
		drawText.destroy();
	}
	
	@Override
	public void draw() {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		// draw the objects
		objects.drawAll();
	}
	
	@Override
	public void onResize(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		camera.setViewportRatio(ratio);
		float vWidth = 10f * ratio;
		
		Matrix.orthoM(camera.getProjectionMatrix(), 0,
				/*left: */ 0, /*right: */ vWidth, 
				/*bottom: */ 0, /*top: */ 10f, 
				/*near: */ 0, /*far: */ -10);
		
		shaderManager.notifyCameraChanged(camera);
		
		// change the background scale of the objects
		for (HUDObject obj: objects.getObjectsByTag("fullsize")) {
			obj.setDimensions(vWidth, 10);
		}
	}
	
	@Override
	public Camera getCamera() {
		return camera;
	}
	
	@Override
	public ShaderManager getShaderManager() {
		return shaderManager;
	}
	
	@Override
	public DrawText getDrawText() {
		return drawText;
	}
	
}
