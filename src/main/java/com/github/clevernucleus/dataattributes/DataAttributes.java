package com.github.clevernucleus.dataattributes;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import com.github.clevernucleus.dataattributes.api.DataAttributesAPI;
import com.github.clevernucleus.dataattributes.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.dataattributes.api.util.Maths;
import com.github.clevernucleus.dataattributes.impl.AttributeManager;

import net.fabricmc.api.ModInitializer;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class DataAttributes implements ModInitializer {
	public static final Identifier HANDSHAKE = new Identifier(DataAttributesAPI.MODID, "handshake");
	public static final Identifier RELOAD = new Identifier(DataAttributesAPI.MODID, "reload");
	public static final AttributeManager MANAGER = new AttributeManager();
	protected static String version = "";
	protected static byte[] semVer;
	private static final byte VER_SIZE = 3;
	
	private static void loginQueryStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
		PacketByteBuf buf = PacketByteBufs.create();
		NbtCompound tag = new NbtCompound();
		DataAttributes.MANAGER.toNbt(tag);
		buf.writeNbt(tag);
		sender.sendPacket(HANDSHAKE, buf);
	}
	
	private static void loginQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, LoginSynchronizer synchronizer, PacketSender responseSender) {
		if(understood) {
			byte[] verClient = buf.readByteArray();
			
			if(verClient[0] != DataAttributes.semVer[0] || verClient[1] != DataAttributes.semVer[1]) {
				handler.disconnect(new LiteralText("Disconnected: version mismatch. Client has version " + verClient + ". Server has version " + DataAttributes.version + "."));
			}
		} else {
			handler.disconnect(new LiteralText("Disconnected: network communication issue."));
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
		version = FabricLoader.getInstance().getModContainer(DataAttributesAPI.MODID).get().getMetadata().getVersion().getFriendlyString();
		String[] versionArray = Arrays.copyOf(version.split("\\."), VER_SIZE);
		semVer = new byte[Math.max(versionArray.length, VER_SIZE)];
		
		for(int i = 0; i < semVer.length; i++) {
			semVer[i] = (byte)Maths.parseInt(versionArray[i]);
		}
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MANAGER);
		ServerLoginConnectionEvents.QUERY_START.register(DataAttributes::loginQueryStart);
		ServerLoginNetworking.registerGlobalReceiver(HANDSHAKE, DataAttributes::loginQueryResponse);
		EntityAttributeModifiedEvents.MODIFIED.register(DataAttributes::healthModified);
	}
}
