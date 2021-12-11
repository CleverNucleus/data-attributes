package com.github.clevernucleus.dataattributes;

import org.jetbrains.annotations.Nullable;

import com.github.clevernucleus.dataattributes.api.DataAttributesAPI;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.api.event.ServerSyncedEvent;
import com.github.clevernucleus.dataattributes.impl.AttributeDataManager;
import com.github.clevernucleus.dataattributes.impl.AttributeWrapper;
import com.github.clevernucleus.dataattributes.impl.EntityTypeAttributes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking.LoginSynchronizer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class DataAttributes implements ModInitializer {
	public static final String VERSION = FabricLoader.getInstance().getModContainer(DataAttributesAPI.MODID).get().getMetadata().getVersion().getFriendlyString();
	public static final Identifier HANDSHAKE = new Identifier(DataAttributesAPI.MODID, "handshake");
	public static final AttributeDataManager MANAGER = new AttributeDataManager();
	
	private static void loginQueryStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
		PacketByteBuf buf = PacketByteBufs.create();
		NbtCompound tag = new NbtCompound();
		NbtList attributes = new NbtList();
		NbtList entityTypes = new NbtList();
		
		for(Identifier identifier : DataAttributes.MANAGER.attributes.keySet()) {
			AttributeWrapper attribute = DataAttributes.MANAGER.attributes.get(identifier);
			NbtCompound entry = new NbtCompound();
			attribute.writeToNbt(entry);
			entry.putString("Identifier", identifier.toString());
			attributes.add(entry);
		}
		
		for(Identifier identifier : DataAttributes.MANAGER.entityTypes.keySet()) {
			EntityTypeAttributes entityType = DataAttributes.MANAGER.entityTypes.get(identifier);
			NbtCompound entry = new NbtCompound();
			entityType.writeToNbt(entry);
			entry.putString("Identifier", identifier.toString());
			entityTypes.add(entry);
		}
		
		tag.put("Attributes", attributes);
		tag.put("EntityTypes", entityTypes);
		buf.writeNbt(tag);
		sender.sendPacket(HANDSHAKE, buf);
	}
	
	private static void loginQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, LoginSynchronizer synchronizer, PacketSender responseSender) {
		if(understood) {
			String version = buf.readString();
			
			if(!version.equals(VERSION)) {
				handler.disconnect(new LiteralText("Disconnected: client has Data Attributes " + version + ", but the server requires Data Attributes " + VERSION + "."));
			} else {
				server.execute(() -> ServerSyncedEvent.EVENT.invoker().onCompleted(server));
			}
		} else {
			handler.disconnect(new LiteralText("Disconnected: server requires client to have Data Attributes version " + VERSION + "."));
		}
	}
	
	private static void healthModified(final EntityAttribute attribute, final @Nullable LivingEntity livingEntity, final EntityAttributeModifier modifier, final double prevValue, final boolean isWasAdded) {
		if(livingEntity == null) return;
		if(livingEntity.world.isClient) return;
		if(attribute != EntityAttributes.GENERIC_MAX_HEALTH) return;
		
		float c0 = livingEntity.getHealth();
		float c1 = c0 * livingEntity.getMaxHealth() / (float)prevValue;
		
		livingEntity.setHealth(c1);
	}
	
	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> MANAGER.refresh());
		ServerLoginConnectionEvents.QUERY_START.register(DataAttributes::loginQueryStart);
		ServerLoginNetworking.registerGlobalReceiver(HANDSHAKE, DataAttributes::loginQueryResponse);
		EntityAttributeModifiedEvents.MODIFIED.register(DataAttributes::healthModified);
	}
}
