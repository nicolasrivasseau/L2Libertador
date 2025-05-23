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
package quests.Q00654_JourneyToASettlement;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00119_LastImperialPrince.Q00119_LastImperialPrince;

public class Q00654_JourneyToASettlement extends Quest
{
	// Item
	private static final int ANTELOPE_SKIN = 8072;
	// Reward
	private static final int FORCE_FIELD_REMOVAL_SCROLL = 8073;
	
	public Q00654_JourneyToASettlement()
	{
		super(654);
		registerQuestItems(ANTELOPE_SKIN);
		addStartNpc(31453); // Nameless Spirit
		addTalkId(31453);
		addKillId(21294, 21295); // Canyon Antelope, Canyon Antelope Slave
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31453-02.htm":
			{
				st.startQuest();
				break;
			}
			case "31453-03.htm":
			{
				st.setCond(2, true);
				break;
			}
			case "31453-06.htm":
			{
				takeItems(player, ANTELOPE_SKIN, -1);
				giveItems(player, FORCE_FIELD_REMOVAL_SCROLL, 1);
				st.exitQuest(true, true);
				break;
			}
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
				final QuestState prevSt = player.getQuestState(Q00119_LastImperialPrince.class.getSimpleName());
				htmltext = ((prevSt == null) || !prevSt.isCompleted() || (player.getLevel() < 74)) ? "31453-00.htm" : "31453-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "31453-02.htm";
				}
				else if (cond == 2)
				{
					htmltext = "31453-04.htm";
				}
				else if (cond == 3)
				{
					htmltext = "31453-05.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isCond(2))
		{
			return null;
		}
		
		if ((getRandom(100) < 5) && hasQuestItems(player, ANTELOPE_SKIN))
		{
			giveItems(player, ANTELOPE_SKIN, 1);
			st.setCond(3, true);
		}
		
		return null;
	}
}
