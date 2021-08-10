package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.DataAttributes;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableContainer;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	
	@Unique
	private AttributeContainer data$attributes;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructorInject(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo info) {
		this.data$attributes = new AttributeContainer(DataAttributes.MANAGER.containers.getOrDefault(entityType, DefaultAttributeRegistry.get(entityType)));
		
		LivingEntity living = (LivingEntity)(Object)this;
		
		living.setHealth(living.getMaxHealth());
		
		((IMutableContainer)this.data$attributes).setIsClient(world.isClient);
		((IMutableContainer)this.data$attributes).setLivingEntity((LivingEntity)(Object)this);
	}
	
	@Inject(method = "getAttributes", at = @At("RETURN"), cancellable = true)
	private void onAttributes(CallbackInfoReturnable<AttributeContainer> info) {
		if(this.data$attributes != null) {
			info.setReturnValue(this.data$attributes);
		}
	}
}
