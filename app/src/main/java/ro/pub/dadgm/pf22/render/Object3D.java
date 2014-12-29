package ro.pub.dadgm.pf22.render;

/**
 * Represents a renderable 3D object that can be drawn on a scene.
 * 
 * <p>The objects can do initialization work like loading shaders / mesh / textures in their 
 * constructors.</p>
 * 
 * <p>The objects' equals method is used for searching for it in the ObjectsManager collection, but 
 * the default Object implementation (reference comparison) is usually fine.</p>
 */
public interface Object3D {
	
	/**
	 * Renders the object.
	 */
	public void draw();
	
}
