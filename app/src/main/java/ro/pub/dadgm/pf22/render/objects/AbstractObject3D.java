package ro.pub.dadgm.pf22.render.objects;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.Shader;

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
	 * The scene that manages this object.
	 */
	protected Scene3D scene;
	
	/**
	 * Stores the object's model matrix.
	 * 
	 * <p>Defaults to the identity matrix.</p>
	 */
	protected float[] modelMatrix = new float[16];
	
	/**
	 * The shader program to use for drawing.
	 * 
	 * <p>Should be set during the constructor.</p>
	 */
	protected Shader shader;
	
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
	 * 
	 * @param scene The parent scene object.
	 */
	protected AbstractObject3D(Scene3D scene) {
		this.scene = scene;
		this.tag = null;
		this.priority = 0;
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	/**
	 * Constructor with tag and priority overriding.
	 * 
	 * @param scene The parent scene object.
	 * @param tag The object's tag.
	 * @param priority The object's priority.
	 */
	protected AbstractObject3D(Scene3D scene, String tag, int priority) {
		this.scene = scene;
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
	
}
