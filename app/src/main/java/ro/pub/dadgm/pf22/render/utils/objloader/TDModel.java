package ro.pub.dadgm.pf22.render.utils.objloader;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import ro.pub.dadgm.pf22.render.utils.BufferUtils;
import ro.pub.dadgm.pf22.render.utils.NormalUtils;

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
	 * A buffer for the texture coordinates.
	 */
	protected Map<Material, Integer> textureCoordsVBO;
	
	/**
	 * The allocated VBO with the vertex data.
	 */
	protected int vbo;
	
	
	
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
	}
	
	/**
	 * Initializes the VBOs.
	 * 
	 * <p>If the VBOs are valid, returns immediately.</p>
	 */
	public void initializeBuffers() {
		// check if already initialized
		if (GLES20.glIsBuffer(vbo))
			return;
		
		// compute the normals buffer from the parts
		float[] vNormals = new float[numVertices() * 3];
		for (TDModelPart part: parts) {
			for (int i=0; i<part.vnPointer.length; i++) {
				short j = part.vnPointer[i];
				short k = part.faces[i];
				vNormals[3*k] += vn[3*j];
				vNormals[3*k+1] += vn[3*j+1];
				vNormals[3*k+2] += vn[3*j+2];
			}
		}
		
		for (int i=0; i<numVertices(); i++) {
			NormalUtils.normalize(vNormals, 3*i);
		}
		
		// aggregate the texture coordinates from the parts
		Map<Material, float[]> vTextureCoordsMap = new HashMap<>();
		for (TDModelPart part: parts) {
			Material mat = part.getMaterial();
			if (mat == null)
				continue;
			
			if (!vTextureCoordsMap.containsKey(mat)) {
				vTextureCoordsMap.put(mat, new float[numVertices() * 2]);
			}
			float[] vTextureCoords = vTextureCoordsMap.get(mat);
			
			for (int i=0; i<part.vtPointer.length; i++) {
				short j = part.vtPointer[i];
				short k = part.faces[i];
				
				vTextureCoords[2*k] = vt[2*j];
				vTextureCoords[2*k+1] = vt[2*j+1];
			}
		}
		
		// generate VBOs
		float[] vertexNormals = new float[numVertices() * 8];
		for (int i=0; i<numVertices(); i++) {
			// vertex data (x, y, z, padding)
			vertexNormals[8*i] = v[i*3];
			vertexNormals[8*i+1] = v[i*3+1];
			vertexNormals[8*i+2] = v[i*3+2];
			// normals (x, y, z, padding)
			vertexNormals[8*i+4] = vNormals[i*3];
			vertexNormals[8*i+5] = vNormals[i*3+1];
			vertexNormals[8*i+6] = vNormals[i*3+2];
		}
		FloatBuffer vertexNormalBuf = BufferUtils.asBuffer(vertexNormals);
		vertexNormalBuf.position(0);
		int[] allocatedVBO = { 0 };
		GLES20.glGenBuffers(1, allocatedVBO, 0);
		if (allocatedVBO[0] <= 0)
			throw new RuntimeException("Unable to allocate VBO!");
		vbo = allocatedVBO[0];
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexNormalBuf.capacity() * 4,
				vertexNormalBuf, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		textureCoordsVBO = new HashMap<>();
		for (Map.Entry<Material, float[]> texCoordsEntry: vTextureCoordsMap.entrySet()) {
			FloatBuffer buf = BufferUtils.asBuffer(texCoordsEntry.getValue());
			
			// allocate a VBO
			allocatedVBO[0] = 0;
			GLES20.glGenBuffers(1, allocatedVBO, 0);
			if (allocatedVBO[0] <= 0)
				throw new RuntimeException("Unable to allocate VBO!");
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, allocatedVBO[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buf.capacity() * 4,
					buf, GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			
			textureCoordsVBO.put(texCoordsEntry.getKey(), allocatedVBO[0]);
		}
		
		// initialize the IBO for all parts
		for (TDModelPart part: parts) {
			part.initializeBuffers();
		}
	}
	
	/**
	 * Returns the vertex count of the object.
	 * 
	 * @return The number of vertices read from the object file.
	 */
	@SuppressWarnings("unused")
	public int numVertices() {
		return v.length / 3;
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
	 * Returns the VBO handle for the vertex data.
	 * 
	 * @return The allocated VBO.
	 */
	@SuppressWarnings("unused")
	public Integer getVBO() {
		return vbo;
	}
	
	/**
	 * Returns the vertex texture coordinates VBO for a specific part.
	 * 
	 * @param material The material to return the VBO for.
	 * @return The texture coordinates VBO.
	 */
	@SuppressWarnings("unused")
	public Integer getTextureCoordsBuf(Material material) {
		return textureCoordsVBO.get(material);
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


