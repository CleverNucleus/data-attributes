package com.github.clevernucleus.dataattributes.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.impl.attribute.IAttributeInstance;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableContainer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;

@Mixin(AttributeContainer.class)
abstract class AttributeContainerMixin implements IMutableContainer {
	
	@Unique
	private boolean data$isClient;
	
	@Unique
	private LivingEntity data$livingEntity;
	
	@Inject(method = "getCustomInstance", at = @At("RETURN"), cancellable = true)
	private void onGetCustomInstance(CallbackInfoReturnable<EntityAttributeInstance> info) {
		EntityAttributeInstance result = info.getReturnValue();
		
		if(result != null) {
			((IAttributeInstance)result).setContainer((AttributeContainer)(Object)this);
		}
		
		info.setReturnValue(result);
	}
	
	@Override
	public void setIsClient(boolean isClient) {
		this.data$isClient = isClient;
	}
	
	@Override
	public boolean isClient() {
		return this.data$isClient;
	}
	
	@Override
	public void setLivingEntity(LivingEntity livingEntity) {
		this.data$livingEntity = livingEntity;
	}
	
	@Override
	public @Nullable LivingEntity livingEntity() {
		return this.data$livingEntity;
	}
}
