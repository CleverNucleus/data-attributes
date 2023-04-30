package com.github.clevernucleus.dataattributes.api;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * 
 * The core API access - provides access to the modid and safe static attribute instantiation.
 * @author CleverNucleus
 *
 */
public final class DataAttributesAPI {
	
	/**
	 * The modid for Data Attributes.
	 */
	public static final String MODID = "dataattributes";
	
	/**
	 * The entity instance for LivingEntity.class.
	 */
	public static final String ENTITY_INSTANCE_LIVING_ENTITY = "living_entity";

	/**
	 * The entity instance for MobEntity.class.
	 */
	public static final String ENTITY_INSTANCE_MOB_ENTITY = "mob_entity";

	/**
	 * The entity instance for PathAwareEntity.class.
	 */
	public static final String ENTITY_INSTANCE_PATH_AWARE_ENTITY = "path_aware_entity";

	/**
	 * The entity instance for HostileEntity.class.
	 */
	public static final String ENTITY_INSTANCE_HOSTILE_ENTITY = "hostile_entity";

	/**
	 * The entity instance for PassiveEntity.class.
	 */
	public static final String ENTITY_INSTANCE_PASSIVE_ENTITY = "passive_entity";

	/**
	 * The entity instance for AnimalEntity.class.
	 */
	public static final String ENTITY_INSTANCE_ANIMAL_ENTITY = "animal_entity";

	/**
	 * @param attributeKey Attribute registry key.
	 * @return A supplier getting the registered attribute assigned to the input key. 
	 * Uses a supplier because attributes added using json are null until datapacks are loaded/synced to the client,
	 * so static initialisation would not work. Using this you can safely access an attribute through a static reference.
	 */
	public static Supplier<EntityAttribute> getAttribute(final Identifier attributeKey) {
		return () -> Registry.ATTRIBUTE.get(attributeKey);
	}
	
	/**
	 * Allows for an Optional-like use of attributes that may or may not exist all the time. This is the correct way of getting and using
	 * values from attributes loaded by datapacks.
	 * @param <T>
	 * @param livingEntity
	 * @param entityAttribute
	 * @param fallback
	 * @param function
	 * @return If the input attribute is both registered to the game and present on the input entity, returns the returning value of the input function.
	 * Else returns the fallback input.
	 */
	public static <T> T ifPresent(final LivingEntity livingEntity, Supplier<EntityAttribute> entityAttribute, final T fallback, Function<Double, T> function) {
		AttributeContainer container = livingEntity.getAttributes();
		EntityAttribute attribute = entityAttribute.get();
		
		if(attribute != null && container.hasAttribute(attribute)) {
			return function.apply(container.getValue(attribute));
		}
		
		return fallback;
	}
}
