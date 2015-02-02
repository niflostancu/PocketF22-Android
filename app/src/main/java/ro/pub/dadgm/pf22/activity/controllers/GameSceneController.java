package ro.pub.dadgm.pf22.activity.controllers;

import android.view.View;

import java.util.HashMap;

import ro.pub.dadgm.pf22.activity.Controller;
import ro.pub.dadgm.pf22.activity.MainActivity;
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
	 * Controller object constructor.
	 */
	public GameSceneController(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.view = new GameScene(this);
		
		actions = new HashMap<>();
		
		// populate the actions
		actions.put("pause_game", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		actions.put("exit_game", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
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
}
