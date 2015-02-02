package ro.pub.dadgm.pf22.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.render.SurfaceView;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.views.MainMenu;

public class MainActivity extends ActionBarActivity {
	
	/**
	 * The view class for the game's main menu.
	 */
	private View mainMenuScene;
	
	/**
	 * The GL Renderer to be used for displaying the game screen.
	 */
	private SurfaceView surfaceView;
	
	private static Context appContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appContext = getApplicationContext();
		
		// build the main menu view object
		mainMenuScene = new MainMenu();
		
		surfaceView = new SurfaceView(this, mainMenuScene);
		setContentView(surfaceView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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
	 * Returns the application's context.
	 */
	public static Context getAppContext() {
		return appContext;
	}
}
