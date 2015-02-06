package ro.pub.dadgm.pf22.render.objects.hud;

import ro.pub.dadgm.pf22.render.Scene3D;
import ro.pub.dadgm.pf22.utils.Point3D;

/**
 * Manages multiple menu items, automatically calculating their positioning.
 * 
 * <p>The class inherits CenteredContainer to automatically display the items as centered.</p>
 */
public class MenuContainer extends CenteredContainer {
	
	/**
	 * Initializes the menu background object.
	 * 
	 * @param scene The parent scene object.
	 * @param tag An optional tag.
	 * @param priority An optional priority.
	 */
	public MenuContainer(Scene3D scene, String tag, int priority) {
		super(scene, tag, priority);
	}
	
	
	/**
	 * Recalculates the new positioning of the menu items.
	 */
	@Override
	public void repositionObjects() {
		final float itemHeight = 0.7f;
		final float itemSpacing = 0.1f;
		
		float curY = position.getY() + height - itemHeight;
		
		// compute the Y position of the menu items, in order
		for (HUDObject object: objects) {
			Point3D position = object.position();
			position.setY(curY);
			object.updateBoundingBox();
			
			curY -= itemHeight + itemSpacing;
		}
		
		// now center the objects
		super.repositionObjects();
	}
	
	// several overrides
	
	@Override
	public void draw() {
		if (!visibility) return;
		
		// draw all children objects
		objects.drawAll();
	}
	
	@Override
	public void destroy() {
		for (HUDObject object: objects) {
			object.destroy();
		}
	}
	
}
