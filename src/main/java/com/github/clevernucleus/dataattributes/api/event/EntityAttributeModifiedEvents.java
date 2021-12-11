package com.github.clevernucleus.dataattributes.api.event;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;


public final class EntityAttributeModifiedEvents {
	
	
	public static final Event<Modified> MODIFIED = EventFactory.createArrayBacked(Modified.class, callbacks -> (attribute, livingEntity, modifier, prevValue, isWasAdded) -> {
		for(Modified callback : callbacks) {
			callback.onModified(attribute, livingEntity, modifier, prevValue, isWasAdded);
		}
	});
	
	
	public static final Event<Clamp> CLAMPED = EventFactory.createArrayBacked(Clamp.class, callbacks -> (attribute, value) -> {
		for(Clamp callback : callbacks) {
			callback.onClamped(attribute, value);
		}
	});
	
	@FunctionalInterface
	public interface Modified {
		void onModified(final EntityAttribute attribute, final @Nullable LivingEntity livingEntity, final EntityAttributeModifier modifier, final double prevValue, final boolean isWasAdded);
	}
	
	@FunctionalInterface
	public interface Clamp {
		void onClamped(final EntityAttribute attribute, final MutableDouble value);
	}
}
