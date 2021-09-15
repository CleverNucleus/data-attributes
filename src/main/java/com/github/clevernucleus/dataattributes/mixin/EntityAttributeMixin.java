package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.api.attribute.IAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.IAttributeFunction;
import com.github.clevernucleus.dataattributes.api.event.MathClampEvent;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableAttribute;
import com.github.clevernucleus.dataattributes.impl.json.AttributeFunctionJson;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.attribute.EntityAttribute;

@Mixin(EntityAttribute.class)
abstract class EntityAttributeMixin implements IAttribute, IMutableAttribute {
	
	@Final
	@Shadow
	private double fallback;
	
	@Final
	@Shadow
	private String translationKey;
	
	@Unique
	private double data$defaultValue, data$minValue, data$maxValue;
	
	@Unique
	private String data$translationKey;
	
	@Unique
	private Collection<AttributeFunctionJson> data$functions;
	
	@Unique
	private Map<String, Float> data$properties;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(String translationKey, double fallback, CallbackInfo info) {
		this.data$translationKey = translationKey;
		this.data$defaultValue = fallback;
		this.data$minValue = fallback;
		this.data$maxValue = fallback;
		this.data$functions = new HashSet<AttributeFunctionJson>();
		this.data$properties = new HashMap<String, Float>();
	}
	
	@Inject(method = "isTracked", at = @At("HEAD"), cancellable = true)
	private void tracked(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(true);
	}
	
	@Inject(method = "getDefaultValue", at = @At("HEAD"), cancellable = true)
	private void defaultValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.data$defaultValue);
	}
	
	@Inject(method = "getTranslationKey", at = @At("HEAD"), cancellable = true)
	private void translationKey(CallbackInfoReturnable<String> info) {
		info.setReturnValue(this.data$translationKey);
	}
	
	@Inject(method = "clamp", at = @At("HEAD"), cancellable = true)
	private void clamped(double value, CallbackInfoReturnable<Double> info) {
		EntityAttribute attribute = (EntityAttribute)(Object)this;
		final MutableDouble mutable = new MutableDouble(value);
		
		MathClampEvent.EVENT.invoker().onClamped(attribute, mutable);
		
		info.setReturnValue(mutable.getValue());
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
	public Collection<IAttributeFunction> functions() {
		return ImmutableSet.copyOf(this.data$functions);
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
	public float getProperty(final String property) {
		return this.data$properties.getOrDefault(property, 0.0F);
	}
	
	@Override
	public void setDefaultValue(final double defaultValue) {
		this.data$defaultValue = defaultValue;
	}
	
	@Override
	public void setMinValue(final double minValue) {
		this.data$minValue = minValue;
	}
	
	@Override
	public void setMaxValue(final double maxValue) {
		this.data$maxValue = maxValue;
	}
	
	@Override
	public void setTranslationKey(final String translationKey) {
		this.data$translationKey = translationKey;
	}
	
	@Override
	public void setFunctions(final Collection<AttributeFunctionJson> functions) {
		if(functions == null) return;
		this.data$functions = functions;
	}
	
	@Override
	public void setProperties(final Map<String, Float> properties) {
		if(properties == null) return;
		this.data$properties = properties;
	}
	
	@Override
	public void reset() {
		this.setDefaultValue(this.fallback);
		this.setMinValue(this.fallback);
		this.setMaxValue(this.fallback);
		this.setTranslationKey(this.translationKey);
		this.setFunctions(new HashSet<AttributeFunctionJson>());
		this.setProperties(new HashMap<String, Float>());
	}
}
