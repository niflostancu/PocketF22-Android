package ro.pub.dadgm.pf22.physics;

import ro.pub.dadgm.pf22.utils.Vector3D;

/**
 * Encapsulates a Newtonian force.
 */
public class Force {
	
	/**
	 * The mass of the object.
	 */
	protected float mass;
	
	/**
	 * Stores the acceleration.
	 */
	protected Vector3D acceleration;
	
	
	/**
	 * Force constructor from mass and acceleration.
	 * 
	 * @param mass The mass.
	 * @param acceleration Times the acceleration.
	 */
	public Force(float mass, Vector3D acceleration) {
		this.mass = mass;
		this.acceleration = acceleration;
	}
	
	/**
	 * Returns the force's acceleration vector.
	 * 
	 * @return Resulting acceleration.
	 */
	@SuppressWarnings("unused")
	public Vector3D getAcceleration() {
		return acceleration;
	}
	
	/**
	 * Returns the force's associated mass value.
	 * 
	 * @return Force's mass component.
	 */
	@SuppressWarnings("unused")
	public float getMass() {
		return mass;
	}
	
}
