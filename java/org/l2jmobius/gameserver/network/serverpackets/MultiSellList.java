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

import static org.l2jmobius.gameserver.data.xml.MultisellData.PAGE_SIZE;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.multisell.Entry;
import org.l2jmobius.gameserver.model.multisell.Ingredient;
import org.l2jmobius.gameserver.model.multisell.ListContainer;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class MultiSellList extends ServerPacket
{
	private int _size;
	private int _index;
	private final ListContainer _list;
	private final boolean _finished;
	
	public MultiSellList(ListContainer list, int index)
	{
		_list = list;
		_index = index;
		_size = list.getEntries().size() - index;
		if (_size > PAGE_SIZE)
		{
			_finished = false;
			_size = PAGE_SIZE;
		}
		else
		{
			_finished = true;
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.MULTI_SELL_LIST.writeId(this, buffer);
		buffer.writeInt(_list.getListId()); // list id
		buffer.writeInt(1 + (_index / PAGE_SIZE)); // page started from 1
		buffer.writeInt(_finished); // finished
		buffer.writeInt(PAGE_SIZE); // size of pages
		buffer.writeInt(_size); // list length
		Entry ent;
		while (_size-- > 0)
		{
			ent = _list.getEntries().get(_index++);
			buffer.writeInt(ent.getEntryId());
			buffer.writeInt(0); // C6
			buffer.writeInt(0); // C6
			buffer.writeByte(1);
			buffer.writeShort(ent.getProducts().size());
			buffer.writeShort(ent.getIngredients().size());
			for (Ingredient ing : ent.getProducts())
			{
				if (ing.getTemplate() != null)
				{
					buffer.writeShort(ing.getTemplate().getDisplayId());
					buffer.writeInt(ing.getTemplate().getBodyPart());
					buffer.writeShort(ing.getTemplate().getType2());
				}
				else
				{
					buffer.writeShort(ing.getItemId());
					buffer.writeInt(0);
					buffer.writeShort(65535);
				}
				buffer.writeInt(ing.getItemCount());
				if (ing.getItemInfo() != null)
				{
					buffer.writeShort(ing.getItemInfo().getEnchantLevel()); // enchant level
					buffer.writeInt(ing.getItemInfo().getAugmentId()); // augment id
					buffer.writeInt(0); // mana
				}
				else
				{
					buffer.writeShort(ing.getEnchantLevel()); // enchant level
					buffer.writeInt(0); // augment id
					buffer.writeInt(0); // mana
				}
			}
			for (Ingredient ing : ent.getIngredients())
			{
				buffer.writeShort(ing.getTemplate() != null ? ing.getTemplate().getDisplayId() : ing.getItemId());
				buffer.writeShort(ing.getTemplate() != null ? ing.getTemplate().getType2() : 65535);
				buffer.writeInt(ing.getItemCount());
				if (ing.getItemInfo() != null)
				{
					buffer.writeShort(ing.getItemInfo().getEnchantLevel()); // enchant level
					buffer.writeInt(ing.getItemInfo().getAugmentId()); // augment id
					buffer.writeInt(0); // mana
				}
				else
				{
					buffer.writeShort(ing.getEnchantLevel()); // enchant level
					buffer.writeInt(0); // augment id
					buffer.writeInt(0); // mana
				}
			}
		}
	}
}
