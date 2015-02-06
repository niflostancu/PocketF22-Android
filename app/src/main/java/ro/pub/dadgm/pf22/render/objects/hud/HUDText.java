package ro.pub.dadgm.pf22.render.objects.hud;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.DrawText;

/**
 * Draws a simple HUD text (centered).
 */
public class HUDText extends HUDObject {
	
	/**
	 * Scene's DrawText instance.
	 */
	protected DrawText drawText;
	
	/**
	 * HUD text to draw.
	 */
	protected String text;
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 * @param text The text to draw.   
	 */
	public HUDText(Scene3D scene, String tag, int priority, String text) {
		super(scene, tag, priority);
		
		this.drawText = scene.getDrawText();
		
		// default dimensions
		height = 0.5f;
		
		setCaption(text);
	}
	
	
	@Override
	public void draw() {
		if (!visibility) return;
		
		prepareDrawText();
		drawText.drawText(text);
	}
	
	/**
	 * Changes the HUD item's caption. 
	 * Automatically recalculates the object's width.
	 *
	 * @param caption The new caption to set.
	 */
	public void setCaption(String caption) {
		this.text = caption;
		
		prepareDrawText();
		width = drawText.calculateDrawWidth(caption) * drawText.getModelScale();
	}
	
	/**
	 * Prepares the DrawText instance for drawing text.
	 *
	 * <p>Aka defines font properties to be used for drawing.</p>
	 */
	protected void prepareDrawText() {
		float fHeight = height;
		
		drawText.reset();
		drawText.useFont("fonts/Roboto-Regular.ttf", 48);
		
		drawText.setStartPosition(position.getX() + width / 2, position.getY(), position.getZ());
		drawText.setScale(fHeight);
		drawText.setAlignment(DrawText.FontAlign.ALIGN_CENTER);
	}
	
}
