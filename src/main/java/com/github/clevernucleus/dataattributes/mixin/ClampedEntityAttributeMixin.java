package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.api.attribute.StackingBehaviour;

import net.minecraft.entity.attribute.ClampedEntityAttribute;

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
		this.transferAttribute(translationKey, min, max, fallback, StackingBehaviour.FLAT);
	}
	
	@Inject(method = "getMinValue", at = @At("HEAD"), cancellable = true)
	private void onGetMinValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.minValue());
	}
	
	@Inject(method = "getMaxValue", at = @At("HEAD"), cancellable = true)
	private void onGetMaxValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.maxValue());
	}
	
	@Inject(method = "clamp", at = @At("HEAD"), cancellable = true)
	private void onClamp(double value, CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.data_clamp(value));
	}
	
	@Override
	public void clear() {
		super.clear();
		this.data_minValue = this.minValue;
		this.data_maxValue = this.maxValue;
	}
}
