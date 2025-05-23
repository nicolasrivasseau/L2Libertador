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
package org.l2jmobius.gameserver.instancemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.xml.ItemData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.enums.PrivateStoreType;
import org.l2jmobius.gameserver.handler.CommunityBoardHandler;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SellBuffHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2jmobius.gameserver.util.HtmlUtil;
import org.l2jmobius.gameserver.util.Util;

/**
 * Sell Buffs Manager
 * @author St3eT
 */
public class SellBuffsManager implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(SellBuffsManager.class.getName());
	private static final Set<Integer> ALLOWED_BUFFS = new HashSet<>();
	private static final String HTML_FOLDER = "data/html/mods/SellBuffs/";
	
	protected SellBuffsManager()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (Config.SELLBUFF_ENABLED)
		{
			ALLOWED_BUFFS.clear();
			parseDatapackFile("data/SellBuffData.xml");
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + ALLOWED_BUFFS.size() + " allowed buffs.");
		}
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		final NodeList node = doc.getDocumentElement().getElementsByTagName("skill");
		for (int i = 0; i < node.getLength(); ++i)
		{
			final Element elem = (Element) node.item(i);
			final int skillId = Integer.parseInt(elem.getAttribute("id"));
			ALLOWED_BUFFS.add(skillId);
		}
	}
	
	public void sendSellMenu(Player player)
	{
		final String html = HtmCache.getInstance().getHtm(player, HTML_FOLDER + (player.isSellingBuffs() ? "BuffMenu_already.html" : "BuffMenu.html"));
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	public void sendBuffChoiceMenu(Player player, int index)
	{
		String html = HtmCache.getInstance().getHtm(player, HTML_FOLDER + "BuffChoice.html");
		html = html.replace("%list%", buildSkillMenu(player, index));
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	public void sendBuffEditMenu(Player player)
	{
		String html = HtmCache.getInstance().getHtm(player, HTML_FOLDER + "BuffChoice.html");
		html = html.replace("%list%", buildEditMenu(player));
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	public void sendBuffMenu(Player player, Player seller, int index)
	{
		if (!seller.isSellingBuffs() || seller.getSellingBuffs().isEmpty())
		{
			return;
		}
		
		String html = HtmCache.getInstance().getHtm(player, HTML_FOLDER + "BuffBuyMenu.html");
		html = html.replace("%list%", buildBuffMenu(seller, index));
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	public void startSellBuffs(Player player, String title)
	{
		player.sitDown();
		player.setSellingBuffs(true);
		player.setPrivateStoreType(PrivateStoreType.PACKAGE_SELL);
		player.getSellList().setTitle(title);
		player.getSellList().setPackaged(true);
		player.broadcastUserInfo();
		
		player.broadcastPacket(new PrivateStoreMsgSell(player));
		player.sendPacket(new PrivateStoreMsgSell(player));
		
		sendSellMenu(player);
	}
	
	public void stopSellBuffs(Player player)
	{
		player.setSellingBuffs(false);
		player.setPrivateStoreType(PrivateStoreType.NONE);
		player.standUp();
		player.broadcastUserInfo();
		sendSellMenu(player);
	}
	
	private String buildBuffMenu(Player seller, int index)
	{
		final int ceiling = 9;
		int nextIndex = -1;
		int previousIndex = -1;
		int emptyFields = 0;
		final StringBuilder sb = new StringBuilder();
		final List<SellBuffHolder> sellList = new ArrayList<>();
		
		int count = 0;
		for (SellBuffHolder holder : seller.getSellingBuffs())
		{
			count++;
			if ((count > index) && (count <= (ceiling + index)))
			{
				sellList.add(holder);
			}
		}
		
		if ((count > 9) && (count > (index + 9)))
		{
			nextIndex = index + 9;
		}
		
		if (index >= 9)
		{
			previousIndex = index - 9;
		}
		
		emptyFields = ceiling - sellList.size();
		
		sb.append("<br>");
		sb.append(HtmlUtil.getMpGauge(250, (long) seller.getCurrentMp(), seller.getMaxMp(), false));
		sb.append("<br>");
		
		sb.append("<table>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr>");
		sb.append("<td fixwidth=\"20\"></td>");
		sb.append("<td> <button action=\"\" value=\"Icon\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Icon
		sb.append("<td> <button action=\"\" value=\"Name\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Name
		sb.append("<td> <button action=\"\" value=\"Level\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Leve
		sb.append("<td> <button action=\"\" value=\"MP Cost\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Price
		sb.append("<td> <button action=\"\" value=\"Price\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Price
		sb.append("<td> <button action=\"\" value=\"Action\" width=95 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Action
		sb.append("<td fixwidth=\"20\"></td>");
		sb.append("</tr>");
		
		for (SellBuffHolder holder : sellList)
		{
			final Skill skill = seller.getKnownSkill(holder.getSkillId());
			if (skill == null)
			{
				emptyFields++;
				continue;
			}
			
			final ItemTemplate item = ItemData.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
			
			sb.append("<tr>");
			sb.append("<td fixwidth=\"20\"></td>");
			sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
			sb.append("<td align=center>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
			sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillData.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>");
			sb.append("<td align=center> <font color=\"1E90FF\">" + (skill.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER) + "</font></td>");
			sb.append("<td align=center> " + Util.formatAdena(holder.getPrice()) + " <font color=\"LEVEL\"> " + (item != null ? item.getName() : "") + "</font> </td>");
			sb.append("<td align=center fixwidth=\"40\"><button value=\"Buy Buff\" action=\"bypass sellbuffbuyskill " + seller.getObjectId() + " " + skill.getId() + " " + index + "\" width=\"95\" height=\"21\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
			sb.append("</tr>");
			sb.append("<tr><td><br></td></tr>");
		}
		
		for (int i = 0; i < emptyFields; i++)
		{
			sb.append("<tr>");
			sb.append("<td fixwidth=\"20\" height=\"32\"></td>");
			sb.append("<td align=center></td>");
			sb.append("<td align=left></td>");
			sb.append("<td align=center></td>");
			sb.append("<td align=center></font></td>");
			sb.append("<td align=center></td>");
			sb.append("<td align=center fixwidth=\"50\"></td>");
			sb.append("</tr>");
			sb.append("<tr><td><br></td></tr>");
		}
		
		sb.append("</table>");
		
		sb.append("<table width=\"250\" border=\"0\">");
		sb.append("<tr>");
		
		if (previousIndex > -1)
		{
			sb.append("<td align=left><button value=\"Previous Page\" action=\"bypass sellbuffbuymenu " + seller.getObjectId() + " " + previousIndex + "\" width=\"95\" height=\"21\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		}
		
		if (nextIndex > -1)
		{
			sb.append("<td align=right><button value=\"Next Page\" action=\"bypass sellbuffbuymenu " + seller.getObjectId() + " " + nextIndex + "\" width=\"95\" height=\"21\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		}
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	private String buildEditMenu(Player player)
	{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<table>");
		sb.append("<tr>");
		sb.append("<td> <button action=\"\" value=\"Icon\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Icon
		sb.append("<td> <button action=\"\" value=\"Name\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Name
		sb.append("<td> <button action=\"\" value=\"Level\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Level
		sb.append("<td> <button action=\"\" value=\"Old Price\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Old price
		sb.append("<td> <button action=\"\" value=\"New Price\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // New price
		sb.append("<td> <button action=\"\" value=\"Action\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Change Price
		sb.append("<td> <button action=\"\" value=\"Remove\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Remove Buff
		sb.append("</tr>");
		
		if (player.getSellingBuffs().isEmpty())
		{
			sb.append("</table>");
			sb.append("<br><br><br>");
			sb.append("You don't have added any buffs yet!");
		}
		else
		{
			for (SellBuffHolder holder : player.getSellingBuffs())
			{
				final Skill skill = player.getKnownSkill(holder.getSkillId());
				if (skill == null)
				{
					continue;
				}
				
				sb.append("<tr>");
				sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>"); // Icon
				sb.append("<td align=center>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>")); // Name + enchant
				sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillData.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>"); // Level
				sb.append("<td align=center> " + Util.formatAdena(holder.getPrice()) + " </td>"); // Price show
				sb.append("<td align=center><edit var=\"price_" + skill.getId() + "\" width=100 type=\"number\"></td>"); // Price edit
				sb.append("<td align=center><button value=\"Edit\" action=\"bypass sellbuffchangeprice " + skill.getId() + " $price_" + skill.getId() + "\" width=\"70\" height=\"20\" back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
				sb.append("<td align=center><button value=\" X \" action=\"bypass sellbuffremove " + skill.getId() + "\" width=\"40\" height=\"20\" back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
		}
		
		return sb.toString();
	}
	
	private String buildSkillMenu(Player player, int index)
	{
		final int ceiling = index + 9;
		int nextIndex = -1;
		int previousIndex = -1;
		final StringBuilder sb = new StringBuilder();
		final List<Skill> skillList = new ArrayList<>();
		
		int count = 0;
		for (Skill skill : player.getAllSkills())
		{
			if (ALLOWED_BUFFS.contains(skill.getId()) && !isInSellList(player, skill))
			{
				count++;
				
				if ((count > index) && (count <= ceiling))
				{
					skillList.add(skill);
				}
			}
		}
		
		if ((count > 9) && (count > (index + 9)))
		{
			nextIndex = index + 9;
		}
		
		if (index >= 9)
		{
			previousIndex = index - 9;
		}
		
		sb.append("<table>");
		sb.append("<tr><td><br></td></tr>");
		sb.append("<tr>");
		sb.append("<td fixwidth=\"20\"></td>");
		sb.append("<td> <button action=\"\" value=\"Icon\" width=80 height=23 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Icon
		sb.append("<td> <button action=\"\" value=\"Name\" width=100 height=23 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Name
		sb.append("<td> <button action=\"\" value=\"Level\" width=70 height=23 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Leve
		sb.append("<td> <button action=\"\" value=\"Price\" width=100 height=23 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Price
		sb.append("<td> <button action=\"\" value=\"Action\" width=110 height=23 back=\"sek.cbui94\" fore=\"sek.cbui92\"> </td>"); // Action
		sb.append("<td fixwidth=\"40\"></td>");
		sb.append("</tr>");
		
		if (skillList.isEmpty())
		{
			sb.append("</table>");
			sb.append("<br><br><br>");
			sb.append("At this moment you cannot add any buffs!");
		}
		else
		{
			for (Skill skill : skillList)
			{
				sb.append("<tr>");
				sb.append("<td fixwidth=\"20\"></td>");
				sb.append("<td align=center><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
				sb.append("<td align=center>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
				sb.append("<td align=center>" + ((skill.getLevel() > 100) ? SkillData.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()) + "</td>");
				sb.append("<td align=left><edit var=\"price_" + skill.getId() + "\" width=100 type=\"number\"></td>");
				sb.append("<td align=center><button value=\" Add Buff \" action=\"bypass sellbuffaddskill " + skill.getId() + " $price_" + skill.getId() + "\" width=\"110\" height=\"20\" back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
				sb.append("</tr>");
				sb.append("<tr><td><br></td></tr>");
			}
			sb.append("</table>");
		}
		
		sb.append("<table width=\"250\" border=\"0\">");
		sb.append("<tr>");
		
		if (previousIndex > -1)
		{
			sb.append("<td align=left><button value=\"Previous Page\" action=\"bypass sellbuffadd " + previousIndex + "\"  width=\"95\" height=\"21\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		}
		
		if (nextIndex > -1)
		{
			sb.append("<td align=right><button value=\"Next Page\" action=\"bypass sellbuffadd " + nextIndex + "\" width=\"95\" height=\"21\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		}
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	public boolean isInSellList(Player player, Skill skill)
	{
		for (SellBuffHolder holder : player.getSellingBuffs())
		{
			if (holder.getSkillId() == skill.getId())
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canStartSellBuffs(Player player)
	{
		if (player.isAlikeDead())
		{
			player.sendMessage("You can't sell buffs in fake death!");
			return false;
		}
		else if (player.isInOlympiadMode() || Olympiad.getInstance().isRegistered(player))
		{
			player.sendMessage("You can't sell buffs with Olympiad status!");
			return false;
		}
		else if (player.isRegisteredOnEvent())
		{
			player.sendMessage("You can't sell buffs while registered in an event!");
			return false;
		}
		else if (player.isCursedWeaponEquipped() || (player.getKarma() > 0))
		{
			player.sendMessage("You can't sell buffs in Chaotic state!");
			return false;
		}
		else if (player.isInDuel())
		{
			player.sendMessage("You can't sell buffs in Duel state!");
			return false;
		}
		else if (player.isFishing())
		{
			player.sendMessage("You can't sell buffs while fishing.");
			return false;
		}
		else if (player.isMounted() || player.isFlying())
		{
			player.sendMessage("You can't sell buffs in Mount state!");
			return false;
		}
		else if (player.isInsideZone(ZoneId.NO_STORE) || !player.isInsideZone(ZoneId.PEACE) || player.isJailed())
		{
			player.sendMessage("You can't sell buffs here!");
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the single instance of {@code SellBuffsManager}.
	 * @return single instance of {@code SellBuffsManager}
	 */
	public static SellBuffsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SellBuffsManager INSTANCE = new SellBuffsManager();
	}
}