package ro.pub.dadgm.pf22.render.utils;

/**
 * Utility class for normals computation.
 * 
 * <p>Uses vector arithmetic, but reimplements the required vector operations for efficiency 
 * (rather than using the {@link ro.pub.dadgm.pf22.utils.Vector3D} class).</p>
 * 
 * <p>All methods work with 3D vectors/points specified as indexed float arrays that must be pre-allocated.</p>
 */
public class NormalUtils {
	
	/**
	 * Computes the normal of a surface given a triangle (3 vertices).
	 * 
	 * <p>The output and v* are vector/point arrays must have the length of at least index+3.</p>
	 * 
	 * @param output The array used to store the result.
	 * @param outIdx The position to insert the result.
	 * @param v1 The first vertex coordinates array pointer.
	 * @param v1Idx First vertex's index.
	 * @param v2 The second vertex coordinates array pointer.
	 * @param v2Idx Second vertex's index.
	 * @param v3 The third vertex coordinates array pointer.
	 * @param v3Idx Third vertex's index.
	 */
	public static void computeNormal(float[] output, int outIdx, 
									 float[] v1, int v1Idx, float[] v2, int v2Idx, 
									 float[] v3, int v3Idx) {
		// compute U and V vectors
		float ux = v2[v2Idx] - v1[v1Idx];
		float uy = v2[v2Idx+1] - v1[v1Idx+1];
		float uz = v2[v2Idx+2] - v1[v1Idx+2];
		float vx = v3[v3Idx] - v1[v1Idx];
		float vy = v3[v3Idx+1] - v1[v1Idx+1];
		float vz = v3[v3Idx+2] - v1[v1Idx+2];
		
		// compute the cross product U X V
		output[outIdx] = uy * vz - uz * vy;
		output[outIdx+1] = uz * vx - ux * vz;
		output[outIdx+2] = ux * vy - uy * vx;
		
		// normalize the resulting vector
		normalize(output, outIdx);
	}
	
	/**
	 * Normalizes (in-place) the specified vector.
	 * 
	 * @param vector The vector array to normalize.
	 * @param vIdx Vector's starting array index.
	 */
	public static void normalize(float[] vector, int vIdx) {
		// calculate vector's length
		float length = (float)Math.sqrt(vector[vIdx]*vector[vIdx] + 
				vector[vIdx+1]*vector[vIdx+1] + vector[vIdx+2]*vector[vIdx+2]);
		
		// divide all components by the vector's length
		vector[vIdx] /= length;
		vector[vIdx+1] /= length;
		vector[vIdx+2] /= length;
	}
	
}
