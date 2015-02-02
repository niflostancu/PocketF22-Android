package ro.pub.dadgm.pf22.render.utils.objloader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

/**
 * Stores a mesh model.
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class TDModel {
	Vector<Float> v;
	Vector<Float> vn;
	Vector<Float> vt;
	Vector<TDModelPart> parts;
	FloatBuffer vertexBuffer;
	
	public TDModel(Vector<Float> v, Vector<Float> vn, Vector<Float> vt,
				   Vector<TDModelPart> parts) {
		super();
		this.v = v;
		this.vn = vn;
		this.vt = vt;
		this.parts = parts;
	}
	
	public String toString() {
		String str = "";
		str += "Number of parts: " + parts.size();
		str += "\nNumber of vertexes: " + v.size();
		str += "\nNumber of vns: " + vn.size();
		str += "\nNumber of vts: " + vt.size();
		str += "\n/////////////////////////\n";
		for (int i = 0; i < parts.size(); i++) {
			str += "Part " + i + '\n';
			str += parts.get(i).toString();
			str += "\n/////////////////////////";
		}
		return str;
	}
	
	public void buildVertexBuffer() {
		ByteBuffer vBuf = ByteBuffer.allocateDirect(v.size() * 4);
		vBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = vBuf.asFloatBuffer();
		vertexBuffer.put(toPrimitiveArrayF(v));
		vertexBuffer.position(0);
	}
	
	private static float[] toPrimitiveArrayF(Vector<Float> vector) {
		float[] f;
		f = new float[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			f[i] = vector.get(i);
		}
		return f;
	}
}


