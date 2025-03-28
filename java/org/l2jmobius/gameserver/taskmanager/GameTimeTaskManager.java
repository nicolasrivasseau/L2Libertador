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
package org.l2jmobius.gameserver.taskmanager;

import java.util.Calendar;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.enums.SkillFinishType;
import org.l2jmobius.gameserver.instancemanager.DayNightSpawnManager;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.OnDayNightChange;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * GameTime task manager class.
 * @author Forsaiken, Mobius
 */
public class GameTimeTaskManager extends Thread
{
	public static final int TICKS_PER_SECOND = 10; // Not able to change this without checking through code.
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	public static final int IG_DAYS_PER_DAY = 6;
	public static final int MILLIS_PER_IG_DAY = (3600000 * 24) / IG_DAYS_PER_DAY;
	public static final int SECONDS_PER_IG_DAY = MILLIS_PER_IG_DAY / 1000;
	public static final int TICKS_PER_IG_DAY = SECONDS_PER_IG_DAY * TICKS_PER_SECOND;
	
	private final long _referenceTime;
	private boolean _isNight;
	private int _gameTicks;
	private int _gameTime;
	private int _gameHour;
	
	protected GameTimeTaskManager()
	{
		super("GameTimeTaskManager");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		_referenceTime = c.getTimeInMillis();
		
		super.start();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			_gameTicks = (int) ((System.currentTimeMillis() - _referenceTime) / MILLIS_IN_TICK);
			_gameTime = (_gameTicks % TICKS_PER_IG_DAY) / MILLIS_IN_TICK;
			_gameHour = _gameTime / 60;
			
			if ((_gameHour < 6) != _isNight)
			{
				_isNight = !_isNight;
				
				// Shadow Sense skill for Dark Elf players.
				for (Player player : World.getInstance().getPlayers())
				{
					if (player.getRace() == Race.DARK_ELF)
					{
						final Skill shadowSense = player.getKnownSkill(CommonSkill.SHADOW_SENSE.getId());
						if (shadowSense != null)
						{
							if (_isNight)
							{
								// It is night, apply the skill.
								player.sendPacket(new SystemMessage(SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(shadowSense));
								player.addSkill(shadowSense, false);
							}
							else
							{
								// It is day, remove the skill.
								player.sendPacket(new SystemMessage(SystemMessageId.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(shadowSense));
								player.getEffectList().stopSkillEffects(SkillFinishType.NORMAL, shadowSense);
							}
						}
					}
				}
				
				ThreadPool.execute(() -> DayNightSpawnManager.getInstance().notifyChangeMode());
				
				if (EventDispatcher.getInstance().hasListener(EventType.ON_DAY_NIGHT_CHANGE))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnDayNightChange(_isNight));
				}
			}
			
			try
			{
				Thread.sleep(MILLIS_IN_TICK);
			}
			catch (InterruptedException e)
			{
				// Ignore.
			}
		}
	}
	
	public boolean isNight()
	{
		return _isNight;
	}
	
	/**
	 * @return The actual GameTime tick. Directly taken from current time.
	 */
	public int getGameTicks()
	{
		return _gameTicks;
	}
	
	public int getGameTime()
	{
		return _gameTime;
	}
	
	public int getGameHour()
	{
		return _gameHour;
	}
	
	public int getGameMinute()
	{
		return _gameTime % 60;
	}
	
	public static final GameTimeTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameTimeTaskManager INSTANCE = new GameTimeTaskManager();
	}
}