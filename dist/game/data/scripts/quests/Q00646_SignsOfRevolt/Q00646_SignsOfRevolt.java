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
package quests.Q00646_SignsOfRevolt;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00646_SignsOfRevolt extends Quest
{
	// NPC
	private static final int TORRANT = 32016;
	// Item
	private static final int CURSED_DOLL = 8087;
	// Rewards
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{1880, 9},
		{1881, 12},
		{1882, 20},
		{57, 21600}
		// @formatter:on
	};
	
	public Q00646_SignsOfRevolt()
	{
		super(646);
		registerQuestItems(CURSED_DOLL);
		addStartNpc(TORRANT);
		addTalkId(TORRANT);
		addKillId(22029, 22030, 22031, 22032, 22033, 22034, 22035, 22036, 22037, 22038, 22039, 22040, 22041, 22042, 22043, 22044, 22045, 22047, 22049);
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
		
		if (event.equals("32016-03.htm"))
		{
			st.startQuest();
		}
		else if (Util.isDigit(event))
		{
			htmltext = "32016-07.htm";
			takeItems(player, CURSED_DOLL, -1);
			
			final int[] reward = REWARDS[Integer.parseInt(event)];
			giveItems(player, reward[0], reward[1]);
			
			st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 40) ? "32016-02.htm" : "32016-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "32016-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "32016-05.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getRandomPartyMemberState(player, 1, 3, npc);
		if (qs == null)
		{
			return null;
		}
		final Player partyMember = qs.getPlayer();
		
		final QuestState st = getQuestState(partyMember, false);
		if (st == null)
		{
			return null;
		}
		
		if (getRandom(100) < 75)
		{
			giveItems(partyMember, CURSED_DOLL, 1);
			if (getQuestItemsCount(partyMember, CURSED_DOLL) < 180)
			{
				playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			else
			{
				st.setCond(2, true);
			}
		}
		
		return null;
	}
}
