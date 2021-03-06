package ro.pub.dadgm.pf22.game;

import android.util.Log;

import java.io.Serializable;
import java.util.Collection;

import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.game.models.*;
import ro.pub.dadgm.pf22.physics.CollisionObject;
import ro.pub.dadgm.pf22.physics.MobileObject;
import ro.pub.dadgm.pf22.physics.PhysicsSimulationListener;
import ro.pub.dadgm.pf22.physics.PhysicsThread;
import ro.pub.dadgm.pf22.utils.Vector3D;

/**
 * Manages the game and enforces its rules.
 * 
 * <p>The game must be started by calling the {@link #start} method and can be stopped by calling the 
 * {@link #stop} method.</p>
 */
public class Game implements Serializable {
	
	/**
	 * Defines the game's possible states.
	 */
	public static enum GameStatus {
		STOPPED, // hard stop, all resources are freed
		RUNNING, // the game is running
		PAUSED // a soft stop that does not require its structures to be deallocated
	}
	
	/**
	 * The difficulty levels of the game (AI).
	 */
	public static enum Difficulty {
		EASY,
		MEDIUM,
		HARD
	}

	/**
	 * The number of planer per difficulty level.
	 */
	public static final int NUM_PLANES_EASY = 5, 
			NUM_PLANES_MEDIUM = 5,
			NUM_PLANES_HARD = 15;
	
	/**
	 * Whether the game is running or has been stopped / paused.
	 */
	protected GameStatus status;
	
	/**
	 * The game world.
	 */
	protected World world;
	
	/**
	 * The difficulty of the game.
	 */
	protected Difficulty difficulty;
	
	/**
	 * Sound status (on / off).
	 */
	protected boolean sound;
	
	/**
	 * The current score.
	 */
	protected float score;
	
	/**
	 * The parent activity.
	 */
	protected transient MainActivity activity;
	
	/**
	 * The simulation thread of the Physics module.
	 */
	protected transient PhysicsThread physicsThread;
	
	/**
	 * The thread for smooth controlling the planes.
	 */
	protected transient SmoothControlThread smoothControl;
	
	
	/**
	 * Game constructor.
	 * 
	 * <p>Initializes an empty game.</p>
	 */
	public Game() {
		status = GameStatus.STOPPED;
		sound = true;
		
		difficulty = Difficulty.EASY;
	}
	
	
	/**
	 * Injects the parent activity dependency into the current Game instance.
	 * 
	 * <p>Must be called before anything else (especially start)</p>
	 * 
	 * @param activity The parent activity.
	 */
	public void injectActivity(MainActivity activity) {
		this.activity = activity;
	}
	
	/**
	 * Starts/unpauses the current game.
	 * 
	 * <p>The instance parameters can't be changed once the game is running!</p>
	 */
	public synchronized void start() {
		if (status == GameStatus.RUNNING)
			return;
		
		if (status == GameStatus.PAUSED) {
			initializeTransientObjects();
			
			// unsleep the threads and continue
			if (physicsThread.isAlive()) {
				physicsThread.resumeProcessing();
			} else {
				// cold start
				physicsThread.start();
			}
			
			if (smoothControl.isAlive()) {
				smoothControl.resumeProcessing();
			} else {
				// cold start
				smoothControl.start();
			}
			
			status = GameStatus.RUNNING;
			return;
		}
		
		// initialize the game world
		world = new World();
		
		Terrain terrain = world.getTerrain();
		
		// set player's initial position to a random point
		PrimaryPlane player = world.getPlayer();
		player.getPosition().setCoordinates(
				(float) Math.random() * (World.WORLD_WIDTH_X / 2) + World.WORLD_WIDTH_X / 4,
				(float) Math.random() * (World.WORLD_WIDTH_Y / 2) + World.WORLD_WIDTH_Y / 4,
				/*(float) Math.random() * (World.WORLD_MAX_HEIGHT/3 - terrain.getMaxHeight()) + 
						terrain.getMaxHeight()*/ terrain.getMaxHeight() + 2f );
		
		// generate the enemy planes
		int numEnemyPlanes = NUM_PLANES_EASY;
		switch (difficulty) {
			case MEDIUM: numEnemyPlanes = NUM_PLANES_MEDIUM; break;
			case HARD: numEnemyPlanes = NUM_PLANES_HARD; break;
		}
		
		for (int i=0; i<numEnemyPlanes; i++) {
			EnemyPlane enemy = new EnemyPlane();
			boolean firstPos = true;
			
			while (firstPos || checkCollisions(enemy, world.getCollidableObjects())) {
				firstPos = false;
				enemy.getPosition().setCoordinates(
						(float) Math.random() * (World.WORLD_WIDTH_X / 2) + World.WORLD_WIDTH_X / 4,
						(float) Math.random() * (World.WORLD_WIDTH_Y / 2) + World.WORLD_WIDTH_Y / 4,
						terrain.getMaxHeight() + (float)Math.random()*5 + 1f);
				enemy.steer((float)Math.random()*360);
			}
			
			world.addPlane(enemy);
		}
		
		initializeTransientObjects();
		
		// the game is on!
		physicsThread.start();
		smoothControl.start();
		
		status = GameStatus.RUNNING;
	}
	
	
	/**
	 * Checks a single object for collisions with a collection.
	 * 
	 * @param object The object to verify.
	 * @param collection The collection to check.
	 * @return True if the object collides, false otherwise.
	 */
	protected boolean checkCollisions(CollisionObject object, Collection<CollisionObject> collection) {
		for (CollisionObject object2: collection) {
			if (object.collidesWith(object2))
				return true;
		}
		return false;
	}
	
	/**
	 * [Re]Initializes the transient objects that can't be serialized.
	 */
	protected void initializeTransientObjects() {
		// initialize the Physics module
		if (physicsThread == null)
			physicsThread = new PhysicsThread(world.getMobileObjects(), world.getCollidableObjects(), new PhysicsListener());
		
		if (smoothControl == null)
			smoothControl = new SmoothControlThread();
		
		// TODO: initialize the AI module
	}
	
	/**
	 * Stops the game, deallocating all the resources used (the simulation Threads, for example). 
	 */
	public synchronized void stop() {
		if (status == GameStatus.STOPPED)
			return;
		
		// stop the threads
		physicsThread.interrupt();
		smoothControl.interrupt();
		
		try {
			physicsThread.join();
			smoothControl.join();
			
		} catch (InterruptedException e) {
			Log.e(Game.class.getSimpleName(), "Thread interrupted!", e);
		}
		
		physicsThread = null;
		smoothControl = null;
		world = null;
		
		status = GameStatus.STOPPED;
	}
	
	/**
	 * Pauses the game.
	 */
	public synchronized void pause() {
		if (status != GameStatus.RUNNING)
			return;
		
		physicsThread.pauseProcessing();
		smoothControl.pauseProcessing();
		
		status = GameStatus.PAUSED;
	}
	
	/**
	 * Runs a task on the Activity's UI thread.
	 * 
	 * @param task The task to run.
	 */
	public void runHandler(Runnable task) {
		activity.runOnUiThread(task);
	}
	
	
	// getters / setters
	
	/**
	 * Returns the current / last score of the game.
	 * 
	 * @return Current game score.
	 */
	public float getScore() {
		return score;
	}
	
	/**
	 * Returns game's status (running / stopped / paused).
	 * 
	 * @return Game's status.
	 */
	public GameStatus getStatus() {
		return status;
	}
	
	/**
	 * Returns whether the sound is enabled.
	 * 
	 * @return Game sound status.
	 */
	public boolean getSound() {
		return sound;
	}
	
	/**
	 * Toggles game sound status.
	 * 
	 * @param status The new status to set.
	 */
	public void setSound(boolean status) {
		sound = status;
	}
	
	/**
	 * Returns the current difficulty of the game.
	 * 
	 * @return The difficulty of the game.
	 */
	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	/**
	 * Changes the difficulty of the game.
	 * 
	 * @param difficulty The new difficulty to set.
	 */
	public void setDifficulty(Difficulty difficulty) {
		if (status != GameStatus.STOPPED)
			return;
		
		this.difficulty = difficulty;
	}
	
	/**
	 * Returns the reference to the current game's World.
	 * 
	 * <p>Returns null if the game isn't started!</p>
	 * 
	 * @return The game World object.
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Queues the specified plane control command.
	 *
	 * @param plane The target plane.
	 * @param parameters The control parameters.
	 */
	public void queuePlaneCommand(Plane plane, SmoothControlThread.PlaneControlParameters parameters) {
		smoothControl.queueCommand(plane, parameters);
	}
	
	/**
	 * Shoots a projectile from the specified plane.
	 * 
	 * <p>The projectile will inherit the plane's forward vector.</p>
	 * 
	 * @param plane The plane that fires.
	 * @param projectileType Projectile type.
	 */
	public void shootProjectile(Plane plane, Projectile.ProjectileType projectileType) {
		Projectile projectile = new Projectile(projectileType);
		Vector3D velocity = new Vector3D(plane.getVelocity());
		Vector3D position = new Vector3D(plane.getPosition());
		
		velocity.normalize();
		// velocity.multiply();
		position.add(velocity);
		
		velocity.normalize();
		velocity.multiply(10f); // projectile's speed FIXME: should be based on projectile type
		
		projectile.getPosition().setCoordinates(position.getX(), position.getY(), position.getZ());
		projectile.getVelocity().setValues(velocity.getX(), velocity.getY(), velocity.getZ());
		
		projectile.setOrientation(plane.getYaw(), plane.getPitch());
		
		world.addProjectile(projectile);
	}
	
	/**
	 * Destroys the specified plane object.
	 * 
	 * @param plane The plane to destroy.
	 */
	protected void destroyObject(Plane plane) {
		if (world == null) return;
		
		if (plane == world.getPlayer()) {
			// if the plane is the current player, the game is over
			stop();
			
		} else if (plane instanceof EnemyPlane) {
			world.removePlane((EnemyPlane)plane);
		}
		// some explosion animations, maybe?
	}
	
	/**
	 * Destroys the specified projectile object.
	 * 
	 * @param projectile The projectile object to destroy.
	 */
	protected void destroyObject(Projectile projectile) {
		if (world == null) return;
		
		world.removeProjectile(projectile);
	}

	/**
	 * Processes (most likely, deletes) the specified object after a collision collided.
	 * 
	 * @param obj The object to process.
	 */
	protected void processCollision(CollisionObject obj) {
		if (obj instanceof Plane) {
			destroyObject((Plane)obj);
			
		} else if (obj instanceof Projectile) {
			destroyObject((Projectile)obj);
			
		} /* else if (obj instanceof Terrain) {
			// the Terrain is indestructible
			return;
		} */
		// else: unknown object, do nothing
	}
	
	/**
	 * Physics listener implementation for the current game instance.
	 */
	protected class PhysicsListener implements PhysicsSimulationListener {
		
		@Override
		public void onObjectPositionChange(final MobileObject object) {
			runHandler(new Runnable() {
				@Override
				public void run() {
					// check object's bounds
					float[] position = object.getPosition().toArray();
					float[] dimensions = world.getTerrain().getDimensions();
					
					// clamp the position
					if (position[0] < 0 || position[1] < 0 ||
							position[0] >= dimensions[0] || position[1] >= dimensions[1]) {
						if (object instanceof Plane) {
							Plane planeObject = (Plane)object;
							
							// reflect the plane
							if (position[0] < 0) {
								planeObject.getPosition().setX(0.1f);
								planeObject.steer(120);
							}
							if (position[1] < 0) {
								planeObject.getPosition().setY(0.1f);
								planeObject.steer(120);
							}
							if (position[0] >= dimensions[0]) {
								planeObject.getPosition().setX(dimensions[0]-0.1f);
								planeObject.steer(120);
							}
							if (position[1] >= dimensions[1]) {
								planeObject.getPosition().setY(dimensions[1]-0.1f);
								planeObject.steer(120);
							}
							
						} else if (object instanceof Projectile) {
							// destroy/hide the projectile
							destroyObject((Projectile)object);
						}
					}
					
					if (position[2] >= World.WORLD_MAX_HEIGHT) {
						if (object instanceof Plane) {
							// clamp the object's height
							object.getPosition().setZ(World.WORLD_MAX_HEIGHT);
							((Plane)object).setPitch(45);
							
						} else if (object instanceof Projectile) {
							// destroy/hide the projectile
							destroyObject((Projectile)object);
						}
					}
				}
			});
		}
		
		@Override
		public void onCollisionDetected(final CollisionObject obj1, final CollisionObject obj2) {
			runHandler(new Runnable() {
				@Override
				public void run() {
					processCollision(obj1);
					processCollision(obj2);
				}
			});
		}
	}
	
}
