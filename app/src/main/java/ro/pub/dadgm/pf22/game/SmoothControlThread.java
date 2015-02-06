package ro.pub.dadgm.pf22.game;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import ro.pub.dadgm.pf22.game.models.Plane;

/**
 * Implements smooth control animator for the planes.
 */
public class SmoothControlThread extends Thread {
	
	/**
	 * Encapsulates the animated plane control parameters.
	 */
	public static class PlaneControlParameters {
		
		/**
		 * The delta-yaw to steer.
		 */
		protected float yaw = 0;
		
		/**
		 * The delta-pitch to control.
		 */
		protected float pitch;
		
		/**
		 * Builds a new object with the specified control parameters.
		 */
		public PlaneControlParameters(float yaw, float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}
		
	}
	
	
	/**
	 * The control step, in milliseconds. 
	 */
	public final static int PLANE_CONTROL_STEP = 100; // milliseconds
	
	/**
	 * The delta control to execute per thread loop.
	 */
	public final static float PLANE_PITCH_DELTA = 0.5f; // delta per step
	public final static float PLANE_YAW_DELTA = 1.0f; // delta per step
	
	
	/**
	 * Stores whether the animation thread is paused.
	 */
	protected boolean paused;
	
	/**
	 * The last time (in system ticks) the objects were animated.
	 */
	protected long lastTime;
	
	/**
	 * The time already elapsed when this thread was paused.
	 */
	protected long pausedTimeElapsed;
	
	/**
	 * Stores the control parameters currently-animated movement parameters for specific planes.
	 */
	protected final IdentityHashMap<Plane, PlaneControlParameters> planeCommands;
	
	
	/**
	 * Initializes the smooth plane control thread.
	 */
	public SmoothControlThread() {
		this.planeCommands = new IdentityHashMap<>();
		
		this.paused = false;
	}
	
	/**
	 * Queues the specified plane control command.
	 * 
	 * @param plane The target plane.
	 * @param parameters The control parameters.
	 */
	public void queueCommand(Plane plane, PlaneControlParameters parameters) {
		synchronized (planeCommands) {
			// replace all existing parameters for the current plane 
			planeCommands.put(plane, parameters);
		}
	}
	
	/**
	 * The thread's main loop.
	 */
	@Override
	public void run() {
		// get the System time
		lastTime = System.nanoTime();
		
		// simulation loop
		while (!isInterrupted()) {
			float td;
			
			if (paused) {
				try {
					Thread.sleep(PLANE_CONTROL_STEP);
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
				continue;
			}
			// calculate the time difference
			long now = System.nanoTime();
			td = ((now - lastTime) / 1000000l) / PLANE_CONTROL_STEP;
			lastTime = now;
			
			// make the steps
			synchronized (planeCommands) {
				List<Plane> cleanUpItems = new ArrayList<>(); 
				for (Map.Entry<Plane, PlaneControlParameters> entry: planeCommands.entrySet()) {
					Plane plane = entry.getKey();
					PlaneControlParameters parameters = entry.getValue();
					boolean cleanup = true;
					
					if (parameters.yaw > 0.0001 || parameters.yaw < -0.0001) {
						float diff = Math.signum(parameters.yaw) * td * PLANE_YAW_DELTA;
						if (Math.abs(diff) >= Math.abs(parameters.yaw))
							diff = parameters.yaw;
						plane.steer(diff);
						parameters.yaw -= diff;
						// use roll for pretty animation
						plane.setRoll(parameters.yaw * -2);
						cleanup = false;
					}
					if (parameters.pitch > 0.0001 || parameters.pitch < -0.0001) {
						float diff = Math.signum(parameters.pitch) * td * PLANE_PITCH_DELTA;
						if (Math.abs(diff) >= Math.abs(parameters.pitch))
							diff = parameters.pitch;
						plane.pitch(diff);
						parameters.pitch -= diff;
						cleanup = false;
					}
					
					if (cleanup)
						cleanUpItems.add(plane);
				}
				
				for (Plane plane: cleanUpItems)
					planeCommands.remove(plane);
			}
			
			// wait and repeat
			try {
				sleep(PLANE_CONTROL_STEP, 0);
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
	
	/**
	 * Pauses the control thread and all its processing (for example, when the game is paused).
	 *
	 * <p>Can be resumed with {@link #resumeProcessing}</p>
	 */
	public void pauseProcessing() {
		paused = true;
		pausedTimeElapsed = System.nanoTime() - lastTime;
	}
	
	/**
	 * Resumes the controls processing.
	 */
	public void resumeProcessing() {
		paused = false;
		lastTime = System.nanoTime() - pausedTimeElapsed;
	}
	
}
