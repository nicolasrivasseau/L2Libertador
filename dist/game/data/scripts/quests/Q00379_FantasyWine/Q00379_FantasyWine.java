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
package quests.Q00379_FantasyWine;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00379_FantasyWine extends Quest
{
	// NPCs
	private static final int HARLAN = 30074;
	// Monsters
	private static final int ENKU_CHAMPION = 20291;
	private static final int ENKU_SHAMAN = 20292;
	// Items
	private static final int LEAF = 5893;
	private static final int STONE = 5894;
	
	public Q00379_FantasyWine()
	{
		super(379);
		registerQuestItems(LEAF, STONE);
		addStartNpc(HARLAN);
		addTalkId(HARLAN);
		addKillId(ENKU_CHAMPION, ENKU_SHAMAN);
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
			case "30074-3.htm":
			{
				st.startQuest();
				break;
			}
			case "30074-6.htm":
			{
				takeItems(player, LEAF, 80);
				takeItems(player, STONE, 100);
				final int rand = getRandom(10);
				if (rand < 3)
				{
					htmltext = "30074-6.htm";
					giveItems(player, 5956, 1);
				}
				else if (rand < 9)
				{
					htmltext = "30074-7.htm";
					giveItems(player, 5957, 1);
				}
				else
				{
					htmltext = "30074-8.htm";
					giveItems(player, 5958, 1);
				}
				st.exitQuest(true, true);
				break;
			}
			case "30074-2a.htm":
			{
				st.exitQuest(true);
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
				htmltext = (player.getLevel() < 20) ? "30074-0a.htm" : "30074-0.htm";
				break;
			}
			case State.STARTED:
			{
				final int leaf = getQuestItemsCount(player, LEAF);
				final int stone = getQuestItemsCount(player, STONE);
				if ((leaf == 80) && (stone == 100))
				{
					htmltext = "30074-5.htm";
				}
				else if (leaf == 80)
				{
					htmltext = "30074-4a.htm";
				}
				else if (stone == 100)
				{
					htmltext = "30074-4b.htm";
				}
				else
				{
					htmltext = "30074-4.htm";
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
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		if (npc.getId() == ENKU_CHAMPION)
		{
			giveItems(player, LEAF, 1);
			if (getQuestItemsCount(player, LEAF) < 80)
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				if (getQuestItemsCount(player, STONE) >= 100)
				{
					st.setCond(2, true);
				}
				else
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		else
		{
			giveItems(player, STONE, 1);
			if (getQuestItemsCount(player, STONE) < 100)
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				if (getQuestItemsCount(player, LEAF) >= 80)
				{
					st.setCond(2, true);
				}
				else
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		
		return null;
	}
}
