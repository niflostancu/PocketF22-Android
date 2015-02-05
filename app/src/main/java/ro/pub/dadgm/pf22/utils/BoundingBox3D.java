package ro.pub.dadgm.pf22.utils;

import java.io.Serializable;

/**
 * Represents a 3D Axis Aligned Bounding Box.
 * 
 * <p>Used to check for collisions between 2 objects.</p>
 */
public class BoundingBox3D implements Serializable {
	
	/**
	 * The minimum / maximum coordinates on all axes.
	 */
	Point3D min, max;
	
	
	/**
	 * Bounding box constructor.
	 * 
	 * @param min The point with the minimum values for all coordinates.
	 * @param max The point with the maximum values for all coordinates.
	 */
	@SuppressWarnings("unused")
	public BoundingBox3D(Point3D min, Point3D max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Constructs a bounding box from the object's position and its dimensions.
	 * 
	 * @param position Object's center position.
	 * @param dimensions A float array with the XYZ dimensions.
	 */
	public BoundingBox3D(Point3D position, float[] dimensions) {
		this.min = new Point3D(position.getX() - dimensions[0]/2,
				position.getY() - dimensions[1]/2,
				position.getZ() - dimensions[2]/2);
		this.max = new Point3D(position.getX() + dimensions[0]/2,
				position.getY() + dimensions[1]/2,
				position.getZ() + dimensions[2]/2);
	}
	
	/**
	 * Tests if the current box intersects with a second.
	 * 
	 * @param box2 The second box to intersect with.
	 * @return True if they intersect, false otherwise.   
	 */
	public boolean intersects(BoundingBox3D box2) {
		float[] min1 = this.min.toArray();
		float[] min2 = box2.min.toArray();
		float[] max1 = this.max.toArray();
		float[] max2 = box2.max.toArray();
		
		return  max1[0] > min2[0] &&
				min1[0] < max2[0] &&
				max1[1] > min2[1] &&
				min1[1] < max2[1] &&
				max1[2] > min2[2] &&
				min1[2] < max2[2];
	}
	
}
