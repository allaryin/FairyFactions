package net.minecraft.src;

import net.minecraft.server.MinecraftServer;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import net.minecraft.src.forge.*;

public class mod_FairyMod extends NetworkMod implements IConnectionHandler, IPacketHandler
{
    public String getVersion()
    {
        return "v1.2.5_forge";
    }

    public mod_FairyMod()
    {
        ModLoader.registerEntityID(FRY_EntityFairy.class, "Fairy", 65);

        //ModLoader.registerEntityID(FRY_EntityFishHook.class, "FairyFishHook", 64);
        try
        {
            if (!MinecraftForge.registerEntity(FRY_EntityFishHook.class, this, 64, 32, 4, true))
            {
                throw(new Exception());
            }
        }
        catch (Exception e)
        {
            ModLoader.getLogger().fine("Couldn't register that ID.");
            e.printStackTrace();
        }

        // ModLoader.AddSpawn(FRY_EntityFairy.class, rarity, 4, 4, EnumCreatureType.creature);
        //DrZhark's Custom Spawner Setup
        ModLoader.setInGameHook(this, true, false);
        fairySpawner = new FRY_Spawner();
        int maxNum = 18;
        int freqNum = 8;
        fairySpawner.setMaxAnimals(maxNum);
        fairySpawner.AddCustomSpawn(FRY_EntityFairy.class, freqNum, EnumCreatureType.creature);
        fairyMod = this;
        EntityList.entityEggs.put(Integer.valueOf(65), new EntityEggInfo(65, 0xea8fde, 0x8658bf));
        ModLoader.getLogger().fine("FRY_Spawner.class is a modified version of CustomSpawner.class, created by DrZhark.");
    }

    @Override public void load()
    {
    }

    private FRY_EntityFairy getMyFairy(int fairyID)
    {
        MinecraftServer mc = ModLoader.getMinecraftServerInstance();

        if (mc != null)
        {
            for (Integer i : DimensionManager.getIDs())
            {
                World theWorld = DimensionManager.getWorld(i);

                if (theWorld != null)
                {
                    List list = theWorld.loadedEntityList;

                    if (list != null)
                    {
                        for (int j = 0; j < list.size(); j++)
                        {
                            Entity entity = (Entity)list.get(j);

                            if (entity instanceof FRY_EntityFairy && entity.entityId == fairyID)
                            {
                                return (FRY_EntityFairy)entity;
                            }
                        }
                    }
                }
            }
        }

        return (FRY_EntityFairy)null;
    }

    @Override public boolean onTickInGame(MinecraftServer minecraftserver)
    {
        MinecraftServer mc = minecraftserver;

        if (mc != null)
        {
            for (Integer i : DimensionManager.getIDs())
            {
                //for(int i = 0; i < 1; i++) {
                World theWorld = DimensionManager.getWorld(i);

                if (theWorld != null)
                {
                    if (!theWorld.isRemote && theWorld.worldInfo.getWorldTime() % 300L == 0L)
                    {
                        List list = theWorld.playerEntities;

                        if (list != null)
                        {
                            int maxNum = 12 + (list.size() * 6);
                            fairySpawner.setMaxAnimals(maxNum);
                        }

                        fairySpawner.doCustomSpawning(theWorld, theWorld.difficultySetting > 0, true);
                    }
                }
            }
        }

        return true;
    }

    public void sendFairyMount(Entity entity1, Entity entity2)   //Packet that handles fairy mounting.
    {
        int[] dataInt = new int[2];
        dataInt[0] = entity1.entityId;
        dataInt[1] = entity2.entityId;

        if (entity1.ridingEntity != null && entity1.ridingEntity == entity2)
        {
            dataInt[1] = -1;
        }

        Packet39AttachEntity packet = new Packet39AttachEntity();
        packet.entityId = dataInt[0];
        packet.vehicleEntityId = dataInt[1];

        for (int i = 0; i < ModLoader.getMinecraftServerInstance().configManager.playerEntities.size(); i++)
        {
            ((EntityPlayerMP)ModLoader.getMinecraftServerInstance().configManager.playerEntities.get(i)).playerNetServerHandler.sendPacket(packet);
        }

        if (!(entity1 instanceof FRY_EntityFishHook))
        {
            entity1.mountEntity(entity2);
        }
    }

    public void sendFairyDespawn(Entity entity1)   //Packet that handles forced fairy despawning.
    {
        int dataInt = entity1.entityId;
        Packet29DestroyEntity packet = new Packet29DestroyEntity();
        packet.entityId = dataInt;

        for (int i = 0; i < ModLoader.getMinecraftServerInstance().configManager.playerEntities.size(); i++)
        {
            ((EntityPlayerMP)ModLoader.getMinecraftServerInstance().configManager.playerEntities.get(i)).playerNetServerHandler.sendPacket(packet);
        }

        entity1.setDead();
    }

    public void sendDisband(EntityPlayerMP player, String s)   //Packet that handles sending text to specific players.
    {
        if (player != null)
        {
            player.playerNetServerHandler.sendPacket(new Packet3Chat(s));
        }

        //Shouldn't enable this by default, could be spammy.
        //MinecraftServer.logger.info(s);
    }

    public static void setPrivateValueBoth(Class var0, Object var1, String obf, String mcp, Object var3)
    {
        try
        {
            ModLoader.setPrivateValue(var0, var1, obf, var3);
        }
        catch (Exception ex)
        {
            try
            {
                ModLoader.setPrivateValue(var0, var1, mcp, var3);
            }
            catch (Exception ex2)
            {
                ex2.printStackTrace();
            }
        }
    }

    @Override
    public void onConnect(NetworkManager network)
    {
    }

    @Override
    public void onLogin(NetworkManager network, Packet1Login login)
    {
        MessageManager.getInstance().registerChannel(network, this, "channelname");
    }

    @Override
    public void onDisconnect(NetworkManager network, String message, Object[] args)
    {
    }

    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] data)
    {
        EntityPlayerMP player = ((NetServerHandler)network.getNetHandler()).getPlayerEntity();
        ModLoader.getLogger().fine("Got a packet.");

        if (player != null && channel.equals("fryName") && data != null)
        {
            String command = new String(data);
            String username = player.username;
            ModLoader.getLogger().fine("Packet has string");

            if (command.toLowerCase().startsWith("setfryname"))
            {
                String commandSplit[] = command.split(" ");

                if (commandSplit.length < 2)
                {
                    ModLoader.getLogger().fine((new StringBuilder()).append("setfryname failed: bad command structure").toString());
                    return;
                }

                int fairyID = Integer.parseInt(commandSplit[1]);

                if (fairyID < 1)
                {
                    ModLoader.getLogger().fine((new StringBuilder()).append("setfryname failed: ").append(commandSplit[1]).append(" is not a valid entity ID").toString());
                    return;
                }

                FRY_EntityFairy myFairy = getMyFairy(fairyID);

                if (myFairy == null)
                {
                    ModLoader.getLogger().fine((new StringBuilder()).append("setfryname failed: no existing fairy with entity ID ").append(commandSplit[1]).toString());
                    return;
                }

                if (myFairy.nameEnabled() && myFairy.rulerName().equals(username))
                {
                    if (commandSplit.length < 3)
                    {
                        myFairy.setCustomName("");
                    }
                    else
                    {
                        String douche = "";

                        for (int i = 2; i < commandSplit.length; i++)
                        {
                            if (i > 2)
                            {
                                douche += " ";
                            }

                            douche += commandSplit[i];
                        }

                        myFairy.setCustomName(douche);
                    }

                    ModLoader.getLogger().fine("fairy name successfully changed");
                    myFairy.setNameEnabled(false);
                }
                else
                {
                    ModLoader.getLogger().fine("setfryname failed: invalid access");
                    myFairy.setNameEnabled(false);
                }
            }
        }
    }

    @Override public boolean clientSideRequired()
    {
        return true;
    }

    @Override public boolean serverSideRequired()
    {
        return true;
    }

    private static FRY_Spawner fairySpawner;
    public static mod_FairyMod fairyMod;
}