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
package quests.Q00294_CovertBusiness;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00294_CovertBusiness extends Quest
{
	// Item
	private static final int BAT_FANG = 1491;
	// Reward
	private static final int RING_OF_RACCOON = 1508;
	
	public Q00294_CovertBusiness()
	{
		super(294);
		registerQuestItems(BAT_FANG);
		addStartNpc(30534); // Keef
		addTalkId(30534);
		addKillId(20370, 20480); // Barded Bat, Blade Bat
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
		
		if (event.equals("30534-03.htm"))
		{
			st.startQuest();
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
				if (player.getRace() != Race.DWARF)
				{
					htmltext = "30534-00.htm";
				}
				else if (player.getLevel() < 10)
				{
					htmltext = "30534-01.htm";
				}
				else
				{
					htmltext = "30534-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "30534-04.htm";
				}
				else
				{
					takeItems(player, BAT_FANG, -1);
					
					if (!hasQuestItems(player, RING_OF_RACCOON))
					{
						htmltext = "30534-05.htm";
						giveItems(player, RING_OF_RACCOON, 1);
					}
					else
					{
						htmltext = "30534-06.htm";
						giveAdena(player, 2400, true);
					}
					addExpAndSp(player, 0, 600);
					st.exitQuest(true, true);
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
		
		int count = 1;
		final int chance = getRandom(10);
		final boolean isBarded = (npc.getId() == 20370);
		
		if (chance < 3)
		{
			count++;
		}
		else if (chance < ((isBarded) ? 5 : 6))
		{
			count += 2;
		}
		else if (isBarded && (chance < 7))
		{
			count += 3;
		}
		
		giveItems(player, BAT_FANG, count);
		if (getQuestItemsCount(player, BAT_FANG) < 100)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else
		{
			st.setCond(2, true);
		}
		
		return null;
	}
}
