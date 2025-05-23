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
package quests.Q00031_SecretBuriedInTheSwamp;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00031_SecretBuriedInTheSwamp extends Quest
{
	// NPCs
	private static final int ABERCROMBIE = 31555;
	private static final int FORGOTTEN_MONUMENT_1 = 31661;
	private static final int FORGOTTEN_MONUMENT_2 = 31662;
	private static final int FORGOTTEN_MONUMENT_3 = 31663;
	private static final int FORGOTTEN_MONUMENT_4 = 31664;
	private static final int CORPSE_OF_DWARF = 31665;
	// Item
	private static final int KRORIN_JOURNAL = 7252;
	
	public Q00031_SecretBuriedInTheSwamp()
	{
		super(31);
		registerQuestItems(KRORIN_JOURNAL);
		addStartNpc(ABERCROMBIE);
		addTalkId(ABERCROMBIE, CORPSE_OF_DWARF, FORGOTTEN_MONUMENT_1, FORGOTTEN_MONUMENT_2, FORGOTTEN_MONUMENT_3, FORGOTTEN_MONUMENT_4);
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
			case "31555-01.htm":
			{
				st.startQuest();
				break;
			}
			case "31665-01.htm":
			{
				st.setCond(2, true);
				giveItems(player, KRORIN_JOURNAL, 1);
				break;
			}
			case "31555-04.htm":
			{
				st.setCond(3, true);
				break;
			}
			case "31661-01.htm":
			{
				st.setCond(4, true);
				break;
			}
			case "31662-01.htm":
			{
				st.setCond(5, true);
				break;
			}
			case "31663-01.htm":
			{
				st.setCond(6, true);
				break;
			}
			case "31664-01.htm":
			{
				st.setCond(7, true);
				break;
			}
			case "31555-07.htm":
			{
				takeItems(player, KRORIN_JOURNAL, 1);
				giveAdena(player, 40000, true);
				addExpAndSp(player, 130000, 0);
				st.exitQuest(false, true);
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
				htmltext = (player.getLevel() < 66) ? "31555-00a.htm" : "31555-00.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ABERCROMBIE:
					{
						if (cond == 1)
						{
							htmltext = "31555-02.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31555-03.htm";
						}
						else if ((cond > 2) && (cond < 7))
						{
							htmltext = "31555-05.htm";
						}
						else if (cond == 7)
						{
							htmltext = "31555-06.htm";
						}
						break;
					}
					case CORPSE_OF_DWARF:
					{
						if (cond == 1)
						{
							htmltext = "31665-00.htm";
						}
						else if (cond > 1)
						{
							htmltext = "31665-02.htm";
						}
						break;
					}
					case FORGOTTEN_MONUMENT_1:
					{
						if (cond == 3)
						{
							htmltext = "31661-00.htm";
						}
						else if (cond > 3)
						{
							htmltext = "31661-02.htm";
						}
						break;
					}
					case FORGOTTEN_MONUMENT_2:
					{
						if (cond == 4)
						{
							htmltext = "31662-00.htm";
						}
						else if (cond > 4)
						{
							htmltext = "31662-02.htm";
						}
						break;
					}
					case FORGOTTEN_MONUMENT_3:
					{
						if (cond == 5)
						{
							htmltext = "31663-00.htm";
						}
						else if (cond > 5)
						{
							htmltext = "31663-02.htm";
						}
						break;
					}
					case FORGOTTEN_MONUMENT_4:
					{
						if (cond == 6)
						{
							htmltext = "31664-00.htm";
						}
						else if (cond > 6)
						{
							htmltext = "31664-02.htm";
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
