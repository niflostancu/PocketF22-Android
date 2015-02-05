package ro.pub.dadgm.pf22.game.models;

/**
 * The model class for terrains.
 * 
 * <p>A terrain is a height map of large dimensions.</p>
 */
public class Terrain extends BaseMobileModel {
	
	// generation constants
	
	/**
	 * The number of line-drawing iterations.
	 */
	public final int GEN_ITERATIONS = 1000;
	
	/**
	 * The possible height displacement values.
	 */
	public final float GEN_DISPLACEMENT_MIN = 0.5f;
	public final float GEN_DISPLACEMENT_SCALE = 5.0f;
	
	
	// terrain attributes
	
	/**
	 * Stores the dimensions (width, length) of the map.
	 */
	public int[] dimensions;
	
	/**
	 * The maximum height to generate.
	 */
	public float maxHeight;
	
	/**
	 * Stores the generated terrain's heightmap.
	 * 
	 * <p>The array is indexed first by X, then Y.</p>
	 */
	public float[][] heightMap;
	
	
	/**
	 * Model object constructor.
	 */
	public Terrain(int wx, int wy, float maxHeight) {
		this.dimensions = new int[] { wx, wy };
		this.maxHeight = maxHeight;
		
		heightMap = new float[wx][wy];
		
		generateMap();
	}
	
	/**
	 * Generates the terrain map using the 
	 * <a href="http://www.lighthouse3d.com/opengl/terrain/index.php3?fault">Fault Algorithm</a>.
	 */
	private void generateMap() {
		// fill the height map with maxHeight / 2
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				heightMap[i][j] = maxHeight / 2.0f;
			}
		}
		
		// iterate the algorithm
		for (int k=0; k<GEN_ITERATIONS; k++) {
			// generate a line
			double v = Math.random();
			double a = Math.sin(v);
			double b = Math.cos(v);
			double d = Math.sqrt(dimensions[0] * dimensions[0] + dimensions[1] * dimensions[1]);
			double c = Math.random() * d - d / 2.0;
			
			for (int i = 0; i < dimensions[0]; i++) {
				for (int j = 0; j < dimensions[1]; j++) {
					float displacement = (float) Math.random() * GEN_DISPLACEMENT_SCALE +
							GEN_DISPLACEMENT_MIN;
					if ((a * i + b * j) > c) {
						heightMap[i][j] += displacement;
					} else {
						heightMap[i][j] -= displacement;
					}
				}
			}
		}
		// normalize the map
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				if (heightMap[i][j] < 0)
					heightMap[i][j] = 0;
				else if (heightMap[i][j] > maxHeight) {
					heightMap[i][j] = maxHeight;
				}
			}
		}
	}
	
	
	// getters / setters
	
	/**
	 * Returns the height map's dimensions.
	 * 
	 * @return The map's dimensions as a 2-element array (width, length).
	 */
	public synchronized int[] getDimensions() {
		return new int[] { dimensions[0], dimensions[1] };
	}
	
	/**
	 * Returns the generated height map.
	 * 
	 * <p>The map should not be modified!</p>
	 * 
	 * @return The generated height map.
	 */
	public synchronized float[][] getHeightMap() {
		return heightMap;
	} 
}
