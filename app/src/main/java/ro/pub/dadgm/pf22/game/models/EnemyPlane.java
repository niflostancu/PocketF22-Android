package ro.pub.dadgm.pf22.game.models;

import android.opengl.Matrix;

/**
 * Defines an enemy (AI-controlled) plane.
 */
public class EnemyPlane extends Plane {
	
	
	/**
	 * Model object constructor.
	 */
	public EnemyPlane() {
		// set the plane facing a random direction with a speed of "5"
		steer((float)Math.random()*360);
		setSpeed(4f);
	}
	
	
	// getters / setters
	
}
