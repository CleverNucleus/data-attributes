package com.github.clevernucleus.dataattributes.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.mutable.MutableDefaultAttributeContainer;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(DefaultAttributeContainer.class)
abstract class DefaultAttributeContainerMixin implements MutableDefaultAttributeContainer {
	
	@Unique
	private Map<Identifier, EntityAttributeInstance> data_instances;
	
	@Final
	@Shadow
	private Map<EntityAttribute, EntityAttributeInstance> instances;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void data_init(Map<EntityAttribute, EntityAttributeInstance> instances, CallbackInfo info) {
		this.data_instances = new HashMap<Identifier, EntityAttributeInstance>();
		
		instances.forEach((attribute, instance) -> {
			Identifier key = Registry.ATTRIBUTE.getId(attribute);
			
			if(key != null) {
				this.data_instances.put(key, instance);
			}
		});
	}
	
	@Redirect(method = "require", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_require(Map<?, ?> instances, Object attribute) {
		EntityAttribute entityAttribute = (EntityAttribute)attribute;
		Identifier identifier = Registry.ATTRIBUTE.getId(entityAttribute);
		return this.data_instances.getOrDefault(identifier, this.instances.get(attribute));
	}
	
	@Redirect(method = "createOverride", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_createOverride(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_instances.getOrDefault(identifier, this.instances.get(attribute));
	}
	
	@Inject(method = "has", at = @At("HEAD"), cancellable = true)
	private void data_has(EntityAttribute type, CallbackInfoReturnable<Boolean> info) {
		Identifier identifier = Registry.ATTRIBUTE.getId(type);
		info.setReturnValue(this.data_instances.containsKey(identifier) || this.instances.containsKey(type));
	}
	
	@Redirect(method = "hasModifier", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_hasModifier(Map<?, ?> instances, Object type) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)type);
		return this.data_instances.getOrDefault(identifier, this.instances.get(type));
	}
	
	@Override
	public void copy(DefaultAttributeContainer.Builder builder) {
		for(EntityAttribute entityAttribute : this.instances.keySet()) {
			EntityAttributeInstance entityAttributeInstance = this.instances.get(entityAttribute);
			double value = entityAttributeInstance.getBaseValue();
			builder.add(entityAttribute, value);
		}
	}
}
