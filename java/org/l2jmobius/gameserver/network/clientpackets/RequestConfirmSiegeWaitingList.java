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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.data.sql.ClanTable;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.network.serverpackets.SiegeDefenderList;

/**
 * @version $Revision: 1.3.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestConfirmSiegeWaitingList extends ClientPacket
{
	private int _approved;
	private int _castleId;
	private int _clanId;
	
	@Override
	protected void readImpl()
	{
		_castleId = readInt();
		_clanId = readInt();
		_approved = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Check if the player has a clan
		if (player.getClan() == null)
		{
			return;
		}
		
		final Castle castle = CastleManager.getInstance().getCastleById(_castleId);
		if (castle == null)
		{
			return;
		}
		
		// Check if leader of the clan who owns the castle?
		if ((castle.getOwnerId() != player.getClanId()) || (!player.isClanLeader()))
		{
			return;
		}
		
		final Clan clan = ClanTable.getInstance().getClan(_clanId);
		if (clan == null)
		{
			return;
		}
		
		if (!castle.getSiege().isRegistrationOver())
		{
			if (_approved == 1)
			{
				if (castle.getSiege().checkIsDefenderWaiting(clan))
				{
					castle.getSiege().approveSiegeDefenderClan(_clanId);
				}
				else
				{
					return;
				}
			}
			else
			{
				if ((castle.getSiege().checkIsDefenderWaiting(clan)) || (castle.getSiege().checkIsDefender(clan)))
				{
					castle.getSiege().removeSiegeClan(_clanId);
				}
			}
		}
		
		// Update the defender list
		player.sendPacket(new SiegeDefenderList(castle));
	}
}
