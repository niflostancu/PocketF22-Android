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
	 * The reversed View*Projection matrix.
	 * 
	 * <p>Used for un-projecting points back to the world space.</p>
	 */
	protected float[] reverseMatrix = new float[16];
	
	/**
	 * Stores the viewport's width/height ratio (used to determine if portrait or landscape).
	 */
	protected float viewportRatio;

	/**
	 * Viewport's dimensions (in pixels).
	 */
	protected float[] viewportDims = new float[2];
	
	
	/**
	 * Default constructor. Initializes view and projection to identity matrix.
	 */
	public Camera() {
		Matrix.setIdentityM(viewMatrix, 0);
		Matrix.setIdentityM(projectionMatrix, 0);
		computeReverseMatrix();
	}
	
	/**
	 * Constructor with predefined view and projection matrices.
	 */
	@SuppressWarnings("unused")
	public Camera(float[] viewMatrix, float[] projectionMatrix) {
		setViewMatrix(viewMatrix);
		setProjectionMatrix(projectionMatrix);
		computeReverseMatrix();
	}
	
	
	// utility methods
	
	/**
	 * Maps viewport coordinates back to the Camera's world space.
	 * 
	 * <p>If the input coordinates are invalid, returns null.</p>
	 * 
	 * @param x The projected X coordinate.
	 * @param y The projected Y coordinate.
	 * @return The world coordinates of the requested point (x, y, z).
	 */
	public float[] unProjectCoordinates(float x, float y) {
		// construct the input vector
		float[] inVec = new float[] {
				/* x: */ x, /* y: */ y, 
				/* z: */ 0, /* w: */ 1
		};
		// map from window coordinates to NDC coordinates
		inVec[0] = (inVec[0] / viewportDims[0]) * 2.0f - 1.0f;
		inVec[1] = (inVec[1] / viewportDims[1]) * 2.0f - 1.0f;
		inVec[1] = -inVec[1];
		
		// get the output coordinates
		float[] outVec = new float[4];
		Matrix.multiplyMV(outVec, 0, reverseMatrix, 0, inVec, 0);
		if (outVec[3] == 0) 
			return null;
		
		// divide by the homogenous coordinates
		outVec[0] /= outVec[3];
		outVec[1] /= outVec[3];
		outVec[2] /= outVec[3];
		
		return outVec;
	}

	/**
	 * Computes the reverse V*P matrix and stores it into the {@link #reverseMatrix} field.
	 * 
	 * <p>Should be called whenever the V/P matrix is modified.</p>
	 */
	public void computeReverseMatrix() {
		float[] tmpMatrix = new float[16];
		Matrix.multiplyMM(tmpMatrix, 0, viewMatrix, 0, projectionMatrix, 0);
		Matrix.invertM(reverseMatrix, 0, tmpMatrix, 0);
	}
	
	/**
	 * Computes the normal matrix (MV^-1)^t.
	 */
	public float[] computeNormalMatrix(float[] modelMatrix) {
		float[] tmpMatrix = new float[16];
		float[] tmp2Matrix = new float[16];
		Matrix.multiplyMM(tmpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.invertM(tmp2Matrix, 0, tmpMatrix, 0);
		Matrix.transposeM(tmpMatrix, 0, tmp2Matrix, 0);
		
		return tmpMatrix;
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
		computeReverseMatrix();
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
		computeReverseMatrix();
	}
	
	/**
	 * Returns the current viewport ratio.
	 * 
	 * @return Viewport's ratio (w/h).
	 */
	public float getViewportRatio() {
		return viewportRatio;
	}

	/**
	 * Sets the viewport dimensions.
	 * 
	 * @param width The new width (window pixels).
	 * @param height The new height (window pixels).
	 */
	public void setViewportDims(float width, float height) {
		this.viewportDims[0] = width;
		this.viewportDims[1] = height;
		this.viewportRatio = width / height;
	}
	
}
