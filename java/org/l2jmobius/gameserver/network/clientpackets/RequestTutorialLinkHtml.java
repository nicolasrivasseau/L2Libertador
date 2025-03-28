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

import org.l2jmobius.gameserver.handler.BypassHandler;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.ClassMaster;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.serverpackets.TutorialCloseHtml;

public class RequestTutorialLinkHtml extends ClientPacket
{
	private String _bypass;
	
	@Override
	protected void readImpl()
	{
		_bypass = readString();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (_bypass.equalsIgnoreCase("close"))
		{
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		}
		
		final IBypassHandler handler = BypassHandler.getInstance().getHandler(_bypass);
		if (handler != null)
		{
			handler.useBypass(_bypass, player, null);
		}
		else
		{
			ClassMaster.onTutorialLink(player, _bypass);
			
			final QuestState qs = player.getQuestState("Q00255_Tutorial");
			if (qs != null)
			{
				qs.getQuest().notifyEvent(_bypass, null, player);
			}
		}
	}
}
