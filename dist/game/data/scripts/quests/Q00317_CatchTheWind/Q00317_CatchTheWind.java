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
package quests.Q00317_CatchTheWind;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00317_CatchTheWind extends Quest
{
	// Item
	private static final int WIND_SHARD = 1078;
	
	public Q00317_CatchTheWind()
	{
		super(317);
		registerQuestItems(WIND_SHARD);
		addStartNpc(30361); // Rizraell
		addTalkId(30361);
		addKillId(20036, 20044);
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
		
		if (event.equals("30361-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30361-08.htm"))
		{
			st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 18) ? "30361-02.htm" : "30361-03.htm";
				break;
			}
			case State.STARTED:
			{
				final int shards = getQuestItemsCount(player, WIND_SHARD);
				if (shards == 0)
				{
					htmltext = "30361-05.htm";
				}
				else
				{
					htmltext = "30361-07.htm";
					takeItems(player, WIND_SHARD, -1);
					
					int reward = 30 * shards;
					if (!Config.ALT_VILLAGES_REPEATABLE_QUEST_REWARD && (shards >= 10))
					{
						reward += 2988;
					}
					
					giveAdena(player, reward, true);
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
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(player, WIND_SHARD, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		
		return null;
	}
}
