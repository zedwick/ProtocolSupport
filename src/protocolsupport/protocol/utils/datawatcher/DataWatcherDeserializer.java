package protocolsupport.protocol.utils.datawatcher;

import java.text.MessageFormat;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.utils.ProtocolVersionsHelper;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectBlockState;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectBoolean;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectByte;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectChat;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectDirection;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectFloat;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectItemStack;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectNBTTagCompound;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectOptionalChat;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectOptionalPosition;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectOptionalUUID;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectParticle;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectPosition;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectString;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectVarInt;
import protocolsupport.protocol.utils.datawatcher.objects.DataWatcherObjectVector3f;
import protocolsupport.utils.CollectionsUtils.ArrayMap;

public class DataWatcherDeserializer {

	//while meta indexes can be now up to 255, we actually use up to 31
	public static final int MAX_USED_META_INDEX = 31;

	@SuppressWarnings("unchecked")
	private static final Supplier<? extends ReadableDataWatcherObject<?>>[] registry = new Supplier[256];
	static {
		try {
			register(DataWatcherObjectByte::new);
			register(DataWatcherObjectVarInt::new);
			register(DataWatcherObjectFloat::new);
			register(DataWatcherObjectString::new);
			register(DataWatcherObjectChat::new);
			register(DataWatcherObjectOptionalChat::new);
			register(DataWatcherObjectItemStack::new);
			register(DataWatcherObjectBoolean::new);
			register(DataWatcherObjectVector3f::new);
			register(DataWatcherObjectPosition::new);
			register(DataWatcherObjectOptionalPosition::new);
			register(DataWatcherObjectDirection::new);
			register(DataWatcherObjectOptionalUUID::new);
			register(DataWatcherObjectBlockState::new);
			register(DataWatcherObjectNBTTagCompound::new);
			register(DataWatcherObjectParticle::new);
		} catch (Exception e) {
			throw new RuntimeException("Exception in datawatcher init", e);
		}
	}

	private static void register(Supplier<? extends ReadableDataWatcherObject<?>> supplier) throws NoSuchMethodException {
		registry[DataWatcherObjectIdRegistry.getTypeId(supplier.get().getClass(), ProtocolVersionsHelper.LATEST_PC)] = supplier;
	}

	public static void decodeDataTo(ByteBuf from, ProtocolVersion version, String locale, ArrayMap<DataWatcherObject<?>> to) {
		do {
			int key = from.readUnsignedByte();
			if (key == 0xFF) {
				break;
			}
			int type = from.readUnsignedByte();
			try {
				ReadableDataWatcherObject<?> object = registry[type].get();
				object.readFromStream(from, version, locale);
				to.put(key, object);
			} catch (Exception e) {
				throw new DecoderException(MessageFormat.format("Unable to decode datawatcher object (type: {0}, index: {1})", type, key), e);
			}
		} while (true);
	}

	public static void encodeData(ByteBuf to, ProtocolVersion version, String locale, ArrayMap<DataWatcherObject<?>> objects) {
		boolean hadObject = false;
		for (int key = objects.getMinKey(); key < objects.getMaxKey(); key++) {
			DataWatcherObject<?> object = objects.get(key);
			if (object != null) {
				hadObject = true;
				to.writeByte(key);
				to.writeByte(DataWatcherObjectIdRegistry.getTypeId(object, version));
				object.writeToStream(to, version, locale);
			}
		}
		if (!hadObject) {
			to.writeByte(31);
			to.writeByte(0);
			to.writeByte(0);
		}
		to.writeByte(0xFF);
	}

}
