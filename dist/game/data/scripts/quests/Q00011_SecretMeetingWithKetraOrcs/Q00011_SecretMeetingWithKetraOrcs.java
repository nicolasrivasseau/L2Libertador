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
package quests.Q00011_SecretMeetingWithKetraOrcs;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00011_SecretMeetingWithKetraOrcs extends Quest
{
	// NPCs
	private static final int CADMON = 31296;
	private static final int LEON = 31256;
	private static final int WAHKAN = 31371;
	// Items
	private static final int MUNITIONS_BOX = 7231;
	
	public Q00011_SecretMeetingWithKetraOrcs()
	{
		super(11);
		registerQuestItems(MUNITIONS_BOX);
		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
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
			case "31296-03.htm":
			{
				st.startQuest();
				break;
			}
			case "31256-02.htm":
			{
				giveItems(player, MUNITIONS_BOX, 1);
				st.setCond(2, true);
				break;
			}
			case "31371-02.htm":
			{
				takeItems(player, MUNITIONS_BOX, 1);
				addExpAndSp(player, 79761, 0);
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
				htmltext = (player.getLevel() < 74) ? "31296-02.htm" : "31296-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case CADMON:
					{
						if (cond == 1)
						{
							htmltext = "31296-04.htm";
						}
						break;
					}
					case LEON:
					{
						if (cond == 1)
						{
							htmltext = "31256-01.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31256-03.htm";
						}
						break;
					}
					case WAHKAN:
					{
						if (cond == 2)
						{
							htmltext = "31371-01.htm";
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
