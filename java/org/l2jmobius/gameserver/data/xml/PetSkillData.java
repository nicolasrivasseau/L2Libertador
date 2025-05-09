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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.skill.Skill;

/**
 * @author Mobius
 */
public class PetSkillData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(PetSkillData.class.getName());
	private final Map<Integer, Map<Long, SkillHolder>> _skillTrees = new HashMap<>();
	
	protected PetSkillData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_skillTrees.clear();
		parseDatapackFile("data/PetSkillData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _skillTrees.size() + " skills.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("skill".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final int npcId = parseInteger(attrs, "npcId");
						final int skillId = parseInteger(attrs, "skillId");
						final int skillLevel = parseInteger(attrs, "skillLevel");
						Map<Long, SkillHolder> skillTree = _skillTrees.get(npcId);
						if (skillTree == null)
						{
							skillTree = new HashMap<>();
							_skillTrees.put(npcId, skillTree);
						}
						
						if (SkillData.getInstance().getSkill(skillId, skillLevel == 0 ? 1 : skillLevel) != null)
						{
							skillTree.put((long) SkillData.getSkillHashCode(skillId, skillLevel + 1), new SkillHolder(skillId, skillLevel));
						}
						else
						{
							LOGGER.info(getClass().getSimpleName() + ": Could not find skill with id " + skillId + ", level " + skillLevel + " for NPC " + npcId + ".");
						}
					}
				}
			}
		}
	}
	
	public int getAvailableLevel(Summon pet, int skillId)
	{
		int level = 0;
		if (!_skillTrees.containsKey(pet.getId()))
		{
			// LOGGER.warning(getClass().getSimpleName() + ": Pet id " + pet.getId() + " does not have any skills assigned.");
			return level;
		}
		
		for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values())
		{
			if (skillHolder.getSkillId() != skillId)
			{
				continue;
			}
			if (skillHolder.getSkillLevel() == 0)
			{
				if (pet.getLevel() < 70)
				{
					level = pet.getLevel() / 10;
					if (level <= 0)
					{
						level = 1;
					}
				}
				else
				{
					level = 7 + ((pet.getLevel() - 70) / 5);
				}
				
				// formula usable for skill that have 10 or more skill levels
				final int maxLevel = SkillData.getInstance().getMaxLevel(skillHolder.getSkillId());
				if (level > maxLevel)
				{
					level = maxLevel;
				}
				break;
			}
			else if ((1 <= pet.getLevel()) && (skillHolder.getSkillLevel() > level))
			{
				level = skillHolder.getSkillLevel();
			}
		}
		
		return level;
	}
	
	public List<Integer> getAvailableSkills(Summon pet)
	{
		final List<Integer> skillIds = new ArrayList<>();
		if (!_skillTrees.containsKey(pet.getId()))
		{
			// LOGGER.warning(getClass().getSimpleName() + ": Pet id " + pet.getId() + " does not have any skills assigned.");
			return skillIds;
		}
		
		for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values())
		{
			if (skillIds.contains(skillHolder.getSkillId()))
			{
				continue;
			}
			
			skillIds.add(skillHolder.getSkillId());
		}
		
		return skillIds;
	}
	
	public List<Skill> getKnownSkills(Summon pet)
	{
		final List<Skill> skills = new ArrayList<>();
		if (!_skillTrees.containsKey(pet.getId()))
		{
			return skills;
		}
		
		for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values())
		{
			final Skill skill = skillHolder.getSkill();
			if (skills.contains(skill))
			{
				continue;
			}
			
			skills.add(skill);
		}
		
		return skills;
	}
	
	public Skill getKnownSkill(Summon pet, int skillId)
	{
		if (!_skillTrees.containsKey(pet.getId()))
		{
			return null;
		}
		
		for (SkillHolder skillHolder : _skillTrees.get(pet.getId()).values())
		{
			if (skillHolder.getSkillId() == skillId)
			{
				return skillHolder.getSkill();
			}
		}
		
		return null;
	}
	
	public static PetSkillData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PetSkillData INSTANCE = new PetSkillData();
	}
}
