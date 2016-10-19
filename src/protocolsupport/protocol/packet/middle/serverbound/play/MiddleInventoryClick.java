package protocolsupport.protocol.packet.middle.serverbound.play;

import protocolsupport.protocol.packet.ServerBoundPacket;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.ServerBoundPacketData;
import protocolsupport.protocol.utils.types.ItemStackWrapper;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public abstract class MiddleInventoryClick extends ServerBoundMiddlePacket {

	protected int windowId;
	protected int slot;
	protected int button;
	protected int actionNumber;
	protected int mode;
	protected ItemStackWrapper itemstack;

	@Override
	public RecyclableCollection<ServerBoundPacketData> toNative() throws Exception {
		ServerBoundPacketData creator = ServerBoundPacketData.create(ServerBoundPacket.PLAY_WINDOW_CLICK);
		creator.writeByte(windowId);
		creator.writeShort(slot);
		creator.writeByte(button);
		creator.writeShort(actionNumber);
		creator.writeByte(mode);
		creator.writeItemStack(itemstack);
		return RecyclableSingletonList.create(creator);
	}

}
