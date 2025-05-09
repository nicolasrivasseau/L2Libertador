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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.enums.HtmlActionScope;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * TutorialShowHtml server packet implementation.
 * @author HorridoJoho
 */
public class TutorialShowHtml extends AbstractHtmlPacket
{
	public TutorialShowHtml(String html)
	{
		super(html);
	}
	
	/**
	 * This constructor is just here to be able to show a tutorial html<br>
	 * window bound to an npc.
	 * @param npcObjId
	 * @param html
	 */
	public TutorialShowHtml(int npcObjId, String html)
	{
		super(npcObjId, html);
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.TUTORIAL_SHOW_HTML.writeId(this, buffer);
		buffer.writeString(getHtml());
	}
	
	@Override
	public HtmlActionScope getScope()
	{
		return HtmlActionScope.TUTORIAL_HTML;
	}
}