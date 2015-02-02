package ro.pub.dadgm.pf22.activity;

import android.view.View;

import java.util.HashMap;

import ro.pub.dadgm.pf22.render.views.MainMenu;

/**
 * Acts as controller for the main menu view, handling menu item click events.
 * 
 * <p>All of its methods must be called from the Activity thread.</p>
 */
public class MainMenuController {
	
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
	public MainMenuController(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.view = new MainMenu(this);
		
		actions = new HashMap<>();
		
		// populate the actions
		actions.put("start_game", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
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
	
	
	/**
	 * Returns the specified action's object.
	 * 
	 * <p>The action MUST be executed from the Activity thread!</p>
	 * 
	 * @param actionName The name of the action to fetch.
	 * @return The action's object, if found, null otherwise.   
	 */
	public synchronized View.OnClickListener getAction(String actionName) {
		if (actions.containsKey(actionName)) 
			return actions.get(actionName);
		return null;
	}
	
	
	// getters / setters
	
	/**
	 * Returns the view object used for rendering.
	 * 
	 * @return Reference to the managed view object
	 */
	public MainMenu getView() {
		return view;
	}
	
	/**
	 * Activates the view.
	 */
	public void activate() {
		mainActivity.getSurfaceView().setView(view);
	}
	
}
