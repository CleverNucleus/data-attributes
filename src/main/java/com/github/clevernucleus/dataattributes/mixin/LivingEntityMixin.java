package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.DataAttributes;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.world.World;

@Mixin(value = LivingEntity.class, priority = 999)
abstract class LivingEntityMixin {
	
	@Mutable
	@Shadow
	private AttributeContainer attributes;
	
	@Unique
	private int data_updateFlag;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void data_init(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
		LivingEntity livingEntity = (LivingEntity)(Object)this;
		this.attributes = DataAttributes.MANAGER.getContainer(entityType, livingEntity);
		this.data_updateFlag = DataAttributes.MANAGER.getUpdateFlag();
		livingEntity.setHealth(livingEntity.getMaxHealth());
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickActiveItemStack()V"))
	private void data_tick(CallbackInfo ci) {
		LivingEntity livingEntity = (LivingEntity)(Object)this;
		final int updateFlag = DataAttributes.MANAGER.getUpdateFlag();
		
		if(this.data_updateFlag != updateFlag) {
			AttributeContainer container = livingEntity.getAttributes();
			
			@SuppressWarnings("unchecked")
			AttributeContainer container2 = DataAttributes.MANAGER.getContainer((EntityType<? extends LivingEntity>)livingEntity.getType(), livingEntity);
			container2.setFrom(container);
			this.attributes = container2;
			this.data_updateFlag = updateFlag;
			
			DataAttributes.refreshAttributes(livingEntity);
		}
	}
}
