package ro.pub.dadgm.pf22.physics;

import java.util.Set;

import ro.pub.dadgm.pf22.utils.Vector3D;

/**
 * The main physics simulation thread.
 * 
 * <p>For receiving the simulation results, see the {@link PhysicsSimulationListener} constructor 
 * parameter.</p>
 * 
 * <p>The simulation algorithm is as follows: 
 * <ul>
 *     <li>the mobile objects' parameters (such as position, direction, speed) will be updated;</li>
 *     <li>next, the collidable objects will be checked for collisions;</li>
 * </ul></p>
 * 
 * <p>Although the events listeners are fired within the simulation, they are safe to modify the 
 * simulated sets (the implementation takes snapshots of them before doing the actual simulation).</p>
 */
public class PhysicsThread extends Thread {
	
	/**
	 * The simulation period, in milliseconds. 
	 */
	public final int PHYSICS_SIMULATION_PERIOD = 100; // milliseconds
	
	
	/**
	 * The simulated mobile objects collection. 
	 * 
	 * <p>Should support concurrent access!</p>
	 */
	protected final Set<MobileObject> mobileObjects;
	
	/**
	 * The simulated collidable objects collection. 
	 *
	 * <p>Should support concurrent access!</p>
	 */
	protected final Set<CollisionObject> collidableObjects;
	
	/**
	 * The simulation listener instance that will receive simulation events.
	 */
	protected final PhysicsSimulationListener listener;
	
	/**
	 * Stores whether the Physics thread is paused.
	 */
	protected boolean paused;
	
	/**
	 * The last time (in system ticks) the objects were simulated.
	 */
	protected long lastTime;
	
	/**
	 * The time already elapsed when this thread was paused.
	 */
	protected long pausedTimeElapsed;
	
	
	/**
	 * Initializes the physics simulation for the specified set of objects.
	 * 
	 * @param mobileObjects The set of mobile objects to continuously simulate.
	 * @param collidableObjects The set of collidable objects to check for collisions.
	 * @param listener The simulation listener instance that will receive simulation events.
	 */
	public PhysicsThread(Set<MobileObject> mobileObjects, Set<CollisionObject> collidableObjects, 
						 PhysicsSimulationListener listener) {
		this.mobileObjects = mobileObjects;
		this.collidableObjects = collidableObjects;
		this.listener = listener;
		this.paused = false;
	}
	
	
	/**
	 * The thread's main loop.
	 */
	@Override
	public void run() {
		// get the System time
		lastTime = System.nanoTime();
		
		// simulation loop
		while (!Thread.interrupted()) {
			int td;
			
			synchronized (this) {
				if (paused) {
					try {
						Thread.sleep(PHYSICS_SIMULATION_PERIOD);

					} catch (InterruptedException e) {
						// nothing
					}
					continue;
				}
				// calculate the time difference
				long now = System.nanoTime();
				td = (int) ((now - lastTime) / 1000000l);
				lastTime = now;
			}
			
			// first, simulate the movement equations
			MobileObject[] mobileObjectsSnapshot = mobileObjects.toArray(new MobileObject[mobileObjects.size()]);
			for (MobileObject object : mobileObjectsSnapshot) {
				updateObjectVelocity(object, td);
				updateObjectPosition(object, td);
			}
			
			// check for collisions
			@SuppressWarnings("unchecked")
			CollisionObject[] collidableObjectsSnapshot = collidableObjects.toArray(new CollisionObject[collidableObjects.size()]);
			for (int i = 0; i < collidableObjectsSnapshot.length; i++) {
				for (int j = i + 1; j < collidableObjectsSnapshot.length; j++) {
					if (collidableObjectsSnapshot[i].collidesWith(collidableObjectsSnapshot[j])) {
						// throw an event
						listener.onCollisionDetected(collidableObjectsSnapshot[i], collidableObjectsSnapshot[j]);
					}
				}
			}
			
			// wait and repeat
			try {
				sleep(PHYSICS_SIMULATION_PERIOD, 0);
				
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}
	
	/**
	 * Pauses the Physics thread and all its processing (for example, when the game is paused).
	 * 
	 * <p>Can be resumed with {@link #resumeProcessing}</p>
	 */
	public void pauseProcessing() {
		synchronized (this) {
			paused = true;
			pausedTimeElapsed = System.nanoTime() - lastTime;
		}
	}
	
	/**
	 * Resumes the Physics processing.
	 */
	public void resumeProcessing() {
		synchronized (this) {
			paused = false;
			lastTime = System.nanoTime() - pausedTimeElapsed;
		}
	}
	
	/**
	 * Calculates the new position for an object.
	 * 
	 * @param object The target object.
	 * @param td The delta-time (step), in milliseconds.   
	 */
	protected void updateObjectPosition(MobileObject object, int td) {
		Vector3D position = new Vector3D(object.getPosition());
		Vector3D velocity = object.getVelocity();
		
		float step = td / 1000.f;
		float dx = velocity.getX()*step;
		float dy = velocity.getY()*step;
		float dz = velocity.getZ()*step;
		
		position.add(new Vector3D(dx, dy, dz));
		
		// update its position
		object.getPosition().setCoordinates(position.getX(), 
				position.getY(), position.getZ());
		
		if (dx != 0 || dy != 0 || dz != 0) 
			listener.onObjectPositionChange(object);
	}
	
	/**
	 * Calculates the new velocity vector for an object.
	 * 
	 * @param object The target object.
	 * @param td The delta-time (step), in milliseconds.   
	 */
	protected void updateObjectVelocity(MobileObject object, int td) {
		// get a snapshot of the object's velocity
		float[] velocity = object.getVelocity().toArray();
		float step = td / 1000.f;
		
		// get the forces acting on the object
		Force[] forces = object.getForces();
		for (Force f: forces) {
			float[] acceleration = f.getAcceleration().toArray();
			
			velocity[0] += acceleration[0] * step;
			velocity[1] += acceleration[1] * step;
			velocity[2] += acceleration[2] * step;
		}
		
		// set the new velocity
		object.getVelocity().setValues(velocity[0], velocity[1], velocity[2]);
	}
	
}
