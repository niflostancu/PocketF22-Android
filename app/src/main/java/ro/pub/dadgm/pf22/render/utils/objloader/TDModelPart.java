package ro.pub.dadgm.pf22.render.utils.objloader;

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
	 * Buffers to store the indices.
	 */
	protected ShortBuffer facesBuf;
	
	
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
		facesBuf = BufferUtils.allocateShortBuffer(faces.length * 2);
		facesBuf.put(faces);
		facesBuf.position(0);
	}
	
	@SuppressWarnings("unused")
	public ShortBuffer getFaceBuffer() {
		return facesBuf;
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
