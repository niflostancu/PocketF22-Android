package ro.pub.dadgm.pf22.render.views;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import java.util.Collections;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.activity.controllers.GameSceneController;
import ro.pub.dadgm.pf22.game.Game;
import ro.pub.dadgm.pf22.game.models.PrimaryPlane;
import ro.pub.dadgm.pf22.game.models.World;
import ro.pub.dadgm.pf22.render.Camera;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.ShaderManager;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.objects.Object3D;
import ro.pub.dadgm.pf22.render.objects.ObjectsManager;
import ro.pub.dadgm.pf22.render.objects.game.FighterJet3D;
import ro.pub.dadgm.pf22.render.objects.game.Terrain3D;
import ro.pub.dadgm.pf22.render.objects.hud.HUDButton;
import ro.pub.dadgm.pf22.render.objects.hud.HUDObject;
import ro.pub.dadgm.pf22.render.objects.hud.HUDText;
import ro.pub.dadgm.pf22.render.objects.hud.MenuContainer;
import ro.pub.dadgm.pf22.render.objects.hud.MenuItem;
import ro.pub.dadgm.pf22.render.objects.hud.MenuOverlay;
import ro.pub.dadgm.pf22.render.utils.DrawText;
import ro.pub.dadgm.pf22.render.utils.ShaderLoader;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;

/**
 * The view for the game's 3D scene.
 * 
 * <p>The world space is given by the Game model instance.</p>
 */
public class GameScene implements View {
	
	
	/**
	 * Implements the Scene3D interface and offers services to the game scene's 3D objects.
	 */
	protected class GameScene3D implements Scene3D {
		@Override
		public Camera getCamera() {
			return GameScene.this.camera;
		}
		
		@Override
		public ShaderManager getShaderManager() {
			return GameScene.this.shaderManager3D;
		}
		
		@Override
		public DrawText getDrawText() {
			return null;
		}
	}
	
	/**
	 * The Scene3D implementation to be offered to the HUD objects.
	 */
	protected class GameHUD implements Scene3D {
		@Override
		public Camera getCamera() {
			return GameScene.this.hudCamera;
		}
		
		@Override
		public ShaderManager getShaderManager() {
			return GameScene.this.shaderManagerHUD;
		}
		
		@Override
		public DrawText getDrawText() {
			return GameScene.this.drawText;
		}
	}
	
	// several constants
	
	/**
	 * The global light's position.
	 */
	public static final float[] LIGHT_POSITION = { World.WORLD_WIDTH_X / 2, World.WORLD_WIDTH_Y / 2, World.WORLD_MAX_HEIGHT * 2 };
	
	/**
	 * The list of HUD shaders to register.
	 */
	protected static final Object[][] REGISTER_SHADERS_HUD = {
			{ "simple_color", R.raw.simple_color_v, R.raw.simple_color_f },
			{ "simple_tex", R.raw.simple_tex_v, R.raw.simple_tex_f },
			{ "draw_text", R.raw.draw_text_v, R.raw.draw_text_f }
	};
	
	/**
	 * The list of 3D shaders to register.
	 */
	protected static final Object[][] REGISTER_SHADERS_3D = {
			{ "s3d_tex_phong", R.raw.s3d_tex_phong_v, R.raw.s3d_tex_phong_f },
			{ "s3d_simple_ilum", R.raw.s3d_simple_ilum_v, R.raw.s3d_simple_ilum_f },
			{ "s3d_simple_color", R.raw.s3d_simple_color_v, R.raw.s3d_simple_color_f }
	};
	
	/**
	 * Reference to the controller.
	 */
	protected GameSceneController controller;
	
	/**
	 * Internal GameScene3D instance to present to the 3D scene objects.
	 */
	protected GameScene3D gameScene3D;
	
	/**
	 * Internal GameHUD instance to present to the HUD objects.
	 */
	protected GameHUD gameHUD;
	
	/**
	 * The camera used for the game.
	 */
	protected Camera camera;
	
	/**
	 * The camera used for the HUD.
	 */
	protected Camera hudCamera;
	
	/**
	 * The list of 3D objects to be rendered.
	 */
	protected ObjectsManager<Object3D> objects;
	
	/**
	 * The clickable menu items.
	 */
	protected ObjectsManager<HUDObject> hudObjects;
	
	/**
	 * The shader manager used for the 3D scene.
	 */
	protected ShaderManager shaderManager3D;
	
	/**
	 * The shader manager used for the HUD objects.
	 */
	protected ShaderManager shaderManagerHUD;
	
	/**
	 * The text drawing library used for the HUD objects.
	 */
	protected DrawText drawText;
	
	/**
	 * Stores the initially-clicked and currently-hovered HUD object.
	 * 
	 * <p>Should only be accessed from the Activity thread!</p>
	 */
	protected HUDObject currentHover = null, initialClick = null;
	
	/**
	 * The template with the hud objects to draw.
	 * 
	 * <p>Format: { object, position, [size] }</p>
	 */
	protected Object[][] hudObjectsTemplate;
	
	/**
	 * Reference to the menu container object.
	 */
	protected MenuContainer menuContainer;
	
	/**
	 * Lock used for Activity/Renderer threads synchronization.
	 * 
	 * <p>Used to protect the read consistency of the camera and objects collection.</p>
	 */
	protected final Object lock = new Object();
	
	
	/**
	 * Stores the current camera angle.
	 */
	protected float[] cameraAngle = new float[2];
	
	/**
	 * The initial coordinates on touch down.
	 */
	protected float[] initialTouchPoint = new float[2];
	
	/**
	 * Reference to the current Game.
	 */
	protected Game game;
	
	/**
	 * Player's model object.
	 */
	protected PrimaryPlane player;
	
	
	/**
	 * Constructs the main menu of the game.
	 */
	public GameScene(GameSceneController controller) {
		this.controller = controller;
		
		// initialize Scene3D interfaces
		gameScene3D = new GameScene3D();
		gameHUD = new GameHUD();
		
		// initialize the objects manager
		objects = new ObjectsManager<>();
		hudObjects = new ObjectsManager<>();
		
		// initialize the camera object with the identity view and projection
		camera = new Camera();
		hudCamera = new Camera();
		
		// initialize the shader manager
		shaderManager3D = new ShaderManager();
		shaderManagerHUD = new ShaderManager();
		
		// draw text library
		drawText = new DrawText(gameHUD);
	}
	
	/**
	 * Build the main menu scene.
	 */
	@Override
	public void onActivate() {
		// clean up structures first
		ShaderLoader.clear();
		TextureLoader.clear();
		objects = new ObjectsManager<>();
		hudObjects = new ObjectsManager<>();
		shaderManager3D.clear();
		shaderManagerHUD.clear();
		drawText.destroy();
		
		// initialize the shaders
		for (Object[] shaderProps: REGISTER_SHADERS_3D) {
			String name = (String)shaderProps[0];
			Integer vertexRes = (Integer)shaderProps[1];
			Integer fragmentRes = (Integer)shaderProps[2];
			
			shaderManager3D.registerShader(name, vertexRes, fragmentRes);
		}
		for (Object[] shaderProps: REGISTER_SHADERS_HUD) {
			String name = (String)shaderProps[0];
			Integer vertexRes = (Integer)shaderProps[1];
			Integer fragmentRes = (Integer)shaderProps[2];
			
			shaderManagerHUD.registerShader(name, vertexRes, fragmentRes);
		}
		
		game = controller.getGame();
		World world = game.getWorld();
		player = world.getPlayer();
		
		// initialize the scene objects
		FighterJet3D testJet = new FighterJet3D(gameScene3D, player, "fighter", 0);
		objects.add(testJet);
		
		Terrain3D terrain = new Terrain3D(gameScene3D, world.getTerrain(), "terrain", 0);
		objects.add(terrain);
		
		hudObjectsTemplate = new Object[][]{
				// { object, position, [size] }
				{ new HUDText(gameHUD, "ingame_hud", 0, "Score: 0" ), new float[]{ 3.1f, 9.18f, 0f } },
				
				{ new HUDButton(gameHUD, "ingame_hud", 0, "R", controller.getAction("hud_shoot_missile") ), new float[]{ 0.4f, 0.4f, 0f } },
				{ new HUDButton(gameHUD, "ingame_hud", 0, "G", controller.getAction("hud_shoot_gun") ), new float[]{ -1.1f, 0.4f, 0f } },
				
				{ new HUDButton(gameHUD, "ingame_hud", 0, "M", controller.getAction("hud_pause") ), new float[]{ 0.4f, 9.1f, 0f } },
				
				{ new MenuOverlay(gameHUD, "paused_menu_overlay", -10 ), new float[]{ 0, 0, 0f }, new float[] { 10f, 10f } },
		};
		MenuItem[] menuObjects = new MenuItem[]{
				// uses the priority to establish the order of the items
				new MenuItem(gameHUD, "menu_item", 0, "Resume", controller.getAction("menu_resume")),
				new MenuItem(gameHUD, "menu_item", 1, "Calibrate", controller.getAction("menu_calibrate")),
				new MenuItem(gameHUD, "menu_item", 2, "Exit", controller.getAction("menu_exit")),
		};
		
		for (Object[] objProps: hudObjectsTemplate) {
			HUDObject hudObject = (HUDObject)objProps[0];
			if (objProps.length > 1) {
				float[] position = (float[]) objProps[1];
				hudObject.position().setCoordinates(position[0], position[1], position[2]);
			}
			if (objProps.length > 2) {
				float[] size = (float[]) objProps[2];
				hudObject.setDimensions(size[0], size[1]);
			}
			hudObject.updateBoundingBox();
			
			hudObjects.add(hudObject);
		}
		
		menuContainer = new MenuContainer(gameHUD, "paused_menu", 0);
		menuContainer.position().setCoordinates(0, 0, -1);
		menuContainer.setDimensions(10, 7);
		hudObjects.add(menuContainer);
		Collections.addAll(menuContainer.getObjects(), menuObjects);
		
		// initialize the camera
		cameraAngle[0] = cameraAngle[1] = 0;
	}
	
	@Override
	public void onClose() {
		// destroy the objects
		for (Object3D obj: objects) {
			obj.destroy();
		}
		objects.clear();
		
		for (HUDObject obj: hudObjects) {
			obj.destroy();
		}
		hudObjects.clear();
		
		// destroy the DrawText instance.
		drawText.destroy();
		shaderManager3D.destroy();
		shaderManagerHUD.destroy();
	}
	
	@Override
	public void draw() {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		updateCamera();
		
		// draw the objects
		objects.drawAll();
		
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		
		// update some HUD objects
		((HUDText)hudObjectsTemplate[0][0]).setCaption("Score: " + game.getScore());
		
		for (HUDObject hudObject: hudObjects) 
			hudObject.setVisibility(false);
		
		if (game.getStatus() == Game.GameStatus.RUNNING) {
			for (HUDObject hudObject: hudObjects.getObjectsByTag("ingame_hud")) {
				hudObject.setVisibility(true);
			}
			
		} else {
			for (HUDObject hudObject: hudObjects.getObjectsByTag("paused_menu")) {
				hudObject.setVisibility(true);
			}
			for (HUDObject hudObject: hudObjects.getObjectsByTag("paused_menu_overlay")) {
				hudObject.setVisibility(true);
			}
		}
		
		hudObjects.drawAll();
	}
	
	@Override
	public void onResize(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		final float ratio = (float) width / height;
		float vWidth = 10f * ratio;
		
		synchronized (lock) {
			camera.setViewportDims(width, height);
			hudCamera.setViewportDims(width, height);
			
			Matrix.orthoM(hudCamera.getProjectionMatrix(), 0,
				/*left: */ 0, /*right: */ vWidth, 
				/*bottom: */ 0, /*top: */ 10f, 
				/*near: */ 0, /*far: */ -10);
			hudCamera.computeReverseMatrix();
			shaderManagerHUD.notifyCameraChanged(hudCamera);
			
			// update the 3D camera
			//Matrix.frustumM(camera.getProjectionMatrix(), 0,
			//		-ratio, ratio, -1f, 1f, 2f, 100f );
			Matrix.perspectiveM(camera.getProjectionMatrix(), 0, 60, ratio, 0.001f, 100f);
			
			// realign hud objects
			for (Object[] objProps: hudObjectsTemplate) {
				HUDObject hudObject = (HUDObject)objProps[0];
				if (objProps.length > 1) {
					float[] position = (float[]) objProps[1];
					hudObject.position().setCoordinates(position[0], position[1], position[2]);
					if (position[0] < 0)
						hudObject.position().setX(vWidth + position[0]);
				}
				
				hudObject.updateBoundingBox();
			}
			menuContainer.setDimensions(vWidth, menuContainer.getDimensions()[1]);
			menuContainer.repositionObjects();
			
			updateCamera();
		}
	}
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent e) {
		// capture all DOWN/MOVE/UP touch events
		if ( e.getAction() == MotionEvent.ACTION_DOWN ||
				e.getAction() == MotionEvent.ACTION_UP ||
				e.getAction() == MotionEvent.ACTION_MOVE ) {
			
			HUDObject target = null;
			synchronized (lock) {
				// convert viewport to world coordinates
				float[] objCoords = hudCamera.unProjectCoordinates(e.getX(), e.getY());
				
				if (objCoords != null) {
					
					// identify the target object
					if (menuContainer.isVisible()) {
						for (HUDObject hudObject : menuContainer.getObjects()) {
							if (hudObject.getBoundingBox().contains(objCoords[0], objCoords[1])) {
								target = hudObject;
							}
						}
						
					} else {
						for (HUDObject hudObject : hudObjects) {
							if (hudObject.isVisible() && hudObject.getBoundingBox().contains(objCoords[0], objCoords[1])) {
								target = hudObject;
							}
						}
					}
					
					if (target == null) {
						if (currentHover != null) {
							// send the currently hovered object a HOVER_EXIT event 
							e.setAction(MotionEvent.ACTION_HOVER_EXIT);
							currentHover.onTouchEvent(e);
						}
						currentHover = null;
					}
				}
			}
			
			if (target == null) {
				if (game.getStatus() != Game.GameStatus.RUNNING)
					return true;
				
				// move the camera
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					initialTouchPoint[0] = e.getX();
					initialTouchPoint[1] = e.getY();
					
				} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
					final float SENSIBILITY = 0.5f;
					
					float dx = initialTouchPoint[0] - e.getX();
					float dy = initialTouchPoint[1] - e.getY();
					cameraAngle[0] = (cameraAngle[0] + dx * SENSIBILITY) % 360;
					cameraAngle[1] = (cameraAngle[1] - dy * SENSIBILITY) % 360;
					
					initialTouchPoint[0] = e.getX();
					initialTouchPoint[1] = e.getY();
					
					controller.queueEvent(new Runnable() {
						@Override
						public void run() {
							synchronized (lock) {
								updateCamera();
							}
						}
					});
				}
				
			} else if ( e.getAction() == MotionEvent.ACTION_DOWN ||
					e.getAction() == MotionEvent.ACTION_MOVE ) {
				
				int oldAction = e.getAction();
				
				if (currentHover == null || target != currentHover) {
					// send the currently hovered object a HOVER_EXIT event 
					e.setAction(MotionEvent.ACTION_HOVER_EXIT);
					if (currentHover != null)
						currentHover.onTouchEvent(e);
					currentHover = target;
				}
				
				if (oldAction == MotionEvent.ACTION_DOWN) {
					initialClick = target;
					e.setAction(MotionEvent.ACTION_HOVER_ENTER);
				} else {
					if (initialClick != target)
						return true;
					
					e.setAction(MotionEvent.ACTION_HOVER_MOVE);
				}
				
				target.onTouchEvent(e);
				
			} else if (e.getAction() == MotionEvent.ACTION_UP) {
				// we have an ACTION_UP event
				// send HOVER_EXIT events
				if (currentHover != null) {
					// send the currently hovered object a HOVER_EXIT event 
					e.setAction(MotionEvent.ACTION_HOVER_EXIT);
					currentHover.onTouchEvent(e);
				}
				target.onTouchEvent(e);
				if (initialClick == null || initialClick != target) {
					// this is not a true click
					currentHover = null;
					initialClick = null;
					return true;
				}
				
				currentHover = null;
				initialClick = null;
				e.setAction(MotionEvent.ACTION_UP);
				target.onTouchEvent(e);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Recalculates camera's position based on the player plane's position and viewing direction.
	 * 
	 * <p>Must be executed from the OpenGL thread!</p>
	 */
	protected void updateCamera() {
		float[] position = player.getPosition().toArray();
		
		// compute camera's facing direction
		float[] initialPoint = new float[] { -0.002f, 0, 0.001f, 1 };
		float[] resPoint = new float[4];
		
		// set the camera to a position around the player's plane
		float[] matr = new float[16];
		Matrix.setIdentityM(matr, 0);
		Matrix.translateM(matr, 0, position[0], position[1], position[2]);
		
		Matrix.rotateM(matr, 0, cameraAngle[0], 0, 0, 1);
		Matrix.rotateM(matr, 0, player.getYaw(), 0, 0, 1);
		Matrix.rotateM(matr, 0, cameraAngle[1], 0, 1, 0);
		
		Matrix.multiplyMV(resPoint, 0, matr, 0, initialPoint, 0);
		
		Matrix.setLookAtM(camera.getViewMatrix(), 0, 
				resPoint[0], resPoint[1], resPoint[2],
				position[0], position[1], position[2] + 0.0005f, 
				0f, 0.0f, 1.0f );
		
		shaderManager3D.notifyCameraChanged(camera);
	}
	
}
