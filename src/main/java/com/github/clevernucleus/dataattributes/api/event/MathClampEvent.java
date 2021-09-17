package com.github.clevernucleus.dataattributes.api.event;

import org.apache.commons.lang3.mutable.MutableDouble;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.attribute.EntityAttribute;

/**
 * Provides hooks into entity attribute value clamping.
 * 
 * @author CleverNucleus
 *
 */
public final class MathClampEvent {
	
	/**
	 * Fired on {@link EntityAttribute#clamp(double)}
	 * and on {@link net.minecraft.entity.attribute.ClampedEntityAttribute#clamp(double)}, 
	 * but before {@link net.minecraft.util.math.MathHelper#clamp(double, double, double)} is called.
	 * 
	 * <p>Exposes:</p>
	 * 
	 * <li> (final) EntityAttribute instance of the attribute. </li>
	 * <li> (final) MutableDouble the incoming value from the original clamp method; mutable so that it can be modified. </li>
	 */
	public static final Event<MathClampEvent.Clamp> EVENT = EventFactory.createArrayBacked(MathClampEvent.Clamp.class, listeners -> (attribute, value) -> {
		for(Clamp listener : listeners) {
			listener.onClamped(attribute, value);
		}
	});
	
	@FunctionalInterface
	public interface Clamp {
		
		/**
		 * 
		 * @param attribute
		 * @param value
		 */
		void onClamped(final EntityAttribute attribute, final MutableDouble value);
	}
}
