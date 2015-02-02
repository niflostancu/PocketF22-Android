package ro.pub.dadgm.pf22.activity.controllers;

import android.view.View;

import java.util.HashMap;

import ro.pub.dadgm.pf22.activity.Controller;
import ro.pub.dadgm.pf22.activity.MainActivity;
import ro.pub.dadgm.pf22.render.views.MainMenu;

/**
 * Acts as controller for the main menu view, handling menu item click events.
 */
public class MainMenuController implements Controller {
	
	/**
	 * Reference to the parent activity.
	 */
	protected final MainActivity mainActivity;
	
	/**
	 * Reference to the MainMenu view object.
	 */
	protected final MainMenu view;

	/**
	 * A list of possible actions.
	 */
	protected final HashMap<String, View.OnClickListener> actions;
	
	
	/**
	 * Controller object constructor.
	 */
	public MainMenuController(final MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.view = new MainMenu(this);
		
		actions = new HashMap<>();
		
		// populate the actions
		actions.put("start_game", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mainActivity.getController("game_scene").activate();
			}
		});
		
		actions.put("toggle_difficulty", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		actions.put("toggle_sound", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		
		actions.put("about", new View.OnClickListener() {
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
