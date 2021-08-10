package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.util.math.MathHelper;

@Mixin(ClampedEntityAttribute.class)
abstract class ClampedEntityAttributeMixin extends EntityAttributeMixin {
	
	@Final
	@Shadow
	private double minValue;
	
	@Final
	@Shadow
	private double maxValue;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(String translationKey, double fallback, double min, double max, CallbackInfo info) {
		this.setDefaultValue(fallback);
		this.setMinValue(min);
		this.setMaxValue(max);
		this.setTranslationKey(translationKey);
	}
	
	@Inject(method = "getMinValue", at = @At("HEAD"), cancellable = true)
	private void minValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(super.getMinValue());
	}
	
	@Inject(method = "getMaxValue", at = @At("HEAD"), cancellable = true)
	private void maxValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(super.getMaxValue());
	}
	
	@Inject(method = "clamp", at = @At("HEAD"), cancellable = true)
	private void clamped(double value, CallbackInfoReturnable<Double> info) {
		double result = MathHelper.clamp(value, this.getMinValue(), this.getMaxValue());
		
		info.setReturnValue(result);
	}
	
	@Override
	public void reset() {
		super.reset();
		this.setMinValue(this.minValue);
		this.setMaxValue(this.maxValue);
	}
}
