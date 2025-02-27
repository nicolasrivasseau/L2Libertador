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
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PartySmallWindowAll extends ServerPacket
{
	private final Party _party;
	private final Player _exclude;
	
	public PartySmallWindowAll(Player exclude, Party party)
	{
		_exclude = exclude;
		_party = party;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PARTY_SMALL_WINDOW_ALL.writeId(this, buffer);
		buffer.writeInt(_party.getLeaderObjectId());
		buffer.writeInt(_party.getDistributionType().getId());
		buffer.writeInt(_party.getMemberCount() - 1);
		for (Player member : _party.getMembers())
		{
			if ((member != null) && (member != _exclude))
			{
				buffer.writeInt(member.getObjectId());
				buffer.writeString(member.getName());
				buffer.writeInt((int) member.getCurrentCp()); // c4
				buffer.writeInt(member.getMaxCp()); // c4
				buffer.writeInt((int) member.getCurrentHp());
				buffer.writeInt(member.getMaxHp());
				buffer.writeInt((int) member.getCurrentMp());
				buffer.writeInt(member.getMaxMp());
				buffer.writeInt(member.getLevel());
				buffer.writeInt(member.getClassId().getId());
				buffer.writeInt(0); // buffer.writeInt(1); ??
				buffer.writeInt(member.getRace().ordinal());
			}
		}
	}
}
