package com.github.clevernucleus.dataattributes.impl.json;

import com.github.clevernucleus.dataattributes.api.attribute.AttributeBehaviour;
import com.github.clevernucleus.dataattributes.api.attribute.IAttributeFunction;
import com.google.gson.annotations.Expose;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public final class AttributeFunctionJson implements IAttributeFunction {
	@Expose private String attribute;
	@Expose private AttributeBehaviour behaviour;
	@Expose private double multiplier;
	
	public AttributeFunctionJson() {}
	
	public void write(NbtCompound tagIn) {
		tagIn.putString("Identifier", this.attribute);
		tagIn.putByte("Behaviour", this.behaviour.id());
		tagIn.putDouble("Multiplier", this.multiplier);
	}
	
	public static AttributeFunctionJson read(NbtCompound tagIn) {
		AttributeFunctionJson attributeFunctionJson = new AttributeFunctionJson();
		attributeFunctionJson.attribute = tagIn.getString("Identifier");
		attributeFunctionJson.behaviour = AttributeBehaviour.fromId(tagIn.getByte("Behaviour"));
		attributeFunctionJson.multiplier = tagIn.getDouble("Multiplier");
		
		return attributeFunctionJson;
	}
	
	@Override
	public Identifier attribute() {
		return new Identifier(this.attribute);
	}
	
	@Override
	public AttributeBehaviour behaviour() {
		return this.behaviour;
	}
	
	@Override
	public double multiplier() {
		return this.multiplier;
	}
	
	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		if(!(object instanceof AttributeFunctionJson)) return false;
		
		AttributeFunctionJson json = (AttributeFunctionJson)object;
		
		return this.attribute.equals(json.attribute);
	}
	
	@Override
	public String toString() {
		return this.attribute;
	}
}
