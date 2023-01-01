package com.github.clevernucleus.dataattributes.json;

import com.github.clevernucleus.dataattributes.api.attribute.FunctionBehaviour;
import com.github.clevernucleus.dataattributes.api.attribute.IAttributeFunction;
import com.github.clevernucleus.dataattributes.api.util.Maths;
import com.google.gson.annotations.Expose;

public final class AttributeFunctionJson implements IAttributeFunction {
	@Expose private FunctionBehaviour behaviour;
	@Expose private double value;
	
	private AttributeFunctionJson() {}
	
	public static AttributeFunctionJson read(byte[] byteArray) {
		AttributeFunctionJson functionTypeJson = new AttributeFunctionJson();
		functionTypeJson.behaviour = FunctionBehaviour.of(byteArray[8]);
		functionTypeJson.value = Maths.byteArrayToDouble(byteArray);
		return functionTypeJson;
	}
	
	public byte[] write() {
		byte[] byteArray = Maths.doubleToByteArray(this.value, 9);
		byteArray[8] = this.behaviour.id();
		return byteArray;
	}
	
	@Override
	public FunctionBehaviour behaviour() {
		return this.behaviour;
	}
	
	@Override
	public double value() {
		return this.value;
	}
}
