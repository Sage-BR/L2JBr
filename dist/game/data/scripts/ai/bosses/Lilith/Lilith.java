/*
 * This file is part of the L2J Br project.
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
package ai.bosses.Lilith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.l2jbr.Config;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.commons.util.Rnd;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.instancemanager.GrandBossManager;
import org.l2jbr.gameserver.instancemanager.MapRegionManager;
import org.l2jbr.gameserver.instancemanager.ZoneManager;
import org.l2jbr.gameserver.model.Location;
import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.Spawn;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.TeleportWhereType;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.GrandBossInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SpawnHolder;
import org.l2jbr.gameserver.model.quest.QuestTimer;
import org.l2jbr.gameserver.model.skills.AbnormalType;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.zone.ZoneType;
import org.l2jbr.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * Lilith AI<br>
 * @author LasTravel<br>
 * @URL http://boards.lineage2.com/showpost.php?p=3386784&postcount=6<br>
 * @video https://www.youtube.com/watch?v=H3MuIwUjjD4
 */
public class Lilith extends AbstractNpcAI
{
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// NPCs
	private static final int LILITH = 29336;
	private static final int REMNANT = 19490; // Using custom NPC.
	private static final int ENTER_CUBIC = 31118;
	private static final int EXIST_CUBIC = 31124;
	private static final int LILITH_CUBIC = 31110;
	//@formatter:off
	private static final int[] LILITH_MINIONS = {29337, 29338, 29339};
	private static final int[] NECRO_MOBS = {29340, 29341, 29342, 29343, 29344, 29345, 29346, 29347};
	//@formatter:on	
	private static final int[] ALL_MOBS =
	{
		LILITH,
		LILITH_MINIONS[0],
		LILITH_MINIONS[1],
		NECRO_MOBS[0],
		NECRO_MOBS[1],
		NECRO_MOBS[2],
		NECRO_MOBS[3],
		NECRO_MOBS[4],
		NECRO_MOBS[5],
		NECRO_MOBS[6],
		NECRO_MOBS[7],
		REMNANT
	};
	// Spawns
	private static final List<SpawnHolder> SPAWNS = new ArrayList<>();
	static
	{
		SPAWNS.add(new SpawnHolder(29341, -16469, 13406, -4905, 32815, false));
		SPAWNS.add(new SpawnHolder(29340, -16756, 13397, -4905, 33094, false));
		SPAWNS.add(new SpawnHolder(29344, -16995, 13398, -4905, 32724, false));
		SPAWNS.add(new SpawnHolder(29342, -17261, 13395, -4905, 32885, false));
		SPAWNS.add(new SpawnHolder(29342, -16474, 13678, -4905, 32864, false));
		SPAWNS.add(new SpawnHolder(29343, -16739, 13675, -4905, 32886, false));
		SPAWNS.add(new SpawnHolder(29342, -15271, 13463, -4905, 16550, false));
		SPAWNS.add(new SpawnHolder(29340, -15273, 13750, -4905, 16456, false));
		SPAWNS.add(new SpawnHolder(29347, -15275, 13998, -4905, 16468, false));
		SPAWNS.add(new SpawnHolder(29345, -15604, 13454, -4905, 16662, false));
		SPAWNS.add(new SpawnHolder(29345, -15608, 13738, -4905, 16530, false));
		SPAWNS.add(new SpawnHolder(29347, -15597, 14014, -4905, 15968, false));
		SPAWNS.add(new SpawnHolder(29342, -15599, 14258, -4905, 16469, false));
		SPAWNS.add(new SpawnHolder(29347, -14242, 13505, -4903, 65400, false));
		SPAWNS.add(new SpawnHolder(29343, -14720, 13505, -4903, 32767, false));
		SPAWNS.add(new SpawnHolder(29345, -14477, 13321, -4903, 49151, false));
		SPAWNS.add(new SpawnHolder(29344, -14480, 13500, -4903, 18259, false));
		SPAWNS.add(new SpawnHolder(29345, -14482, 13734, -4903, 16473, false));
		SPAWNS.add(new SpawnHolder(29346, -13146, 13508, -4903, 42, false));
		SPAWNS.add(new SpawnHolder(29341, -13625, 13506, -4903, 32426, false));
		SPAWNS.add(new SpawnHolder(29341, -13390, 13261, -4903, 48922, false));
		SPAWNS.add(new SpawnHolder(29343, -13390, 13752, -4903, 15915, false));
		SPAWNS.add(new SpawnHolder(29347, -13379, 13508, -4903, 49621, false));
		SPAWNS.add(new SpawnHolder(29340, -13422, 15555, -4905, 65425, false));
		SPAWNS.add(new SpawnHolder(29347, -13404, 15224, -4905, 65263, false));
		SPAWNS.add(new SpawnHolder(29343, -13156, 15224, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29341, -12896, 15223, -4905, 65495, false));
		SPAWNS.add(new SpawnHolder(29346, -12624, 15220, -4905, 1401, false));
		SPAWNS.add(new SpawnHolder(29341, -11488, 15432, -4905, 49313, false));
		SPAWNS.add(new SpawnHolder(29340, -11490, 15132, -4905, 49082, false));
		SPAWNS.add(new SpawnHolder(29347, -11497, 14893, -4905, 48846, false));
		SPAWNS.add(new SpawnHolder(29344, -11485, 14647, -4905, 49660, false));
		SPAWNS.add(new SpawnHolder(29347, -11476, 14397, -4905, 49527, false));
		SPAWNS.add(new SpawnHolder(29344, -11079, 15682, -4905, 49406, false));
		SPAWNS.add(new SpawnHolder(29346, -11079, 15152, -4905, 49033, false));
		SPAWNS.add(new SpawnHolder(29343, -11077, 14912, -4905, 49238, false));
		SPAWNS.add(new SpawnHolder(29346, -11075, 14670, -4905, 49238, false));
		SPAWNS.add(new SpawnHolder(29342, -11073, 14347, -4905, 49707, false));
		SPAWNS.add(new SpawnHolder(29340, -8785, 13753, -4903, 16351, false));
		SPAWNS.add(new SpawnHolder(29344, -9026, 13498, -4903, 33142, false));
		SPAWNS.add(new SpawnHolder(29346, -8520, 13506, -4903, 151, false));
		SPAWNS.add(new SpawnHolder(29344, -8784, 13501, -4903, 33079, false));
		SPAWNS.add(new SpawnHolder(29341, -13393, 18350, -4903, 16498, false));
		SPAWNS.add(new SpawnHolder(29343, -13389, 17881, -4903, 49681, false));
		SPAWNS.add(new SpawnHolder(29342, -13635, 18105, -4903, 33289, false));
		SPAWNS.add(new SpawnHolder(29346, -13397, 18111, -4903, 0, false));
		SPAWNS.add(new SpawnHolder(29341, -12597, 18131, -4905, 49273, false));
		SPAWNS.add(new SpawnHolder(29344, -12593, 17897, -4905, 49330, false));
		SPAWNS.add(new SpawnHolder(29342, -12594, 17606, -4905, 49116, false));
		SPAWNS.add(new SpawnHolder(29342, -12595, 17367, -4905, 49108, false));
		SPAWNS.add(new SpawnHolder(29344, -12269, 18143, -4905, 48851, false));
		SPAWNS.add(new SpawnHolder(29342, -12270, 17872, -4905, 49113, false));
		SPAWNS.add(new SpawnHolder(29345, -12274, 17612, -4905, 48991, false));
		SPAWNS.add(new SpawnHolder(29345, -12279, 17337, -4905, 51622, false));
		SPAWNS.add(new SpawnHolder(29347, -13395, 19509, -4903, 17094, false));
		SPAWNS.add(new SpawnHolder(29342, -13388, 18895, -4903, 49301, false));
		SPAWNS.add(new SpawnHolder(29346, -13114, 19198, -4903, 65211, false));
		SPAWNS.add(new SpawnHolder(29344, -13383, 19196, -4903, 32845, false));
		SPAWNS.add(new SpawnHolder(29343, -13711, 19197, -4903, 32519, false));
		SPAWNS.add(new SpawnHolder(29343, -14781, 19199, -4903, 33074, false));
		SPAWNS.add(new SpawnHolder(29345, -14188, 19205, -4903, 867, false));
		SPAWNS.add(new SpawnHolder(29343, -14495, 19193, -4903, 32351, false));
		SPAWNS.add(new SpawnHolder(29342, -14481, 19503, -4903, 15913, false));
		SPAWNS.add(new SpawnHolder(29340, -14475, 18881, -4903, 49151, false));
		SPAWNS.add(new SpawnHolder(29345, -14477, 17797, -4903, 49700, false));
		SPAWNS.add(new SpawnHolder(29343, -14481, 18417, -4903, 16947, false));
		SPAWNS.add(new SpawnHolder(29347, -14178, 18110, -4903, 66, false));
		SPAWNS.add(new SpawnHolder(29343, -14476, 18110, -4903, 32767, false));
		SPAWNS.add(new SpawnHolder(29344, -16404, 15929, -4905, 15862, false));
		SPAWNS.add(new SpawnHolder(29340, -16401, 16194, -4905, 16265, false));
		SPAWNS.add(new SpawnHolder(29347, -16402, 16472, -4905, 16421, false));
		SPAWNS.add(new SpawnHolder(29346, -16405, 16721, -4905, 16509, false));
		SPAWNS.add(new SpawnHolder(29347, -16409, 16966, -4905, 16554, false));
		SPAWNS.add(new SpawnHolder(29347, -16415, 17223, -4905, 16627, false));
		SPAWNS.add(new SpawnHolder(29342, -16794, 15936, -4905, 16009, false));
		SPAWNS.add(new SpawnHolder(29344, -16786, 16208, -4905, 16077, false));
		SPAWNS.add(new SpawnHolder(29344, -16775, 16485, -4905, 15970, false));
		SPAWNS.add(new SpawnHolder(29342, -16781, 16725, -4905, 16644, false));
		SPAWNS.add(new SpawnHolder(29342, -16774, 17008, -4905, 16126, false));
		SPAWNS.add(new SpawnHolder(29346, -16773, 17266, -4905, 16343, false));
		SPAWNS.add(new SpawnHolder(29344, -19089, 17842, -4903, 49586, false));
		SPAWNS.add(new SpawnHolder(29345, -19092, 18111, -4903, 62852, false));
		SPAWNS.add(new SpawnHolder(29341, -18810, 18106, -4903, 65351, false));
		SPAWNS.add(new SpawnHolder(29345, -19085, 19523, -4903, 16324, false));
		SPAWNS.add(new SpawnHolder(29342, -19086, 18927, -4903, 49134, false));
		SPAWNS.add(new SpawnHolder(29345, -18789, 19197, -4903, 65226, false));
		SPAWNS.add(new SpawnHolder(29345, -19078, 19200, -4903, 32659, false));
		SPAWNS.add(new SpawnHolder(29340, -19367, 19195, -4903, 32767, false));
		SPAWNS.add(new SpawnHolder(29345, -16217, 19163, -4905, 15989, false));
		SPAWNS.add(new SpawnHolder(29346, -16208, 19432, -4905, 15967, false));
		SPAWNS.add(new SpawnHolder(29346, -16208, 19683, -4905, 16383, false));
		SPAWNS.add(new SpawnHolder(29342, -16532, 19180, -4905, 16218, false));
		SPAWNS.add(new SpawnHolder(29340, -16531, 19459, -4905, 16346, false));
		SPAWNS.add(new SpawnHolder(29343, -16531, 19704, -4905, 16383, false));
		SPAWNS.add(new SpawnHolder(29341, -19123, 19994, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29341, -18858, 19996, -4905, 78, false));
		SPAWNS.add(new SpawnHolder(29341, -18608, 19996, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29342, -18326, 19999, -4905, 110, false));
		SPAWNS.add(new SpawnHolder(29341, -19088, 20318, -4905, 175, false));
		SPAWNS.add(new SpawnHolder(29341, -18842, 20318, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29340, -18579, 20318, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29346, -18313, 20320, -4905, 78, false));
		SPAWNS.add(new SpawnHolder(29341, -18920, 21182, -4905, 15992, false));
		SPAWNS.add(new SpawnHolder(29346, -18918, 21455, -4905, 16307, false));
		SPAWNS.add(new SpawnHolder(29342, -18930, 21719, -4905, 16857, false));
		SPAWNS.add(new SpawnHolder(29345, -19239, 21188, -4905, 16562, false));
		SPAWNS.add(new SpawnHolder(29345, -19244, 21447, -4905, 16585, false));
		SPAWNS.add(new SpawnHolder(29344, -19240, 21994, -4905, 18219, false));
		SPAWNS.add(new SpawnHolder(29343, -17843, 23849, -4905, 49203, false));
		SPAWNS.add(new SpawnHolder(29343, -17842, 23583, -4905, 49191, false));
		SPAWNS.add(new SpawnHolder(29346, -17843, 23310, -4905, 49113, false));
		SPAWNS.add(new SpawnHolder(29345, -17526, 23552, -4905, 49003, false));
		SPAWNS.add(new SpawnHolder(29340, -17532, 23281, -4905, 48921, false));
		SPAWNS.add(new SpawnHolder(29340, -17526, 23019, -4905, 49390, false));
		SPAWNS.add(new SpawnHolder(29345, -19079, 24048, -4903, 16704, false));
		SPAWNS.add(new SpawnHolder(29347, -19090, 23519, -4903, 49362, false));
		SPAWNS.add(new SpawnHolder(29347, -19356, 23804, -4903, 32408, false));
		SPAWNS.add(new SpawnHolder(29344, -19093, 23808, -4903, 65106, false));
		SPAWNS.add(new SpawnHolder(29347, -18777, 23810, -4903, 347, false));
		SPAWNS.add(new SpawnHolder(29342, -14131, 23807, -4903, 64686, false));
		SPAWNS.add(new SpawnHolder(29341, -14769, 23804, -4903, 34020, false));
		SPAWNS.add(new SpawnHolder(29345, -14479, 23507, -4903, 50019, false));
		SPAWNS.add(new SpawnHolder(29340, -14479, 23799, -4903, 16383, false));
		SPAWNS.add(new SpawnHolder(29346, -14471, 24087, -4903, 15031, false));
		SPAWNS.add(new SpawnHolder(29340, -13060, 23806, -4903, 108, false));
		SPAWNS.add(new SpawnHolder(29343, -13392, 23807, -4903, 30946, false));
		SPAWNS.add(new SpawnHolder(29341, -13393, 24098, -4903, 15393, false));
		SPAWNS.add(new SpawnHolder(29341, -13390, 23480, -4903, 47204, false));
		SPAWNS.add(new SpawnHolder(29340, -12589, 23863, -4905, 48374, false));
		SPAWNS.add(new SpawnHolder(29345, -12591, 23560, -4905, 49083, false));
		SPAWNS.add(new SpawnHolder(29343, -12578, 23295, -4905, 49663, false));
		SPAWNS.add(new SpawnHolder(29344, -12582, 23026, -4905, 48996, false));
		SPAWNS.add(new SpawnHolder(29345, -12268, 23817, -4905, 49887, false));
		SPAWNS.add(new SpawnHolder(29347, -12270, 23573, -4905, 49066, false));
		SPAWNS.add(new SpawnHolder(29346, -12272, 23061, -4905, 49390, false));
		SPAWNS.add(new SpawnHolder(29342, -11156, 23641, -4905, 122, false));
		SPAWNS.add(new SpawnHolder(29346, -10897, 23653, -4905, 482, false));
		SPAWNS.add(new SpawnHolder(29342, -10636, 23651, -4905, 65456, false));
		SPAWNS.add(new SpawnHolder(29342, -11386, 23958, -4905, 529, false));
		SPAWNS.add(new SpawnHolder(29341, -11126, 23964, -4905, 240, false));
		SPAWNS.add(new SpawnHolder(29342, -10857, 23968, -4905, 155, false));
		SPAWNS.add(new SpawnHolder(29340, -10608, 23966, -4905, 65452, false));
		SPAWNS.add(new SpawnHolder(29345, -8540, 23806, -4903, 65438, false));
		SPAWNS.add(new SpawnHolder(29342, -9103, 23804, -4903, 32907, false));
		SPAWNS.add(new SpawnHolder(29342, -8774, 23809, -4903, 50950, false));
		SPAWNS.add(new SpawnHolder(29342, -8784, 23493, -4903, 47854, false));
		SPAWNS.add(new SpawnHolder(29342, -8758, 22562, -4905, 32767, false));
		SPAWNS.add(new SpawnHolder(29344, -9037, 22562, -4905, 32767, false));
		SPAWNS.add(new SpawnHolder(29345, -9297, 22554, -4905, 32012, false));
		SPAWNS.add(new SpawnHolder(29345, -9536, 22554, -4905, 32767, false));
		SPAWNS.add(new SpawnHolder(29342, -8761, 22246, -4905, 32767, false));
		SPAWNS.add(new SpawnHolder(29344, -9007, 22247, -4905, 32725, false));
		SPAWNS.add(new SpawnHolder(29343, -9275, 22240, -4905, 33040, false));
		SPAWNS.add(new SpawnHolder(29345, -9545, 22231, -4905, 33115, false));
		SPAWNS.add(new SpawnHolder(29345, -8787, 18829, -4903, 49288, false));
		SPAWNS.add(new SpawnHolder(29346, -8785, 19512, -4903, 17015, false));
		SPAWNS.add(new SpawnHolder(29340, -8516, 19196, -4903, 147, false));
		SPAWNS.add(new SpawnHolder(29342, -8774, 19194, -4903, 32465, false));
		SPAWNS.add(new SpawnHolder(29342, -9151, 19200, -4903, 33236, false));
		SPAWNS.add(new SpawnHolder(29346, -8781, 17784, -4903, 49334, false));
		SPAWNS.add(new SpawnHolder(29345, -8778, 18444, -4903, 16507, false));
		SPAWNS.add(new SpawnHolder(29345, -8527, 18116, -4903, 65299, false));
		SPAWNS.add(new SpawnHolder(29345, -8784, 18110, -4903, 32767, false));
		SPAWNS.add(new SpawnHolder(29346, -9116, 18107, -4903, 32862, false));
		SPAWNS.add(new SpawnHolder(29342, -8775, 16859, -4905, 33548, false));
		SPAWNS.add(new SpawnHolder(29347, -9288, 16862, -4905, 33196, false));
		SPAWNS.add(new SpawnHolder(29341, -9547, 16871, -4905, 32405, false));
		SPAWNS.add(new SpawnHolder(29347, -8760, 16543, -4905, 32517, false));
		SPAWNS.add(new SpawnHolder(29342, -9068, 16540, -4905, 32869, false));
		SPAWNS.add(new SpawnHolder(29345, -9305, 16548, -4905, 32416, false));
		SPAWNS.add(new SpawnHolder(29343, -9581, 16562, -4905, 32239, false));
		SPAWNS.add(new SpawnHolder(29342, -11386, 17976, -4905, 64074, false));
		SPAWNS.add(new SpawnHolder(29343, -11129, 17977, -4905, 40, false));
		SPAWNS.add(new SpawnHolder(29345, -10864, 17977, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29343, -10610, 17973, -4905, 65371, false));
		SPAWNS.add(new SpawnHolder(29346, -11383, 18238, -4905, 481, false));
		SPAWNS.add(new SpawnHolder(29343, -11129, 18248, -4905, 410, false));
		SPAWNS.add(new SpawnHolder(29341, -10861, 18255, -4905, 142, false));
		SPAWNS.add(new SpawnHolder(29345, -10629, 18264, -4905, 436, false));
		SPAWNS.add(new SpawnHolder(29347, -11475, 21103, -4905, 48879, false));
		SPAWNS.add(new SpawnHolder(29342, -11466, 20854, -4905, 49528, false));
		SPAWNS.add(new SpawnHolder(29347, -11468, 20567, -4905, 49079, false));
		SPAWNS.add(new SpawnHolder(29347, -11462, 20304, -4905, 49389, false));
		SPAWNS.add(new SpawnHolder(29345, -11446, 20056, -4905, 49823, false));
		SPAWNS.add(new SpawnHolder(29344, -11081, 21367, -4905, 48668, false));
		SPAWNS.add(new SpawnHolder(29345, -11094, 21069, -4905, 48697, false));
		SPAWNS.add(new SpawnHolder(29343, -11088, 20812, -4905, 49395, false));
		SPAWNS.add(new SpawnHolder(29347, -11081, 20553, -4905, 49433, false));
		SPAWNS.add(new SpawnHolder(29340, -11087, 20281, -4905, 48921, false));
		SPAWNS.add(new SpawnHolder(29340, -11077, 20010, -4905, 49536, false));
		SPAWNS.add(new SpawnHolder(29345, -13405, 20920, -4905, 64568, false));
		SPAWNS.add(new SpawnHolder(29343, -13146, 20928, -4905, 322, false));
		SPAWNS.add(new SpawnHolder(29340, -12901, 20923, -4905, 65323, false));
		SPAWNS.add(new SpawnHolder(29346, -12637, 20927, -4905, 158, false));
		SPAWNS.add(new SpawnHolder(29347, -13437, 21244, -4905, 47, false));
		SPAWNS.add(new SpawnHolder(29345, -13125, 21245, -4905, 33, false));
		SPAWNS.add(new SpawnHolder(29340, -12876, 21257, -4905, 502, false));
		SPAWNS.add(new SpawnHolder(29345, -12647, 21242, -4905, 64853, false));
		SPAWNS.add(new SpawnHolder(29345, -16669, 21115, -4905, 496, false));
		SPAWNS.add(new SpawnHolder(29341, -16395, 21117, -4905, 76, false));
		SPAWNS.add(new SpawnHolder(29344, -16106, 21119, -4905, 72, false));
		SPAWNS.add(new SpawnHolder(29341, -15871, 21117, -4905, 65447, false));
		SPAWNS.add(new SpawnHolder(29343, -15595, 21120, -4905, 113, false));
		SPAWNS.add(new SpawnHolder(29345, -15317, 21135, -4905, 562, false));
		SPAWNS.add(new SpawnHolder(29347, -16403, 21506, -4905, 65281, false));
		SPAWNS.add(new SpawnHolder(29341, -16124, 21500, -4905, 65311, false));
		SPAWNS.add(new SpawnHolder(29347, -15846, 21499, -4905, 64349, false));
		SPAWNS.add(new SpawnHolder(29344, -15591, 21510, -4905, 449, false));
		SPAWNS.add(new SpawnHolder(29346, -15331, 21510, -4905, 0, false));
		SPAWNS.add(new SpawnHolder(29347, -18842, 14750, -4905, 36, false));
		SPAWNS.add(new SpawnHolder(29343, -18584, 14748, -4905, 65455, false));
		SPAWNS.add(new SpawnHolder(29342, -18321, 14746, -4905, 65456, false));
		SPAWNS.add(new SpawnHolder(29347, -19101, 15060, -4905, 65437, false));
		SPAWNS.add(new SpawnHolder(29346, -18821, 15071, -4905, 409, false));
		SPAWNS.add(new SpawnHolder(29346, -18583, 15069, -4905, 65448, false));
		SPAWNS.add(new SpawnHolder(29345, -18303, 15070, -4905, 37, false));
		SPAWNS.add(new SpawnHolder(29346, -14736, 16367, -4905, 32403, false));
		SPAWNS.add(new SpawnHolder(29340, -14975, 16370, -4905, 32637, false));
		SPAWNS.add(new SpawnHolder(29346, -15234, 16378, -4905, 32445, false));
		SPAWNS.add(new SpawnHolder(29340, -14706, 16066, -4905, 32878, false));
		SPAWNS.add(new SpawnHolder(29345, -14996, 16061, -4905, 32947, false));
		SPAWNS.add(new SpawnHolder(29346, -15242, 16055, -4905, 33022, false));
	}
	// Skill
	private static final Skill REMANT_TELE = SkillData.getInstance().getSkill(23303, 1);
	// Misc
	private static final Location ENTER_LOC = new Location(-19361, 13504, -4906);
	private static final Location ENTER_LILITH_LOC = new Location(184449, -9032, -5499);
	private static final ZoneType BOSS_ZONE = ZoneManager.getInstance().getZoneById(12005);
	private static final ZoneType PRE_LILITH_ZONE = ZoneManager.getInstance().getZoneById(12006);
	// Others
	private static List<Npc> _spawns = new ArrayList<>();
	private static List<Npc> _remnants = new ArrayList<>();
	private static long _lastAction;
	private static Npc _lilithBoss;
	
	public Lilith()
	{
		addTalkId(ENTER_CUBIC, EXIST_CUBIC, LILITH_CUBIC);
		addStartNpc(ENTER_CUBIC, EXIST_CUBIC, LILITH_CUBIC);
		addFirstTalkId(ENTER_CUBIC, EXIST_CUBIC, LILITH_CUBIC);
		addSpellFinishedId(REMNANT);
		addAttackId(ALL_MOBS);
		addKillId(ALL_MOBS);
		addSkillSeeId(ALL_MOBS);
		
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(LILITH);
		final int status = GrandBossManager.getInstance().getBossStatus(LILITH);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_lilith", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(LILITH, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(LILITH, ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "unlock_lilith":
			{
				GrandBossManager.getInstance().setBossStatus(LILITH, ALIVE);
				break;
			}
			case "check_activity_task":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					GrandBossManager.getInstance().setBossStatus(LILITH, ALIVE);
					for (Creature creature : BOSS_ZONE.getCharactersInside())
					{
						if (creature != null)
						{
							if (creature.isNpc())
							{
								creature.deleteMe();
							}
							else if (creature.isPlayer())
							{
								creature.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(creature, TeleportWhereType.TOWN));
							}
						}
					}
					startQuestTimer("end_lilith", 2000, null, null);
				}
				else
				{
					startQuestTimer("check_activity_task", 60000, null, null);
				}
				break;
			}
			case "spawn_remant":
			{
				Npc randomSpawn = null;
				if (npc == null)
				{
					for (int i = 0; i < 2; i++)
					{
						randomSpawn = _spawns.get(Rnd.get(_spawns.size()));
						if (randomSpawn != null)
						{
							Npc remnant = addSpawn(REMNANT, randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ(), randomSpawn.getHeading(), true, 0, false, 0);
							_remnants.add(remnant);
						}
					}
				}
				else
				{
					randomSpawn = _spawns.get(Rnd.get(_spawns.size()));
					if (randomSpawn != null)
					{
						npc.teleToLocation(randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ());
						_spawns.add(npc);
					}
				}
				break;
			}
			case "cancel_timers":
			{
				QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				
				QuestTimer forceEnd = getQuestTimer("end_lilith", null, null);
				if (forceEnd != null)
				{
					forceEnd.cancel();
				}
				break;
			}
			case "end_lilith":
			{
				notifyEvent("cancel_timers", null, null);
				if (_lilithBoss != null)
				{
					_lilithBoss.deleteMe();
				}
				BOSS_ZONE.oustAllPlayers();
				PRE_LILITH_ZONE.oustAllPlayers();
				for (Npc spawn : _spawns)
				{
					if (spawn != null)
					{
						spawn.deleteMe();
					}
				}
				_spawns.clear();
				for (Npc remnant : _remnants)
				{
					if (remnant == null)
					{
						continue;
					}
					remnant.deleteMe();
				}
				if (GrandBossManager.getInstance().getBossStatus(LILITH) != DEAD)
				{
					GrandBossManager.getInstance().setBossStatus(LILITH, ALIVE);
				}
				break;
			}
			case "exist":
			{
				player.teleToLocation(TeleportWhereType.TOWN);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		if ((npc.getId() == ENTER_CUBIC) || (npc.getId() == LILITH_CUBIC))
		{
			final int _lilithStatus = GrandBossManager.getInstance().getBossStatus(LILITH);
			if ((npc.getId() == ENTER_CUBIC) && (_lilithStatus > ALIVE))
			{
				return "31118-01.html";
			}
			if (!player.isInParty())
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "31118-02.html"));
				packet.replace("%min%", Integer.toString(Config.LILITH_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			final Party party = player.getParty();
			final boolean isInCC = party.isInCommandChannel();
			final List<PlayerInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
			final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
			if (!isPartyLeader)
			{
				return "31118-03.html";
			}
			
			if ((members.size() < Config.LILITH_MIN_PLAYERS) || (members.size() > Config.LILITH_MAX_PLAYERS))
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "31118-02.html"));
				packet.replace("%min%", Integer.toString(Config.LILITH_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			
			for (PlayerInstance member : members)
			{
				if (member.getLevel() < Config.LILITH_MIN_PLAYER_LVL)
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "31118-04.html"));
					packet.replace("%minlvl%", Integer.toString(Config.LILITH_MIN_PLAYER_LVL));
					player.sendPacket(packet);
					return null;
				}
			}
			
			for (PlayerInstance member : members)
			{
				if (member.isInsideRadius3D(npc, 1000) && (npc.getId() == ENTER_CUBIC))
				{
					member.teleToLocation(ENTER_LOC, true);
				}
				else if (member.isInsideRadius3D(npc, 1000) && (npc.getId() == LILITH_CUBIC))
				{
					member.teleToLocation(ENTER_LILITH_LOC, true);
				}
			}
			
			if ((_lilithStatus == ALIVE) && (npc.getId() == ENTER_CUBIC))
			{
				GrandBossManager.getInstance().setBossStatus(LILITH, WAITING);
				_spawns.clear();
				for (SpawnHolder spawn : SPAWNS)
				{
					_spawns.add(addSpawn(spawn.getNpcId(), spawn.getLocation()));
				}
				_remnants.clear();
				notifyEvent("spawn_remant", null, null);
				_lastAction = System.currentTimeMillis();
				startQuestTimer("check_activity_task", 60000, null, null, true);
			}
			else if ((_lilithStatus == WAITING) && (npc.getId() == LILITH_CUBIC))
			{
				GrandBossManager.getInstance().setBossStatus(LILITH, FIGHTING);
				// Spawn the rb
				_lilithBoss = addSpawn(LILITH, 185062, -9605, -5499, 15640, false, 0);
				GrandBossManager.getInstance().addBoss((GrandBossInstance) _lilithBoss);
				startQuestTimer("end_lilith", 60 * 60000, null, null); // 1h
			}
		}
		return super.onTalk(npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isPet)
	{
		_lastAction = System.currentTimeMillis();
		if (npc.isMinion() || npc.isRaid()) // Lilith and minions
		{
			// Anti BUGGERS
			if (!BOSS_ZONE.isInsideZone(attacker)) // Character attacking out of zone
			{
				attacker.doDie(null);
			}
			if (!BOSS_ZONE.isInsideZone(npc)) // Npc moved out of the zone
			{
				Spawn spawn = npc.getSpawn();
				if (spawn != null)
				{
					npc.teleToLocation(spawn.getX(), spawn.getY(), spawn.getZ());
				}
			}
		}
		if (npc.getId() == REMNANT)
		{
			if (npc.getCurrentHp() < (npc.getMaxHp() * 0.30))
			{
				if (!npc.isCastingNow() && (Rnd.get(100) > 95))
				{
					npc.doCast(REMANT_TELE);
				}
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isPet)
	{
		if (npc.getId() == LILITH)
		{
			notifyEvent("cancel_timers", null, null);
			addSpawn(EXIST_CUBIC, 185062, -9605, -5499, 15640, false, 900000); // 15min
			
			GrandBossManager.getInstance().setBossStatus(LILITH, DEAD);
			final long respawnTime = getRespawnTime();
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(LILITH);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(LILITH, info);
			
			startQuestTimer("unlock_lilith", respawnTime, null, null);
			startQuestTimer("end_lilith", 900000, null, null);
		}
		else if (npc.getId() == REMNANT)
		{
			_remnants.remove(npc);
			if (_remnants.isEmpty())
			{
				addSpawn(LILITH_CUBIC, -19410, 23805, -4903, 62917, false, 60 * 60000, false, 0);
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpellFinished(Npc npc, PlayerInstance player, Skill skill)
	{
		if ((npc.getId() == REMNANT) && PRE_LILITH_ZONE.isInsideZone(npc))
		{
			if (skill == REMANT_TELE)
			{
				notifyEvent("spawn_remant", npc, null);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isPet)
	{
		if (CommonUtil.contains(LILITH_MINIONS, npc.getId()) && Rnd.nextBoolean())
		{
			if (skill.getAbnormalType() == AbnormalType.HP_RECOVER)
			{
				if (!npc.isCastingNow() && (npc.getTarget() != npc) && (npc.getTarget() != caster) && (npc.getTarget() != _lilithBoss))
				{
					((Attackable) npc).clearAggroList();
					npc.setTarget(caster);
					((Attackable) npc).addDamageHate(caster, 500, 99999);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, caster);
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	private int getRespawnTime()
	{
		return (int) calcReuseFromDays(0, 21, Calendar.THURSDAY, 0, 14, Calendar.SATURDAY);
	}
	
	private long calcReuseFromDays(int day1Minute, int day1Hour, int day1Day, int day2Minute, int day2Hour, int day2Day)
	{
		Calendar now = Calendar.getInstance();
		Calendar day1 = (Calendar) now.clone();
		day1.set(Calendar.MINUTE, day1Minute);
		day1.set(Calendar.HOUR_OF_DAY, day1Hour);
		day1.set(Calendar.DAY_OF_WEEK, day1Day);
		
		Calendar day2 = (Calendar) day1.clone();
		day2.set(Calendar.MINUTE, day2Minute);
		day2.set(Calendar.HOUR_OF_DAY, day2Hour);
		day2.set(Calendar.DAY_OF_WEEK, day2Day);
		
		if (now.after(day1))
		{
			day1.add(Calendar.WEEK_OF_MONTH, 1);
		}
		if (now.after(day2))
		{
			day2.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		Calendar reenter = day1;
		if (day2.before(day1))
		{
			reenter = day2;
		}
		return reenter.getTimeInMillis() - System.currentTimeMillis();
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return npc.getId() + ".html";
	}
	
	public static void main(String[] args)
	{
		new Lilith();
	}
}