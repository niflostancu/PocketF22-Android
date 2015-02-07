package ro.pub.dadgm.pf22.render;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

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
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		
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
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent e) {
		return currentView != null && currentView.onTouchEvent(e);
	}
	
	/**
	 * Changes the current view.
	 * 
	 * <p>Can be called from the Activity thread.</p>
	 *
	 * @param view The new view object to set.
	 */
	public void setView(View view) {
		synchronized (this) {
			currentView = view;
		}
		
		queueEvent(new Runnable() {
			@Override
			public void run() {
				renderer.setView(getCurrentView());
			}
		});
	}

	/**
	 * Returns the current view.
	 * 
	 * @return The current View object to be drawn.
	 */
	public synchronized View getCurrentView() {
		return currentView;
	}
	
}
