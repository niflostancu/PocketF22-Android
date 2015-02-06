package ro.pub.dadgm.pf22.render.utils.objloader;

import android.opengl.GLES20;

import java.nio.ShortBuffer;

import ro.pub.dadgm.pf22.render.utils.BufferUtils;

/**
 * Stores a model part.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class TDModelPart {
	
	/**
	 * The part's faces (loaded/converted as triangles).
	 */
	protected short[] faces;
	
	/**
	 * An array with texture coordinates' indices the current part. 
	 */
	protected short[] vtPointer;
	
	/**
	 * An array with vertex normals' indices for the current part.
	 */
	protected short[] vnPointer;
	
	/**
	 * The material to use.
	 */
	protected Material material;
	
	/**
	 * The IBO allocated for the vertex indices.
	 */
	protected int ibo;
	
	
	/**
	 * Model part constructor.
	 * 
	 * @param faces Part's vertex indices array.
	 * @param vtPointer Texture coordinates' indices array.
	 * @param vnPointer Vertex normals' indices array.
	 * @param material The part's associated material, if any.
	 */
	public TDModelPart(short[] faces, short[] vtPointer,
					   short[] vnPointer, 
					   Material material) {
		super();
		
		this.faces = faces;
		this.vtPointer = vtPointer;
		this.vnPointer = vnPointer;
		this.material = material;
		
		// allocate buffers
		ShortBuffer facesBuf = BufferUtils.allocateShortBuffer(faces.length * 2);
		facesBuf.put(faces);
		facesBuf.position(0);

		// allocate an IBO
		int[] allocatedIBO = { 0 };
		GLES20.glGenBuffers(1, allocatedIBO, 0);
		if (allocatedIBO[0] <= 0)
			throw new RuntimeException("Unable to allocate IBO!");
		ibo = allocatedIBO[0];
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, allocatedIBO[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, facesBuf.capacity() * 2,
				facesBuf, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	@SuppressWarnings("unused")
	public int getIBO() {
		return ibo;
	}
	
	@SuppressWarnings("unused")
	public int getFacesCount() {
		return faces.length;
	}
	
	@SuppressWarnings("unused")
	public Material getMaterial() {
		return material;
	}
	
	
}
