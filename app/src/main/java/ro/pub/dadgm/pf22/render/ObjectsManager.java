package ro.pub.dadgm.pf22.render;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of renderable [3D] objects and provides utility methods for querying / manipulating it.
 */
public class ObjectsManager<O3D extends Object3D> implements Collection<O3D> {
	
	/**
	 * The internal objects collection.
	 */
	protected Collection<O3D> objectsList;
	
	
	public ObjectsManager() {
		
	}
	
	
	/**
	 * Draws all objects in the collection.
	 */
	public void drawAll() {
		for (O3D obj: objectsList) {
			obj.draw();
		}
	}
	
	
	/// decorate the internal list
	
	@Override
	public boolean add(O3D object) {
		return objectsList.add(object);
	}
	
	@Override
	public boolean addAll(Collection<? extends O3D> collection) {
		return objectsList.addAll(collection);
	}
	
	@Override
	public void clear() {
		objectsList.clear();
	}
	
	@Override
	public boolean contains(Object object) {
		return objectsList.contains(object);
	}
	
	@Override
	public boolean containsAll(Collection<?> collection) {
		return objectsList.containsAll(collection);
	}
	
	@Override
	public boolean isEmpty() {
		return objectsList.isEmpty();
	}
	
	@NonNull
	@Override
	public Iterator<O3D> iterator() {
		return objectsList.iterator();
	}
	
	@Override
	public boolean remove(Object object) {
		return objectsList.remove(object);
	}
	
	@Override
	public boolean removeAll(Collection<?> collection) {
		return objectsList.removeAll(collection);
	}
	
	@Override
	public boolean retainAll(Collection<?> collection) {
		return objectsList.retainAll(collection);
	}
	
	@Override
	public int size() {
		return objectsList.size();
	}
	
	@NonNull
	@Override
	public Object[] toArray() {
		return objectsList.toArray();
	}
	
	@NonNull
	@Override
	public <T> T[] toArray(T[] contents) {
		return objectsList.toArray(contents);
	}
}
