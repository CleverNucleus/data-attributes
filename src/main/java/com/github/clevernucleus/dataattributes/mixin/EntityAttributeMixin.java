package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityAttribute.class)
abstract class EntityAttributeMixin implements MutableEntityAttribute {
	@Unique private Map<IEntityAttribute, Double> data$parents, data$children;
	@Unique private Map<String, String> data$properties;
	@Unique private StackingBehaviour data$stackingBehaviour;
	@Unique private String data$translationKey;
	@Unique protected double data$fallbackValue, data$minValue, data$maxValue;
	
	@Final
	@Shadow
	private double fallback;
	
	@Final
	@Shadow
	private String translationKey;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(String translationKey, double fallback, CallbackInfo info) {
		this.data$translationKey = translationKey;
		this.data$fallbackValue = fallback;
		this.data$minValue = fallback;
		this.data$maxValue = fallback;
		this.data$stackingBehaviour = StackingBehaviour.FLAT;
		this.data$parents = new Object2DoubleArrayMap<IEntityAttribute>();
		this.data$children = new Object2DoubleArrayMap<IEntityAttribute>();
		this.data$properties = new HashMap<String, String>();
	}
	
	@Override
	public boolean contains(MutableEntityAttribute a, MutableEntityAttribute b) {
		if(b == null || a == b) return true;
		
		for(IEntityAttribute n : a.parentsMutable().keySet()) {
			MutableEntityAttribute m = (MutableEntityAttribute)n;
			
			if(m.contains(m, b)) return true;
		}
		
		return false;
	}
	
	@Override
	public void addParent(MutableEntityAttribute attributeIn, double multiplier) {
		this.data$parents.put(attributeIn, multiplier);
	}
	
	@Override
	public void addChild(MutableEntityAttribute attributeIn, final double multiplier) {
		if(this.contains(this, attributeIn)) return;
		
		attributeIn.addParent(this, multiplier);
		this.data$children.put(attributeIn, multiplier);
	}
	
	@Override
	public void transferAttribute(String translationKey, double minValue, double maxValue, double defaultValue, StackingBehaviour stackingBehaviour) {
		this.data$translationKey = translationKey;
		this.data$minValue = minValue;
		this.data$maxValue = maxValue;
		this.data$fallbackValue = defaultValue;
		this.data$stackingBehaviour = stackingBehaviour;
	}
	
	@Override
	public void transferProperties(Map<String, String> properties) {
		if(properties == null) return;
		this.data$properties = properties;
	}
	
	@Override
	public void clear() {
		this.transferAttribute(this.translationKey, this.fallback, this.fallback, this.fallback, StackingBehaviour.FLAT);
		this.transferProperties(new HashMap<String, String>());
		this.data$parents.clear();
		this.data$children.clear();
	}
	
	@Override
	public double getDefaultValue() {
		return this.data$fallbackValue;
	}
	
	@Override
	public double getMinValue() {
		return this.data$minValue;
	}
	
	@Override
	public double getMaxValue() {
		return this.data$maxValue;
	}
	
	@Override
	public boolean isTracked() {
		return true;
	}
	
	@Override
	public double clamp(double value) {
		final MutableDouble mutable = new MutableDouble(value);
		
		EntityAttributeModifiedEvents.CLAMPED.invoker().onClamped((EntityAttribute)(Object)this, mutable);
		
		return MathHelper.clamp(mutable.getValue(), this.getMinValue(), this.getMaxValue());
	}
	
	@Override
	public double stack(double current, double adding) {
		return this.data$stackingBehaviour.result(current, adding, this.getMaxValue());
	}
	
	@Override
	public String getTranslationKey() {
		return this.data$translationKey;
	}
	
	@Override
	public Map<IEntityAttribute, Double> parentsMutable() {
		return this.data$parents;
	}
	
	@Override
	public Map<IEntityAttribute, Double> childrenMutable() {
		return this.data$children;
	}
	
	@Override
	public Map<IEntityAttribute, Double> parents() {
		return ImmutableMap.copyOf(this.data$parents);
	}
	
	@Override
	public Map<IEntityAttribute, Double> children() {
		return ImmutableMap.copyOf(this.data$children);
	}
	
	@Override
	public Collection<String> properties() {
		return ImmutableSet.copyOf(this.data$properties.keySet());
	}
	
	@Override
	public boolean hasProperty(final String property) {
		return this.data$properties.containsKey(property);
	}
	
	@Override
	public String getProperty(final String property) {
		return this.data$properties.getOrDefault(property, "");
	}
}
