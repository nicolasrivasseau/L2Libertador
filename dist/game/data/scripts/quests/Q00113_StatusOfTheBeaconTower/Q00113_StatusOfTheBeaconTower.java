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
package quests.Q00113_StatusOfTheBeaconTower;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00113_StatusOfTheBeaconTower extends Quest
{
	// NPCs
	private static final int MOIRA = 31979;
	private static final int TORRANT = 32016;
	// Item
	private static final int BOX = 8086;
	
	public Q00113_StatusOfTheBeaconTower()
	{
		super(113);
		registerQuestItems(BOX);
		addStartNpc(MOIRA);
		addTalkId(MOIRA, TORRANT);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31979-02.htm"))
		{
			st.startQuest();
			giveItems(player, BOX, 1);
		}
		else if (event.equals("32016-02.htm"))
		{
			takeItems(player, BOX, 1);
			giveAdena(player, 21578, true);
			st.exitQuest(false, true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 40) ? "31979-00.htm" : "31979-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case MOIRA:
					{
						htmltext = "31979-03.htm";
						break;
					}
					case TORRANT:
					{
						htmltext = "32016-01.htm";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		
		return htmltext;
	}
}