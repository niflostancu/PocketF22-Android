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
		float vRatio = scene.getCamera().getViewportRatio();
		float fHeight = vRatio / 1.0f;
		if (vRatio > 1) fHeight = 1;
		float fHeight2 = fHeight * 0.7f;
		
		drawText.reset();

		drawText.setStartPosition(position.getX(), position.getY() - fHeight, position.getZ());
		drawText.setAlignment(DrawText.FontAlign.ALIGN_CENTER);
		drawText.setColor(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
		drawText.setScale(fHeight);
		
		drawText.useFont("fonts/Roboto-Regular.ttf", 48);
		drawText.drawText("Pocket F22");
		
		drawText.setStartPosition(position.getX(), position.getY() - fHeight - fHeight2, position.getZ());
		drawText.setScale(fHeight * 0.7f);
		drawText.drawText("Flight Simulator 0.1 alpha");
	}
	
}
