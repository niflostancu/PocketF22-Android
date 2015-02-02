package ro.pub.dadgm.pf22.render.views;

import android.opengl.GLES20;
import android.opengl.Matrix;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.render.Camera;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.objects.ObjectsManager;
import ro.pub.dadgm.pf22.render.objects.hud.HUDObject;
import ro.pub.dadgm.pf22.render.objects.hud.MenuBackground;
import ro.pub.dadgm.pf22.render.utils.ShaderLoader;

/**
 * The view for the game's main menu.
 */
public class MainMenu implements View {
	
	/**
	 * The camera used for the menu.
	 */
	protected Camera camera;
	
	/**
	 * The list of objects to be rendered.
	 */
	protected ObjectsManager<HUDObject> objects;
	
	/**
	 * Shader references.
	 */
	protected int simpleTexShader;
	
	
	/**
	 * Constructs the main menu of the game.
	 */
	public MainMenu() {
		// initialize the objects manager
		objects = new ObjectsManager<>();
		
		// initialize the camera object with the identity view and projection
		camera = new Camera();
	}
	
	@Override
	public void onActivate() {
		// add the background objects
		objects.add(new MenuBackground("background", 0, camera));
		
		// initialize the shaders
		simpleTexShader = ShaderLoader.createProgram(R.raw.simple_tex_v, R.raw.simple_tex_f, null);
	}
	
	@Override
	public void onClose() {
		// destroy the objects
		for (HUDObject obj: objects) {
			obj.destroy();
		}
		objects.clear();
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
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = -1.0f;
		final float far = 10.0f;
		
		Matrix.orthoM(camera.getProjectionMatrix(), 0,
				left, right, bottom, top, near, far);
		sendVPMatrices();
		
		// change the background scale of the objects
		for (HUDObject obj: objects.getObjectsByTag("background")) {
			obj.setDimensions(1.0f / ratio, 1);
		}
	}
	
	/**
	 * Sends the View and Projection matrices to the used shader programs.
	 */
	protected void sendVPMatrices() {
		GLES20.glUseProgram(simpleTexShader);
		int u_viewMatrix = GLES20.glGetUniformLocation(simpleTexShader, "u_viewMatrix");
		int u_projectionMatrix = GLES20.glGetUniformLocation(simpleTexShader, "u_projectionMatrix");
		GLES20.glUniformMatrix4fv(u_viewMatrix, 1, false, camera.getViewMatrix(), 0);
		GLES20.glUniformMatrix4fv(u_projectionMatrix, 1, false, camera.getProjectionMatrix(), 0);
	}
}
