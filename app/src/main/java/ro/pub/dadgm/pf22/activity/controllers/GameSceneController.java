package ro.pub.dadgm.pf22.activity.controllers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;

import java.util.HashMap;

import ro.pub.dadgm.pf22.activity.Controller;
import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.game.Game;
import ro.pub.dadgm.pf22.game.SmoothControlThread;
import ro.pub.dadgm.pf22.render.views.GameScene;

/**
 * Acts as controller for the game scene.
 */
public class GameSceneController implements Controller {
	
	/**
	 * Reference to the parent activity.
	 */
	protected final MainActivity mainActivity;
	
	/**
	 * Reference to the MainMenu view object.
	 */
	protected final GameScene view;
	
	/**
	 * A list of possible actions.
	 */
	protected final HashMap<String, View.OnClickListener> actions;
	
	/**
	 * The game object.
	 */
	protected final Game game;
	
	/**
	 * Controller object constructor.
	 */
	public GameSceneController(final MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.view = new GameScene(this);
		this.game = mainActivity.getGame();
		
		actions = new HashMap<>();
		
		// populate the actions
		actions.put("hud_pause", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.pause();
			}
		});
		
		actions.put("hud_steer_left", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.queuePlaneCommand(game.getWorld().getPlayer(), new SmoothControlThread.PlaneControlParameters(+45, 0));
			}
		});
		actions.put("hud_steer_right", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.queuePlaneCommand(game.getWorld().getPlayer(), new SmoothControlThread.PlaneControlParameters(-45, 0));
			}
		});
		actions.put("hud_pitch_up", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.queuePlaneCommand(game.getWorld().getPlayer(), new SmoothControlThread.PlaneControlParameters(0, -20));
			}
		});
		actions.put("hud_pitch_down", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.queuePlaneCommand(game.getWorld().getPlayer(), new SmoothControlThread.PlaneControlParameters(0, +20));
			}
		});
		
		actions.put("hud_shoot_missile", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		actions.put("hud_shoot_gun", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		actions.put("menu_resume", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.start(); // resume game
			}
		});

		actions.put("menu_calibrate", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});

		actions.put("menu_exit", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				game.stop();
				mainActivity.getController("main_menu").activate();
			}
		});

		mainActivity.registerGravitySensorListener(new GravityListener());
	}
	
	/**
	 * The gravity (accelerometer-based) events listener.
	 */
	protected class GravityListener implements SensorEventListener {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			//Log.d(GameSceneController.class.getSimpleName(), "Sensor event: " + 
			//		event.values[0] + ", " + event.values[1] + ", " + event.values[2] );
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// nothing to do
		}
	}
	
	@Override
	public synchronized View.OnClickListener getAction(String actionName) {
		if (actions.containsKey(actionName)) 
			return actions.get(actionName);
		return null;
	}
	
	@Override
	public ro.pub.dadgm.pf22.render.View getView() {
		return view;
	}
	
	@Override
	public void activate() {
		mainActivity.getSurfaceView().setView(view);
	}
	
	@Override
	public void queueEvent(Runnable worker) {
		mainActivity.getSurfaceView().queueEvent(worker);
	}
	
	
	// getters / setters
	
	/**
	 * Returns the Game's model object.
	 * 
	 * @return Reference to the Game model object.
	 */
	public Game getGame() {
		return game;
	}
	
}
