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
package quests.Q00009_IntoTheCityOfHumans;

import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00009_IntoTheCityOfHumans extends Quest
{
	// NPCs
	private static final int PETUKAI = 30583;
	private static final int TANAPI = 30571;
	private static final int TAMIL = 30576;
	// Rewards
	private static final int MARK_OF_TRAVELER = 7570;
	private static final int SOE_GIRAN = 7126;
	
	public Q00009_IntoTheCityOfHumans()
	{
		super(9);
		addStartNpc(PETUKAI);
		addTalkId(PETUKAI, TANAPI, TAMIL);
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
			case "30583-01.htm":
			{
				st.startQuest();
				break;
			}
			case "30571-01.htm":
			{
				st.setCond(2, true);
				break;
			}
			case "30576-01.htm":
			{
				giveItems(player, MARK_OF_TRAVELER, 1);
				rewardItems(player, SOE_GIRAN, 1);
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
				if ((player.getLevel() >= 3) && (player.getRace() == Race.ORC))
				{
					htmltext = "30583-00.htm";
				}
				else
				{
					htmltext = "30583-00a.htm";
				}
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case PETUKAI:
					{
						if (cond == 1)
						{
							htmltext = "30583-01a.htm";
						}
						break;
					}
					case TANAPI:
					{
						if (cond == 1)
						{
							htmltext = "30571-00.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30571-01a.htm";
						}
						break;
					}
					case TAMIL:
					{
						if (cond == 2)
						{
							htmltext = "30576-00.htm";
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
