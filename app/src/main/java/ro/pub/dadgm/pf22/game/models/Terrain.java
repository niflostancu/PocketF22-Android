package ro.pub.dadgm.pf22.game.models;

import java.util.Stack;

import ro.pub.dadgm.pf22.physics.CollisionObject;

/**
 * The model class for terrains.
 * 
 * <p>A terrain is a height map of large dimensions.</p>
 */
public class Terrain extends BaseModel implements CollisionObject {
	
	// generation constants
	
	/**
	 * The number of line-drawing iterations for the fault algorithm.
	 */
	public final int GEN_ITERATIONS = 20;
	
	/**
	 * The possible height displacement values (for the fault algorithm).
	 */
	public final float GEN_DISPLACEMENT_MIN = 0.5f;
	public final float GEN_DISPLACEMENT_SCALE = 1.0f;

	/**
	 * Level of detail for the fractal algorithm.
	 */
	public final int FRACTAL_LOD = 8;
	
	
	/**
	 * Describes all possible terrain types.
	 */
	public final static Object[][] TERRAIN_TYPES = {
			// { name, texture_file, start_height, end_height }
			{ "grass", "grass.jpg", 0.0f, World.WORLD_MAX_HEIGHT / 3f },
			{ "snow", "snow.jpg", World.WORLD_MAX_HEIGHT / 2.5f, World.WORLD_MAX_HEIGHT },
			{ "mountain", "mountain.png", World.WORLD_MAX_HEIGHT / 2.5f, World.WORLD_MAX_HEIGHT },
	};
	
	
	// terrain attributes
	
	/**
	 * Stores the dimensions (width, length) of the map.
	 */
	protected int[] dimensions;
	
	/**
	 * The maximum height to generate.
	 */
	protected float maxHeight;
	
	/**
	 * Stores the generated terrain's heightmap.
	 * 
	 * <p>The array is indexed first by X, then Y.</p>
	 */
	protected float[][] heightMap;
	
	/**
	 * Each terrain point has a specific type of terrain.
	 * This matrix stores its identifier (index of {@link Terrain#TERRAIN_TYPES}).
	 */
	protected byte[][] typeMap;
	
	/**
	 * Stores the point count for each generated terrain type.
	 */
	protected int[] typeCount;
	
	
	/**
	 * Model object constructor.
	 * 
	 * @param wx Terrain's width (on X).
	 * @param wy Terrain's length (on Y).
	 * @param maxHeight The maximum height to generate.   
	 */
	public Terrain(int wx, int wy, float maxHeight) {
		this.dimensions = new int[] { wx, wy };
		this.maxHeight = maxHeight;
		
		heightMap = new float[wx][wy];
		typeMap = new byte[wx][wy];
		typeCount = new int[TERRAIN_TYPES.length];
		
		// generateMap();
		generateFractalMap();
		generateTypeMap();
	}

	/**
	 * Generates terrain using the <a href="http://www.javaworld.com/article/2076745">Diamond-Square Algorithm</a>.
	 */
	private void generateFractalMap() {
		final float roughness = 0.4f;
		final int lod = FRACTAL_LOD;
		
		int divisions = 1 << lod;
		
		float[][] terrain = new float[divisions + 1][divisions + 1];
		
		terrain[0][0] = rnd();
		terrain[0][divisions] = rnd();
		terrain[divisions][divisions] = rnd();
		terrain[divisions][0] = rnd();
		
		float rough = roughness;
		for (int i = 0; i < lod; ++ i) {
			int r = 1 << (lod - i), s = r >> 1;
			for (int j = 0; j < divisions; j += r)
				for (int k = 0; k < divisions; k += r)
					diamond(terrain, j, k, r, rough);
			if (s > 0)
				for (int j = 0; j <= divisions; j += s)
					for (int k = (j + s) % r; k <= divisions; k += r)
						square (terrain, j - s, k - s, r, rough, divisions);
			
			rough *= roughness;
		}
		
		// generate the height map using the fractal terrain
		float min = 0;
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				float tx = (i / (float)dimensions[0]) * divisions;
				float ty = (j / (float)dimensions[1]) * divisions;
				
				// do a bilinear interpolation
				float s00 = terrain[(int)Math.floor(tx)][(int)Math.floor(ty)];
				float s01 = terrain[(int)Math.floor(tx)][(int)Math.ceil(ty)];
				float s10 = terrain[(int)Math.ceil(tx)][(int)Math.floor(ty)];
				float s11 = terrain[(int)Math.ceil(tx)][(int)Math.ceil(ty)];
				
				float xfrac = tx - (int)tx;
				float yfrac = ty - (int)ty;
				float tval = (1 - yfrac) * ( (1 - xfrac)*s00 + xfrac*s01) +
						yfrac * ( (1 - xfrac)*s10 + xfrac*s11);
				
				heightMap[i][j] = tval * maxHeight;
				
				min = Math.min(heightMap[i][j], min);
			}
		}
		
		// level the map
		float minLevel = (float)Math.random() * min*0.5f;
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				heightMap[i][j] = heightMap[i][j] - minLevel;
			}
		}
		
		// apply a Gaussian Blur on the heightmap to smoothen it
		float[][] kernel = makeGaussianKernel(11, 30);
		heightMap = convolutionFilter(heightMap, kernel, 1, 0, maxHeight);
	}

	/**
	 * Generates a diamond on the terrain.
	 * 
	 * @param terrain The target terrain matrix.
	 * @param x The X coordinate of the diamond.
	 * @param y The Y coordinate.
	 * @param side Diamond's side width.
	 * @param scale Diamond's scale.
	 */
	private void diamond (float[][] terrain, int x, int y, int side, float scale) {
		if (side > 1) {
			int half = side / 2;
			float avg = (terrain[x][y] + terrain[x + side][y] +
					terrain[x + side][y + side] + terrain[x][y + side]) * 0.25f;
			terrain[x + half][y + half] = avg + rnd () * scale;
		}
	}

	/**
	 * Generates a square.
	 * 
	 * @param terrain The target terrain matrix.
	 * @param x The X coordinate of the square.
	 * @param y The Y coordinate.
	 * @param side Square's side width.
	 * @param scale Diamond's side width.
	 * @param divisions Matrix's number of divisions.
	 */
	private void square (float[][] terrain, int x, int y, int side, float scale, int divisions) {
		int half = side / 2;
		float avg = 0.0f, sum = 0.0f;
		
		if (x >= 0) {
			avg += terrain[x][y + half]; sum += 1.0;
		}
		if (y >= 0) {
			avg += terrain[x + half][y]; sum += 1.0;
		}
		if (x + side <= divisions) {
			avg += terrain[x + side][y + half]; sum += 1.0;
		}
		if (y + side <= divisions) {
			avg += terrain[x + half][y + side]; sum += 1.0;
		}
		
		terrain[x + half][y + half] = avg / sum + rnd () * scale;
	}
	
	private float rnd () {
		return (float)(2.0 * Math.random() - 1.0);
	}
	
	/**
	 * Generates the terrain map using the 
	 * <a href="http://www.lighthouse3d.com/opengl/terrain/index.php3?fault">Fault Algorithm</a>.
	 */
	@SuppressWarnings("unused")
	private void generateFaultMap() {
		// fill the height map with maxHeight / 2
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				heightMap[i][j] = maxHeight / 2.0f;
			}
		}
		
		// iterate the algorithm
		for (int k=0; k<GEN_ITERATIONS; k++) {
			// generate a line
			double v = Math.random() * 2 * Math.PI;
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
		float min = 0;
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				min = Math.min(heightMap[i][j], min);
			}
		}
		
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				heightMap[i][j] -= min;
				if (heightMap[i][j] < 0)
					heightMap[i][j] = 0;
				else if (heightMap[i][j] > maxHeight) {
					heightMap[i][j] = maxHeight;
				}
			}
		}
		
		// apply a Gaussian Blur on the heightmap to smoothen it
		float[][] kernel = makeGaussianKernel(11, 30);
		heightMap = convolutionFilter(heightMap, kernel, 1, 0, maxHeight);
	}

	/**
	 * Applies a convolution filter to the specified heightMap.
	 * 
	 * @param heightMap The height map to convolute.
	 * @param filterMatrix The convolution kernel to use.
	 * @param factor Convolution factor.
	 * @param bias Bias value.
	 * @param maxHeight The maximum value to clamp.
	 * @return The convoluted height map.
	 */
	public static float[][] convolutionFilter(float[][] heightMap, float[][] filterMatrix, 
											  float factor, int bias, float maxHeight) {
		
		int height = heightMap.length;
		int width = heightMap[0].length;
		
		float[][] resultMap = new float[height][width];
		
		float value;
		
		int filterWidth = filterMatrix[0].length;
		// int filterHeight = filterMatrix.length;
		int filterOffset = (filterWidth-1) / 2;
		
		for (int offsetY = filterOffset; offsetY < height - filterOffset; offsetY++) {
			for (int offsetX = filterOffset; offsetX < width - filterOffset; offsetX++) {
				value = 0;
				
				for (int filterY = -filterOffset; filterY <= filterOffset; filterY++) {
					for (int filterX = -filterOffset; filterX <= filterOffset; filterX++) {
						value += heightMap[offsetY][offsetX] *
								filterMatrix[filterY + filterOffset][filterX + filterOffset];
					}
				}
				
				value = factor * value + bias;
				value = (value > maxHeight ? maxHeight : (value < 0 ? 0 : value));
				
				resultMap[offsetY][offsetX] = value;
			}
		}
		
		return resultMap;
	}
	
	/**
	 * Make a Gaussian blur kernel.
	 * 
	 * @param length Kernel matrix's length.
	 * @param weight Blur's weight (sigma).
	 */
	public static float[][] makeGaussianKernel(int length, float weight) {
		float[][] kernel = new float[length][length];
		float sumTotal = 0;
		
		int kernelRadius = length / 2;
		float distance;
		
		float calculatedEuler = (float)(1.0 /
				(2.0 * Math.PI * Math.pow(weight, 2)));
		
		for (int filterY = -kernelRadius; filterY <= kernelRadius; filterY++) {
			for (int filterX = -kernelRadius; filterX <= kernelRadius; filterX++) {
				distance = ((filterX * filterX) +
						(filterY * filterY)) /
						(2 * (weight * weight));
				
				kernel[filterY + kernelRadius][filterX + kernelRadius] =
						calculatedEuler * (float)Math.exp(-distance);
				
				sumTotal += kernel[filterY + kernelRadius][filterX + kernelRadius];
			}
		}
		
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < length; x++) {
				kernel[y][x] = kernel[y][x] * (1.0f / sumTotal);
			}
		}
		
		return kernel;
	}
	
	/**
	 * Generates the terrain types for each point on the height map.
	 * 
	 * <p>The algorithm is the following: 
	 * <ul>
	 *     <li>a random type is extracted from {@link #TERRAIN_TYPES};</li>
	 *     <li>if it doesn't meet the constraints, it has a 5% probability of passing;</li>
	 *     <li>otherwise, the probability of selection is 50%;</li>
	 *     <li>on success, there is a 50% probability of a neighbor to also be populated with the 
	 *     same type;</li>
	 *     <li>repeat until all points on the map are populated.</li>
	 * </ul>
	 * </p>
	 */
	private void generateTypeMap() {
		// use a stack for doing the floodfill
		Stack<int[]> stack = new Stack<>();
		
		// initialization
		for (int i=0; i<dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				typeMap[i][j] = -1; // uninitialized
				stack.add(new int[]{ i, j });
			}
		}
		
		// process all points
		while (!stack.isEmpty()) {
			int[] d = stack.pop();
			int i = d[0];
			int j = d[1];
			if (typeMap[i][j] >= 0)
				continue;
			
			float h = heightMap[i][j];
			// byte t = (byte)(Math.random() * TERRAIN_TYPES.length);
			byte t = 0;
			
			boolean passed = dice(0.05); // 5% chance to pass anyways
			if (!passed) {
				// extract the terrain type
				Float min = (Float)TERRAIN_TYPES[t][2];
				Float max = (Float)TERRAIN_TYPES[t][3];
				if (min < h && h < max) {
					passed = dice(0.9); // 90% probability to pass
				}
			}
			
			if (passed) {
				typeMap[i][j] = t;
				typeCount[t]++;
				
				// 50% chance to fill to a neighbor
				// 4 neighbors
				if (i>0 && dice(0.8)) { typeMap[i - 1][j] = t; typeCount[t]++; }
				if (j>0 && dice(0.8)) { typeMap[i][j-1] = t; typeCount[t]++; }
				if (i<dimensions[0]-1 && dice(0.8)) { typeMap[i+1][j] = t; typeCount[t]++; }
				if (j<dimensions[1]-1 && dice(0.8)) { typeMap[i][j+1] = t; typeCount[t]++; }
				
			} else {
				// reprocess
				stack.add(d);
			}
		}
	}

	/**
	 * Rolls a 2-sided dice (boolean :D ) with the specified chance.
	 * 
	 * @param chance The chance to return true.
	 * @return Returns true or false, depending on the roll.
	 */
	private boolean dice(double chance) {
		double r = Math.random();
		return (0.5 - chance/2) <= r && r <= (0.5 + chance/2);
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

	/**
	 * Returns the generated terrain type map.
	 * 
	 * <p>The map should not be modified!</p>
	 * 
	 * @return The generated type map.
	 */
	public synchronized byte[][] getTypeMap() {
		return typeMap;
	}
	
	/**
	 * Returns the area count for each terrain type.
	 * 
	 * <p>The array should not be modified!</p>
	 * 
	 * @return The type count map.
	 */
	@SuppressWarnings("unused")
	public synchronized int[] getTypeCount() {
		return typeCount;
	}
	
	/**
	 * Returns the guaranteed maximum height of a point on the terrain.
	 * 
	 * @return The maximum height of a generated point.
	 */
	public synchronized float getMaxHeight() {
		return maxHeight;
	}
	
	@Override
	public boolean collidesWith(CollisionObject obj) {
		// TODO implement this
		return false;
	}
	
}
