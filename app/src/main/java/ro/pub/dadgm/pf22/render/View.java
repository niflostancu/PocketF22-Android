package ro.pub.dadgm.pf22.render;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

/**
 * Views are classes that render the actual OpenGL scenes. 
 * 
 * <p>This is the facade interface that the renderer uses (see {@link SurfaceView})</p>
 */
public interface View {
	
	/**
	 * Called when the view is activated.
	 * 
	 * <p>You can do any specific initialization here.</p>
	 */
	public void onActivate();
	
	/**
	 * Called before the view is closed or when another view must be rendered.
	 * 
	 * <p>Use this to clean up any resources allocated when the view was activated.</p>
	 */
	public void onClose();
	
	/**
	 * Called when a frame needs to be drawed.
	 */
	public void draw();
	
	/**
	 * Called when the view needs to be resized.
	 * 
	 * @param width The new width.
	 * @param height The new height.
	 */
	public void onResize(int width, int height);
	
	/**
	 * Receives a touchscreen event.
	 * 
	 * <p>Warning: This method is executed on the Activity thread!</p> 
	 * 
	 * @param e The touch event.
	 * @return True if the event was handled, false otherwise.
	 */
	public boolean onTouchEvent(@NonNull MotionEvent e);
	
}
