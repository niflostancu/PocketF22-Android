package ro.pub.dadgm.pf22.game.models;

/**
 * The base model class for the fighter planes (both player and AI).
 * 
 * <p>In the world, the plane . The plane's direction is given by the velocity vector (which is always non-zero).</p>
 */
public class Plane extends BaseMobileModel {
	
	// several constants
	
	/**
	 * Plane's default health.
	 */
	public final float PLANE_HEALTH = 100;
	
	
	// beside position and velocity, a plane has several other attributes.
	
	/**
	 * Plane's roll (rotation around the front-back axis).
	 */
	protected float roll;
	
	/**
	 * Plane's pitch (up/down rotation).
	 */
	protected float pitch;

	/**
	 * Current plane health.
	 */
	protected float health;
	
	
	/**
	 * Model object constructor.
	 */
	public Plane() {
		roll = 0;
		pitch = 0;
		
		// max health at the beginning
		health = PLANE_HEALTH;
	}
	
	
	
	// getters / setters
	
	/**
	 * Returns the plane's health.
	 * 
	 * @return Current plane's health.
	 */
	public synchronized float getHealth() {
		return health;
	}
	
	/**
	 * Returns plane's roll.
	 * 
	 * @return Plane's current roll.
	 */
	public synchronized float getRoll() {
		return roll;
	}
	
	/**
	 * Returns plane's pitch.
	 * 
	 * @return Plane's current pitch.
	 */
	public synchronized float getPitch() {
		return pitch;
	}

}
