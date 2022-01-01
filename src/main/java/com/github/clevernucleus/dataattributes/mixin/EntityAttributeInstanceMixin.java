package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttributeInstance;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.api.util.VoidConsumer;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeContainer;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeInstance;
import com.github.clevernucleus.dataattributes.mutable.MutableAttributeModifier;
import com.github.clevernucleus.dataattributes.mutable.MutableEntityAttribute;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;

@Mixin(EntityAttributeInstance.class)
abstract class EntityAttributeInstanceMixin implements IEntityAttributeInstance, MutableAttributeInstance {
	
	@Unique
	private Optional<AttributeContainer> data_containerCallback = Optional.empty();
	
	@Final
	@Shadow
	private Map<UUID, EntityAttributeModifier> idToModifiers;
	
	@Final
	@Shadow
	private Set<EntityAttributeModifier> persistentModifiers;
	
	@Final
	@Shadow
	private EntityAttribute type;
	
	@Shadow
	private double baseValue;
	
	@Shadow
	protected void onUpdate() {}
	
	@Shadow
	private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
		return Collections.emptySet();
	}
	
	@Inject(method = "computeValue", at = @At("HEAD"), cancellable = true)
	private void onComputeValue(CallbackInfoReturnable<Double> info) {
		IEntityAttribute attribute = (IEntityAttribute)this.type;
		double k = 1.0D, v = 1.0D;
		
		if(this.baseValue > 0.0D) {
			k = attribute.stack(k, this.baseValue);
		} else {
			v = attribute.stack(v, this.baseValue);
		}
		
		for(EntityAttributeModifier modifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
			double value = modifier.getValue();
			
			if(value > 0.0D) {
				k = attribute.stack(k, value);
			} else {
				v = attribute.stack(v, value);
			}
		}
		
		if(this.data_containerCallback.isPresent()) {
			Map<IEntityAttribute, Double> parents = ((MutableEntityAttribute)this.type).parentsMutable();
			
			for(IEntityAttribute parent : parents.keySet()) {
				EntityAttribute dataAttribute = (EntityAttribute)parent;
				EntityAttributeInstance instance = this.data_containerCallback.get().getCustomInstance(dataAttribute);
				
				if(instance == null) continue;
				
				double mult = parents.get(parent);
				double value = mult * instance.getValue();
				
				if(value > 0.0D) {
					k = attribute.stack(k, value);
				} else {
					v = attribute.stack(v, value);
				}
			}
		}
		
		double d = attribute.sumStack(k, v);
		double e = d;
		
		for(EntityAttributeModifier modifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
			e += d * modifier.getValue();
		}
		
		for(EntityAttributeModifier modifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
			e *= 1.0D + modifier.getValue();
		}
		
		double r = this.type.clamp(e);
		
		info.setReturnValue(r);
	}
	
	@Inject(method = "addModifier", at = @At("HEAD"), cancellable = true)
	private void onAddModifier(EntityAttributeModifier modifier, CallbackInfo info) {
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		UUID key = modifier.getId();
		EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.get(key);
		
		if(entityAttributeModifier != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		} else {
			this.actionModifier(() -> {
				this.idToModifiers.put(key, modifier);
				instance.getModifiers(modifier.getOperation()).add(modifier);
			}, instance, modifier, true);
		}
		
		info.cancel();
	}
	
	@Inject(method = "removeModifier", at = @At("HEAD"), cancellable = true)
	private void onRemoveModifier(EntityAttributeModifier modifier, CallbackInfo info) {
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		
		this.actionModifier(() -> {
			instance.getModifiers(modifier.getOperation()).remove(modifier);
			this.idToModifiers.remove(modifier.getId());
			this.persistentModifiers.remove(modifier);
		}, instance, modifier, false);
		
		info.cancel();
	}
	
	@Override
	public void actionModifier(final VoidConsumer consumerIn, final EntityAttributeInstance instanceIn, final EntityAttributeModifier modifierIn, final boolean isWasAdded) {
		MutableEntityAttribute parent = (MutableEntityAttribute)this.type;
		
		this.data_containerCallback.ifPresent(container -> {
			for(IEntityAttribute child : parent.childrenMutable().keySet()) {
				EntityAttribute attribute = (EntityAttribute)child;
				EntityAttributeInstance instance = container.getCustomInstance(attribute);
				
				if(instance != null) {
					instance.getValue();
				}
			}
			
			final double value = instanceIn.getValue();
			
			consumerIn.accept();
			
			this.onUpdate();
			
			LivingEntity livingEntity = ((MutableAttributeContainer)container).getLivingEntity();
			
			EntityAttributeModifiedEvents.MODIFIED.invoker().onModified(this.type, livingEntity, modifierIn, value, isWasAdded);
			
			for(IEntityAttribute child : parent.childrenMutable().keySet()) {
				EntityAttribute attribute = (EntityAttribute)child;
				EntityAttributeInstance instance = container.getCustomInstance(attribute);
				
				if(instance != null) {
					((MutableAttributeInstance)instance).actionModifier(() -> {}, instance, modifierIn, isWasAdded);
				}
			}
		});
	}
	
	@Override
	public void updateModifier(final UUID uuid, final double value) {
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		EntityAttributeModifier modifier = instance.getModifier(uuid);
		
		if(modifier == null) return;
		
		this.actionModifier(() -> {
			((MutableAttributeModifier)modifier).updateValue(value);
		}, instance, modifier, false);
	}
	
	@Override
	public void setContainerCallback(AttributeContainer container) {
		this.data_containerCallback = Optional.ofNullable(container);
	}
	
	@Override
	public void update() {
		this.onUpdate();
	}
}
