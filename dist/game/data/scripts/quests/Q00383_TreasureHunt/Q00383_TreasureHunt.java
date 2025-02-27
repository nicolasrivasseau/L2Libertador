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
package quests.Q00383_TreasureHunt;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00383_TreasureHunt extends Quest
{
	// NPCs
	private static final int ESPEN = 30890;
	private static final int PIRATE_CHEST = 31148;
	// Items
	private static final int PIRATE_TREASURE_MAP = 5915;
	private static final int THIEF_KEY = 1661;
	
	public Q00383_TreasureHunt()
	{
		super(383);
		addStartNpc(ESPEN);
		addTalkId(ESPEN, PIRATE_CHEST);
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
			case "30890-04.htm":
			{
				// Sell the map.
				if (hasQuestItems(player, PIRATE_TREASURE_MAP))
				{
					takeItems(player, PIRATE_TREASURE_MAP, 1);
					giveAdena(player, 1000, true);
				}
				else
				{
					htmltext = "30890-06.htm";
				}
				break;
			}
			case "30890-07.htm":
			{
				// Listen the story.
				if (hasQuestItems(player, PIRATE_TREASURE_MAP))
				{
					st.startQuest();
				}
				else
				{
					htmltext = "30890-06.htm";
				}
				break;
			}
			case "30890-11.htm":
			{
				// Decipher the map.
				if (hasQuestItems(player, PIRATE_TREASURE_MAP))
				{
					st.setCond(2, true);
					takeItems(player, PIRATE_TREASURE_MAP, 1);
				}
				else
				{
					htmltext = "30890-06.htm";
				}
				break;
			}
			case "31148-02.htm":
			{
				if (hasQuestItems(player, THIEF_KEY))
				{
					takeItems(player, THIEF_KEY, 1);
					
					// Adena reward.
					int i1 = 0;
					int i0 = getRandom(100);
					if (i0 < 5)
					{
						giveItems(player, 2450, 1);
					}
					else if (i0 < 6)
					{
						giveItems(player, 2451, 1);
					}
					else if (i0 < 18)
					{
						giveItems(player, 956, 1);
					}
					else if (i0 < 28)
					{
						giveItems(player, 952, 1);
					}
					else
					{
						i1 += 500;
					}
					
					i0 = getRandom(1000);
					if (i0 < 25)
					{
						giveItems(player, 4481, 1);
					}
					else if (i0 < 50)
					{
						giveItems(player, 4482, 1);
					}
					else if (i0 < 75)
					{
						giveItems(player, 4483, 1);
					}
					else if (i0 < 100)
					{
						giveItems(player, 4484, 1);
					}
					else if (i0 < 125)
					{
						giveItems(player, 4485, 1);
					}
					else if (i0 < 150)
					{
						giveItems(player, 4486, 1);
					}
					else if (i0 < 175)
					{
						giveItems(player, 4487, 1);
					}
					else if (i0 < 200)
					{
						giveItems(player, 4488, 1);
					}
					else if (i0 < 225)
					{
						giveItems(player, 4489, 1);
					}
					else if (i0 < 250)
					{
						giveItems(player, 4490, 1);
					}
					else if (i0 < 275)
					{
						giveItems(player, 4491, 1);
					}
					else if (i0 < 300)
					{
						giveItems(player, 4492, 1);
					}
					else
					{
						i1 += 300;
					}
					
					i0 = getRandom(100);
					if (i0 < 4)
					{
						giveItems(player, 1337, 1);
					}
					else if (i0 < 8)
					{
						giveItems(player, 1338, 2);
					}
					else if (i0 < 12)
					{
						giveItems(player, 1339, 2);
					}
					else if (i0 < 16)
					{
						giveItems(player, 3447, 2);
					}
					else if (i0 < 20)
					{
						giveItems(player, 3450, 1);
					}
					else if (i0 < 25)
					{
						giveItems(player, 3453, 1);
					}
					else if (i0 < 27)
					{
						giveItems(player, 3456, 1);
					}
					else
					{
						i1 += 500;
					}
					
					i0 = getRandom(100);
					if (i0 < 20)
					{
						giveItems(player, 4408, 1);
					}
					else if (i0 < 40)
					{
						giveItems(player, 4409, 1);
					}
					else if (i0 < 60)
					{
						giveItems(player, 4418, 1);
					}
					else if (i0 < 80)
					{
						giveItems(player, 4419, 1);
					}
					else
					{
						i1 += 500;
					}
					
					giveAdena(player, i1, true);
					st.exitQuest(true, true);
				}
				else
				{
					htmltext = "31148-03.htm";
				}
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
				htmltext = ((player.getLevel() < 42) || !hasQuestItems(player, PIRATE_TREASURE_MAP)) ? "30890-01.htm" : "30890-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ESPEN:
					{
						if (cond == 1)
						{
							htmltext = "30890-07a.htm";
						}
						else
						{
							htmltext = "30890-12.htm";
						}
						break;
					}
					case PIRATE_CHEST:
					{
						if (cond == 2)
						{
							htmltext = "31148-01.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
}
