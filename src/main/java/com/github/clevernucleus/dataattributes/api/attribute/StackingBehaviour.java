package com.github.clevernucleus.dataattributes.api.attribute;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.util.math.MathHelper;

/**
 * Contains both the FLAT and DIMINISHING stacking behaviours. Stacking behaviours determine how different sources of an attribute
 * will stack(add). For example: Helmet provides +1 Armor and Chestplate provides +3 Armor; FLAT behaviour means the result is +4.
 * DIMINISHING means the result is c * (1 - i) and does not account for the full sum, only the instance.
 * 
 * @author CleverNucleus
 *
 */
public enum StackingBehaviour {
	FLAT((byte)0, (c, i) -> c + i, v -> v),
	DIMINISHING((byte)1, (c, i) -> c * (1.0D - Math.abs(i)), v -> MathHelper.clamp(v, -1.0D, 1.0D));
	
	private final byte id;
	private final BiFunction<Double, Double, Double> function;
	private final Function<Double, Double> clamp;
	
	private StackingBehaviour(final byte id, final BiFunction<Double, Double, Double> function, final Function<Double, Double> clamp) {
		this.id = id;
		this.function = function;
		this.clamp = clamp;
	}
	
	public static StackingBehaviour of(final byte id) {
		switch(id) {
			case 0 : return FLAT;
			case 1 : return DIMINISHING;
			default : return FLAT;
		}
	}
	
	public byte id() {
		return this.id;
	}
	
	public double result(final double current, final double input) {
		final double value = this.clamp.apply(input);
		return this.function.apply(current, value);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
