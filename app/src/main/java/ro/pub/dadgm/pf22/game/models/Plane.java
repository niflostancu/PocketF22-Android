package ro.pub.dadgm.pf22.game.models;

import android.opengl.Matrix;

import ro.pub.dadgm.pf22.physics.CollisionObject;
import ro.pub.dadgm.pf22.utils.BoundingBox3D;
import ro.pub.dadgm.pf22.utils.Vector3D;

/**
 * The base model class for the fighter planes (both player and AI).
 * 
 * <p>The plane is an object that can move on the XOY (using the yaw attribute) and separately on 
 * the Z axis (using the pitch). The roll is only used for animation purposes.</p>
 * 
 * <p>The plane's velocity vector is dynamically calculated using these angle attributes and the 
 * {@link #speed} property. It can't be changed by the Physics engine (for a much more simplified 
 * model of the plane, the angles/speed are controlled directly).</p>
 */
public class Plane extends BaseMobileModel {
	
	// several constants
	
	/**
	 * Plane's default health.
	 */
	public final float PLANE_HEALTH = 100;
	
	/**
	 * Plane's length (from back to front).
	 */
	public final float PLANE_LENGTH = 19 / 19000.0f; // mesh length: 19
	
	/**
	 * Plane's side width (from wing to wing).
	 */
	public final float PLANE_WIDTH = 13 / 19000.0f; // mesh width: 13
	
	/**
	 * Plane's height.
	 */
	public final float PLANE_HEIGHT = 4 / 19000.0f; // mesh height: 4
	
	
	// beside position and velocity, a plane has several other attributes.
	
	/**
	 * Plane's roll (rotation around the front-back axis).
	 * In degrees.
	 */
	protected float roll;
	
	/**
	 * Plane's pitch (up/down rotation).
	 * In degrees. The reference is perfect horizontal (in parallel with the XOY plane).
	 * 
	 * <p>Clamped at 45 degrees.</p>
	 */
	protected float pitch;
	
	/**
	 * The plane's angle/direction on the XOY (ground) plane.
	 * In degrees. The reference is parallel with the OX axis.
	 */
	protected float yaw;
	
	/**
	 * Plane's speed (world units / second).
	 */
	protected float speed;
	
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
		yaw = 0;
		speed = 0.5f;
		
		// max health at the beginning
		health = PLANE_HEALTH;
	}
	
	/**
	 * Changes the plane's yaw angle.
	 * 
	 * @param angle The angle to steer by (degrees).
	 */
	@SuppressWarnings("unused")
	public void steer(float angle) {
		yaw = (yaw + angle) % 360;
	}

	/**
	 * Pitches the plane up / down (negative values for down).
	 * 
	 * <p>Note that there is a limit of +/-45 degrees.</p>
	 * 
	 * @param angle The angle to pitch by.
	 */
	@SuppressWarnings("unused")
	public void pitch(float angle) {
		setPitch(pitch + angle);
	}
	
	// getters / setters
	
	/**
	 * Returns the plane's health.
	 * 
	 * @return Current plane's health.
	 */
	@SuppressWarnings("unused")
	public synchronized float getHealth() {
		return health;
	}
	
	/**
	 * Returns plane's roll.
	 * 
	 * @return Plane's current roll.
	 */
	@SuppressWarnings("unused")
	public synchronized float getRoll() {
		return roll;
	}
	
	/**
	 * Returns plane's pitch.
	 * 
	 * @return Plane's current pitch.
	 */
	@SuppressWarnings("unused")
	public synchronized float getPitch() {
		return pitch;
	}
	
	/**
	 * Sets the plane's pitch as absolute value.
	 * 
	 * @param angle The angle to set as pitch.
	 */
	@SuppressWarnings("unused")
	public synchronized void setPitch(float angle) {
		if (angle > 45) angle = 45;
		if (angle < -45) angle = -45;
		this.pitch = angle;
	}
	
	/**
	 * Sets the plane's roll as absolute value.
	 *
	 * @param angle The angle to set as roll.
	 */
	@SuppressWarnings("unused")
	public synchronized void setRoll(float angle) {
		if (angle > 90) angle = 90;
		if (angle < -90) angle = -90;
		this.roll = angle;
	}
	
	/**
	 * Returns plane's yaw.
	 * 
	 * @return Plane's current yaw.
	 */
	@SuppressWarnings("unused")
	public synchronized float getYaw() {
		return yaw;
	}

	/**
	 * Returns plane's forward speed.
	 * 
	 * @return Plane's speed.
	 */
	@SuppressWarnings("unused")
	public synchronized float getSpeed() {
		return speed;
	}
	
	/**
	 * Changes the plane's speed. The direction is left unaltered.
	 *
	 * @param speed Speed value (in "world units")
	 */
	@SuppressWarnings("unused")
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	
	/**
	 * Computes the current velocity of the plane.
	 * 
	 * <p>The velocity object should not be modified from the outside. 
	 * If you want to control the plane, use the yaw, pitch and speed attributes.</p>
	 * 
	 * @return The velocity vector of the plane.
	 */
	@Override
	public synchronized Vector3D getVelocity() {
		float[] tmpMatrix = new float[16];
		Matrix.setIdentityM(tmpMatrix, 0);
		Matrix.rotateM(tmpMatrix, 0, pitch, 1, 0, 0);
		Matrix.rotateM(tmpMatrix, 0, yaw, 0, 0, 1f);
		
		float[] velocityArr = new float[]{ speed, 0, 0, 0 };
		float[] direction = new float[4];
		Matrix.multiplyMV(direction, 0, tmpMatrix, 0, velocityArr, 0);
		
		velocity.setValues(direction[0], direction[1], direction[2]);
		
		return velocity;
	}
	
	
	/**
	 * Returns the plane's bounding box.
	 */
	public BoundingBox3D getBoundingBox() {
		return new BoundingBox3D(position, new float[]{ PLANE_LENGTH, PLANE_WIDTH, PLANE_HEIGHT });
	}
	
	@Override
	public boolean collidesWith(CollisionObject obj) {
		if (obj instanceof Plane) {
			Plane planeObj = (Plane)obj;
			return this.getBoundingBox().intersects(planeObj.getBoundingBox());
			
		} else if (obj instanceof Terrain) {
			// Terrain already has this implemented
			return obj.collidesWith(this);
			
		} else if (obj instanceof Projectile) {
			Projectile projectileObj = (Projectile)obj;
			return this.getBoundingBox().intersects(projectileObj.getBoundingBox());
		}
		
		return false;
	}
	
}
