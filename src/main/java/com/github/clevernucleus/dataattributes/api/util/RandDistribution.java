package com.github.clevernucleus.dataattributes.api.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Specialised object that takes in a type and works out weighted distributions.
 * @param <T> Input type.
 */
public final class RandDistribution<T> {
	private Map<T, Float> distribution;
	private T defualtValue;
	private float distSum;
	
	/**
	 * Constructor.
	 * @param fallback Default value that is returned in case something fails.
	 */
	public RandDistribution(final T fallback) {
		this.distribution = new HashMap<>();
		this.defualtValue = fallback;
	}
	
	/**
	 * Adds to the internal inventory of the distributor.
	 * @param object Input object type.
	 * @param weight Input object type's weight.
	 */
	public void add(T object, float weight) {
		if(this.distribution.get(object) != null) {
			this.distSum -= this.distribution.get(object);
		}
		
		this.distribution.put(object, weight);
		this.distSum += weight;
	}
	
	/**
	 * @return A randomly distributed and weighted result.
	 */
	public T getDistributedRandom() {
		float rand = (float)Math.random();
		float dist = 1.0F / this.distSum;
		float mean = 0.0F;
		
		for(T object : this.distribution.keySet()) {
			mean += this.distribution.get(object);
			
			if(rand / dist <= mean) return object;
		}
		
		return this.defualtValue;
	}
}
