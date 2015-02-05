package ro.pub.dadgm.pf22.physics;

import java.util.Collection;

import ro.pub.dadgm.pf22.utils.Point3D;
import ro.pub.dadgm.pf22.utils.Vector3D;

/**
 * The interface that all mobile objects that need to have their physics simulated must implement.
 */
public interface MobileObject {
	
	/**
	 * Returns the position object.
	 * 
	 * <p>It is also used for modifying the object's position.</p>
	 * 
	 * @return Object's position point.
	 */
	public Point3D getPosition();
	
	/**
	 * Returns the velocity vector object.
	 * 
	 * <p>It is also used for modifying the object's velocity.</p>
	 * 
	 * @return
	 */
	public Vector3D getVelocity();
	
	/**
	 * Returns the list of forces that currently act on the object.
	 * 
	 * @return A collection of forces.
	 */
	public Collection<Force> getForces();
	
}
