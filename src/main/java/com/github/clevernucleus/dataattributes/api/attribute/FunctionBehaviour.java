package com.github.clevernucleus.dataattributes.api.attribute;

/**
 * @since 1.3.0
 * @author CleverNucleus
 */
public enum FunctionBehaviour {
	/** Addition of values as defined by the parent attribute. Equivalent of EntityAttributeModifier.Operation.ADDITION. */
	ADDITION((byte)0),
	/** Multiplication of parent attribute. Equivalent of EntityAttributeModifier.Operation.MULTIPLY_TOTAL. */
	MULTIPLY((byte)1);
	
	private final byte id;
	
	private FunctionBehaviour(final byte id) {
		this.id = id;
	}
	
	public static FunctionBehaviour of(final byte id) {
		return switch(id) {
			case 0 -> ADDITION;
			case 1 -> MULTIPLY;
			default -> ADDITION;
		};
	}
	
	public byte id() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
