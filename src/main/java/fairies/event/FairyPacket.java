package fairies.event;

import fairies.Version;
import fairies.event.FairyEventListener.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public abstract class FairyPacket extends FMLProxyPacket {

	protected FairyPacket(PacketType packetType) {
		super( new PacketBuffer(Unpooled.buffer()), Version.CHANNEL );
		
		final PacketBuffer buf = (PacketBuffer)this.payload();
		buf.writeByte(packetType.packet_id);
	}
	
	protected abstract void pack();
	public abstract void init(PacketBuffer buf);
	public abstract void handle(NetworkManager networkManager);

}
