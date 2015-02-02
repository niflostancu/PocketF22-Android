package ro.pub.dadgm.pf22.render.utils.objloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements basic triangulation of a polygon read from an .obj file.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class Triangulator {
	
	public static List<Short> triangulate(List<Short> polygon) {
		List<Short> triangles = new ArrayList<>();
		for (int i = 1; i < polygon.size() - 1; i++) {
			triangles.add(polygon.get(0));
			triangles.add(polygon.get(i));
			triangles.add(polygon.get(i + 1));
		}
		return triangles;
	}
	
}