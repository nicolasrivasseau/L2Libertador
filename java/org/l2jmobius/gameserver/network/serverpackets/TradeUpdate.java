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
import org.l2jmobius.gameserver.model.TradeList;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Beetle
 */
public class TradeUpdate extends ServerPacket
{
	private final Collection<Item> _items;
	private final Collection<TradeItem> _tradeItems;
	
	public TradeUpdate(TradeList trade, Player player)
	{
		_items = player.getInventory().getItems();
		_tradeItems = trade.getItems();
	}
	
	private int getItemCount(int objectId)
	{
		for (Item item : _items)
		{
			if (item.getObjectId() == objectId)
			{
				return item.getCount();
			}
		}
		return 0;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.TRADE_UPDATE.writeId(this, buffer);
		buffer.writeShort(_tradeItems.size());
		for (TradeItem item : _tradeItems)
		{
			int aveCount = getItemCount(item.getObjectId()) - item.getCount();
			boolean stackable = item.getItem().isStackable();
			if (aveCount == 0)
			{
				aveCount = 1;
				stackable = false;
			}
			buffer.writeShort(stackable ? 3 : 2);
			buffer.writeShort(item.getItem().getType1()); // item type1
			buffer.writeInt(item.getObjectId());
			buffer.writeInt(item.getItem().getId());
			buffer.writeInt(aveCount);
			buffer.writeShort(item.getItem().getType2()); // item type2
			buffer.writeShort(0); // ?
			buffer.writeInt(item.getItem().getBodyPart()); // rev 415 slot 0006-lr.ear 0008-neck 0030-lr.finger 0040-head 0080-?? 0100-l.hand 0200-gloves 0400-chest 0800-pants 1000-feet 2000-?? 4000-r.hand 8000-r.hand
			buffer.writeShort(item.getEnchant()); // enchant level
			buffer.writeShort(0); // ?
			buffer.writeShort(0);
		}
	}
}