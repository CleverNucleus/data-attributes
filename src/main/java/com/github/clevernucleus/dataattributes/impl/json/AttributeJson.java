package com.github.clevernucleus.dataattributes.impl.json;

import com.github.clevernucleus.dataattributes.impl.attribute.IMutableAttribute;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.nbt.NbtCompound;

public final class AttributeJson {
	@Expose private double defaultValue;
	@Expose private double minValue;
	@Expose private double maxValue;
	@Expose private String translationKey;
	
	private AttributeJson() {}
	
	public EntityAttribute newAttribute() {
		return new ClampedEntityAttribute(this.translationKey, this.defaultValue, this.minValue, this.maxValue);
	}
	
	public void impart(IMutableAttribute attribute) {
		attribute.setDefaultValue(this.defaultValue);
		attribute.setMinValue(this.minValue);
		attribute.setMaxValue(this.maxValue);
		attribute.setTranslationKey(this.translationKey);
	}
	
	public void write(NbtCompound tagIn) {
		tagIn.putDouble("DefaultValue", this.defaultValue);
		tagIn.putDouble("MinValue", this.minValue);
		tagIn.putDouble("MaxValue", this.maxValue);
		tagIn.putString("TranslationKey", this.translationKey);
	}
	
	public static AttributeJson read(NbtCompound tagIn) {
		AttributeJson json = new AttributeJson();
		json.defaultValue = tagIn.getDouble("DefaultValue");
		json.minValue = tagIn.getDouble("MinValue");
		json.maxValue = tagIn.getDouble("MaxValue");
		json.translationKey = tagIn.getString("TranslationKey");
		
		return json;
	}
}
