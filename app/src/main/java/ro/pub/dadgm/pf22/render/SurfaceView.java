package ro.pub.dadgm.pf22.render;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import ro.pub.dadgm.pf22.R;

/**
 * Sets up the OpenGL ES 2.0 drawing context and sets the accompanying {@link GLRenderer} class as 
 * the renderer.
 */
public class SurfaceView extends android.opengl.GLSurfaceView {

	/**
	 * The current view that needs to be displayed.
	 */
	protected View currentView = null;
	
	/**
	 * The GL renderer used for drawing the scene.
	 */
	protected GLRenderer renderer;
	
	
	/**
	 * SurfaceView constructor.
	 * 
	 * @param context Parent context.
	 */
	public SurfaceView(Context context) {
		super(context);
		
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		
		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);
		
		// Set the Renderer for drawing on the GLSurfaceView
		renderer = new GLRenderer();
		setRenderer(renderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	/**
	 * Constructor with initial view parameter.
	 * 
	 * @param context Parent context.
	 * @param initialView The initial view to show.
	 */
	public SurfaceView(Context context, View initialView) {
		this(context);
		
		setView(initialView);
	}
	
	
	/**
	 * Changes the current view.
	 *
	 * @param view The new view object to set.
	 */
	public void setView(View view) {
		currentView = view;
		renderer.setView(view);
	}
	
}
