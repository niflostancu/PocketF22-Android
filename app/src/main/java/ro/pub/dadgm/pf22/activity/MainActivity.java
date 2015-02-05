package ro.pub.dadgm.pf22.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.activity.controllers.GameSceneController;
import ro.pub.dadgm.pf22.activity.controllers.MainMenuController;
import ro.pub.dadgm.pf22.game.Game;
import ro.pub.dadgm.pf22.render.SurfaceView;

public class MainActivity extends Activity {
	
	/**
	 * Stores the controllers associates to the different game scenes / views.
	 */
	private Map<String, Controller> controllers = new HashMap<>();
	
	/**
	 * The GL Renderer to be used for displaying the game screen.
	 */
	private SurfaceView surfaceView;
	
	/**
	 * A singleton field used to store the current Activity's context.
	 */
	private static Context appContext = null;
	
	/**
	 * Stores the Game object that controls the game.
	 */
	private Game game;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		
		appContext = getApplicationContext();

		Log.d(MainActivity.class.getSimpleName(), "Initializing activity...");
		
		// initialize the game's objects
		game = null;
		if (savedInstanceState != null) {
			game = (Game) savedInstanceState.getSerializable("gameObj");
			Log.d(MainActivity.class.getSimpleName(), "Loaded saved Game object.");
		}
		if (game == null)
			game = new Game();
		
		game.injectActivity(this);
		
		
		// build the controller objects
		MainMenuController mainMenu = new MainMenuController(this);
		GameSceneController gameScene = new GameSceneController(this);
		
		controllers.put("main_menu", mainMenu);
		controllers.put("game_scene", gameScene);
		
		// initialize the surface
		surfaceView = new SurfaceView(this, null);
		setContentView(surfaceView);
		
		// activate the game
		switch (game.getStatus()) {
			case STOPPED: // show the main menu
				Log.d(MainActivity.class.getSimpleName(), "Game status before activity was stopped: stopped.");
				mainMenu.activate();
				break;
			
			case RUNNING:
				Log.d(MainActivity.class.getSimpleName(), "Game status before activity was stopped: running.");
				gameScene.activate();
				break;
			case PAUSED: // show the game scene
				Log.d(MainActivity.class.getSimpleName(), "Game status before activity was stopped: paused.");
				gameScene.activate();
				break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(MainActivity.class.getSimpleName(), "Activity paused.");
		surfaceView.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
		// serialize the Game model
		savedInstanceState.putSerializable("gameObject", game);
		Log.d(MainActivity.class.getSimpleName(), "Activity instance saved.");
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(MainActivity.class.getSimpleName(), "Activity resumed.");
		surfaceView.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu_main, menu);
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * Returns the SurfaceView reference.
	 * 
	 * @return A reference to the OpenGL surface view object.
	 */
	public SurfaceView getSurfaceView() {
		return surfaceView;
	}
	
	/**
	 * Returns a named controller.
	 * 
	 * @param name The controller to retrieve.
	 * @return The controller's object, null if not found.
	 */
	public Controller getController(String name) {
		return controllers.get(name);
	}
	
	/**
	 * Returns the current Game model object.
	 * 
	 * @return The game model.
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * Returns the application's context.
	 */
	public static Context getAppContext() {
		return appContext;
	}
	
}
