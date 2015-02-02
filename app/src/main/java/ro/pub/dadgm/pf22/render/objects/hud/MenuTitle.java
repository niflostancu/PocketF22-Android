package ro.pub.dadgm.pf22.render.objects.hud;

import android.opengl.Matrix;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.DrawText;

/**
 * Draws the menu title (as text).
 */
public class MenuTitle extends HUDObject {
	
	/**
	 * Static color to blend the font's texture.
	 */
	protected static final float[] staticColor = {
		0.5f, 0.5f, 0.5f, 1.0f
	};

	/**
	 * Scene's DrawText instance.
	 */
	protected DrawText drawText;
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public MenuTitle(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
		
		this.drawText = scene.getDrawText();
	}
	
	
	@Override
	public void draw() {
		drawText.reset();
		
		drawText.setAlignment(DrawText.FontAlign.ALIGN_CENTER);
		drawText.setStartPosition(position);
		drawText.setColor(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
		drawText.setScale(height);
		
		drawText.useFont("fonts/Roboto-Regular.ttf", 48);
		drawText.drawText("Pocket F22");
		
		drawText.setStartPosition(position.getX(), position.getY() - height, position.getZ());
		drawText.setScale(height * 0.7f);
		drawText.drawText("Flight Simulator 0.1 alpha");
	}
	
}
