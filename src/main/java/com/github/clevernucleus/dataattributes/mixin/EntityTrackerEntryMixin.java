package com.github.clevernucleus.dataattributes.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.server.network.EntityTrackerEntry;

@Mixin(EntityTrackerEntry.class)
abstract class EntityTrackerEntryMixin {
	
	@Final
	@Shadow
	private Entity entity;

	@Redirect(method = "syncEntityData",at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"))
	private void data_syncEntityData(Set<EntityAttributeInstance> set) {
		MutableAttributeContainer container = (MutableAttributeContainer)((LivingEntity)this.entity).getAttributes();
		container.clearTracked();
	}
}
