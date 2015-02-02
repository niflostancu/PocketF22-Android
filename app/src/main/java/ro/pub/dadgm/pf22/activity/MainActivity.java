package ro.pub.dadgm.pf22.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import ro.pub.dadgm.pf22.R;
import ro.pub.dadgm.pf22.render.SurfaceView;
import ro.pub.dadgm.pf22.render.View;
import ro.pub.dadgm.pf22.render.views.MainMenu;

public class MainActivity extends Activity {
	
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		
		appContext = getApplicationContext();
		
		// build the main menu view object
		mainMenuScene = new MainMenu();
		
		surfaceView = new SurfaceView(this, mainMenuScene);
		setContentView(surfaceView);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
	 * Returns the application's context.
	 */
	public static Context getAppContext() {
		return appContext;
	}
}
