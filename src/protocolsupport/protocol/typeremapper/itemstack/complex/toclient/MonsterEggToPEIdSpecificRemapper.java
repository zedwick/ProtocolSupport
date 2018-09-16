package protocolsupport.protocol.typeremapper.itemstack.complex.toclient;

import org.apache.commons.lang3.StringUtils;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.complex.ItemStackComplexRemapper;
import protocolsupport.protocol.typeremapper.pe.PEDataValues;
import protocolsupport.protocol.utils.networkentity.NetworkEntityType;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NetworkItemStack;

public class MonsterEggToPEIdSpecificRemapper implements ItemStackComplexRemapper {

	@Override
	public NetworkItemStack remap(ProtocolVersion version, String locale, NetworkItemStack itemstack) {
		NBTTagCompoundWrapper tag = itemstack.getNBT();
		if (tag.isNull()) {
			return itemstack;
		}
		String id = tag.getCompound("EntityTag").getString("id");

		if (StringUtils.isEmpty(id)) {
			return itemstack;
		}

		itemstack.setLegacyData(PEDataValues.getEntityTypeId(NetworkEntityType.getByRegistrySTypeId(id)));
		return itemstack;
	}

}