package ro.pub.dadgm.pf22.render.objects.hud;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.render.utils.DrawText;

/**
 * Draws a menu item (as hover-able text).
 */
public class MenuItem extends HUDObject {
	
	/**
	 * Static color to blend the font's texture.
	 */
	protected static final float[] staticColor = {
		0.5f, 0.5f, 1.0f, 1.0f
	};

	/**
	 * The hover color of the item.
	 */
	protected static final float[] hoverColor = {
			0.1f, 1.0f, 1.0f, 1.0f
	};
	
	/**
	 * Scene's DrawText instance.
	 */
	protected DrawText drawText;
	
	/**
	 * Stores true if the current item is hovered.
	 */
	protected boolean hovered = false;
	
	/**
	 * Menu item's caption (text).
	 */
	protected String caption;
	
	/**
	 * The onclick listerer bound to the current item.
	 */
	protected View.OnClickListener clickListener;
	
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 * @param caption The menu item's text.
	 * @param clickListener Menu item's click action.   
	 */
	public MenuItem(Scene3D scene, String tag, int priority, String caption, View.OnClickListener clickListener) {
		super(scene, tag, priority);
		
		this.drawText = scene.getDrawText();
		this.clickListener = clickListener;
		
		// fixed height
		height = 0.7f;
		
		setCaption(caption);
	}
	
	@Override
	public void draw() {
		prepareDrawText();
		
		synchronized (this) { // hover is set from the Activity thread
			if (hovered) {
				drawText.setColor(hoverColor);
			} else {
				drawText.setColor(staticColor);
			}
		}
		
		drawText.drawText(caption);
	}
	
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_HOVER_ENTER || 
				e.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
			synchronized (this) {
				hovered = true;
			}
			return true;
		}
		
		if (e.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
			synchronized (this) {
				hovered = false;
			}
			return true;
		}
		
		if (e.getAction() == MotionEvent.ACTION_UP) {
			synchronized (this) {
				hovered = false;
			}
			clickListener.onClick(null);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Changes the menu item's caption. 
	 * Automatically recalculates the object's width.
	 * 
	 * @param caption The new caption to set.
	 */
	public void setCaption(String caption) {
		this.caption = caption;
		
		prepareDrawText();
		width = drawText.calculateDrawWidth(caption) * drawText.getModelScale();
	}
	
	/**
	 * Prepares the DrawText instance for drawing text.
	 * 
	 * <p>Aka defines font properties to be used for drawing.</p>
	 */
	protected void prepareDrawText() {
		float fHeight = height * 0.8f;
		
		drawText.reset();
		drawText.useFont("fonts/Roboto-Regular.ttf", 48);
		
		drawText.setStartPosition(position.getX() + width / 2, position.getY(), position.getZ());
		drawText.setScale(fHeight);
		drawText.setAlignment(DrawText.FontAlign.ALIGN_CENTER);
	}
	
}
