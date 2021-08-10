package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.api.attribute.IAttribute;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableAttribute;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.util.math.MathHelper;

@Mixin(ClampedEntityAttribute.class)
public abstract class ClampedEntityAttributeMixin {
	
	@Unique
	private double data$minValue, data$maxValue;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(String translationKey, double fallback, double min, double max, CallbackInfo info) {
		this.data$minValue = min;
		this.data$maxValue = max;
		
		IMutableAttribute attribute = (IMutableAttribute)(Object)this;
		attribute.setDefaultValue(fallback);
		attribute.setMinValue(min);
		attribute.setMaxValue(max);
		attribute.setTranslationKey(translationKey);
	}
	
	@Inject(method = "getMinValue", at = @At("HEAD"), cancellable = true)
	private void minValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.data$minValue);
	}
	
	@Inject(method = "getMaxValue", at = @At("HEAD"), cancellable = true)
	private void maxValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.data$maxValue);
	}
	
	@Inject(method = "clamp", at = @At("HEAD"), cancellable = true)
	private void clamped(double value, CallbackInfoReturnable<Double> info) {
		IAttribute attribute = (IAttribute)(Object)this;
		double result = MathHelper.clamp(value, attribute.getMinValue(), attribute.getMaxValue());
		
		info.setReturnValue(result);
	}
}
