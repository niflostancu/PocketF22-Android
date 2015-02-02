package ro.pub.dadgm.pf22.utils;

/**
 * Stores the X, Y, Z coordinates of a 3D point.
 * 
 * <p>The class is mutable (for performance reasons).</p>
 * 
 * <p>Has {@link Object#equals} and {@link Object#hashCode} overrides.</p>
 */
public class Point3D {
	/**
	 * The 3D coordinates of the point.
	 */
	private float x, y, z;

	/**
	 * Constructs the (0, 0, 0) point.
	 */
	public Point3D() {
		x = y = z = 0;
	}
	
	/**
	 * Construct a point with the specified coordinates.
	 * 
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 */
	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	/**
	 * Getter for the X coordinate.
	 * 
	 * @return X
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Setter for the X coordinate.
	 *
	 * @param x The new X value.
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Getter for the Y coordinate.
	 *
	 * @return Y
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Setter for the Y coordinate.
	 *
	 * @param y The new Y value.
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Getter for the Z coordinate.
	 *
	 * @return Z
	 */
	public float getZ() {
		return z;
	}
	
	/**
	 * Setter for the Z coordinate.
	 *
	 * @param z The new Z value.
	 */
	public void setZ(float z) {
		this.z = z;
	}
	
	
	/**
	 * Setter for all point's coordinates.
	 * 
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 * @param z The Z coordinate.
	 */
	public void setCoordinates(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Converts the point to a [x, y, z] float array.
	 *
	 * @return A float[] array with the point's coordinates.
	 */
	public float[] toArray() {
		return new float[] { x, y, z };
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		
		if (!(obj instanceof Point3D))
			return false;
		
		Point3D p2 = (Point3D) obj;
		return (x == p2.x) && (y == p2.y) && (z == p2.z);
	}
	
	@Override
	public int hashCode() {
		final float p = 853; // some prime number
		return Math.round(p*p*x + p*y + z);
	}
	
}
