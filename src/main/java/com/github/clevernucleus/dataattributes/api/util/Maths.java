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
	 * Safe string to int.
	 * @param stringIn
	 * @return
	 */
	public static int parseInt(final String stringIn) {
		int value;
		
		try {
			value = Integer.parseInt(stringIn);
		} catch(NumberFormatException e) {
			value = 0;
		}
		
		return value;
	}
}
