package com.github.clevernucleus.dataattributes.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

/**
 * 
 * Provides a hook to the server, AFTER it has sent packets to the client and synced all Data Attributes' relevant data,
 * but BEFORE the player exists (i.e. during login).
 * 
 * @author CleverNucleus
 *
 */
public final class ServerSyncedEvent {
	
	/**
	 * Fired after all Data Attributes' syncing has completed, but before the player exists (i.e. during login).
	 * 
	 * <p>Exposes: </p>
	 * 
	 * <li> (final) MinecraftServer instance </li>
	 */
	public static final Event<ServerSyncedEvent.Synced> EVENT = EventFactory.createArrayBacked(ServerSyncedEvent.Synced.class, listeners -> server -> {
		for(Synced listener : listeners) {
			listener.onCompletion(server);
		}
	});
	
	@FunctionalInterface
	public interface Synced {
		
		/**
		 * 
		 * @param server
		 */
		void onCompletion(final MinecraftServer server);
	}
}
