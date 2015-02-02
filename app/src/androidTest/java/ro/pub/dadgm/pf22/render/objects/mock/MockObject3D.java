package ro.pub.dadgm.pf22.render.objects.mock;

import ro.pub.dadgm.pf22.render.objects.Object3D;

/**
 * Simple mock Object3D implementation.
 * 
 * <p>Has an unique identifier property useful for use in comparing the objects 
 * (note: equals still uses the default {@link Object#equals(Object)}) implementation.</p>
 */
public class MockObject3D implements Object3D {
	
	/**
	 * An unique identifier for this object.
	 */
	protected int id;
	
	/**
	 * Stores the object's tag.
	 */
	protected final String tag;
	
	/**
	 * Object's priority.
	 */
	protected final int priority;
	
	/**
	 * Constructor with default arguments.
	 * 
	 * @param id Object's unique identifier.
	 */
	public MockObject3D(int id) {
		this.id = id;
		tag = null;
		priority = 0;
	}
	
	/**
	 * Constructor with tag and priority override.
	 * 
	 * @param id Object's unique identifier.
	 * @param tag Override default tag (null).
	 * @param priority Override default priority (0).
	 */
	public MockObject3D(int id, String tag, int priority) {
		this.id = id;
		this.tag = tag;
		this.priority = priority;
	}
	
	/**
	 * Returns the object's id.
	 * 
	 * @return Object's unique identifier.
	 */
	public int getId() {
		return id;
	};
	
	
	@Override
	public void draw() {
		// do nothing
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void destroy() {
		// do nothing
	}
}
