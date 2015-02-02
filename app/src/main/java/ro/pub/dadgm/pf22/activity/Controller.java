package ro.pub.dadgm.pf22.activity;

import ro.pub.dadgm.pf22.render.View;

/**
 * Defines the scene controller interface that links together a View and the Models.
 * 
 * <p>The controller usually receives events from user interaction and calls the appropiate methods 
 * on the model objects and the View in order to accomplish the required actions.</p>
 * 
 * <p>The View object receives some of the events and dispatches them to the targeted UI object. 
 * Then, the controller is requested to do a specific action using the {@link #getAction} method.</p>
 * 
 * <p>All of its methods must be called from the Activity thread (unless noted otherwise).</p>
 */
public interface Controller {
	
	/**
	 * Returns the a named action executable object.
	 * 
	 * <p>The action MUST be executed from the Activity thread!</p>
	 * 
	 * @param actionName The name of the action to fetch.
	 * @return The action's object, if found, null otherwise.
	 */
	public android.view.View.OnClickListener getAction(String actionName);
	
	/**
	 * Returns the view object used for rendering the scene.
	 * 
	 * @return Reference to the managed view object.
	 */
	public View getView();
	
	/**
	 * Switches to controller's managed view.
	 * 
	 * <p>This will request the parent activity to exit the current view and make the controller's 
	 * as active.</p>
	 */
	public void activate();
	
}
