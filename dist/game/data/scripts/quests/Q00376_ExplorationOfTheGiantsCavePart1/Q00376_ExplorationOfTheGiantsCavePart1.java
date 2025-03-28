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
package quests.Q00376_ExplorationOfTheGiantsCavePart1;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00376_ExplorationOfTheGiantsCavePart1 extends Quest
{
	// NPCs
	private static final int SOBLING = 31147;
	private static final int CLIFF = 30182;
	
	// Items
	private static final int PARCHMENT = 5944;
	private static final int DICTIONARY_BASIC = 5891;
	private static final int MYSTERIOUS_BOOK = 5890;
	private static final int DICTIONARY_INTERMEDIATE = 5892;
	private static final int[][] BOOKS =
	{
		// @formatter:off
		// medical theory -> tallum tunic, tallum stockings
		{5937, 5938, 5939, 5940, 5941},
		// architecture -> dark crystal leather, tallum leather
		{5932, 5933, 5934, 5935, 5936},
		// golem plans -> dark crystal breastplate, tallum plate
		{5922, 5923, 5924, 5925, 5926},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{5927, 5928, 5929, 5930, 5931}
		// @formatter:on
	};
	// Rewards
	private static final int[][] RECIPES =
	{
		// @formatter:off
		// medical theory -> tallum tunic, tallum stockings
		{5346, 5354},
		// architecture -> dark crystal leather, tallum leather
		{5332, 5334},
		// golem plans -> dark crystal breastplate, tallum plate
		{5416, 5418},
		// basics of magic -> dark crystal gaiters, dark crystal leggings
		{5424, 5340}
		// @formatter:on
	};
	
	public Q00376_ExplorationOfTheGiantsCavePart1()
	{
		super(376);
		registerQuestItems(DICTIONARY_BASIC, MYSTERIOUS_BOOK);
		addStartNpc(SOBLING);
		addTalkId(SOBLING, CLIFF);
		addKillId(20647, 20648, 20649, 20650);
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
			case "31147-03.htm":
			{
				st.startQuest();
				st.set("condBook", "1");
				giveItems(player, DICTIONARY_BASIC, 1);
				break;
			}
			case "31147-04.htm":
			{
				htmltext = checkItems(player, st);
				break;
			}
			case "31147-09.htm":
			{
				st.exitQuest(true, true);
				break;
			}
			case "30182-02.htm":
			{
				st.setCond(3, true);
				takeItems(player, MYSTERIOUS_BOOK, -1);
				giveItems(player, DICTIONARY_INTERMEDIATE, 1);
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
				htmltext = (player.getLevel() < 51) ? "31147-01.htm" : "31147-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case SOBLING:
					{
						htmltext = checkItems(player, st);
						break;
					}
					case CLIFF:
					{
						if ((cond == 2) && hasQuestItems(player, MYSTERIOUS_BOOK))
						{
							htmltext = "30182-01.htm";
						}
						else if (cond == 3)
						{
							htmltext = "30182-03.htm";
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
		// Drop parchment to anyone
		Player partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		QuestState st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		giveItemRandomly(partyMember, npc, PARCHMENT, 1, 0, 0.2, true);
		
		// Drop mysterious book to person who still need it
		partyMember = getRandomPartyMember(player, "condBook", "1");
		if (partyMember == null)
		{
			return null;
		}
		
		st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		if (Rnd.get(100d) < 0.1)
		{
			giveItems(partyMember, MYSTERIOUS_BOOK, 1);
			playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			st.unset("condBook");
		}
		
		return null;
	}
	
	private static String checkItems(Player player, QuestState st)
	{
		if (hasQuestItems(player, MYSTERIOUS_BOOK))
		{
			final int cond = st.getCond();
			if (cond == 1)
			{
				st.setCond(2, true);
				return "31147-07.htm";
			}
			return "31147-08.htm";
		}
		
		for (int type = 0; type < BOOKS.length; type++)
		{
			boolean complete = true;
			for (int book : BOOKS[type])
			{
				if (!hasQuestItems(player, book))
				{
					complete = false;
				}
			}
			
			if (complete)
			{
				for (int book : BOOKS[type])
				{
					takeItems(player, book, 1);
				}
				
				giveItems(player, RECIPES[type][getRandom(RECIPES[type].length)], 1);
				return "31147-04.htm";
			}
		}
		return "31147-05.htm";
	}
}
