package ro.pub.dadgm.pf22.game.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ro.pub.dadgm.pf22.physics.CollisionObject;
import ro.pub.dadgm.pf22.physics.MobileObject;
import ro.pub.dadgm.pf22.utils.events.CollectionListener;

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
public class World extends BaseModel {
	
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
	protected final IdentityHashMap<EnemyPlane, EnemyPlane> enemyPlanes;
	
	/**
	 * The list of currently present projectiles.
	 * 
	 * <p>Uses a map for quick retrieval and deletion of objects, since the list is very dynamic.</p>
	 */
	protected final IdentityHashMap<Projectile, Projectile> projectiles;
	
	/**
	 * Maintains the set of mobile objects. Used by the Physics engine.
	 * 
	 * <p>Needs to be concurrent.</p>
	 */
	protected final Set<MobileObject> mobileObjects;
	
	/**
	 * Maintains the set of collidable objects. Used by the Physics engine.
	 * 
	 * <p>Needs to be concurrent.</p>
	 */
	protected final Set<CollisionObject> collidableObjects;
	
	// collection listeners
	
	/**
	 * Used to allow listening to the enemy planes collection changes.
	 */
	protected transient final List<CollectionListener<EnemyPlane>> enemyCollectionListeners;
	
	/**
	 * Used to allow listening to the projectiles collection changes.
	 */
	protected transient final List<CollectionListener<Projectile>> projectileCollectionListeners;
	
	
	/**
	 * Model object constructor.
	 */
	public World() {
		// generate the terrain
		terrain = new Terrain(WORLD_WIDTH_X, WORLD_WIDTH_Y, WORLD_MAX_HEIGHT * 0.75f);
		
		player = new PrimaryPlane();
		
		// initialize structures
		enemyPlanes = new IdentityHashMap<>();
		projectiles = new IdentityHashMap<>();
		enemyCollectionListeners = new ArrayList<>();
		projectileCollectionListeners = new ArrayList<>();
		
		// the following are synchronized identity sets
		mobileObjects = Collections.newSetFromMap(
				new ConcurrentHashMap<MobileObject, Boolean>());
		collidableObjects = Collections.newSetFromMap(
				new ConcurrentHashMap<CollisionObject, Boolean>());
		
		// add the fixed objects for physics simulation
		mobileObjects.add(player);
		collidableObjects.add(terrain);
		collidableObjects.add(player);
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
	 * Returns the dynamic set of mobile objects.
	 * 
	 * <p>The set can be accessed from any thread (it has concurrent access). 
	 * It is readonly, though (but its contents can change over time!).</p>
	 * 
	 * @return The concurrent mobile objects set.
	 */
	public Set<MobileObject> getMobileObjects() {
		return Collections.unmodifiableSet(mobileObjects);
	}
	
	/**
	 * Returns the dynamic set of collidable objects.
	 *
	 * <p>The set can be accessed from any thread (it has concurrent access). 
	 * It is readonly, though (but its contents can change over time!).</p>
	 *
	 * @return The concurrent collidable objects set.
	 */
	public Set<CollisionObject> getCollidableObjects() {
		return Collections.unmodifiableSet(collidableObjects);
	}
	
	/**
	 * Adds a new enemy planes collection listener.
	 * 
	 * <p>Warning: the listener's events may be called from any of the game's threads (Activity, Physics or AI)!</p>
	 * 
	 * @param listener The listener object.
	 */
	public void addEnemyCollectionListener(CollectionListener<EnemyPlane> listener) {
		enemyCollectionListeners.add(listener);
	}
	
	/**
	 * Adds a new projectile collection listener.
	 * 
	 * <p>Warning: the listener's events may be called from any of the game's threads (Activity, Physics or AI)!</p>
	 * 
	 * @param listener The listener object.
	 */
	public void addProjectileCollectionListener(CollectionListener<Projectile> listener) {
		projectileCollectionListeners.add(listener);
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
		collidableObjects.add(plane);
		mobileObjects.add(plane);
		
		for (CollectionListener<EnemyPlane> listener: enemyCollectionListeners) {
			listener.onObjectAdded(plane);
		}
	}
	
	/**
	 * Deletes the specified enemy plane from the world.
	 * 
	 * @param plane The plane to remove.
	 */
	public synchronized void removePlane(EnemyPlane plane) {
		enemyPlanes.remove(plane);
		collidableObjects.remove(plane);
		mobileObjects.remove(plane);
		
		for (CollectionListener<EnemyPlane> listener: enemyCollectionListeners) {
			listener.onObjectRemoved(plane);
		}
	}
	
	/**
	 * Adds projectile to the world.
	 * 
	 * @param projectile The projectile object to add.
	 */
	public synchronized void addProjectile(Projectile projectile) {
		projectiles.put(projectile, projectile);
		collidableObjects.add(projectile);
		mobileObjects.add(projectile);
		
		for (CollectionListener<Projectile> listener: projectileCollectionListeners) {
			listener.onObjectAdded(projectile);
		}
	}
	
	/**
	 * Deletes the specified projectile from the world.
	 * 
	 * @param projectile The projectile to remove.
	 */
	public synchronized void removeProjectile(Projectile projectile) {
		projectiles.remove(projectile);
		collidableObjects.remove(projectile);
		mobileObjects.remove(projectile);
		
		for (CollectionListener<Projectile> listener: projectileCollectionListeners) {
			listener.onObjectAdded(projectile);
		}
	}
	
}
