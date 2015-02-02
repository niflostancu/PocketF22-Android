package ro.pub.dadgm.pf22.render.objects.hud;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.ObjectsManager;

/**
 * A drawable HUD component that contains other components (using Composite Pattern).
 * 
 * <p>As the name implies, its contents are to be centered automatically on the X axis.</p>
 */
public class CenteredContainer extends HUDObject {
	
	/**
	 * The list of child objects to manage.
	 */
	protected ObjectsManager<HUDObject> objects;
	
	
	/**
	 * Initializes the object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public CenteredContainer(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		
		// initialize the objects manager
		objects = new ObjectsManager<>();
	}
	
	
	/**
	 * Recalculates the new positioning of the objects.
	 * 
	 * <p>Should be called when the scene's dimensions are modified or this object is repositioned.</p>
	 */
	public void repositionObjects() {
		for (HUDObject object: objects) {
			// center the object
			float[] dims = object.getDimensions();
			object.position().setX( position.getX() + (width / 2) - (dims[0] / 2) );
		}
	}
	
	// several overrides
	
	@Override
	public void draw() {
		// draw all children objects
		objects.drawAll();
	}
	
	@Override
	public void setDimensions(float width, float height) {
		super.setDimensions(width, height);
		repositionObjects();
	}
	
	
	// getters / setters
	
	/**
	 * Gives access to the container's ObjectsManager to add/remove/find objects.
	 * 
	 * @return The container's ObjectsManager collection.
	 */
	public ObjectsManager<HUDObject> getObjects() {
		return objects;
	}
	
}
