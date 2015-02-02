package ro.pub.dadgm.pf22.render;

import ro.pub.dadgm.pf22.render.utils.DrawText;

/**
 * Scene3D is the facade interface that {@link View}s use to provide drawing services to the 
 * child objects.
 */
public interface Scene3D {
	
	/**
	 * Returns the current camera used for drawing the scene.
	 * 
	 * @return The current scene camera.
	 */
	public Camera getCamera();
	
	/**
	 * Provides access to the scene's {@link ShaderManager} instance.
	 * 
	 * @return View's ShaderManager instance.
	 */
	public ShaderManager getShaderManager();

	/**
	 * Provides access to the scene's text drawing library instance.
	 * 
	 * @return An useable instance of DrawText.
	 */
	public DrawText getDrawText();
	
}
