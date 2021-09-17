package com.github.clevernucleus.dataattributes.api.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

/**
 * 
 * Provides a hook to the client, AFTER it has received serverside packets and synced all Data Attributes' relevant data,
 * but BEFORE the player exists (i.e. during login).
 * 
 * @author CleverNucleus
 *
 */
@Environment(EnvType.CLIENT)
public final class ClientSyncedEvent {
	
	/**
	 * Fired after all Data Attributes' syncing has completed, but before the player exists (i.e. during login).
	 * 
	 * <p>Exposes: </p>
	 * 
	 * <li> (final) MinecraftClient instance </li>
	 */
	public static final Event<ClientSyncedEvent.Synced> EVENT = EventFactory.createArrayBacked(ClientSyncedEvent.Synced.class, listeners -> client -> {
		for(Synced listener : listeners) {
			listener.onCompletion(client);
		}
	});
	
	@FunctionalInterface
	public interface Synced {
		
		/**
		 * 
		 * @param client
		 */
		void onCompletion(final MinecraftClient client);
	}
}
