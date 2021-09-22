package com.github.clevernucleus.dataattributes;

import com.github.clevernucleus.dataattributes.api.API;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeEvents;
import com.github.clevernucleus.dataattributes.api.event.ServerSyncedEvent;
import com.github.clevernucleus.dataattributes.impl.LoaderJsonManager;
import com.github.clevernucleus.dataattributes.impl.attribute.AttributeObject;
import com.github.clevernucleus.dataattributes.impl.attribute.EntityTypeObject;
import com.github.clevernucleus.dataattributes.impl.json.AttributeJson;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public final class DataAttributes implements ModInitializer {
	public static final LoaderJsonManager MANAGER = new LoaderJsonManager();
	public static final Identifier SYNC = new Identifier(API.MODID, "sync");
	/** Manual; ugh, I know. */
	public static final String VERSION = "1.0.5";
	
	private static void loginQueryStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
		PacketByteBuf buf = PacketByteBufs.create();
		NbtCompound tag = new NbtCompound();
		
		NbtList overrides = new NbtList();
		NbtList attributes = new NbtList();
		NbtList entityTypes = new NbtList();
		
		for(Identifier identifier : DataAttributes.MANAGER.overrides.keySet()) {
			AttributeJson attributeJson = DataAttributes.MANAGER.overrides.get(identifier);
			NbtCompound entry = new NbtCompound();
			attributeJson.write(entry);
			entry.putString("Identifier", identifier.toString());
			overrides.add(entry);
		}
		
		for(Identifier identifier : DataAttributes.MANAGER.attributes.keySet()) {
			AttributeObject attributeObject = DataAttributes.MANAGER.attributes.get(identifier);
			NbtCompound entry = new NbtCompound();
			attributeObject.write(entry);
			entry.putString("Identifier", identifier.toString());
			attributes.add(entry);
		}
		
		for(Identifier identifier : DataAttributes.MANAGER.entityTypes.keySet()) {
			EntityTypeObject entityTypeObject = DataAttributes.MANAGER.entityTypes.get(identifier);
			NbtCompound entry = new NbtCompound();
			entityTypeObject.write(entry);
			entry.putString("Identifier", identifier.toString());
			entityTypes.add(entry);
		}
		
		tag.put("Overrides", overrides);
		tag.put("Attributes", attributes);
		tag.put("EntityTypes", entityTypes);
		
		buf.writeNbt(tag);
		sender.sendPacket(SYNC, buf);
	}
	
	private static void loginQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, LoginSynchronizer synchronizer, PacketSender responseSender) {
		if(understood) {
			String version = buf.readString();
			
			if(!version.equals(VERSION)) {
				handler.disconnect(new LiteralText("Disconnected: client has Data Attributes " + version + ", but the server requires Data Attributes " + VERSION + "."));
			} else {
				server.execute(() -> ServerSyncedEvent.EVENT.invoker().onCompletion(server));
			}
		} else {
			handler.disconnect(new LiteralText("Disconnected: server requires client to have Data Attributes version " + VERSION + "."));
		}
	}
	
	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> MANAGER.refresh());
		ServerLoginConnectionEvents.QUERY_START.register(DataAttributes::loginQueryStart);
		ServerLoginNetworking.registerGlobalReceiver(SYNC, DataAttributes::loginQueryResponse);
		
		EntityAttributeEvents.MODIFIER_REMOVED_POST.register((entity, attribute, modifier) -> {
			if(attribute != EntityAttributes.GENERIC_MAX_HEALTH) return;
			if(entity == null) return;
			if(entity.getHealth() > entity.getMaxHealth()) {
				entity.setHealth(entity.getMaxHealth());
			}
		});
	}
}
