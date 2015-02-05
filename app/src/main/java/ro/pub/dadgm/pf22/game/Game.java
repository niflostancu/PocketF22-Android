package ro.pub.dadgm.pf22.game;

import android.util.Log;

import ro.pub.dadgm.pf22.game.models.*;
import ro.pub.dadgm.pf22.physics.CollisionObject;
import ro.pub.dadgm.pf22.physics.MobileObject;
import ro.pub.dadgm.pf22.physics.PhysicsSimulationListener;
import ro.pub.dadgm.pf22.physics.PhysicsThread;

/**
 * Manages the game and enforces its rules.
 * 
 * <p>The game must be started by calling the {@link #start} method and can be stopped by calling the 
 * {@link #stop} method.</p>
 */
public class Game {
	
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
	 * The current score.
	 */
	protected float score;
	
	/**
	 * The simulation thread of the Physics module.
	 */
	protected PhysicsThread physicsThread;
	
	
	/**
	 * Game constructor.
	 * 
	 * <p>Initializes an empty game.</p>
	 */
	public Game() {
		status = GameStatus.STOPPED;
	}
	
	
	/**
	 * Starts/unpauses the current game.
	 * 
	 * <p>The instance parameters can't be changed once the game is running!</p>
	 */
	public void start() {
		if (status != GameStatus.RUNNING)
			return;
		
		if (status == GameStatus.PAUSED) {
			// unsleep the threads and continue
			physicsThread.resumeProcessing();
			
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
				(float) Math.random() * (World.WORLD_MAX_HEIGHT - terrain.getMaxHeight() - 5) + 
						terrain.getMaxHeight() + 5.0f );
		
		// TODO: generate the enemy planes
		
		// initialize the Physics module
		physicsThread = new PhysicsThread(world.getMobileObjects(), world.getCollidableObjects(), new PhysicsListener());
		
		// TODO: initialize the AI module
		
		// the game is on!
		physicsThread.start();
		
		status = GameStatus.RUNNING;
	}
	
	/**
	 * Stops the game, deallocating all the resources used (the simulation Threads, for example). 
	 */
	public void stop() {
		if (status == GameStatus.STOPPED)
			return;
		
		// stop the threads
		physicsThread.interrupt();
		
		try {
			physicsThread.join();
			
		} catch (InterruptedException e) {
			Log.e(Game.class.getSimpleName(), "Thread interrupted!", e);
		}
		
		world = null;
		
		status = GameStatus.STOPPED;
	}
	
	/**
	 * Pauses the game.
	 */
	public void pause() {
		if (status != GameStatus.RUNNING)
			return;
		
		physicsThread.pauseProcessing();
		
		status = GameStatus.PAUSED;
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
	 * Physics listener implementation for the current game instance.
	 */
	protected class PhysicsListener implements PhysicsSimulationListener {
		
		@Override
		public void onObjectPositionChange(MobileObject object) {
			// TODO check object's bounds
		}
		
		@Override
		public void onCollisionDetected(CollisionObject obj1, CollisionObject obj2) {
			// TODO: destroy an object!
		}
	}
	
}
