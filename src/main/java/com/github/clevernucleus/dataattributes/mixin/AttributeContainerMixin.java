package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeInstance;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;

@Mixin(AttributeContainer.class)
abstract class AttributeContainerMixin implements MutableAttributeContainer {
	
	@Unique
	private LivingEntity data$livingEntity;
	
	@Inject(method = "getCustomInstance", at = @At("RETURN"), cancellable = true)
	private void onGetCustomInstance(CallbackInfoReturnable<EntityAttributeInstance> info) {
		EntityAttributeInstance result = info.getReturnValue();
		
		if(result != null) {
			((MutableAttributeInstance)result).setContainerCallback((AttributeContainer)(Object)this);
		}
		
		info.setReturnValue(result);
	}
	
	@Override
	public void setLivingEntity(final LivingEntity livingEntity) {
		this.data$livingEntity = livingEntity;
	}
	
	@Override
	public LivingEntity getLivingEntity() {
		return this.data$livingEntity;
	}
}
