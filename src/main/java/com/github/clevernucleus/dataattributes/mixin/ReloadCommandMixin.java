package com.github.clevernucleus.dataattributes.mixin;

import java.util.Collection;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.dataattributes.DataAttributes;
import com.github.clevernucleus.dataattributes.mutable.MutableIntFlag;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.level.ServerWorldProperties;

@Mixin(ReloadCommand.class)
abstract class ReloadCommandMixin {
	
	@Inject(method = "tryReloadDataPacks", at = @At("TAIL"))
	private static void data_tryReloadDataPacks(Collection<String> dataPacks, ServerCommandSource source, CallbackInfo ci) {
		MinecraftServer server = source.getServer();
		ServerWorldProperties serverWorldProperties = server.getSaveProperties().getMainWorldProperties();
		
		if(serverWorldProperties instanceof MutableIntFlag) {
			MutableIntFlag mutableUpdateFlag = (MutableIntFlag)serverWorldProperties;
			final int updateFlag = mutableUpdateFlag.getUpdateFlag();
			int updateFlag2 = updateFlag;
			
			while(updateFlag2 == updateFlag) {
				updateFlag2 = (new Random()).nextInt();
			}
			
			mutableUpdateFlag.setUpdateFlag(updateFlag2);
			
			PacketByteBuf buf = PacketByteBufs.create();
			final byte[] bytes = DataAttributes.MANAGER.getCurrentData();
			buf.writeByteArray(bytes);
			buf.writeInt(mutableUpdateFlag.getUpdateFlag());
			PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, DataAttributes.RELOAD, buf));
		}
	}
}
