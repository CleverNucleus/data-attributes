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
	 * Adds or subtracts the current and adding values, with diminishing returns tending towards the limit.
	 * @param current Current value (add to or subtract from).
	 * @param adding Modifying value (+/-).
	 * @param limit The limiting value (output respects this, unless limit == 0).
	 * @return The diminishing returns.
	 */
	public static double add(final double current, final double adding, final double limit) {
		if(limit == 0.0D) return current;
		if(adding > 0.0D) return limit * ((current + adding) / (limit + adding));
		if(adding < 0.0D) return adding + current - (current * adding / limit);
		
		return current;
	}
	
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
	 * Parses a string to a float.
	 * @param stringIn
	 * @return
	 */
	public static float parse(final String stringIn) {
		float result;
		
		try {
			result = (float)Float.valueOf(stringIn);
		} catch(NumberFormatException e) {
			result = 0.0F;
		}
		
		return result;
	}
}
