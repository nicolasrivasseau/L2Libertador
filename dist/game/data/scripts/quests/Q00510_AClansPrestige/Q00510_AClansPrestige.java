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
package quests.Q00510_AClansPrestige;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

public class Q00510_AClansPrestige extends Quest
{
	// NPC
	private static final int VALDIS = 31331;
	// Quest Item
	private static final int CLAW = 8767;
	// Reward
	private static final int CLAN_POINTS_REWARD = 50; // Quantity of points
	
	public Q00510_AClansPrestige()
	{
		super(510);
		registerQuestItems(CLAW);
		addStartNpc(VALDIS);
		addTalkId(VALDIS);
		addKillId(22215, 22216, 22217);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31331-3.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31331-6.htm"))
		{
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
				if (!player.isClanLeader())
				{
					st.exitQuest(true);
					htmltext = "31331-0.htm";
				}
				else if (player.getClan().getLevel() < 5)
				{
					st.exitQuest(true);
					htmltext = "31331-0.htm";
				}
				else
				{
					htmltext = "31331-1.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					final int count = getQuestItemsCount(player, CLAW);
					if (count > 0)
					{
						final int reward = (CLAN_POINTS_REWARD * count);
						takeItems(player, CLAW, -1);
						final Clan clan = player.getClan();
						clan.addReputationScore(reward);
						player.sendPacket(new SystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_S_REPUTATION_SCORE).addInt(reward));
						clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
						htmltext = "31331-7.htm";
					}
					else
					{
						htmltext = "31331-4.htm";
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
		// Retrieve the qs of the clan leader.
		final QuestState st = getClanLeaderQuestState(player, npc);
		if ((st == null) || !st.isStarted())
		{
			return null;
		}
		
		giveItems(player, CLAW, 1);
		playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		
		return null;
	}
}
