package ro.pub.dadgm.pf22.render.utils.objloader;

import java.util.Vector;

/**
 * Implements basic triangulation of a polygon read from an .obj file.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class Triangulator {
	
	public static Vector<Short> triangulate(Vector<Short> polygon) {
		Vector<Short> triangles = new Vector<>();
		for (int i = 1; i < polygon.size() - 1; i++) {
			triangles.add(polygon.get(0));
			triangles.add(polygon.get(i));
			triangles.add(polygon.get(i + 1));
		}
		return triangles;
	}
	
}