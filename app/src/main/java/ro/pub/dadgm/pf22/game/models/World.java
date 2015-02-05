package ro.pub.dadgm.pf22.game.models;

import java.util.IdentityHashMap;

/**
 * Defines the game's world model.
 * 
 * <p>The world a box with the dimensions {@link #WORLD_WIDTH_X} x {@link #WORLD_WIDTH_Y} units. 
 * The maximum elevation is defined in {@link #WORLD_MAX_HEIGHT} and if a plane exceeds it, it will 
 * be set back to an inferior location.</p>
 * 
 * <p>This class acts as a container of other model objects and manages them. 
 * All methods are thread-safe and can be called from any thread (unless otherwise noted).</p>
 */
public class World {
	
	// several constants
	
	/**
	 * World's horizontal space (X and Y).
	 * 
	 * <p>Warning: their product must be smaller than the maximum value of a unsigned short (65536)!</p>
	 */
	public static int WORLD_WIDTH_X = 250;
	public static int WORLD_WIDTH_Y = 250;
	
	/**
	 * Maximum world's height.
	 */
	public static float WORLD_MAX_HEIGHT = 80.0f;
	
	
	// world components
	
	/**
	 * Defines the terrain object.
	 */
	protected Terrain terrain;
	
	/**
	 * Player's Plane object.
	 */
	protected PrimaryPlane player;
	
	/**
	 * The enemy planes currently present on the scene.
	 * 
	 * <p>Uses a map for quick retrieval and deletion of objects, since the list is slightly dynamic.</p>
	 */
	protected IdentityHashMap<EnemyPlane, EnemyPlane> enemyPlanes;
	
	/**
	 * The list of currently present projectiles.
	 * 
	 * <p>Uses a map for quick retrieval and deletion of objects, since the list is very dynamic.</p>
	 */
	protected IdentityHashMap<Projectile, Projectile> projectiles;
	
	
	/**
	 * Model object constructor.
	 */
	public World() {
		// generate the terrain
		terrain = new Terrain(WORLD_WIDTH_X, WORLD_WIDTH_Y, WORLD_MAX_HEIGHT - 30.0f);
		
		player = new PrimaryPlane();
		// set player's initial position to the center of the world
		player.position.setCoordinates(WORLD_WIDTH_X / 2, WORLD_WIDTH_Y / 2, 40.0f);
		
		// initialize structures
		enemyPlanes = new IdentityHashMap<>();
		projectiles = new IdentityHashMap<>();
	}
	
	
	/**
	 * Returns the world's terrain object.
	 *
	 * @return The terrain object of the world.
	 */
	public synchronized Terrain getTerrain() {
		return terrain;
	}
	
	/**
	 * Returns the player's plane object.
	 *
	 * @return Player's plane.
	 */
	public synchronized PrimaryPlane getPlayer() {
		return player;
	}
	
	/**
	 * Returns the list of enemy planes (an immutable snapshot).
	 *
	 * @return An array with all present enemy planes.
	 */
	public synchronized EnemyPlane[] getEnemyPlanes() {
		return enemyPlanes.values().toArray(new EnemyPlane[enemyPlanes.size()]);
	}
	
	/**
	 * Returns the list of projectiles (an immutable snapshot).
	 *
	 * @return An array with all present projectiles.
	 */
	public synchronized Projectile[] getProjectiles() {
		return projectiles.values().toArray(new Projectile[projectiles.size()]);
	}
	
	
	/**
	 * Adds an enemy plane to the world.
	 * 
	 * @param plane The plane to add.
	 */
	public synchronized void addPlane(EnemyPlane plane) {
		enemyPlanes.put(plane, plane);
	}
	
	/**
	 * Deletes the specified enemy plane from the world.
	 * 
	 * @param plane The plane to remove.
	 */
	public synchronized void removePlane(EnemyPlane plane) {
		enemyPlanes.remove(plane);
	}
	
	/**
	 * Adds projectile to the world.
	 * 
	 * @param projectile The projectile object to add.
	 */
	public synchronized void addProjectile(Projectile projectile) {
		projectiles.put(projectile, projectile);
	}
	
	/**
	 * Deletes the specified projectile from the world.
	 * 
	 * @param projectile The projectile to remove.
	 */
	public synchronized void removeProjectile(Projectile projectile) {
		projectiles.remove(projectile);
	}
	
}
