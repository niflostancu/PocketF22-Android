package ro.pub.dadgm.pf22.game.models;

import ro.pub.dadgm.pf22.physics.CollisionObject;

/**
 * The common model class for all game objects.
 */
public abstract class BaseModel implements CollisionObject<BaseModel> {
	
	/**
	 * Default constructor.
	 */
	protected BaseModel() {
	}
	
	/**
	 * Default collision detection implementation. Returns false.
	 */
	@Override
	public boolean collidesWith(BaseModel obj) {
		return false;
	}
	
}
