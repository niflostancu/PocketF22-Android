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
		// set the plane facing a random direction with a speed of "1"
		float[] tmpMatrix = new float[16];
		Matrix.setIdentityM(tmpMatrix, 0);
		Matrix.rotateM(tmpMatrix, 0, (float)Math.random()*360, 0, 0, 1f);
		float[] direction = new float[4];
		Matrix.multiplyMV(direction, 0, tmpMatrix, 0, new float[]{ 5, 0, 0, 0 }, 0);
		
		velocity.setValues(direction[0], direction[1], direction[2]);
	}
	
	
	// getters / setters
	
}
