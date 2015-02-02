package ro.pub.dadgm.pf22.utils;

/**
 * Represents a 3D bounding box.
 * 
 * <p>Used to check for collisions between 2 objects.</p>
 */
public class BoundingBox3D {

	/**
	 * The coordinates of the four most important corners of the bounding box.
	 */
	Point3D minmin, minmax, maxmin, maxmax;
	
	
	public void intersects(BoundingBox3D box2) {
		
	}
	
}
