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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.Henna;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Zoey76
 */
public class HennaRemoveList extends ServerPacket
{
	private final Player _player;
	
	public HennaRemoveList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.HENNA_REMOVE_LIST.writeId(this, buffer);
		buffer.writeInt(_player.getAdena());
		buffer.writeInt(_player.getHennaEmptySlots());
		buffer.writeInt(Math.abs(_player.getHennaEmptySlots() - 3));
		for (Henna henna : _player.getHennaList())
		{
			if (henna != null)
			{
				buffer.writeInt(henna.getDyeId());
				buffer.writeInt(henna.getDyeItemId());
				buffer.writeInt(henna.getCancelCount());
				buffer.writeInt(henna.getCancelFee());
				buffer.writeInt(1);
			}
		}
	}
}
