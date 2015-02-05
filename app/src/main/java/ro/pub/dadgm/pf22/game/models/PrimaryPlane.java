package ro.pub.dadgm.pf22.game.models;

import android.opengl.Matrix;

/**
 * Defines a player-controlled plane.
 */
public class PrimaryPlane extends Plane {
	
	
	/**
	 * Model object constructor.
	 */
	public PrimaryPlane() {
		// set the plane facing a random direction with a speed of "5"
		steer((float)Math.random()*360);
		setSpeed(10);
	}
	
	
	// getters / setters
	
}
