package com.github.clevernucleus.dataattributes.api.attribute;

import com.github.clevernucleus.dataattributes.api.util.AdditionFunction;
import com.github.clevernucleus.dataattributes.api.util.Maths;

public enum StackingBehaviour {
	FLAT((byte)0, (current, adding, limit) -> current + adding),
	DIMINISHING((byte)1, Maths::add);
	
	private byte id;
	private AdditionFunction function;
	
	private StackingBehaviour(final byte id, final AdditionFunction function) {
		this.id = id;
		this.function = function;
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
	
	public double result(final double current, final double adding, final double limit) {
		return this.function.add(current, adding, limit);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.id);
	}
}
