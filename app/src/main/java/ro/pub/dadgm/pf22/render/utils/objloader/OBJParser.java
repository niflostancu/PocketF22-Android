package ro.pub.dadgm.pf22.render.utils.objloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parses a Wavefront .OBJ file.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class OBJParser {
	
	List<Short> faces = new ArrayList<>();
	List<Short> vtPointer = new ArrayList<>();
	List<Short> vnPointer = new ArrayList<>();
	
	List<Float> v = new ArrayList<>();
	List<Float> vn = new ArrayList<>();
	List<Float> vt = new ArrayList<>();
	
	List<TDModelPart> parts = new ArrayList<>();
	
	Map<String, Material> materials = null;
	
	public OBJParser() {
		// nothing here
	}
	
	public TDModel parseOBJ(InputStream inStream, InputStream mtlStream) {
		Material m = null;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		String line;
		
		try { // try to read lines of the file
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith("f")) { // a polygonal face
					processFLine(line);
				} else if (line.startsWith("vn")) {
					processVNLine(line);
				} else if (line.startsWith("vt")) {
					processVTLine(line);
				} else if (line.startsWith("v")) { // line having geometric position of single vertex
					processVLine(line);
					
				} else if (line.startsWith("g")) {
					if (faces.size() > 0) {
						short[] aFaces = convertToArrayS(faces);
						short[] aVTPointer = convertToArrayS(vtPointer);
						short[] aVNPointer = convertToArrayS(vnPointer);
						
						TDModelPart model = new TDModelPart(aFaces, aVTPointer, aVNPointer, m);
						parts.add(model);
					}
					faces.clear();
					vtPointer.clear();
					vnPointer.clear();
					
				} else if(line.startsWith("usemtl")){
					String mtlName = line.split("[ ]+", 2)[1]; // the name of the material
					m = materials.get(mtlName);
					
				} else if (line.startsWith("mtllib")) {
					materials = MTLParser.loadMTL(mtlStream);
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Could not parse the object file!", e);
		}
		
		if (faces.size() > 0) {
			short[] aFaces = convertToArrayS(faces);
			short[] aVTPointer = convertToArrayS(vtPointer);
			short[] aVNPointer = convertToArrayS(vnPointer);
			
			TDModelPart model = new TDModelPart(aFaces, aVTPointer, aVNPointer, m);
			parts.add(model);
		}
		
		float[] aV = convertToArrayF(v);
		float[] aVn = convertToArrayF(vn);
		float[] aVt = convertToArrayF(vt);
		TDModelPart[] aParts = parts.toArray(new TDModelPart[parts.size()]);
		
		return new TDModel(aV, aVn, aVt, aParts);
	}
	
	
	/**
	 * Converts the specified Float list to a float[].
	 * 
	 * @param floatList The target list to convert.
	 * @return The resulting float array.
	 */
	protected float[] convertToArrayF(List<Float> floatList) {
		float[] arr = new float[floatList.size()];
		for (int i=0; i< arr.length; i++)
			arr[i] = floatList.get(i);
		
		return arr;
	}
	
	/**
	 * Converts the specified Short list to a short[].
	 *
	 * @param shortList The target list to convert.
	 * @return The resulting short array.
	 */
	protected short[] convertToArrayS(List<Short> shortList) {
		short[] arr = new short[shortList.size()];
		for (int i=0; i< arr.length; i++)
			arr[i] = shortList.get(i);
		
		return arr;
	}
	
	
	private void processVLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;
		for (int i = 1; i < c; i++) {
			v.add(Float.valueOf(tokens[i]));
		}
	}
	
	private void processVNLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;
		for (int i = 1; i < c; i++) {
			vn.add(Float.valueOf(tokens[i]));
		}
	}
	
	private void processVTLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;
		for (int i = 1; i < c; i++) {
			vt.add(Float.valueOf(tokens[i]));
		}
	}
	
	private void processFLine(String line) {
		String[] tokens = line.split("[ ]+");
		int c = tokens.length;

		if (tokens[1].matches("[0-9]+")) {//f: v
			if (c == 4) {//3 faces
				for (int i = 1; i < c; i++) {
					Short s = Short.valueOf(tokens[i]);
					s--;
					faces.add(s);
				}
				
			} else { // more faces
				List<Short> polygon = new ArrayList<>();
				for (int i = 1; i < tokens.length; i++) {
					Short s = Short.valueOf(tokens[i]);
					s--;
					polygon.add(s);
				}
				
				// triangulate the polygon and add the resulting faces
				faces.addAll(Triangulator.triangulate(polygon));
			}
		}
		if (tokens[1].matches("[0-9]+/[0-9]+")) {//if: v/vt
			if (c == 4) {//3 faces
				for (int i = 1; i < c; i++) {
					Short s = Short.valueOf(tokens[i].split("/")[0]);
					s--;
					faces.add(s);
					s = Short.valueOf(tokens[i].split("/")[1]);
					s--;
					vtPointer.add(s);
				}
				
			} else { // triangulate
				List<Short> tmpFaces = new ArrayList<>();
				List<Short> tmpVt = new ArrayList<>();
				for (int i = 1; i < tokens.length; i++) {
					Short s = Short.valueOf(tokens[i].split("/")[0]);
					s--;
					tmpFaces.add(s);
					s = Short.valueOf(tokens[i].split("/")[1]);
					s--;
					tmpVt.add(s);
				}
				faces.addAll(Triangulator.triangulate(tmpFaces));
				vtPointer.addAll(Triangulator.triangulate(tmpVt));
			}
		}
		if (tokens[1].matches("[0-9]+//[0-9]+")) { // f: v//vn
			if (c == 4) {//3 faces
				for (int i = 1; i < c; i++) {
					Short s = Short.valueOf(tokens[i].split("//")[0]);
					s--;
					faces.add(s);
					s = Short.valueOf(tokens[i].split("//")[1]);
					s--;
					vnPointer.add(s);
				}
			} else {//triangulate
				List<Short> tmpFaces = new ArrayList<>();
				List<Short> tmpVn = new ArrayList<>();
				for (int i = 1; i < tokens.length; i++) {
					Short s = Short.valueOf(tokens[i].split("//")[0]);
					s--;
					tmpFaces.add(s);
					s = Short.valueOf(tokens[i].split("//")[1]);
					s--;
					tmpVn.add(s);
				}
				faces.addAll(Triangulator.triangulate(tmpFaces));
				vnPointer.addAll(Triangulator.triangulate(tmpVn));
			}
		}
		if (tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")) {//f: v/vt/vn

			if (c == 4) {//3 faces
				for (int i = 1; i < c; i++) {
					Short s = Short.valueOf(tokens[i].split("/")[0]);
					s--;
					faces.add(s);
					s = Short.valueOf(tokens[i].split("/")[1]);
					s--;
					vtPointer.add(s);
					s = Short.valueOf(tokens[i].split("/")[2]);
					s--;
					vnPointer.add(s);
				}
			} else {//triangulate
				List<Short> tmpFaces = new ArrayList<>();
				List<Short> tmpVn = new ArrayList<>();
				//List<Short> tmpVt=new ArrayList<>();
				for (int i = 1; i < tokens.length; i++) {
					Short s = Short.valueOf(tokens[i].split("/")[0]);
					s--;
					tmpFaces.add(s);
					//s=Short.valueOf(tokens[i].split("/")[1]);
					//s--;
					//tmpVt.add(s);
					//s=Short.valueOf(tokens[i].split("/")[2]);
					//s--;
					//tmpVn.add(s);
				}
				faces.addAll(Triangulator.triangulate(tmpFaces));
				vtPointer.addAll(Triangulator.triangulate(tmpVn));
				vnPointer.addAll(Triangulator.triangulate(tmpVn));
			}
		}
	}

}

