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
package handlers.effecthandlers;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.conditions.Condition;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * Heal Over Time effect implementation.
 */
public class HealOverTime extends AbstractEffect
{
	private final double _power;
	
	public HealOverTime(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isDead() || effected.isDoor())
		{
			return false;
		}
		
		double hp = effected.getCurrentHp();
		final double maxhp = effected.getMaxRecoverableHp();
		
		// Not needed to set the HP and send update packet if player is already at max HP
		if (hp >= maxhp)
		{
			return false;
		}
		
		hp += _power * getTicksMultiplier();
		hp = Math.min(hp, maxhp);
		effected.setCurrentHp(hp);
		return skill.isToggle();
	}
	
	// Packet does not exist on C6?
	// @Override
	// public void onStart(Creature effector, Creature effected, Skill skill)
	// {
	// if (effected.isPlayer() && (getTicks() > 0) && (skill.getAbnormalType() == AbnormalType.HP_RECOVER))
	// {
	// // effected.sendPacket(new ExRegenMax(Formulas.calcEffectAbnormalTime(effector, effected, skill), getTicks(), _power));
	// }
	// }
}
