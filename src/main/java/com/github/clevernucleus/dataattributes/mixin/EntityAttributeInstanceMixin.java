package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.api.attribute.AttributeBehaviour;
import com.github.clevernucleus.dataattributes.api.attribute.IAttribute;
import com.github.clevernucleus.dataattributes.api.attribute.IAttributeFunction;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeEvents;
import com.github.clevernucleus.dataattributes.impl.attribute.IAttributeInstance;
import com.github.clevernucleus.dataattributes.impl.attribute.IMutableContainer;
import com.github.clevernucleus.dataattributes.impl.attribute.MutableEntityAttributeModifier;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(EntityAttributeInstance.class)
abstract class EntityAttributeInstanceMixin implements IAttributeInstance {
	
	@Final
	@Shadow
	private EntityAttribute type;
	
	@Shadow
	@Final
	private Set<EntityAttributeModifier> persistentModifiers;
	
	@Shadow
	private double baseValue;
	
	@Unique
	private AttributeContainer data$container;
	
	private void applyModifier(EntityAttributeModifier modifier, BiConsumer<EntityAttributeInstance, EntityAttributeModifier> alt) {
		if(((IMutableContainer)this.data$container).isClient()) return;
		
		UUID uuid = modifier.getId();
		String name = modifier.getName();
		EntityAttributeModifier.Operation op = modifier.getOperation();
		IAttribute attribute = (IAttribute)this.type;
		double value = modifier.getValue();
		double current = this.data$container.getValue(this.type);
		double delta = 0.0D;
		
		if(op == EntityAttributeModifier.Operation.ADDITION) {
			delta = value;
		} else if(op == EntityAttributeModifier.Operation.MULTIPLY_BASE) {
			delta = this.baseValue * value;
		} else if(op == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
			delta = current - (current / (1.0D + value));
		}
		
		for(IAttributeFunction function : attribute.functions()) {
			Identifier identifier = function.attribute();
			EntityAttribute subAttribute = Registry.ATTRIBUTE.get(identifier);
			
			if(subAttribute == null || !this.data$container.hasAttribute(subAttribute)) continue;
			
			IAttribute subAttributeAccess = (IAttribute)subAttribute;
			AttributeBehaviour behaviour = function.behaviour();
			final double limit = subAttributeAccess.getMaxValue();
			final double multiplier = function.multiplier();
			final double subCurrent = this.data$container.getValue(subAttribute);
			final double result = behaviour.result(subCurrent, delta * multiplier, limit) - subCurrent;
			
			EntityAttributeModifier subModifier = new EntityAttributeModifier(uuid, name, result, EntityAttributeModifier.Operation.ADDITION);
			EntityAttributeInstance subInstance = this.data$container.getCustomInstance(subAttribute);
			
			if(subInstance == null) continue;
			
			alt.accept(subInstance, subModifier);
		}
	}
	
	private void onAddModifier(EntityAttributeModifier modifier, Consumer<EntityAttributeInstance> consumer) {
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		EntityAttributeInstanceInvoker invoker = (EntityAttributeInstanceInvoker)instance;
		
		if(instance.getModifiers().isEmpty()) {
			consumer.accept(instance);
		} else {
			Set<EntityAttributeModifier> modifiers = ImmutableSet.copyOf(instance.getModifiers());
			Collection<EntityAttributeModifier> persistent = ImmutableSet.copyOf(this.persistentModifiers);
			Collection<EntityAttributeModifier> addition = ImmutableSet.copyOf(invoker.invokeGetModifiersByOperation(EntityAttributeModifier.Operation.ADDITION));
			Collection<EntityAttributeModifier> multiplyBase = ImmutableSet.copyOf(invoker.invokeGetModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE));
			Collection<EntityAttributeModifier> multiplyTotal = ImmutableSet.copyOf(invoker.invokeGetModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
			
			for(EntityAttributeModifier mod : modifiers) {
				instance.removeModifier(mod);
			}
			
			if(modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
				consumer.accept(instance);
			}
			
			for(EntityAttributeModifier mod : addition) {
				if(persistent.contains(mod)) {
					this.addPersistentModifierToInstance(mod);
				} else {
					this.addTemporaryModifierToInstance(mod);
				}
			}
			
			if(modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE) {
				consumer.accept(instance);
			}
			
			for(EntityAttributeModifier mod : multiplyBase) {
				if(persistent.contains(mod)) {
					this.addPersistentModifierToInstance(mod);
				} else {
					this.addTemporaryModifierToInstance(mod);
				}
			}
			
			if(modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
				consumer.accept(instance);
			}
			
			for(EntityAttributeModifier mod : multiplyTotal) {
				if(persistent.contains(mod)) {
					this.addPersistentModifierToInstance(mod);
				} else {
					this.addTemporaryModifierToInstance(mod);
				}
			}
		}
	}
	
	@Inject(method = "addTemporaryModifier", at = @At("HEAD"), cancellable = true)
	private void onAddTemporaryModifier(EntityAttributeModifier modifier, CallbackInfo info) {
		this.onAddModifier(modifier, instance -> ((IAttributeInstance)instance).addTemporaryModifierToInstance(modifier));
		
		info.cancel();
	}
	
	@Inject(method = "addPersistentModifier", at = @At("HEAD"), cancellable = true)
	private void onAddPersistentModifier(EntityAttributeModifier modifier, CallbackInfo info) {
		this.onAddModifier(modifier, instance -> ((IAttributeInstance)instance).addPersistentModifierToInstance(modifier));
		
		info.cancel();
	}
	
	@Inject(method = "removeModifier(Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", at = @At("HEAD"))
	private void onRemoveModifierPre(EntityAttributeModifier modifier, CallbackInfo info) {
		EntityAttributeEvents.MODIFIER_REMOVED_PRE.invoker().onRemoved(((IMutableContainer)this.data$container).livingEntity(), this.type, modifier);
	}
	
	@Inject(method = "removeModifier(Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V", at = @At("TAIL"))
	private void onRemoveModifierPost(EntityAttributeModifier modifier, CallbackInfo info) {
		if(!((IMutableContainer)this.data$container).isClient()) {
			IAttribute attribute = (IAttribute)this.type;
			
			for(IAttributeFunction function : attribute.functions()) {
				Identifier identifier = function.attribute();
				EntityAttribute subAttribute = Registry.ATTRIBUTE.get(identifier);
				
				if(subAttribute == null || !this.data$container.hasAttribute(subAttribute)) continue;
				
				EntityAttributeInstance subInstance = this.data$container.getCustomInstance(subAttribute);
				
				if(subInstance == null) continue;
				
				EntityAttributeModifier subModifier = subInstance.getModifier(modifier.getId());
				
				if(subModifier == null) continue;
				
				subInstance.removeModifier(subModifier);
			}
			
			EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
			Set<EntityAttributeModifier> modifiers = ImmutableSet.copyOf(instance.getModifiers());
			Set<EntityAttributeModifier> persistent = ImmutableSet.copyOf(this.persistentModifiers);
			
			for(EntityAttributeModifier mod : modifiers) {
				instance.removeModifier(mod);
			}
			
			for(EntityAttributeModifier mod : modifiers) {
				if(persistent.contains(mod)) {
					instance.addPersistentModifier(mod);
				} else {
					instance.addTemporaryModifier(mod);
				}
			}
		}
		
		EntityAttributeEvents.MODIFIER_REMOVED_POST.invoker().onRemoved(((IMutableContainer)this.data$container).livingEntity(), this.type, modifier);
	}
	
	@Override
	public void addTemporaryModifierToInstance(EntityAttributeModifier modifierIn) {
		MutableEntityAttributeModifier mutableModifier = new MutableEntityAttributeModifier(modifierIn);
		
		EntityAttributeEvents.MODIFIER_ADDED_PRE.invoker().onAdded(((IMutableContainer)this.data$container).livingEntity(), this.type, mutableModifier);
		
		EntityAttributeModifier modifier = mutableModifier.getModifier();
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		((EntityAttributeInstanceInvoker)instance).invokeAddModifier(modifier);
		this.applyModifier(modifier, (subInstance, subModifier) -> ((IAttributeInstance)subInstance).addTemporaryModifierToInstance(subModifier));
		
		EntityAttributeEvents.MODIFIER_ADDED_POST.invoker().onAdded(((IMutableContainer)this.data$container).livingEntity(), this.type, modifier);
	}
	
	@Override
	public void addPersistentModifierToInstance(EntityAttributeModifier modifierIn) {
		MutableEntityAttributeModifier mutableModifier = new MutableEntityAttributeModifier(modifierIn);
		
		EntityAttributeEvents.MODIFIER_ADDED_PRE.invoker().onAdded(((IMutableContainer)this.data$container).livingEntity(), this.type, mutableModifier);
		
		EntityAttributeModifier modifier = mutableModifier.getModifier();
		EntityAttributeInstance instance = (EntityAttributeInstance)(Object)this;
		((EntityAttributeInstanceInvoker)instance).invokeAddModifier(modifier);
		this.persistentModifiers.add(modifier);
		this.applyModifier(modifier, (subInstance, subModifier) -> ((IAttributeInstance)subInstance).addPersistentModifierToInstance(subModifier));
		
		EntityAttributeEvents.MODIFIER_ADDED_POST.invoker().onAdded(((IMutableContainer)this.data$container).livingEntity(), this.type, modifier);
	}
	
	@Override
	public void setContainer(AttributeContainer container) {
		this.data$container = container;
	}
}
