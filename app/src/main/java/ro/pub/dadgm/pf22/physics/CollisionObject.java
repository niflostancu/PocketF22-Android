package ro.pub.dadgm.pf22.physics;

/**
 * All objects which can collide with other objects must implement this interface.
 * 
 * <p>Care should be taken when implementing the methods defines in this interface: they will be 
 * executed on the PhysicsThread!</p>
 */
public interface CollisionObject {
	
	/**
	 * Tests if the current object collides with the specified one.
	 * 
	 * <p>All implementations should be reflexive (A collides with B <=> B collides with A).</p>
	 * 
	 * @param obj The object to test the collision with.
	 * @return True if the two objects collide, false otherwise.
	 */
	public boolean collidesWith(CollisionObject obj);
	
}
