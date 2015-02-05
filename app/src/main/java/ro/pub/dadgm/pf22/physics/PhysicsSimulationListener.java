package ro.pub.dadgm.pf22.physics;

/**
 * Defines the interface for physics simulation event listeners.
 * 
 * <p>Note: these methods will be executed on the PhysicsThread!</p>
 */
public interface PhysicsSimulationListener {
	
	/**
	 * Called after an object's position has been changed. 
	 * 
	 * <p>Used to validate if the object is within bounds. 
	 * Feel free to alter the object in any way as action.</p>
	 * 
	 * @param object The object whose position changed.
	 */
	public void onObjectPositionChange(MobileObject object);
	
	/**
	 * Notifies the listener that a collision has been detected between two objects.
	 * 
	 * @param obj1 The first object.
	 * @param obj2 The second object.
	 */
	public void onCollisionDetected(CollisionObject obj1, CollisionObject obj2);
	
}
