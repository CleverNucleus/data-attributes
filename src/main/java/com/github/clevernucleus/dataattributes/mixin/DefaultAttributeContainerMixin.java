package com.github.clevernucleus.dataattributes.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.github.clevernucleus.dataattributes.mutable.MutableDefaultAttributeContainer;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;

@Mixin(DefaultAttributeContainer.class)
abstract class DefaultAttributeContainerMixin implements MutableDefaultAttributeContainer {
	
	@Final
	@Shadow
	private Map<EntityAttribute, EntityAttributeInstance> instances;
	
	@Override
	public void build(DefaultAttributeContainer.Builder builder) {
		for(EntityAttribute attribute : this.instances.keySet()) {
			EntityAttributeInstance instance = this.instances.get(attribute);
			double value = instance.getBaseValue();
			
			builder.add(attribute, value);
		}
	}
}
