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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.gameserver.enums.PlayerCondOverride;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;

public class AttackRequest extends ClientPacket
{
	// cddddc
	private int _objectId;
	@SuppressWarnings("unused")
	private int _originX;
	@SuppressWarnings("unused")
	private int _originY;
	@SuppressWarnings("unused")
	private int _originZ;
	@SuppressWarnings("unused")
	private int _attackId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readInt();
		_originX = readInt();
		_originY = readInt();
		_originZ = readInt();
		_attackId = readByte(); // 0 for simple click 1 for shift-click
	}
	
	@Override
	protected void runImpl()
	{
		if (!getClient().getFloodProtectors().canPerformPlayerAction())
		{
			return;
		}
		
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Avoid Attacks in Boat.
		if (player.isPlayable() && player.isInBoat())
		{
			player.sendPacket(SystemMessageId.THIS_IS_NOT_ALLOWED_WHILE_RIDING_A_FERRY_OR_BOAT);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// avoid using expensive operations if not needed
		final WorldObject target;
		if (player.getTargetId() == _objectId)
		{
			target = player.getTarget();
		}
		else
		{
			target = World.getInstance().findObject(_objectId);
		}
		
		if (target == null)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!target.isTargetable() && !player.canOverrideCond(PlayerCondOverride.TARGET_ALL))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Players can't attack objects in the other instances
		// except from multiverse
		else if ((target.getInstanceId() != player.getInstanceId()) && (player.getInstanceId() != -1))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Only GMs can directly attack invisible characters
		else if (!target.isVisibleFor(player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.onActionRequest();
		
		if (player.getTarget() != target)
		{
			target.onAction(player);
		}
		else
		{
			if ((target.getObjectId() != player.getObjectId()) && !player.isInStoreMode() && (player.getActiveRequester() == null))
			{
				target.onForcedAttack(player);
			}
			else
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
}
