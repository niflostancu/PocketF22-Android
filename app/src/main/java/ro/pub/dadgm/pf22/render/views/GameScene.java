package ro.pub.dadgm.pf22.render.views;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.activity.controllers.GameSceneController;
import ro.pub.dadgm.pf22.render.Camera;
import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.ShaderManager;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.objects.Object3D;
import ro.pub.dadgm.pf22.render.objects.ObjectsManager;
import ro.pub.dadgm.pf22.render.objects.game.FighterJet3D;
import ro.pub.dadgm.pf22.render.objects.hud.HUDObject;
import ro.pub.dadgm.pf22.render.utils.DrawText;
import ro.pub.dadgm.pf22.render.utils.ShaderLoader;
import ro.pub.dadgm.pf22.render.utils.TextureLoader;
import ro.pub.dadgm.pf22.utils.Point3D;

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
	
	/**
	 * The list of HUD shaders to register.
	 */
	protected static final Object[][] REGISTER_SHADERS_HUD = {
			{ "simple_tex", R.raw.simple_tex_v, R.raw.simple_tex_f },
			{ "draw_text", R.raw.draw_text_v, R.raw.draw_text_f }
	};
	
	/**
	 * The list of 3D shaders to register.
	 */
	protected static final Object[][] REGISTER_SHADERS_3D = {
			{ "s3d_tex_phong", R.raw.s3d_tex_phong_v, R.raw.s3d_tex_phong_f },
			{ "s3d_simple_ilum", R.raw.s3d_simple_ilum_v, R.raw.s3d_simple_ilum_f }
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
	 * Stores camera's center position (player's fighter jet).
	 */
	protected Point3D cameraPosition = new Point3D();
	
	/**
	 * The initial coordinates on touch down.
	 */
	protected float[] initialTouchPoint = new float[2];
	
	
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
		objects.clear();
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
		
		// initialize the scene objects
		FighterJet3D testJet = new FighterJet3D(gameScene3D, "fighter", 0);
		objects.add(testJet);
		
		// initialize the camera
		cameraAngle[0] = cameraAngle[1] = 0;
		cameraPosition.setCoordinates(0, 0, 0);
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
		
		// draw the objects
		objects.drawAll();
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
			Matrix.frustumM(camera.getProjectionMatrix(), 0,
					-ratio, ratio, -1f, 1f, 1f, 50f );
			
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
				float[] objCoords = camera.unProjectCoordinates(e.getX(), e.getY());
				
				if (objCoords != null) {
					// identify the target object
					for (HUDObject hudObject : hudObjects) {
						if (hudObject.getBoundingBox().contains(objCoords[0], objCoords[1])) {
							target = hudObject;
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
		// set the camera to a position around the center
		// TODO: calculate it based on the direction that the plane faces
		float[] initialPoint = new float[] { 0, 1.0f, -5.0f, 1 };
		float[] resPoint = new float[4];
		
		// rotate the point around the center
		float[] matr = new float[16];
		Matrix.setIdentityM(matr, 0);
		Matrix.translateM(matr, 0, cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ());
		Matrix.rotateM(matr, 0, cameraAngle[0], 0, 1, 0);
		Matrix.rotateM(matr, 0, cameraAngle[1], 1, 0, 0);
		Matrix.multiplyMV(resPoint, 0, matr, 0, initialPoint, 0);
		
		Matrix.setLookAtM(camera.getViewMatrix(), 0, 
				resPoint[0], resPoint[1], resPoint[2],  
				cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ(), 
				0f, 1.0f, 0.0f );
		
		shaderManager3D.notifyCameraChanged(camera);
	}
	
}
