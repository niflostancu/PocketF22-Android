package ro.pub.dadgm.pf22.render.utils.objloader;


/**
 * Defines an object's materials (textures / colors).
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class Material {
	
	/**
	 * The material section's name.
	 */
	protected String name;
	
	/**
	 * Ambient (independent) color of the object.
	 */
	protected float[] ambientColor;
	
	/**
	 * The diffuse (lambert) color of the object.
	 */
	protected float[] diffuseColor;
	
	/**
	 * The specular (reflective) color of the object.
	 */
	protected float[] specularColor;
	
	/**
	 * Part's alpha.
	 */
	protected float alpha;
	
	/**
	 * The specular shininess of the part.
	 */
	protected float shine;
	
	/**
	 * The illumination model to use.
	 */
	protected int illum;
	
	/**
	 * The name of the texture file.
	 */
	protected String textureFile;
	
	
	public Material(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unused")
	public float[] getAmbientColor() {
		return ambientColor;
	}
	
	public void setAmbientColor(float r, float g, float b) {
		ambientColor = new float[3];
		ambientColor[0] = r;
		ambientColor[1] = g;
		ambientColor[2] = b;
	}
	
	@SuppressWarnings("unused")
	public float[] getDiffuseColor() {
		return diffuseColor;
	}
	
	public void setDiffuseColor(float r, float g, float b) {
		diffuseColor = new float[3];
		diffuseColor[0] = r;
		diffuseColor[1] = g;
		diffuseColor[2] = b;
	}
	
	@SuppressWarnings("unused")
	public float[] getSpecularColor() {
		return specularColor;
	}
	
	public void setSpecularColor(float r, float g, float b) {
		specularColor = new float[3];
		specularColor[0] = r;
		specularColor[1] = g;
		specularColor[2] = b;
	}
	
	@SuppressWarnings("unused")
	public float getAlpha() {
		return alpha;
	}
	
	@SuppressWarnings("unused")
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	@SuppressWarnings("unused")
	public float getShine() {
		return shine;
	}
	
	@SuppressWarnings("unused")
	public void setShine(float shine) {
		this.shine = shine;
	}
	
	@SuppressWarnings("unused")
	public int getIllum() {
		return illum;
	}
	
	@SuppressWarnings("unused")
	public void setIllum(int illum) {
		this.illum = illum;
	}
	
	@SuppressWarnings("unused")
	public String getTextureFile() {
		return textureFile;
	}
	
	@SuppressWarnings("unused")
	public void setTextureFile(String textureFile) {
		this.textureFile = textureFile;
	}
	
}
