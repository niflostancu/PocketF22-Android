package ro.pub.dadgm.pf22.render.objects;

/**
 * Represents a renderable 3D object that can be drawn on a scene.
 * 
 * <p>The objects can do initialization work like loading shaders / mesh / textures in their 
 * constructors.</p>
 * 
 * <p>The objects' equals method is used for searching for it in the {@link ObjectsManager} collection, 
 * but the default {@link Object} implementation (reference comparison) is usually fine.</p>
 */
public interface Object3D {
	
	/**
	 * Renders the object.
	 */
	public void draw();
	
	/**
	 * Returns the object's tag.
	 * 
	 * <p>An object can be tagged with a string, so that multiple objects with the same tag can be 
	 * collectively managed using {@link ObjectsManager}.</p>
	 * 
	 * <p>The tag should not be modified after the object has been added to a {@link ObjectsManager} 
	 * collection!</p>
	 * 
	 * @return Object tag.
	 */
	public String getTag();
	
	/**
	 * Returns the object's draw priority. The first object to be drawn on the screen will be the 
	 * one with the lowest piority.
	 * 
	 * <p>The recommended default priority is 0 (it's a signed integer ;)</p>
	 * 
	 * <p>The priority should not be modified after the object has been added to a ObjectsManager 
	 * collection!</p>
	 * 
	 * @return Object's draw priority.
	 */
	public int getPriority();
	
	/**
	 * Called when the object needs to be destroyed.
	 * 
	 * <p>Use this to free any allocated resources (during the constructor / draw methods).</p>
	 */
	public void destroy();
	
}
