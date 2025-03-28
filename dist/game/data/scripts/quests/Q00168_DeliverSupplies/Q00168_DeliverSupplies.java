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
package quests.Q00168_DeliverSupplies;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00168_DeliverSupplies extends Quest
{
	// NPCs
	private static final int JENNA = 30349;
	private static final int ROSELYN = 30355;
	private static final int KRISTIN = 30357;
	private static final int HARANT = 30360;
	// Items
	private static final int JENNA_LETTER = 1153;
	private static final int SENTRY_BLADE_1 = 1154;
	private static final int SENTRY_BLADE_2 = 1155;
	private static final int SENTRY_BLADE_3 = 1156;
	private static final int OLD_BRONZE_SWORD = 1157;
	
	public Q00168_DeliverSupplies()
	{
		super(168);
		registerQuestItems(JENNA_LETTER, SENTRY_BLADE_1, SENTRY_BLADE_2, SENTRY_BLADE_3, OLD_BRONZE_SWORD);
		addStartNpc(JENNA);
		addTalkId(JENNA, ROSELYN, KRISTIN, HARANT);
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
		
		if (event.equals("30349-03.htm"))
		{
			st.startQuest();
			giveItems(player, JENNA_LETTER, 1);
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
				if (player.getRace() != Race.DARK_ELF)
				{
					htmltext = "30349-00.htm";
				}
				else if (player.getLevel() < 3)
				{
					htmltext = "30349-01.htm";
				}
				else
				{
					htmltext = "30349-02.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case JENNA:
					{
						if (cond == 1)
						{
							htmltext = "30349-04.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30349-05.htm";
							st.setCond(3, true);
							takeItems(player, SENTRY_BLADE_1, 1);
						}
						else if (cond == 3)
						{
							htmltext = "30349-07.htm";
						}
						else if (cond == 4)
						{
							htmltext = "30349-06.htm";
							takeItems(player, OLD_BRONZE_SWORD, 2);
							giveAdena(player, 820, true);
							st.exitQuest(false, true);
						}
						break;
					}
					case HARANT:
					{
						if (cond == 1)
						{
							htmltext = "30360-01.htm";
							st.setCond(2, true);
							takeItems(player, JENNA_LETTER, 1);
							giveItems(player, SENTRY_BLADE_1, 1);
							giveItems(player, SENTRY_BLADE_2, 1);
							giveItems(player, SENTRY_BLADE_3, 1);
						}
						else if (cond == 2)
						{
							htmltext = "30360-02.htm";
						}
						break;
					}
					case ROSELYN:
					{
						if (cond == 3)
						{
							if (hasQuestItems(player, SENTRY_BLADE_2))
							{
								htmltext = "30355-01.htm";
								takeItems(player, SENTRY_BLADE_2, 1);
								giveItems(player, OLD_BRONZE_SWORD, 1);
								if (getQuestItemsCount(player, OLD_BRONZE_SWORD) == 2)
								{
									st.setCond(4, true);
								}
							}
							else
							{
								htmltext = "30355-02.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "30355-02.htm";
						}
						break;
					}
					case KRISTIN:
					{
						if (cond == 3)
						{
							if (hasQuestItems(player, SENTRY_BLADE_3))
							{
								htmltext = "30357-01.htm";
								takeItems(player, SENTRY_BLADE_3, 1);
								giveItems(player, OLD_BRONZE_SWORD, 1);
								if (getQuestItemsCount(player, OLD_BRONZE_SWORD) == 2)
								{
									st.setCond(4, true);
								}
							}
							else
							{
								htmltext = "30357-02.htm";
							}
						}
						else if (cond == 4)
						{
							htmltext = "30357-02.htm";
						}
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
