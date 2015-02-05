package ro.pub.dadgm.pf22.game.models;

import ro.pub.dadgm.pf22.physics.CollisionObject;

/**
 * The base class for all projectiles.
 */
public class Projectile extends BaseMobileModel {
	
	
	/**
	 * Model object constructor.
	 */
	public Projectile() {
		// empty
	}
	
	
	@Override
	public boolean collidesWith(CollisionObject obj) {
		// TODO implement this
		return false;
	}
	
	// getters / setters
	
	
}
