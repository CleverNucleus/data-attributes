package com.github.clevernucleus.dataattributes.json;

import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;

public final class AttributeOverrideJson {
	@Expose private double fallbackValue;
	@Expose private double minValue;
	@Expose private double maxValue;
	@Expose private String translationKey;
	@Expose private StackingBehaviour stackingBehaviour;
	
	public AttributeOverrideJson() {}
	
	public EntityAttribute create() {
		return new ClampedEntityAttribute(this.translationKey, this.fallbackValue, this.minValue, this.maxValue);
	}
	
	public void transfer(final MutableEntityAttribute attribute) {
		attribute.transferAttribute(this.translationKey, this.minValue, this.maxValue, this.fallbackValue, this.stackingBehaviour);
	}
	
	public void readFromNbt(NbtCompound tag) {
		this.fallbackValue = tag.getDouble("FallbackValue");
		this.minValue = tag.getDouble("MinValue");
		this.maxValue = tag.getDouble("MaxValue");
		this.translationKey = tag.getString("TranslationKey");
		byte stackingBehaviour = tag.getByte("StackingBehaviour");
		this.stackingBehaviour = StackingBehaviour.of(stackingBehaviour);
	}
	
	public void writeToNbt(NbtCompound tag) {
		tag.putDouble("FallbackValue", this.fallbackValue);
		tag.putDouble("MinValue", this.minValue);
		tag.putDouble("MaxValue", this.maxValue);
		tag.putString("TranslationKey", this.translationKey);
		tag.putByte("StackingBehaviour", this.stackingBehaviour.id());
	}
}
