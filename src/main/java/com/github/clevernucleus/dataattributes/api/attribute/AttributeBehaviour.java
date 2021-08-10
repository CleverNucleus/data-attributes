package com.github.clevernucleus.dataattributes.api.attribute;

import com.github.clevernucleus.dataattributes.api.API;

/**
 * An enum for addition types (flat/normal and diminishing).
 * 
 * @author CleverNucleus
 *
 */
public enum AttributeBehaviour {
	/** Flat/normal addition. */
	FLAT("flat", (byte)0, (current, adding, limit) -> current + adding),
	/** Diminishing addition */
	DIMINISHING("diminishing", (byte)1, API::add);
	
	private String key;
	private byte id;
	private AdditionFunction function;
	
	private AttributeBehaviour(final String key, final byte id, final AdditionFunction function) {
		this.key = key;
		this.id = id;
		this.function = function;
	}
	
	/**
	 * @param id
	 * @return If the input byte is 1, returns {@link AttributeBehaviour#DIMINISHING}, else returns {@link AttributeBehaviour#FLAT}.
	 */
	public static AttributeBehaviour fromId(final byte id) {
		if(id == 1) return DIMINISHING;
		return FLAT;
	}
	
	/**
	 * @return The addition type's id.
	 */
	public byte id() {
		return this.id;
	}
	
	/**
	 * @param current
	 * @param adding
	 * @param limit
	 * @return Runs the addition type's logic on the inputs.
	 */
	public double result(final double current, final double adding, final double limit) {
		return this.function.add(current, adding, limit);
	}
	
	@Override
	public String toString() {
		return this.key;
	}
}
