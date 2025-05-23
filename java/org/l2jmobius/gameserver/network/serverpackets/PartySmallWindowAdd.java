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

public class PartySmallWindowAdd extends ServerPacket
{
	private final Player _member;
	private final Party _party;
	
	public PartySmallWindowAdd(Player member, Party party)
	{
		_member = member;
		_party = party;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.PARTY_SMALL_WINDOW_ADD.writeId(this, buffer);
		buffer.writeInt(_party.getLeaderObjectId()); // c3
		buffer.writeInt(_party.getDistributionType().getId()); // buffer.writeInt(4); ?? //c3
		buffer.writeInt(_member.getObjectId());
		buffer.writeString(_member.getName());
		buffer.writeInt((int) _member.getCurrentCp()); // c4
		buffer.writeInt(_member.getMaxCp()); // c4
		buffer.writeInt((int) _member.getCurrentHp());
		buffer.writeInt(_member.getMaxHp());
		buffer.writeInt((int) _member.getCurrentMp());
		buffer.writeInt(_member.getMaxMp());
		buffer.writeInt(_member.getLevel());
		buffer.writeInt(_member.getClassId().getId());
		buffer.writeInt(0); // ?
		buffer.writeInt(0); // ?
	}
}
