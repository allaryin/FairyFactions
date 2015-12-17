package fairies.proxy;

import java.util.List;

import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import fairies.FairyFactions;
import fairies.Version;
import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import fairies.event.FairyEventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommonProxy {
	
	protected FairyEventListener eventListener;
	protected FMLEventChannel eventChannel;

	public void preInit() {
		this.eventChannel =  NetworkRegistry.INSTANCE.newEventDrivenChannel( Version.CHANNEL );
	}

	public void initChannel(FairyEventListener listener) {
		this.eventListener = listener;
		this.eventChannel.register(this.eventListener);
	}

	public void initEntities() {
		EntityRegistry.registerGlobalEntityID( EntityFairy.class, "Fairy", EntityRegistry.findGlobalUniqueEntityId() );	
		EntityRegistry.registerModEntity( FairyEntityFishHook.class, "FairyFishhook", EntityRegistry.findGlobalUniqueEntityId(), FairyFactions.INSTANCE, 64, 4, true );
	}

	public void initGUI() {
		// TODO Auto-generated method stub
	}

	public void postInit() {
	}
	
	////////// packet handling

	public void sendChat( EntityPlayerMP player, String s ) {
		if ( player != null && !s.isEmpty() )
			player.playerNetServerHandler.sendPacket( new S02PacketChat( new ChatComponentText( s ) ) );
	}
	
	public void sendToClient(FMLProxyPacket packet, EntityPlayerMP player) {
		eventChannel.sendTo( packet, player );
	}
	public void sendToServer(FMLProxyPacket packet) {
		eventChannel.sendToServer( packet );
	}
	public void sendToAllPlayers(Packet packet) {
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for( EntityPlayerMP player : players ) {
			player.playerNetServerHandler.sendPacket(packet);
		}
	}

	public void sendFairyRename(String name) {
		// TODO Auto-generated method stub
	}
	
	// Packet that handles fairy mounting.
	public void sendFairyMount(final Entity entity1, final Entity entity2) {
		Entity rider = entity1;
		Entity vehicle = entity2;

		if (entity1.ridingEntity != null && entity1.ridingEntity == entity2) {
			vehicle = null;
		}

		S1BPacketEntityAttach packet = new S1BPacketEntityAttach(0, entity1, entity2);
		sendToAllPlayers(packet);

		/*
		 * TODO: deal with fishhook case :)
		if (!( entity1 instanceof FRY_EntityFishHook )) {
			entity1.mountEntity(entity2);
		}
		*/
	}
	
	// Packet that handles forced fairy despawning.
	public void sendFairyDespawn(Entity entity) {
		final int[] eid = new int[] { entity.getEntityId() };
		final S13PacketDestroyEntities packet = new S13PacketDestroyEntities(eid);
		sendToAllPlayers(packet);
		entity.setDead();
	}
	
	// Packet that handles sending text to specific players.
	@Deprecated
	public void sendDisband(EntityPlayerMP player, String s) {
		sendChat(player, s);
		/*
        if (player != null) {
        	final S02PacketChat packet = new S02PacketChat(new ChatComponentText(s));
            player.playerNetServerHandler.sendPacket(packet);
        }

        //Shouldn't enable this by default, could be spammy.
        //MinecraftServer.logger.info(s);
         */
    }

}
