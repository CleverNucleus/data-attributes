package com.github.clevernucleus.dataattributes.api.event;

import org.jetbrains.annotations.Nullable;

import com.github.clevernucleus.dataattributes.api.attribute.IEntityAttributeModifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

/**
 * Provides hooks into entity attribute modifier events. 
 * 
 * @author CleverNucleus
 *
 */
public final class EntityAttributeEvents {
	
	/**
	 * Fired before an EntityAttributeModifier is added to an EntityAttributeInstance.
	 * 
	 * <p>Exposes:</p>
	 * <li> (final) LivingEntity instance (can be null, checks required) of the living entity that the AttributeContainer belongs to.</li>
	 * <li> (final) EntityAttribute instance of the attribute that the modifier is being applied to.</li>
	 * <li> (final) IEntityAttributeModifier instance; allows the modifier's value to be changed (mutable). </li>
	 * 
	 * <p>Fires on both the client and server, but the number of times fired for a given modifier is unreliable - 
	 * Dev's should utilise idempotency design principles in their code.</p>
	 */
	public static final Event<EntityAttributeEvents.AddedPre> MODIFIER_ADDED_PRE = EventFactory.createArrayBacked(EntityAttributeEvents.AddedPre.class, listeners -> (entity, attribute, modifier) -> {
		for(AddedPre listener : listeners) {
			listener.onAdded(entity, attribute, modifier);
		}
	});
	
	/**
	 * Fired after an EntityAttributeModifier has been added to an EntityAttributeInstance.
	 * 
	 * <p>Exposes:</p>
	 * <li> (final) LivingEntity instance (can be null, checks required) of the living entity that the AttributeContainer belongs to.</li>
	 * <li> (final) EntityAttribute instance of the attribute that the modifier was applied to.</li>
	 * <li> (final) EntityAttributeModifier the modifier that was applied.</li>
	 * 
	 * <p>Fires on both the client and server, but the number of times fired for a given modifier is unreliable - 
	 * Dev's should utilise idempotency design principles in their code.</p>
	 */
	public static final Event<EntityAttributeEvents.AddedPost> MODIFIER_ADDED_POST = EventFactory.createArrayBacked(EntityAttributeEvents.AddedPost.class, listeners -> (entity, attribute, modifier) -> {
		for(AddedPost listener : listeners) {
			listener.onAdded(entity, attribute, modifier);
		}
	});
	
	/**
	 * Fired before an EntityAttributeModifier is removed from an EntityAttributeInstance.
	 * 
	 * <p>Exposes:</p>
	 * <li> (final) LivingEntity instance (can be null, checks required) of the living entity that the AttributeContainer belongs to.</li>
	 * <li> (final) EntityAttribute instance of the attribute that the modifier is applied to.</li>
	 * <li> (final) EntityAttributeModifier the modifier that is applied.</li>
	 * 
	 * <p>Fires on both the client and server, but the number of times fired for a given modifier is unreliable - 
	 * Dev's should utilise idempotency design principles in their code.</p>
	 */
	public static final Event<EntityAttributeEvents.RemovedPre> MODIFIER_REMOVED_PRE = EventFactory.createArrayBacked(EntityAttributeEvents.RemovedPre.class, listeners -> (entity, attribute, modifier) -> {
		for(RemovedPre listener : listeners) {
			listener.onRemoved(entity, attribute, modifier);
		}
	});
	
	/**
	 * Fired after an EntityAttributeModifier is removed from an EntityAttributeInstance.
	 * 
	 * <p>Exposes:</p>
	 * <li> (final) LivingEntity instance (can be null, checks required) of the living entity that the AttributeContainer belongs to.</li>
	 * <li> (final) EntityAttribute instance of the attribute that the modifier was applied to.</li>
	 * <li> (final) EntityAttributeModifier the modifier that was applied.</li>
	 * 
	 * <p>Fires on both the client and server, but the number of times fired for a given modifier is unreliable - 
	 * Dev's should utilise idempotency design principles in their code.</p>
	 */
	public static final Event<EntityAttributeEvents.RemovedPost> MODIFIER_REMOVED_POST = EventFactory.createArrayBacked(EntityAttributeEvents.RemovedPost.class, listeners -> (entity, attribute, modifier) -> {
		for(RemovedPost listener : listeners) {
			listener.onRemoved(entity, attribute, modifier);
		}
	});
	
	@FunctionalInterface
	public interface AddedPre {
		
		void onAdded(@Nullable final LivingEntity entity, final EntityAttribute attribute, final IEntityAttributeModifier modifier);
	}
	
	@FunctionalInterface
	public interface AddedPost {
		
		void onAdded(@Nullable final LivingEntity entity, final EntityAttribute attribute, final EntityAttributeModifier modifier);
	}
	
	@FunctionalInterface
	public interface RemovedPre {
		
		void onRemoved(@Nullable final LivingEntity entity, final EntityAttribute attribute, final EntityAttributeModifier modifier);
	}
	
	@FunctionalInterface
	public interface RemovedPost {
		
		void onRemoved(@Nullable final LivingEntity entity, final EntityAttribute attribute, final EntityAttributeModifier modifier);
	}
}
