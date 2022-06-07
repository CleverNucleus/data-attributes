package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.function.Function;

import org.apache.commons.lang3.function.TriFunction;

import net.minecraft.util.math.MathHelper;

public enum StackingBehaviour {
	FLAT((byte)0, x -> x, (k, v, m) -> k - v),
	DIMINISHING((byte)1, x -> MathHelper.clamp(x, -1.0D, 1.0D), (k, v, m) -> Math.pow(1.0D - m, v / m) - Math.pow(1.0D - m, k / m));
	
	private final byte id;
	private final Function<Double, Double> clamp;
	private final TriFunction<Double, Double, Double, Double> stack;
	
	private StackingBehaviour(final byte id, final Function<Double, Double> clamp, final TriFunction<Double, Double, Double, Double> stack) {
		this.id = id;
		this.clamp = clamp;
		this.stack = stack;
	}
	
	public static StackingBehaviour of(final byte id) {
		return switch(id) {
			case 0 -> FLAT;
			case 1 -> DIMINISHING;
			default -> FLAT;
		};
	}
	
	public byte id() {
		return this.id;
	}
	
	public double stack(final double current, final double input) {
		final double value = this.clamp.apply(input);
		return current + Math.abs(value);
	}
	
	public double result(final double current, final double input, final double increment) {
		return this.stack.apply(current, input, increment);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
