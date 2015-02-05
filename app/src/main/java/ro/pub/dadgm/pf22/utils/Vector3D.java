package ro.pub.dadgm.pf22.utils;

/**
 * Defines a 3D vector (used to represent velocity in physics, for example).
 * 
 * <p>The instances are mutable (their value can be modified after creation) for efficiency purposes.</p>
 * 
 * <p>All instance methods are synchronized (they guarantee consistent access from multiple threads).</p>
 * 
 * <p>The class also implements several vector operations.</p>
 */
public class Vector3D {
	
	/**
	 * The values of the vector.
	 */
	private float x, y, z;
	
	/**
	 * Constructs the (0, 0, 0) vector.
	 */
	public Vector3D() {
		x = y = z = 0;
	}
	
	/**
	 * Construct a vector with the specified values.
	 * 
	 * @param x The X value.
	 * @param y The Y value.
	 * @param z The Z value.
	 */
	@SuppressWarnings("unused")
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param obj The object to copy.
	 */
	@SuppressWarnings("unused")
	public Vector3D(Vector3D obj) {
		this.setValues(obj.getX(), obj.getY(), obj.getZ());
	}
	
	/**
	 * Getter for the X value.
	 * 
	 * @return X
	 */
	@SuppressWarnings("unused")
	public synchronized float getX() {
		return x;
	}
	
	/**
	 * Setter for the X value.
	 *
	 * @param x The new X value.
	 */
	@SuppressWarnings("unused")
	public synchronized void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Getter for the Y value.
	 *
	 * @return Y
	 */
	@SuppressWarnings("unused")
	public synchronized float getY() {
		return y;
	}
	
	/**
	 * Setter for the Y value.
	 *
	 * @param y The new Y value.
	 */
	@SuppressWarnings("unused")
	public synchronized void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Getter for the Z value.
	 *
	 * @return Z
	 */
	@SuppressWarnings("unused")
	public synchronized float getZ() {
		return z;
	}
	
	/**
	 * Setter for the Z value.
	 *
	 * @param z The new Z value.
	 */
	@SuppressWarnings("unused")
	public synchronized void setZ(float z) {
		this.z = z;
	}
	
	
	/**
	 * Setter for all point's values.
	 * 
	 * @param x The X value.
	 * @param y The Y value.
	 * @param z The Z value.
	 */
	public synchronized void setValues(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	/**
	 * Converts the vector to a [x, y, z] float array.
	 *
	 * @return A float[] array with the vector's values.
	 */
	@SuppressWarnings("unused")
	public synchronized float[] toArray() {
		return new float[] { getX(), getY(), getZ() };
	}

	/**
	 * Adds another vector to the current instance.
	 */
	@SuppressWarnings("unused")
	public synchronized void add(Vector3D vec2) {
		setX(getX() + vec2.getX());
		setY(getY() + vec2.getY());
		setZ(getZ() + vec2.getZ());
	}
	
	/**
	 * Subtracts another vector from the current instance.
	 */
	@SuppressWarnings("unused")
	public synchronized void subtract(Vector3D vec2) {
		setX(getX() - vec2.getX());
		setY(getY() - vec2.getY());
		setZ(getZ() - vec2.getZ());
	}

	/**
	 * Calculates the length of the vector (square root of the dot product with itself).
	 * 
	 * @return Vector's length.
	 */
	public synchronized float length() {
		return (float)Math.sqrt( getX()*getX() + getY()*getY() + getZ()*getZ() );
	}
	
	/**
	 * Returns the dot product of two vectors.
	 * 
	 * @param vec2 The second vector.
	 * @return The dot product between this and vec2.
	 */
	@SuppressWarnings("unused")
	public synchronized float dotProduct(Vector3D vec2) {
		return ( getX()*vec2.getX() + getY()*vec2.getY() + getZ()*vec2.getZ() );
	}
	
	/**
	 * Computes the cross product between two 3D vectors.
	 * 
	 * @param vec1 First vector.
	 * @param vec2 Second vector.
	 * @return The resulting vector.
	 */
	@SuppressWarnings("unused")
	public synchronized static Vector3D cross(Vector3D vec1, Vector3D vec2) {
		Vector3D res = new Vector3D();
		
		res.setX(vec1.getY() * vec2.getZ() - vec2.getY() * vec1.getZ());
		res.setY(vec1.getZ() * vec2.getX() - vec2.getZ() * vec1.getX());
		res.setZ(vec1.getX() * vec2.getY() - vec2.getX() * vec1.getY());
		
		return res;
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		if (obj == null) return false;
		
		if (!(obj instanceof Vector3D))
			return false;
		
		Vector3D p2 = (Vector3D) obj;
		return (getX() == p2.getX()) && (getY() == p2.getY()) && (getZ() == p2.getZ());
	}
	
	@Override
	public synchronized int hashCode() {
		final float p = 853; // some prime number
		return Math.round(p*p*getX() + p*getY() + getZ());
	}
	
}
