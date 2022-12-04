package com.github.clevernucleus.dataattributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
	
	@Shadow
	@Final
	private PlayerAbilities abilities;
	
	@Inject(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeInstance(Lnet/minecraft/entity/attribute/EntityAttribute;)Lnet/minecraft/entity/attribute/EntityAttributeInstance;"))
	private void data_readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		this.abilities.setWalkSpeed((float)((PlayerEntity)(Object)this).getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
	}
}
