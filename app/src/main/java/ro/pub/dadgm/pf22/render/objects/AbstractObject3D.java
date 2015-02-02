package ro.pub.dadgm.pf22.render.objects;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ro.pub.dadgm.pf22.render.Camera;

/**
 * The AbstractObject3D provides an abstract but flexible implementation of a drawable 3D object.
 */
public abstract class AbstractObject3D implements Object3D {
	
	/**
	 * Object's tag (group). 
	 * 
	 * <p>Note: constant! Once assigned (by the constructor), it cannot be changed!</p>
	 */
	protected final String tag;
	
	/**
	 * Object's priority.
	 * 
	 * <p>Note: constant! Once assigned (by the constructor), it cannot be changed!</p>
	 */
	protected final int priority;
	
	/**
	 * The camera that stores the view and projection matrices.
	 * 
	 * <p>Optional (used only if the object uses its own shaders).</p>
	 */
	protected Camera camera;
	
	/**
	 * Stores the object's model matrix.
	 * 
	 * <p>Defaults to the identity matrix.</p>
	 */
	protected float[] modelMatrix = new float[16];
	
	/**
	 * The shader program to use for drawing.
	 * <p>Should be set during the constructor.</p>
	 */
	protected int program;
	
	/**
	 * A buffer used for storing the object's vertices.
	 */
	protected FloatBuffer vertexBuffer;

	/**
	 * A buffer with vertex indices that form the triangles.
	 */
	protected ShortBuffer vertexIndexBuffer;
	
	
	/**
	 * Default constructor.
	 */
	protected AbstractObject3D() {
		this.tag = null;
		this.priority = 0;
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	/**
	 * Constructor with tag and priority overriding.
	 * 
	 * @param tag The object's tag.
	 * @param priority The object's priority.
	 */
	protected AbstractObject3D(String tag, int priority) {
		this.tag = tag;
		this.priority = priority;
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void destroy() {
		
	}
	
	
	// utility methods

	/**
	 * Initializes a FloatBuffer from a float[] array.
	 * 
	 * @param data The float[] array to use for filling the buffer.
	 */
	protected FloatBuffer allocateBuffer(float[] data) {
		FloatBuffer floatBuf = ByteBuffer
				.allocateDirect(data.length * 4) // float == 4 bytes
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		floatBuf.put(data);
		floatBuf.position(0);
		
		return floatBuf;
	}
	
	/**
	 * Initializes a ShortBuffer from a short[] array.
	 *
	 * @param data The short[] array to use for filling the buffer.
	 */
	protected ShortBuffer allocateBuffer(short[] data) {
		ShortBuffer shortBuf = ByteBuffer
				.allocateDirect(data.length * 4) // short == 2 bytes
				.order(ByteOrder.nativeOrder())
				.asShortBuffer();
		shortBuf.put(data);
		shortBuf.position(0);
		
		return shortBuf;
	}
	
	
	// getters and setters
	
	/**
	 * Returns the object's camera.
	 * 
	 * @return Object's camera.
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * Changes the camera of the object.
	 * 
	 * @param camera The new camera object.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
}
