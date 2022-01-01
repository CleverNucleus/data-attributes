package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.DataAttributes;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.world.World;

@Mixin(value = LivingEntity.class, priority = 999)
abstract class LivingEntityMixin {
	
	@Unique
	private AttributeContainer data_attributes;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo info) {
		this.data_attributes = new AttributeContainer(DataAttributes.MANAGER.containers.getOrDefault(entityType, DefaultAttributeRegistry.get(entityType)));
		
		LivingEntity livingEntity = (LivingEntity)(Object)this;
		livingEntity.setHealth(livingEntity.getMaxHealth());
		((MutableAttributeContainer)this.data_attributes).setLivingEntity(livingEntity);
	}
	
	@Inject(method = "getAttributes", at = @At("RETURN"), cancellable = true)
	private void attributes(CallbackInfoReturnable<AttributeContainer> info) {
		if(this.data_attributes != null) {
			info.setReturnValue(this.data_attributes);
		}
	}
}
