package ro.pub.dadgm.pf22.render;

import android.opengl.Matrix;

/**
 * Stores 3D scene camera information (the view and projection matrices).
 */
public class Camera {
	
	/**
	 * The raw view matrix that defines the position and orientation of the camera.
	 * 
	 * <p>Must be initialized by the subclasses.</p>
	 */
	protected float[] viewMatrix = new float[16];
	
	/**
	 * The raw projection matrix that translates the 3D world into a 2D projection.
	 * 
	 * <p>Must be initialized by the subclasses.</p>
	 */
	protected float[] projectionMatrix = new float[16];
	
	/**
	 * Stores the viewport's width/height ratio (used to determine if portrait or landscape).
	 */
	protected float viewportRatio;
	
	
	/**
	 * Default constructor. Initializes view and projection to identity matrix.
	 */
	public Camera() {
		Matrix.setIdentityM(viewMatrix, 0);
		Matrix.setIdentityM(projectionMatrix, 0);
	}
	
	/**
	 * Constructor with predefined view and projection matrices.
	 */
	public Camera(float[] viewMatrix, float[] projectionMatrix) {
		setViewMatrix(viewMatrix);
		setProjectionMatrix(projectionMatrix);
	}
	
	
	// getters / setters
	
	/**
	 * Returns the camera's view matrix.
	 * 
	 * @return The GL view matrix.
	 */
	public float[] getViewMatrix() {
		return viewMatrix;
	}
	
	/**
	 * Sets the camera's view matrix.
	 * 
	 * @param viewMatrix The GL view matrix.
	 */
	public void setViewMatrix(float[] viewMatrix) {
		if (viewMatrix.length != 16) 
			throw new IllegalArgumentException("Invalid view matrix specified!");
		
		System.arraycopy(this.viewMatrix, 0, viewMatrix, 0, viewMatrix.length);
	}
	
	/**
	 * Returns the camera's projection matrix.
	 *
	 * @return The GL projection matrix.
	 */
	public float[] getProjectionMatrix() {
		return projectionMatrix;
	}
	
	/**
	 * Sets the camera's projection matrix.
	 *
	 * @param projectionMatrix The GL projection matrix.
	 */
	public void setProjectionMatrix(float[] projectionMatrix) {
		if (projectionMatrix.length != 16)
			throw new IllegalArgumentException("Invalid projection matrix specified!");
		
		System.arraycopy(this.projectionMatrix, 0, projectionMatrix, 0, projectionMatrix.length);
	}
	
	/**
	 * Returns the current viewport ratio.
	 * 
	 * @return Viewport's ratio (w/h).
	 */
	public float getViewportRatio() {
		return viewportRatio;
	}
	
	
	public void setViewportRatio(float viewportRatio) {
		this.viewportRatio = viewportRatio;
	}
	
}
