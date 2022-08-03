package com.github.clevernucleus.dataattributes.api.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Yes with an s because it's Mathematics not Mathematic.
 */
public final class Maths {
	
	/**
	 * Creates a static lookup map for the input enum#values implementation.
	 * @param <K> The enum
	 * @param <V> The key
	 * @param values Enum#values
	 * @param keyMapping
	 * @return
	 */
	public static <K, V> Map<K, V> enumLookupMap(V[] values, Function<V, K> keyMapping) {
		return Arrays.stream(values).collect(Collectors.toMap(keyMapping, e -> e));
	}
	
	/**
	 * Safe string to float.
	 * @param stringIn
	 * @return
	 */
	public static float parse(final String stringIn) {
		float value;
		
		try {
			value = Float.parseFloat(stringIn);
		} catch(NumberFormatException | NullPointerException e) {
			value = 0F;
		}
		
		return value;
	}
	
	/**
	 * A staircase function.
	 * @param x
	 * @param stretch
	 * @param steepness
	 * @param xOffset
	 * @param yOffset
	 * @return y
	 */
	public static double stairs(final double x, final double stretch, final double steepness, final double xOffset, final double yOffset) {
		return steepness * stretch * (x - xOffset) - steepness * Math.sin(stretch * (x - xOffset)) + yOffset;
	}
	
	/**
	 * @param value
	 * @param min
	 * @param max
	 * @return Returns true if the value is less than max and greater than or equal to min.
	 */
	public static boolean isWithinLimits(final double value, final double min, final double max) {
		if(value < min) return false;
		if(value >= max) return false;
		return true;
	}
}
