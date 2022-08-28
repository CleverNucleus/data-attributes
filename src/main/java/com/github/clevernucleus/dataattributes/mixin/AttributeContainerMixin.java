package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
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

import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeInstance;
import com.google.common.collect.Multimap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(AttributeContainer.class)
abstract class AttributeContainerMixin implements MutableAttributeContainer {
	
	@Unique
	private Map<Identifier, EntityAttributeInstance> data_custom = new HashMap<Identifier, EntityAttributeInstance>();
	
	@Unique
	private LivingEntity data_livingEntity;
	
	@Final
	@Shadow
	private DefaultAttributeContainer fallback;
	
	@Shadow
	private void updateTrackedStatus(EntityAttributeInstance instance) {}
	
	@Redirect(method = "getAttributesToSend", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
	private Collection<?> data_getAttributesToSend(Map<?, ?> instances) {
		return this.data_custom.values();
	}
	
	@Inject(method = "getCustomInstance", at = @At("HEAD"), cancellable = true)
	private void data_getCustomInstance(EntityAttribute attribute2, CallbackInfoReturnable<EntityAttributeInstance> info) {
		Identifier identifier = Registry.ATTRIBUTE.getId(attribute2);
		
		if(identifier != null) {
			EntityAttributeInstance entityAttributeInstance = this.data_custom.computeIfAbsent(identifier, id -> this.fallback.createOverride(this::updateTrackedStatus, attribute2));
			
			if(entityAttributeInstance != null) {
				MutableAttributeInstance mutable = (MutableAttributeInstance)entityAttributeInstance;
				mutable.setContainerCallback((AttributeContainer)(Object)this);
				
				if(mutable.getId() == null) {
					mutable.updateId(identifier);
				}
			}
			
			info.setReturnValue(entityAttributeInstance);
		} else {
			info.setReturnValue((EntityAttributeInstance)null);
		}
	}
	
	@Redirect(method = "hasAttribute", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_hasAttribute(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_custom.get(identifier);
	}
	
	@Redirect(method = "hasModifierForAttribute", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_hasModifierForAttribute(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_custom.get(identifier);
	}
	
	@Redirect(method = "getValue", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_getValue(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_custom.get(identifier);
	}
	
	@Redirect(method = "getBaseValue", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_getBaseValue(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_custom.get(identifier);
	}
	
	@Redirect(method = "getModifierValue", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object data_getModifierValue(Map<?, ?> instances, Object attribute) {
		Identifier identifier = Registry.ATTRIBUTE.getId((EntityAttribute)attribute);
		return this.data_custom.get(identifier);
	}
	
	@Inject(method = "removeModifiers", at = @At("HEAD"), cancellable = true)
	private void data_removeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers, CallbackInfo info) {
		attributeModifiers.asMap().forEach((attribute, collection) -> {
			Identifier identifier = Registry.ATTRIBUTE.getId(attribute);
            EntityAttributeInstance entityAttributeInstance = this.data_custom.get(identifier);
            
            if(entityAttributeInstance != null) {
            	collection.forEach(entityAttributeInstance::removeModifier);
            }
        });
		
		info.cancel();
	}
	
	@Inject(method = "setFrom", at = @At("HEAD"), cancellable = true)
	private void data_setFrom(AttributeContainer other, CallbackInfo info) {
		AttributeContainer container = (AttributeContainer)(Object)this;
		
		((MutableAttributeContainer)other).custom().values().forEach(attributeInstance -> {
			EntityAttribute entityAttribute = attributeInstance.getAttribute();
			EntityAttributeInstance entityAttributeInstance = container.getCustomInstance(entityAttribute);
			
			if(entityAttributeInstance != null) {
				entityAttributeInstance.setFrom(attributeInstance);
				final double value = entityAttributeInstance.getValue();
				EntityAttributeModifiedEvents.MODIFIED.invoker().onModified(entityAttribute, this.data_livingEntity, null, value, false);
			}
		});
		
		info.cancel();
	}
	
	@Redirect(method = "toNbt", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
	private Collection<?> data_toNbt(Map<?, ?> instances) {
		return this.data_custom.values();
	}
	
	@Override
	public Map<Identifier, EntityAttributeInstance> custom() {
		return this.data_custom;
	}
	
	@Override
	public LivingEntity getLivingEntity() {
		return this.data_livingEntity;
	}
	
	@Override
	public void setLivingEntity(final LivingEntity livingEntity) {
		this.data_livingEntity = livingEntity;
	}
	
	@Override
	public void refresh() {
		for(EntityAttributeInstance instance : this.data_custom.values()) {
			((MutableAttributeInstance)instance).refresh();
		}
	}
}
