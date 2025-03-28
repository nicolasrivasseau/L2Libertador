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
package org.l2jmobius.gameserver.model.zone.type;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * A mother-trees zone Basic type zone for Hp, MP regen
 * @author durgus
 */
public class MotherTreeZone extends ZoneType
{
	private int _enterMsg;
	private int _leaveMsg;
	private int _mpRegen;
	private int _hpRegen;
	
	public MotherTreeZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("enterMsgId"))
		{
			_enterMsg = Integer.parseInt(value);
		}
		else if (name.equals("leaveMsgId"))
		{
			_leaveMsg = Integer.parseInt(value);
		}
		else if (name.equals("MpRegenBonus"))
		{
			_mpRegen = Integer.parseInt(value);
		}
		else if (name.equals("HpRegenBonus"))
		{
			_hpRegen = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			creature.setInsideZone(ZoneId.MOTHER_TREE, true);
			if (_enterMsg != 0)
			{
				player.sendPacket(new SystemMessage(_enterMsg));
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature.isPlayer())
		{
			final Player player = creature.asPlayer();
			player.setInsideZone(ZoneId.MOTHER_TREE, false);
			if (_leaveMsg != 0)
			{
				player.sendPacket(new SystemMessage(_leaveMsg));
			}
		}
	}
	
	/**
	 * @return the _mpRegen
	 */
	public int getMpRegenBonus()
	{
		return _mpRegen;
	}
	
	/**
	 * @return the _hpRegen
	 */
	public int getHpRegenBonus()
	{
		return _hpRegen;
	}
}
