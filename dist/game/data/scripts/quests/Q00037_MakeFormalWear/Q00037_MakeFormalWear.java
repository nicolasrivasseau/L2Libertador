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
package quests.Q00037_MakeFormalWear;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00037_MakeFormalWear extends Quest
{
	// NPCs
	private static final int ALEXIS = 30842;
	private static final int LEIKAR = 31520;
	private static final int JEREMY = 31521;
	private static final int MIST = 31627;
	// Items
	private static final int MYSTERIOUS_CLOTH = 7076;
	private static final int JEWEL_BOX = 7077;
	private static final int SEWING_KIT = 7078;
	private static final int DRESS_SHOES_BOX = 7113;
	private static final int SIGNET_RING = 7164;
	private static final int ICE_WINE = 7160;
	private static final int BOX_OF_COOKIES = 7159;
	// Reward
	private static final int FORMAL_WEAR = 6408;
	
	public Q00037_MakeFormalWear()
	{
		super(37);
		registerQuestItems(SIGNET_RING, ICE_WINE, BOX_OF_COOKIES);
		addStartNpc(ALEXIS);
		addTalkId(ALEXIS, LEIKAR, JEREMY, MIST);
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
			case "30842-1.htm":
			{
				st.startQuest();
				break;
			}
			case "31520-1.htm":
			{
				st.setCond(2, true);
				giveItems(player, SIGNET_RING, 1);
				break;
			}
			case "31521-1.htm":
			{
				st.setCond(3, true);
				takeItems(player, SIGNET_RING, 1);
				giveItems(player, ICE_WINE, 1);
				break;
			}
			case "31627-1.htm":
			{
				st.setCond(4, true);
				takeItems(player, ICE_WINE, 1);
				break;
			}
			case "31521-3.htm":
			{
				st.setCond(5, true);
				giveItems(player, BOX_OF_COOKIES, 1);
				break;
			}
			case "31520-3.htm":
			{
				st.setCond(6, true);
				takeItems(player, BOX_OF_COOKIES, 1);
				break;
			}
			case "31520-5.htm":
			{
				st.setCond(7, true);
				takeItems(player, JEWEL_BOX, 1);
				takeItems(player, MYSTERIOUS_CLOTH, 1);
				takeItems(player, SEWING_KIT, 1);
				break;
			}
			case "31520-7.htm":
			{
				takeItems(player, DRESS_SHOES_BOX, 1);
				giveItems(player, FORMAL_WEAR, 1);
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
				htmltext = (player.getLevel() < 60) ? "30842-0a.htm" : "30842-0.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case ALEXIS:
					{
						if (cond == 1)
						{
							htmltext = "30842-2.htm";
						}
						break;
					}
					case LEIKAR:
					{
						if (cond == 1)
						{
							htmltext = "31520-0.htm";
						}
						else if (cond == 2)
						{
							htmltext = "31520-1a.htm";
						}
						else if ((cond == 5) || (cond == 6))
						{
							if (hasQuestItems(player, MYSTERIOUS_CLOTH, JEWEL_BOX, SEWING_KIT))
							{
								htmltext = "31520-4.htm";
							}
							else if (hasQuestItems(player, BOX_OF_COOKIES))
							{
								htmltext = "31520-2.htm";
							}
							else
							{
								htmltext = "31520-3a.htm";
							}
						}
						else if (cond == 7)
						{
							htmltext = (hasQuestItems(player, DRESS_SHOES_BOX)) ? "31520-6.htm" : "31520-5a.htm";
						}
						break;
					}
					case JEREMY:
					{
						if (hasQuestItems(player, SIGNET_RING))
						{
							htmltext = "31521-0.htm";
						}
						else if (cond == 3)
						{
							htmltext = "31521-1a.htm";
						}
						else if (cond == 4)
						{
							htmltext = "31521-2.htm";
						}
						else if (cond > 4)
						{
							htmltext = "31521-3a.htm";
						}
						break;
					}
					case MIST:
					{
						if (cond == 3)
						{
							htmltext = "31627-0.htm";
						}
						else if (cond > 3)
						{
							htmltext = "31627-2.htm";
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
