package ro.pub.dadgm.pf22.render.objects;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Manages a list of renderable [3D] objects and provides utility methods for querying / manipulating 
 * it.
 * 
 * <p>An object ({@link Object3D}) has two properties: a drawing priority which specified the order 
 * in which the objects will be iterated and, optionally, a tag (a string that can be used to group 
 * multiple objects for easy management).</p>
 */
public class ObjectsManager<O3D extends Object3D> extends AbstractCollection<O3D> {
	
	/**
	 * An internally used collection (set) that propagates its changes to the parent 
	 * manager.
	 */
	protected class ObjectsCollection extends AbstractCollection<O3D> {
		
		/**
		 * The internal objects list.
		 */
		protected HashSet<O3D> objects;
		
		/**
		 * Internal collection constructor.
		 */
		protected ObjectsCollection() {
			this.objects = new HashSet<>();
		}
		
		@Override
		public boolean add(O3D obj) {
			boolean added = objects.add(obj);
			if (!added) return false;
			
			// notify the manager a new object has been added
			ObjectsManager.this.objectAdded(obj);
			
			return true;
		}
		
		@Override
		public boolean remove(Object obj) {
			if (!(obj instanceof Object3D))
				throw new ClassCastException("The object must implement Object3D!");
			
			if (!objects.remove(obj)) return false;
			
			// notify the manager an object has been removed
			ObjectsManager.this.objectRemoved(((Object3D)obj));
			
			return true;
		}
		
		@Override
		@NotNull
		public Iterator<O3D> iterator() {
			final Iterator<O3D> internalIterator = objects.iterator();
			
			return new Iterator<O3D>() {
				protected O3D current;
				
				@Override
				public boolean hasNext() {
					return internalIterator.hasNext();
				}
				
				@Override
				public O3D next() {
					current = internalIterator.next();
					return current;
				}
				
				@Override
				public void remove() {
					if (current == null)
						throw new IllegalStateException("No current element in iteration!");
					
					// use the collection's implementation
					ObjectsCollection.this.remove(current);
				}
			};
		}
		
		@Override
		public boolean contains(Object obj) {
			return objects.contains(obj);
		}
		
		@Override
		public int size() {
			return objects.size();
		}
		
	}
	
	/**
	 * The objects are stored inside a map.
	 * 
	 * <p>Must support the null key! (HashMap does that)</p>
	 */
	protected Map<String, ObjectsCollection> objectsMap;
	
	/**
	 * An internal map that retains the objects grouped by priority.
	 */
	protected Map<Integer, Collection<O3D>> priorityMap;
	
	/**
	 * Cached collection size.
	 */
	protected int size = 0;
	
	
	/**
	 * Constructs an empty objects collection.
	 */
	public ObjectsManager() {
		objectsMap = new HashMap<>();
		priorityMap = new TreeMap<>();
	}
	
	/**
	 * Constructs a new objects manager instance with the objects from the given collection.
	 * 
	 * @param collection The initial objects add to the managed collection.
	 */
	public ObjectsManager(Collection<O3D> collection) {
		this();
		this.addAll(collection);
	}
	
	/**
	 * Draws all objects in the collection.
	 */
	public void drawAll() {
		for (O3D obj: this) {
			obj.draw();
		}
	}
	
	
	/**
	 * Returns all objects that have the specified tag.
	 * 
	 * <p>The returned collection is mutable, the changes (addition / deletion) will be propagated 
	 * to the parent ObjectsManager instance.</p>
	 * 
	 * <p>Warning: the objects in this collection are not sorted by priority!</p>
	 * 
	 * @param tag The tag to search by. Null is a valid value (objects without tag).
	 * @return A mutable collection with all objects with the specified tag.
	 */
	public Collection<O3D> getObjectsByTag(String tag) {
		return objectsMap.get(tag);
	}
	
	
	// Collection implementation below
	
	@Override
	public boolean add(O3D obj) {
		String tag = obj.getTag();
		
		if (!objectsMap.containsKey(tag)) {
			objectsMap.put(tag, this.new ObjectsCollection());
		}
		
		return objectsMap.get(tag).add(obj);
	}
	
	/**
	 * Finds and removes the specified object.
	 * 
	 * @param obj The object to remove.
	 * @return True if the element was found and removed, false otherwise.
	 */
	@Override
	public boolean remove(Object obj) {
		if (!(obj instanceof Object3D)) 
			throw new ClassCastException("The object must implement Object3D!");
		
		String tag = ((Object3D)obj).getTag();
		return objectsMap.containsKey(tag) && objectsMap.get(tag).remove(obj);
	}
	
	@Override
	@NotNull
	public Iterator<O3D> iterator() {
		final Iterator<Map.Entry<Integer, Collection<O3D>>> mapIter = 
				priorityMap.entrySet().iterator();
		
		/**
		 * The iterator class for the objects manager's collection.
		 * Iterates through all the objects in the (sorted) priority map.
		 */
		return new Iterator<O3D>() {
			protected Iterator<O3D> curIter = null;
			protected O3D current;
			
			@Override
			public boolean hasNext() {
				if (curIter == null) {
					if (!mapIter.hasNext()) 
						return false;
					curIter = mapIter.next().getValue().iterator();
				}
				if (curIter.hasNext())
					return true;
				
				while (mapIter.hasNext()) {
					curIter = mapIter.next().getValue().iterator();
					if (curIter.hasNext()) 
						return true;
				}
				
				return false;
			}
			
			@Override
			public O3D next() {
				if (!hasNext())
					throw new NoSuchElementException("hasNext() == false");
				current = curIter.next();
				return current;
			}
			
			@Override
			public void remove() {
				if (current == null)
					throw new IllegalStateException("No current element in iteration!");
				
				// use the parent remove() method
				ObjectsManager.this.remove(current);
			}
		};
	}
	
	@Override
	public void clear() {
		objectsMap = new HashMap<>();
		priorityMap = new TreeMap<>();
	}
	
	@Override
	public boolean contains(Object obj) {
		if (!(obj instanceof Object3D))
			throw new ClassCastException("The object must implement Object3D!");
		
		String tag = ((Object3D)obj).getTag();
		return objectsMap.containsKey(tag) && objectsMap.get(tag).contains(obj);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	
	// some internally used methods
	
	/**
	 * Called internally when a new object is added to the collection.
	 * 
	 * @param obj the object that was added
	 */
	protected void objectAdded(O3D obj) {
		// increment size
		size++;
		
		Integer priority = obj.getPriority();
		if (!priorityMap.containsKey(priority)) {
			// use a HashSet because of the efficient contains() and remove() implementations
			priorityMap.put(priority, new HashSet<O3D>());
		}
		
		priorityMap.get(priority).add(obj);
	}

	/**
	 * Called internally when a single object is removed from the collection.
	 * 
	 * @param obj The object that was removed.
	 */
	protected void objectRemoved(Object3D obj) {
		// decrement size
		size--;
		
		// remove it from the sorted objects map
		Integer priority = obj.getPriority();
		if (priorityMap.containsKey(priority)) {
			priorityMap.get(priority).remove(obj);
		}
	}
	
}
