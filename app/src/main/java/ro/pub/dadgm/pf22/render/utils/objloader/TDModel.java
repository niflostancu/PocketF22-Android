package ro.pub.dadgm.pf22.render.utils.objloader;

import java.nio.FloatBuffer;

import ro.pub.dadgm.pf22.render.utils.BufferUtils;

/**
 * Stores an object's mesh.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class TDModel {
	
	/**
	 * The list of vertices.
	 */
	protected float[] v;
	
	/**
	 * The list of vertex normals.
	 */
	protected float[] vn;
	
	/**
	 * The list of vertices' texture coordinates.
	 */
	protected float[] vt;
	
	/**
	 * Model parts definitions.
	 */
	protected TDModelPart[] parts;
	
	/**
	 * The nio buffer for the vertex coordinates.
	 */
	protected FloatBuffer vertexBuf;
	
	/**
	 * A buffer for the vertex normals.
	 */
	protected FloatBuffer normalBuf;
	
	/**
	 * A buffer for the texture coordinates.
	 */
	protected FloatBuffer textureCoordsBuf;
	
	
	/**
	 * TDModel constructor.
	 * 
	 * @param v The list of vertices.
	 * @param vn The list of vertex normals.
	 * @param vt The list of vertices' texture coordinates.
	 * @param parts Model parts definitions.
	 */
	public TDModel(float[] v, float[] vn, float[] vt,
				   TDModelPart[] parts) {
		super();
		this.v = v;
		this.vn = vn;
		this.vt = vt;
		this.parts = parts;
		
		// compute vertex normals buffer from the parts
		float[] vNormals = new float[v.length];
		for (TDModelPart part: parts) {
			for (int i=0; i<part.vnPointer.length; i++) {
				short j = part.vnPointer[i];
				short k = part.faces[i];
				vNormals[3*k] = vn[3*j];
				vNormals[3*k+1] = vn[3*j+1];
				vNormals[3*k+2] = vn[3*j+2];
			}
		}
		
		// aggregate the texture coordinates from the parts
		float[] vTextureCoords = new float[v.length];
		for (TDModelPart part: parts) {
			for (int i=0; i<part.vtPointer.length; i++) {
				short j = part.vtPointer[i];
				short k = part.faces[i];
				vTextureCoords[2*k] = vt[2*j];
				vTextureCoords[2*k+1] = vt[2*j+1];
				vTextureCoords[2*k+2] = vt[2*j+2];
			}
		}
		
		// initialize nio buffers
		vertexBuf = BufferUtils.asBuffer(v);
		normalBuf = BufferUtils.asBuffer(vNormals);
		textureCoordsBuf = BufferUtils.asBuffer(vTextureCoords);
		vertexBuf.position(0);
		normalBuf.position(0);
		textureCoordsBuf.position(0);
	}

	/**
	 * Returns the vertex count of the object.
	 * 
	 * @return The number of vertices read from the object file.
	 */
	@SuppressWarnings("unused")
	public int numVertices() {
		return v.length;
	}
	
	/**
	 * Returns the vertex array.
	 * 
	 * <p>Any changes will not reflect back into the allocated buffers!</p>
	 * 
	 * @return The vertex array of the object.
	 */
	@SuppressWarnings("unused")
	public float[] getVertexArray() {
		return v;
	}
	
	/**
	 * Returns the vertex normals array (as read from the object file).
	 *
	 * <p>Any changes will not reflect back into the allocated buffers!</p>
	 *
	 * @return The object's normals.
	 */
	@SuppressWarnings("unused")
	public float[] getNormalsArray() {
		return vn;
	}
	
	/**
	 * Returns the texture coordinates array (as read from the object file).
	 *
	 * <p>Any changes will not reflect back into the allocated buffers!</p>
	 *
	 * @return The object's texture coordinates.
	 */
	@SuppressWarnings("unused")
	public float[] getTextureCoordsArray() {
		return vn;
	}
	
	/**
	 * Returns the vertex buffer containing the vertices.
	 * 
	 * @return The vertex buffer.
	 */
	@SuppressWarnings("unused")
	public FloatBuffer getVertexBuf() {
		return vertexBuf;
	}
	
	/**
	 * Returns the normals buffer.
	 * 
	 * @return The normals buffer.
	 */
	@SuppressWarnings("unused")
	public FloatBuffer getNormalBuf() {
		return normalBuf;
	}
	
	/**
	 * Returns the vertex texture coordinates buffer.
	 * 
	 * @return The texture coordinates buffer.
	 */
	@SuppressWarnings("unused")
	public FloatBuffer getTextureCoordsBuf() {
		return textureCoordsBuf;
	}
	
	/**
	 * Returns the parts array.
	 * 
	 * @return Object's parts.
	 */
	@SuppressWarnings("unused")
	public TDModelPart[] getParts() {
		return parts;
	}
	
}


