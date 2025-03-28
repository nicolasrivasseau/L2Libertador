/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.TradeItem;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PrivateStoreListBuy extends ServerPacket
{
	private final int _objId;
	private final long _playerAdena;
	private final Collection<TradeItem> _items;
	
	public PrivateStoreListBuy(Player player, Player storePlayer)
	{
		_objId = storePlayer.getObjectId();
		_playerAdena = player.getAdena();
		storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
		_items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PRIVATE_STORE_BUY_LIST.writeId(this, buffer);
		buffer.writeInt(_objId);
		buffer.writeInt((int) _playerAdena);
		buffer.writeInt(_items.size());
		for (TradeItem item : _items)
		{
			buffer.writeInt(item.getObjectId());
			buffer.writeInt(item.getItem().getId());
			buffer.writeShort(item.getEnchant());
			buffer.writeInt(item.getCount()); // give max possible sell amount
			buffer.writeInt(item.getItem().getReferencePrice());
			buffer.writeShort(0);
			buffer.writeInt(item.getItem().getBodyPart());
			buffer.writeShort(item.getItem().getType2());
			buffer.writeInt(item.getPrice()); // buyers price
			buffer.writeInt(item.getStoreCount()); // maximum possible tradecount
		}
	}
}
