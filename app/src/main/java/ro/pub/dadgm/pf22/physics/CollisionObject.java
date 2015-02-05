package ro.pub.dadgm.pf22.physics;

/**
 * All objects which can collide with other objects must implement this interface.
 */
public interface CollisionObject<T extends CollisionObject> {
	
	/**
	 * Tests if the current object collides with the specified one.
	 * 
	 * <p>All implementations should be reflexive (A collides with B <=> B collides with A).</p>
	 * 
	 * @param obj The object to test the collision with.
	 * @return True if the two objects collide, false otherwise.
	 */
	public boolean collidesWith(T obj);
	
}
