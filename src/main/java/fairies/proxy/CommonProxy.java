package fairies.proxy;

import java.util.List;

import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.EntityRegistry;
import fairies.FairyFactions;
import fairies.Version;
import fairies.entity.EntityFairy;
import fairies.entity.FairyEntityFishHook;
import fairies.event.FairyEventListener;
import fairies.event.PacketSetFairyName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
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
		int entityID = 0;
		registerEntity( entityID++, EntityFairy.class, "Fairy", 0xea8fde, 0x8658bf );
		registerEntity( entityID++, FairyEntityFishHook.class, "FairyFishhook" );
	}
	
	private void registerEntity( int entityID, Class<?extends Entity> entityClass, String entityName ) {
		EntityRegistry.registerModEntity( entityClass, entityName, entityID, FairyFactions.INSTANCE, 64, 4, true);
	}
	private void registerEntity( int entityID, Class<?extends Entity> entityClass, String entityName, int backgroundEggColor, int foregroundEggColor ) {
		registerEntity( entityID, entityClass, entityName );
		
		// Thanks to the Metallurgy team for the clean implementation of this in Atum that I am adapting.
		int i = 120;
		do {
			++i;
		} while( EntityList.getStringFromID(i) != null );
		final Integer eggID = Integer.valueOf(i);
			
		EntityList.IDtoClassMapping.put(eggID, entityClass);
		EntityList.entityEggs.put(eggID, new EntityList.EntityEggInfo(eggID, backgroundEggColor, foregroundEggColor));
	}

	public void initGUI() {
		// should only ever be implemented on client
	}
	
	public void openRenameGUI(EntityFairy fairy) {
		// should only ever be implemented on client
	}

	public void postInit() {
	}
	
	public EntityPlayer getCurrentPlayer() {
		return null; 
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

	public void sendFairyRename(final EntityFairy fairy, final String name) {
		final PacketSetFairyName packet = new PacketSetFairyName( fairy, name );
		sendToServer( packet );
	}
	
	// Packet that handles fairy mounting.
	public void sendFairyMount(final Entity rider, final Entity vehicle) {
		final Entity newVehicle;
		if (rider.ridingEntity != null && rider.ridingEntity == vehicle) {
			newVehicle = null;
		} else {
			newVehicle = vehicle;
		}
			
		final S1BPacketEntityAttach packet = new S1BPacketEntityAttach(0, rider, newVehicle);
		sendToAllPlayers(packet);

		if (!(rider instanceof FairyEntityFishHook)) {
			rider.mountEntity(newVehicle);
		}
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
