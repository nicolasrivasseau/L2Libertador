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
package quests.Q00410_PathOfThePalusKnight;

import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

public class Q00410_PathOfThePalusKnight extends Quest
{
	// NPCs
	private static final int KALINTA = 30422;
	private static final int VIRGIL = 30329;
	// Monsters
	private static final int POISON_SPIDER = 20038;
	private static final int ARACHNID_TRACKER = 20043;
	private static final int LYCANTHROPE = 20049;
	// Items
	private static final int PALUS_TALISMAN = 1237;
	private static final int LYCANTHROPE_SKULL = 1238;
	private static final int VIRGIL_LETTER = 1239;
	private static final int MORTE_TALISMAN = 1240;
	private static final int PREDATOR_CARAPACE = 1241;
	private static final int ARACHNID_TRACKER_SILK = 1242;
	private static final int COFFIN_OF_ETERNAL_REST = 1243;
	private static final int GAZE_OF_ABYSS = 1244;
	
	public Q00410_PathOfThePalusKnight()
	{
		super(410);
		registerQuestItems(PALUS_TALISMAN, LYCANTHROPE_SKULL, VIRGIL_LETTER, MORTE_TALISMAN, PREDATOR_CARAPACE, ARACHNID_TRACKER_SILK, COFFIN_OF_ETERNAL_REST);
		addStartNpc(VIRGIL);
		addTalkId(VIRGIL, KALINTA);
		addKillId(POISON_SPIDER, ARACHNID_TRACKER, LYCANTHROPE);
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
			case "30329-05.htm":
			{
				if (player.getClassId() != ClassId.DARK_FIGHTER)
				{
					htmltext = (player.getClassId() == ClassId.PALUS_KNIGHT) ? "30329-02a.htm" : "30329-03.htm";
				}
				else if (player.getLevel() < 19)
				{
					htmltext = "30329-02.htm";
				}
				else if (hasQuestItems(player, GAZE_OF_ABYSS))
				{
					htmltext = "30329-04.htm";
				}
				break;
			}
			case "30329-06.htm":
			{
				st.startQuest();
				giveItems(player, PALUS_TALISMAN, 1);
				break;
			}
			case "30329-10.htm":
			{
				st.setCond(3, true);
				takeItems(player, LYCANTHROPE_SKULL, -1);
				takeItems(player, PALUS_TALISMAN, 1);
				giveItems(player, VIRGIL_LETTER, 1);
				break;
			}
			case "30422-02.htm":
			{
				st.setCond(4, true);
				takeItems(player, VIRGIL_LETTER, 1);
				giveItems(player, MORTE_TALISMAN, 1);
				break;
			}
			case "30422-06.htm":
			{
				st.setCond(6, true);
				takeItems(player, ARACHNID_TRACKER_SILK, -1);
				takeItems(player, MORTE_TALISMAN, 1);
				takeItems(player, PREDATOR_CARAPACE, -1);
				giveItems(player, COFFIN_OF_ETERNAL_REST, 1);
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
				htmltext = "30329-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case VIRGIL:
					{
						if (cond == 1)
						{
							htmltext = (!hasQuestItems(player, LYCANTHROPE_SKULL)) ? "30329-07.htm" : "30329-08.htm";
						}
						else if (cond == 2)
						{
							htmltext = "30329-09.htm";
						}
						else if ((cond > 2) && (cond < 6))
						{
							htmltext = "30329-12.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30329-11.htm";
							takeItems(player, COFFIN_OF_ETERNAL_REST, 1);
							giveItems(player, GAZE_OF_ABYSS, 1);
							addExpAndSp(player, 3200, 1500);
							player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
							st.exitQuest(true, true);
						}
						break;
					}
					case KALINTA:
					{
						if (cond == 3)
						{
							htmltext = "30422-01.htm";
						}
						else if (cond == 4)
						{
							if (!hasQuestItems(player, ARACHNID_TRACKER_SILK) || !hasQuestItems(player, PREDATOR_CARAPACE))
							{
								htmltext = "30422-03.htm";
							}
							else
							{
								htmltext = "30422-04.htm";
							}
						}
						else if (cond == 5)
						{
							htmltext = "30422-05.htm";
						}
						else if (cond == 6)
						{
							htmltext = "30422-06.htm";
						}
						break;
					}
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
		
		switch (npc.getId())
		{
			case LYCANTHROPE:
			{
				if (st.isCond(1))
				{
					giveItems(player, LYCANTHROPE_SKULL, 1);
					if (getQuestItemsCount(player, LYCANTHROPE_SKULL) < 13)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(2, true);
					}
				}
				break;
			}
			case ARACHNID_TRACKER:
			{
				if (st.isCond(4))
				{
					giveItems(player, ARACHNID_TRACKER_SILK, 1);
					if (getQuestItemsCount(player, ARACHNID_TRACKER_SILK) < 5)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else if (hasQuestItems(player, PREDATOR_CARAPACE))
					{
						st.setCond(5, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
			case POISON_SPIDER:
			{
				if (st.isCond(4))
				{
					giveItems(player, PREDATOR_CARAPACE, 1);
					if (getQuestItemsCount(player, ARACHNID_TRACKER_SILK) >= 5)
					{
						st.setCond(5, true);
					}
					else
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				break;
			}
		}
		
		return null;
	}
}
