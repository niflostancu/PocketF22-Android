package ro.pub.dadgm.pf22.render.utils.objloader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Defines an object's materials (textures / colors).
 * 
 * <p>Based on http://sourceforge.net/projects/objloaderforand/</p>
 */
public class Material {
	String name;
	float[] ambientColor;
	float[] diffuseColor;
	float[] specularColor;
	float alpha;
	float shine;
	int illum;
	String textureFile;
	
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
	
	public FloatBuffer getAmbientColorBuffer() {
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(ambientColor);
		f.position(0);
		return f;
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
	
	public FloatBuffer getDiffuseColorBuffer() {
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(diffuseColor);
		f.position(0);
		return f;
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
	
	public FloatBuffer getSpecularColorBuffer() {
		FloatBuffer f;
		ByteBuffer b = ByteBuffer.allocateDirect(12);
		b.order(ByteOrder.nativeOrder());
		f = b.asFloatBuffer();
		f.put(specularColor);
		f.position(0);
		return f;
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
	
	public String toString() {
		String str = "";
		str += "Material name: " + name;
		str += "\nAmbient color: " + Arrays.toString(ambientColor);
		str += "\nDiffuse color: " + Arrays.toString(diffuseColor);
		str += "\nSpecular color: " + Arrays.toString(specularColor);
		str += "\nAlpha: " + alpha;
		str += "\nShine: " + shine;
		return str;
	}
}
