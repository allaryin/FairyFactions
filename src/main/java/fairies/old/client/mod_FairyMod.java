package net.minecraft.src;

import java.util.Map;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.*;

public class mod_FairyMod extends NetworkMod implements IConnectionHandler, IPacketHandler
{
    public String getVersion()
    {
        return "v1.2.5_forge";
    }

    public mod_FairyMod()
    {
        fairyMod = this;
    }

    @Override public void load()
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

        //ModLoader.AddSpawn(FRY_EntityFairy.class, rarity, 4, 4, EnumCreatureType.creature); rarity was 12
        //DrZhark's Custom Spawner Setup
        ModLoader.setInGameHook(this, true, false);
        fairySpawner = new FRY_Spawner();
        int maxNum = 18;
        int freqNum = 8;
        fairySpawner.setMaxAnimals(maxNum);
        fairySpawner.AddCustomSpawn(FRY_EntityFairy.class, freqNum, EnumCreatureType.creature);
        EntityList.entityEggs.put(Integer.valueOf(65), new EntityEggInfo(65, 0xea8fde, 0x8658bf));
        ModLoader.addLocalization("entity.Fairy.name", "Fairy");
        ModLoader.getLogger().fine("FRY_Spawner.class is a modified version of CustomSpawner.class, created by DrZhark.");
        System.out.println("FRY_Spawner.class is a modified version of CustomSpawner.class, created by DrZhark.");
    }

    public void sendFairyRename(String s)
    {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "setFryName";
        packet.data = s.getBytes();
        packet.length = packet.data.length;
        ModLoader.sendPacket(packet);
    }

    @Override public boolean onTickInGame(float f, Minecraft minecraft)
    {
        Minecraft mc = minecraft;

        if (mc != null && mc.theWorld != null && !mc.theWorld.isRemote && mc.theWorld.worldInfo.getWorldTime() % 300L == 0L)
        {
            fairySpawner.doCustomSpawning(mc.theWorld, mc.gameSettings.difficulty > 0, true);
        }

        return true;
    }

    @Override public void addRenderer(Map map)
    {
        map.put(FRY_EntityFairy.class, new FRY_RenderFairy(new FRY_ModelFairy(), 0.25F));
        map.put(FRY_EntityFishHook.class, new FRY_RenderFish());
    }

    public static void setPrivateValueBoth(Class var0, Object var1, String obf, String mcp, Object var3)
    {
        try
        {
            try
            {
                ModLoader.setPrivateValue(var0, var1, obf, var3);
            }
            catch (NoSuchFieldException ex)
            {
                ModLoader.setPrivateValue(var0, var1, mcp, var3);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        // HANDLE INCOMING PACKET DATA HERE
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
