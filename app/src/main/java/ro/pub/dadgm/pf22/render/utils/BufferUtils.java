package ro.pub.dadgm.pf22.render.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Contains static methods for NIO buffer allocation.
 */
public class BufferUtils {
	
	/**
	 * Allocates a new FloatBuffer and copies its contents from the specified float[] array.
	 * 
	 * @param data The float[] array to use for filling the buffer.
	 * @return The newly allocated buffer.   
	 */
	public static FloatBuffer asBuffer(float[] data) {
		FloatBuffer floatBuf = allocateFloatBuffer(data.length);
		floatBuf.put(data);
		floatBuf.position(0);
		
		return floatBuf;
	}
	
	/**
	 * Allocates a new ShortBuffer and copies its contents from the specified short[] array.
	 *
	 * @param data The short[] array to use for filling the buffer.
	 * @return The newly allocated buffer.   
	 */
	public static ShortBuffer asBuffer(short[] data) {
		ShortBuffer shortBuf = allocateShortBuffer(data.length);
		shortBuf.put(data);
		shortBuf.position(0);
		
		return shortBuf;
	}
	
	/**
	 * Allocates a new IntBuffer and copies its contents from the specified int[] array.
	 * 
	 * @param data The int[] array to use for filling the buffer.
	 * @return The newly allocated buffer.
	 */
	public static IntBuffer asBuffer(int[] data) {
		IntBuffer intBuf = allocateIntBuffer(data.length);
		intBuf.put(data);
		intBuf.position(0);
		
		return intBuf;
	}
	
	/**
	 * Allocates a FloatBuffer with the given length.
	 * 
	 * @param length The length of the buffer to allocate (in elements, not bytes!).
	 * @return The newly allocated buffer.
	 */
	public static FloatBuffer allocateFloatBuffer(int length) {
		return ByteBuffer
				.allocateDirect(length * 4) // float == 4 bytes
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
	}
	
	/**
	 * Allocates a ShortBuffer with the given length.
	 *
	 * @param length The length of the buffer to allocate (in elements, not bytes!).
	 * @return The newly allocated buffer.
	 */
	public static ShortBuffer allocateShortBuffer(int length) {
		return ByteBuffer
				.allocateDirect(length * 2) // short == 2 bytes
				.order(ByteOrder.nativeOrder())
				.asShortBuffer();
	}
	
	/**
	 * Allocates a IntBuffer with the given length.
	 *
	 * @param length The length of the buffer to allocate (in elements, not bytes!).
	 * @return The newly allocated buffer.
	 */
	public static IntBuffer allocateIntBuffer(int length) {
		return ByteBuffer
				.allocateDirect(length * 4) // int == 4 bytes
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();
	}
	
}
