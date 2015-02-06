package ro.pub.dadgm.pf22.game.models;

import ro.pub.dadgm.pf22.physics.CollisionObject;
import ro.pub.dadgm.pf22.utils.BoundingBox3D;

/**
 * The base class for all projectiles.
 */
public class Projectile extends BaseMobileModel {

	/**
	 * Defines a projectile's collision dimensions.
	 */
	public final static float PROJECTILE_DIMS = 0.1f;
	
	/**
	 * Defines the possible projectile types.
	 */
	public static enum ProjectileType {
		PROJECTILE_ROCKET, 
		PROJECTILE_BULLETS
	}
	
	
	/**
	 * Projectile's type.
	 */
	protected ProjectileType type;

	/**
	 * The orientation of the projectile.
	 */
	protected float yaw = 0, pitch = 0;
	
	/**
	 * Model object constructor.
	 */
	public Projectile(ProjectileType type) {
		this.type = type;
	}
	
	
	/**
	 * Returns the plane's bounding box.
	 */
	public BoundingBox3D getBoundingBox() {
		return new BoundingBox3D(position, new float[]{ PROJECTILE_DIMS, PROJECTILE_DIMS, PROJECTILE_DIMS });
	}

	/**
	 * Sets the orientation angles of the projectile.
	 * 
	 * @param yaw Projectile's yaw.
	 * @param pitch Projectile's pitch.
	 */
	public void setOrientation(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	/**
	 * Returns the yaw angle.
	 * 
	 * @return The yaw of the projectile.
	 */
	public float getYaw() {
		return yaw;
	}
	
	/**
	 * Returns the pitch angle.
	 * 
	 * @return The yaw of the projectile.
	 */
	public float getPitch() {
		return pitch;
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
	
	
	// getters / setters
	
	/**
	 * Returns the projectile type.
	 * 
	 * @return Projectile's type.
	 */
	public ProjectileType getType() {
		return type;
	}
	
}
