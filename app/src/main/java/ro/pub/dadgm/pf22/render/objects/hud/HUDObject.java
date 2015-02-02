package ro.pub.dadgm.pf22.render.objects.hud;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.objects.AbstractObject3D;
import ro.pub.dadgm.pf22.utils.Point3D;

/**
 * A HUD object is a 2D object to be drawed on top of a 3D scene (or a static background).
 * 
 * <p>This abstract class defines the common characteristics for all HUD objects.</p>
 * 
 * <p>A HUD object has a set of coordinates: (x,y) defines the 2D position on the screen and z is 
 * used for depth-positioning (for overlapping objects).</p>
 */
public abstract class HUDObject extends AbstractObject3D {
	
	/**
	 * We use a 3D point that also stores a Z (used for overlapping elements).
	 */
	protected Point3D position;

	/**
	 * As most HUD 2D objects also have a width and a height.
	 */
	protected float width = 1, height = 1;
	
	
	/**
	 * The constructor with mandatory parameters.
	 * 
	 * @param scene The parent scene object.
	 * @param tag The object's tag.
	 * @param priority The object's priority.
	 */
	public HUDObject(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		this.position = new Point3D();
	}
	
	
	// several getters and setters
	
	/**
	 * Returns the position mutable object. All modifications will affect the object.
	 * 
	 * @return Position's Point3D object.
	 */
	public Point3D position() {
		return position;
	}
	
	/**
	 * Returns the 2D dimensions of the object.
	 * 
	 * @return A float[2] array with the width and the height of the object.
	 */
	public float[] getDimensions() {
		return new float[] { width, height };
	}
	
	/**
	 * Changes the dimensions of the 2D object.
	 * 
	 * @param width The new width.
	 * @param height The new height.
	 */
	public void setDimensions(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
}
