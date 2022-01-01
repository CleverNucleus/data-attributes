package com.github.clevernucleus.dataattributes.mixin;

import java.util.UUID;
import java.util.function.Supplier;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.mutable.MutableAttributeModifier;

import net.minecraft.entity.attribute.EntityAttributeModifier;

@Mixin(EntityAttributeModifier.class)
abstract class EntityAttributeModifierMixin implements MutableAttributeModifier {
	
	@Unique
	private double data_value;
	
	@Inject(method = "<init>(Ljava/util/UUID;Ljava/util/function/Supplier;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V", at = @At("TAIL"))
	private void init(UUID uuid, Supplier<String> nameGetter, double value, EntityAttributeModifier.Operation operation, CallbackInfo info) {
		this.data_value = value;
	}
	
	@Inject(method = "getValue", at = @At("HEAD"), cancellable = true)
	private void onGetValue(CallbackInfoReturnable<Double> info) {
		info.setReturnValue(this.data_value);
	}
	
	@Redirect(method = "toString", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;value:D", opcode = Opcodes.GETFIELD))
	private double onToString(EntityAttributeModifier modifier) {
		return this.data_value;
	}
	
	@Redirect(method = "toNbt", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;value:D", opcode = Opcodes.GETFIELD))
	private double onToNbt(EntityAttributeModifier modifier) {
		return this.data_value;
	}
	
	@Override
	public void updateValue(double value) {
		this.data_value = value;
	}
}
