package com.github.clevernucleus.dataattributes.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;


public final class ClientSyncedEvent {
	
	
	public static final Event<Synced> EVENT = EventFactory.createArrayBacked(Synced.class, callbacks -> server -> {
		for(Synced callback : callbacks) {
			callback.onCompleted(server);
		}
	});
	
	@FunctionalInterface
	public interface Synced {
		void onCompleted(final MinecraftClient server);
	}
}
