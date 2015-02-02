package ro.pub.dadgm.pf22.utils;

/**
 * Represents a 2D bounding box.
 * 
 * <p>Used to check an object for touch events (see {@link ro.pub.dadgm.pf22.render.View#onTouchEvent}).</p>
 */
public class BoundingBox2D {
	
	/**
	 * The box's coordinates.
	 */
	private float boxX, boxY;
	
	/**
	 * The box's dimensions.
	 */
	private float width, height;
	
	
	/**
	 * Bounding box object constructor.
	 * 
	 * @param x Box's X coordinate.
	 * @param y Box's Y coordinate.
	 * @param width Box's width.
	 * @param height Box's height.
	 */
	public BoundingBox2D(float x, float y, float width, float height) {
		this.boxX = x;
		this.boxY = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Checks if the specified point (x, y) is contained within the box.
	 * 
	 * @param x The X coordinate of the point.
	 * @param y The Y coordinate of the point.
	 * @return True if the point is contained, false otherwise.
	 */
	public boolean contains(float x, float y) {
		return !(x < boxX || y < boxY || x > (boxX + width) || y > (boxY + height));
	}
	
}
