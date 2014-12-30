package ro.pub.dadgm.pf22.render.objects;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ro.pub.dadgm.pf22.render.objects.mock.MockObject3D;

/**
 * Unit test for the {@link ObjectsManager} class.
 */
public class ObjectsManagerTest extends TestCase {
	
	/**
	 * Tests being able to construct a collection and add items to it.
	 */
	public void testConstructionAdd() {
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		
		col1.add(new MockObject3D(1));
		col1.add(new MockObject3D(2, "test", 0));
		
		assertEquals(2, col1.size());
		
		List<MockObject3D> dupeThis = new ArrayList<>();
		dupeThis.add(new MockObject3D(1));
		dupeThis.add(new MockObject3D(2, "test", 0));
		
		ObjectsManager<MockObject3D> col2 = new ObjectsManager<>(dupeThis);
		
		assertEquals(2, col2.size());
	}
	
	/**
	 * Tests a simple iteration over 4 elements (null tag, same priority: 0). 
	 */
	public void testIterationSimple() {
		MockObject3D objs[] = new MockObject3D[] {
				new MockObject3D(0),
				new MockObject3D(1),
				new MockObject3D(2),
				new MockObject3D(3)
		};
		Set<Integer> validIds = new HashSet<>(Arrays.asList(
				new Integer[] { 0, 1, 2, 3 }));
		
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		Collections.addAll(col1, objs);
		
		assertEquals(objs.length, col1.size());
		
		int i = 0;
		// the order of the iteration is undefined
		for (MockObject3D obj: col1) {
			assertTrue(col1.contains(obj));
			
			boolean found = validIds.contains(obj.getId());
			if (found)
				validIds.remove(obj.getId());
			
			assertTrue(found);
			i++;
		}
		assertEquals(objs.length, i);
	}
	
	/**
	 * Tests iteration order (objects with different priorities). 
	 */
	public void testIterationOrder() {
		MockObject3D objs[] = new MockObject3D[] {
				new MockObject3D(0, null, -1),
				new MockObject3D(1, null, 0),
				new MockObject3D(2, null, 1),
				new MockObject3D(3, null, 2)
		};
		
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		Collections.addAll(col1, objs);
		
		assertEquals(objs.length, col1.size());
		
		int i = 0;
		// the objects should be iterated in the specified order
		for (MockObject3D obj: col1) {
			assertTrue(col1.contains(obj));
			assertTrue(objs[i] == obj);
			i++;
		}
		assertEquals(objs.length, i);
	}
	
	/**
	 * Tests iteration when we have objects of different tags. 
	 * Just like the previous test, the order should be unaffected.
	 */
	public void testIterationMultiTags() {
		MockObject3D objs[] = new MockObject3D[] {
				new MockObject3D(0, "1", -1),
				new MockObject3D(1, "2", 0),
				new MockObject3D(2, "3", 1),
				new MockObject3D(3, "2", 2),
				new MockObject3D(4, "1", 3),
		};
		
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		Collections.addAll(col1, objs);
		
		assertEquals(objs.length, col1.size());
		
		int i = 0;
		// the objects should be iterated in the specified order
		for (MockObject3D obj: col1) {
			assertTrue(col1.contains(obj));
			assertTrue(objs[i] == obj);
			i++;
		}
		assertEquals(objs.length, i);
	}
	
	/**
	 * Tests iterating a tag's subcollection.
	 */
	public void testTagCollectionIteration() {
		MockObject3D objs[] = new MockObject3D[] {
				new MockObject3D(0, "six", -1),
				new MockObject3D(1, "six", 0),
				new MockObject3D(2, "6", 1),
				new MockObject3D(3, "2", 2),
				new MockObject3D(4, "six", 3),
		};
		Set<Integer> validIds = new HashSet<>(Arrays.asList(
				new Integer[] { 0, 1, 4 }));
		
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		Collections.addAll(col1, objs);
		
		Collection<MockObject3D> subcol1 = col1.getObjectsByTag("six");
		assertEquals(3, subcol1.size());
		
		int i = 0;
		// the order of the iteration is undefined
		for (MockObject3D obj: subcol1) {
			boolean found = validIds.contains(obj.getId());
			if (found)
				validIds.remove(obj.getId());
			
			assertTrue(found);
			i++;
		}
		assertEquals(3, i);
	}
	
	/**
	 * Test removal of objects from the collection (remove, iterator remove and clear).
	 */
	public void testObjectRemoval() {
		MockObject3D objs[] = new MockObject3D[] {
				new MockObject3D(0, "six", -1),
				new MockObject3D(1, "six", 0),
				new MockObject3D(2, "6", 1),
				new MockObject3D(3, "2", 2),
				new MockObject3D(4, "six", 3),
		};
		
		ObjectsManager<MockObject3D> col1 = new ObjectsManager<>();
		Collections.addAll(col1, objs);

		ObjectsManager<MockObject3D> col2 = new ObjectsManager<>();
		Collections.addAll(col2, objs);

		ObjectsManager<MockObject3D> col3 = new ObjectsManager<>();
		Collections.addAll(col3, objs);
		
		// remove using remove()
		for (MockObject3D obj: objs) {
			assertTrue(col1.contains(obj));
			col1.remove(obj);
			assertFalse(col1.contains(obj));
		}
		assertEquals(0, col1.size());
		if (col1.iterator().hasNext()) {
			fail("The collection should be empty!");
		}
		
		// collection clear()
		col2.clear();
		assertEquals(0, col2.size());
		if (col2.iterator().hasNext()) {
			fail("The collection should be empty!");
		}
		
		// Iterator.remove()
		Iterator<MockObject3D> iter = col3.iterator();
		while (iter.hasNext()) {
			MockObject3D obj = iter.next();
			assertTrue(col3.contains(obj));
			iter.remove();
			assertFalse(col3.contains(obj));
		}
		assertEquals(0, col3.size());
		if (col3.iterator().hasNext()) {
			fail("The collection should be empty!");
		}
	}
	
}
