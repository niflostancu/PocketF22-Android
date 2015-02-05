package ro.pub.dadgm.pf22.utils.events;

/**
 * A simple event listener interface that lets an object know whenever a collection is changed.
 */
public interface CollectionListener<T> {
	
	/**
	 * Fired when a new object has been added to the collection.
	 * 
	 * @param object The object that was added.
	 */
	public void onObjectAdded(T object);
	
	/**
	 * Fired when a new object has been removed from the collection.
	 * 
	 * @param object The object that was removed.
	 */
	public void onObjectRemoved(T object);
	
}
