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
package custom.SubclassSkillNpc;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import ai.AbstractNpcAI;
import java.util.HashMap;
import java.util.Map;

public class SubclassSkillNPC extends AbstractNpcAI {
	private static final int MAX_SKILLS_PER_SUBCLASS = 2;

	private static final Map<Integer, int[][]> subclassSkills = new HashMap<>();

	static {
		// Segunda profesión - Todas las 31 subclases
		subclassSkills.put(1, new int[][] { {1001, 1}, {1002, 1}, {1003, 1}, {1004, 1} }); // Paladin
		subclassSkills.put(2, new int[][] { {1011, 1}, {1012, 1}, {1013, 1}, {1014, 1} }); // Dark Avenger
		subclassSkills.put(3, new int[][] { {1021, 1}, {1022, 1}, {1023, 1}, {1024, 1} }); // Temple Knight
		subclassSkills.put(4, new int[][] { {1031, 1}, {1032, 1}, {1033, 1}, {1034, 1} }); // Shillien Knight
		subclassSkills.put(5, new int[][] { {1041, 1}, {1042, 1}, {1043, 1}, {1044, 1} }); // Treasure Hunter
		subclassSkills.put(6, new int[][] { {1051, 1}, {1052, 1}, {1053, 1}, {1054, 1} }); // Plains Walker
		subclassSkills.put(7, new int[][] { {1061, 1}, {1062, 1}, {1063, 1}, {1064, 1} }); // Abyss Walker
		subclassSkills.put(8, new int[][] { {1071, 1}, {1072, 1}, {1073, 1}, {1074, 1} }); // Hawkeye
		subclassSkills.put(9, new int[][] { {1081, 1}, {1082, 1}, {1083, 1}, {1084, 1} }); // Silver Ranger
		subclassSkills.put(10, new int[][] { {1091, 1}, {1092, 1}, {1093, 1}, {1094, 1} }); // Phantom Ranger
		subclassSkills.put(11, new int[][] { {1101, 1}, {1102, 1}, {1103, 1}, {1104, 1} }); // Warlock
		subclassSkills.put(12, new int[][] { {1111, 1}, {1112, 1}, {1113, 1}, {1114, 1} }); // Elemental Summoner
		subclassSkills.put(13, new int[][] { {1121, 1}, {1122, 1}, {1123, 1}, {1124, 1} }); // Phantom Summoner
		subclassSkills.put(14, new int[][] { {1131, 1}, {1132, 1}, {1133, 1}, {1134, 1} }); // Bishop
		subclassSkills.put(15, new int[][] { {1141, 1}, {1142, 1}, {1143, 1}, {1144, 1} }); // Prophet
		subclassSkills.put(16, new int[][] { {1151, 1}, {1152, 1}, {1153, 1}, {1154, 1} }); // Elven Elder
		subclassSkills.put(17, new int[][] { {1161, 1}, {1162, 1}, {1163, 1}, {1164, 1} }); // Shillien Elder
		subclassSkills.put(18, new int[][] { {1171, 1}, {1172, 1}, {1173, 1}, {1174, 1} }); // Overlord
		subclassSkills.put(19, new int[][] { {1181, 1}, {1182, 1}, {1183, 1}, {1184, 1} }); // Warcryer
		subclassSkills.put(20, new int[][] { {1191, 1}, {1192, 1}, {1193, 1}, {1194, 1} }); // Destroyer
		subclassSkills.put(21, new int[][] { {1201, 1}, {1202, 1}, {1203, 1}, {1204, 1} }); // Tyrant
		subclassSkills.put(22, new int[][] { {1211, 1}, {1212, 1}, {1213, 1}, {1214, 1} }); // Warlord
		subclassSkills.put(23, new int[][] { {1221, 1}, {1222, 1}, {1223, 1}, {1224, 1} }); // Gladiator
		subclassSkills.put(24, new int[][] { {1231, 1}, {1232, 1}, {1233, 1}, {1234, 1} }); // Sorcerer
		subclassSkills.put(25, new int[][] { {1241, 1}, {1242, 1}, {1243, 1}, {1244, 1} }); // Necromancer
		subclassSkills.put(26, new int[][] { {1251, 1}, {1252, 1}, {1253, 1}, {1254, 1} }); // Spellsinger
		subclassSkills.put(27, new int[][] { {1261, 1}, {1262, 1}, {1263, 1}, {1264, 1} }); // Spellhowler
		subclassSkills.put(28, new int[][] { {1271, 1}, {1272, 1}, {1273, 1}, {1274, 1} }); // Bounty Hunter
		subclassSkills.put(29, new int[][] { {1281, 1}, {1282, 1}, {1283, 1}, {1284, 1} }); // Warsmith
	}

	private SubclassSkillNPC() {
		addStartNpc(SUBCLASSSKILLNPC_NPCID);
		addTalkId(SUBCLASSSKILLNPC_NPCID);
		addFirstTalkId(SUBCLASSSKILLNPC_NPCID);
	}

	private String showSkillSelection(Npc npc, Player player) {
		NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append("<br>Selecciona hasta 2 habilidades por subclase:<br>");

		for (int subclassId : player.getSubClasses().keySet()) {
			if (subclassSkills.containsKey(subclassId)) {
				int count = 0;
				for (int[] skill : subclassSkills.get(subclassId)) {
					if (count >= MAX_SKILLS_PER_SUBCLASS) break;
					sb.append("<button value=\"").append(SkillData.getInstance().getSkill(skill[0], skill[1]).getName())
							.append("\" action=\"bypass -h npc_").append(npc.getObjectId())
							.append("_select_skill_").append(skill[0]).append("\" width=200 height=25><br>");
					count++;
				}
			}
		}

		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return null;
	}

	public void giveSkillToPlayer(Player player, int skillId) {
		Skill skill = SkillData.getInstance().getSkill(skillId, 1);
		if (skill != null) {
			player.addSkill(skill, false);
			player.sendMessage("Has recibido la habilidad: " + skill.getName());
		}
	}




	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return showSkillSelection(npc, player);
	}
	
	public static void main(String[] args)
	{
		new SubclassSkillNPC();
	}
}
