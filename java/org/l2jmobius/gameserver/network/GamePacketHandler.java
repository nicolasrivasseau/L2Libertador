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
package org.l2jmobius.gameserver.network;

import java.util.logging.Logger;

import org.l2jmobius.commons.network.PacketHandler;
import org.l2jmobius.commons.network.ReadableBuffer;
import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.commons.util.CommonUtil;

/**
 * @author Mobius
 */
public class GamePacketHandler implements PacketHandler<GameClient>
{
	private static final Logger LOGGER = Logger.getLogger(GamePacketHandler.class.getName());
	
	@Override
	public ReadablePacket<GameClient> handlePacket(ReadableBuffer buffer, GameClient client)
	{
		// Read packet id.
		final int packetId;
		try
		{
			packetId = Byte.toUnsignedInt(buffer.readByte());
		}
		catch (Exception e)
		{
			LOGGER.warning("PacketHandler: Problem receiving packet id from " + client);
			LOGGER.warning(CommonUtil.getStackTrace(e));
			client.closeNow();
			return null;
		}
		
		switch (packetId)
		{
			case 0xD0: // Ex client packet.
			{
				// Check if packet id is within valid range.
				final int exPacketId = Short.toUnsignedInt(buffer.readShort());
				if ((exPacketId < 0) || (exPacketId >= ExClientPackets.PACKET_ARRAY.length))
				{
					return null;
				}
				
				// Find packet enum.
				final ExClientPackets packetEnum = ExClientPackets.PACKET_ARRAY[exPacketId];
				if (packetEnum == null)
				{
					return null;
				}
				
				// Check connection state.
				if (!packetEnum.getConnectionStates().contains(client.getConnectionState()))
				{
					return null;
				}
				
				// Create new ClientPacket.
				return packetEnum.newPacket();
			}
			default: // Normal client packet.
			{
				// Check if packet id is within valid range.
				if ((packetId < 0) || (packetId >= ClientPackets.PACKET_ARRAY.length))
				{
					return null;
				}
				
				// Find packet enum.
				final ClientPackets packetEnum = ClientPackets.PACKET_ARRAY[packetId];
				if (packetEnum == null)
				{
					return null;
				}
				
				// Check connection state.
				if (!packetEnum.getConnectionStates().contains(client.getConnectionState()))
				{
					return null;
				}
				
				// Create new ClientPacket.
				return packetEnum.newPacket();
			}
		}
	}
}
