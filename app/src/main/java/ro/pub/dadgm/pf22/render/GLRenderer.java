package ro.pub.dadgm.pf22.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Handles OpenGL rendering.
 */
public class GLRenderer implements GLSurfaceView.Renderer {

	/**
	 * Whether the GL surface has been initialized.
	 */
	protected boolean surfaceCreated = false;
	
	/**
	 * The current view that needs to be displayed.
	 */
	protected View currentView = null;
	
	/**
	 * Default constructor.
	 */
	public void GLRenderer() {
		// nothing to do
	}
	
	public void GLRenderer(View initialView) {
		setView(initialView);
	}
	
	/**
	 * Changes the current view.
	 * 
	 * @param view The new view to set.
	 */
	public void setView(View view) {
		if (currentView != null) {
			currentView.onClose();
		}
		currentView = view;
		
		if (surfaceCreated)
			view.onActivate();
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		surfaceCreated = true;
		
		if (currentView != null)
			currentView.onActivate();
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		if (currentView != null) {
			currentView.onResize(width, height);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		
		// draw background
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
		if (currentView != null) {
			currentView.draw();
		}
	}
}
