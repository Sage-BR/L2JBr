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
package ai.areas.BeastFarm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.instance.TamedBeastInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Growth-capable mobs: Polymorphing upon successful feeding.
 * @author Fulminus
 */
public class FeedableBeasts extends AbstractNpcAI
{
	private static final int GOLDEN_SPICE = 6643;
	private static final int CRYSTAL_SPICE = 6644;
	private static final int SKILL_GOLDEN_SPICE = 2188;
	private static final int SKILL_CRYSTAL_SPICE = 2189;
	private static final int FOODSKILLDIFF = GOLDEN_SPICE - SKILL_GOLDEN_SPICE;
	// Tamed Wild Beasts
	private static final int TRAINED_BUFFALO1 = 16013;
	private static final int TRAINED_BUFFALO2 = 16014;
	private static final int TRAINED_COUGAR1 = 16015;
	private static final int TRAINED_COUGAR2 = 16016;
	private static final int TRAINED_KOOKABURRA1 = 16017;
	private static final int TRAINED_KOOKABURRA2 = 16018;
	// private static final int TRAINED_TINY_BABY_BUFFALO = 16020; // TODO: Implement.
	// private static final int TRAINED_TINY_BABY_COUGAR = 16022; // TODO: Implement.
	// private static final int TRAINED_TINY_BABY_KOOKABURRA = 16024; // TODO: Implement.
	// @formatter:off
	private static final int[] TAMED_BEASTS =
	{
		TRAINED_BUFFALO1, TRAINED_BUFFALO2, TRAINED_COUGAR1, TRAINED_COUGAR2, TRAINED_KOOKABURRA1, TRAINED_KOOKABURRA2
	};
	// all mobs that can eat...
	private static final int[] FEEDABLE_BEASTS =
	{
		TRAINED_BUFFALO1, TRAINED_BUFFALO2, TRAINED_COUGAR1, TRAINED_COUGAR2, TRAINED_KOOKABURRA1, TRAINED_KOOKABURRA2
	};
	// @formatter:on
	
	private static final Map<Integer, Integer> MAD_COW_POLYMORPH = new HashMap<>(6);
	static
	{
		MAD_COW_POLYMORPH.put(21824, 21468);
		MAD_COW_POLYMORPH.put(21825, 21469);
		MAD_COW_POLYMORPH.put(21826, 21487);
		MAD_COW_POLYMORPH.put(21827, 21488);
		MAD_COW_POLYMORPH.put(21828, 21506);
		MAD_COW_POLYMORPH.put(21829, 21507);
	}
	
	private static final NpcStringId[][] TEXT =
	{
		{
			NpcStringId.WHAT_DID_YOU_JUST_DO_TO_ME,
			NpcStringId.ARE_YOU_TRYING_TO_TAME_ME_DON_T_DO_THAT,
			NpcStringId.DON_T_GIVE_SUCH_A_THING_YOU_CAN_ENDANGER_YOURSELF,
			NpcStringId.YUCK_WHAT_IS_THIS_IT_TASTES_TERRIBLE,
			NpcStringId.I_M_HUNGRY_GIVE_ME_A_LITTLE_MORE_PLEASE,
			NpcStringId.WHAT_IS_THIS_IS_THIS_EDIBLE,
			NpcStringId.DON_T_WORRY_ABOUT_ME,
			NpcStringId.THANK_YOU_THAT_WAS_DELICIOUS,
			NpcStringId.I_THINK_I_AM_STARTING_TO_LIKE_YOU,
			NpcStringId.EEEEEK_EEEEEK
		},
		{
			NpcStringId.DON_T_KEEP_TRYING_TO_TAME_ME_I_DON_T_WANT_TO_BE_TAMED,
			NpcStringId.IT_IS_JUST_FOOD_TO_ME_ALTHOUGH_IT_MAY_ALSO_BE_YOUR_HAND,
			NpcStringId.IF_I_KEEP_EATING_LIKE_THIS_WON_T_I_BECOME_FAT_CHOMP_CHOMP,
			NpcStringId.WHY_DO_YOU_KEEP_FEEDING_ME,
			NpcStringId.DON_T_TRUST_ME_I_M_AFRAID_I_MAY_BETRAY_YOU_LATER
		},
		{
			NpcStringId.GRRRRR,
			NpcStringId.YOU_BROUGHT_THIS_UPON_YOURSELF,
			NpcStringId.I_FEEL_STRANGE_I_KEEP_HAVING_THESE_EVIL_THOUGHTS,
			NpcStringId.ALAS_SO_THIS_IS_HOW_IT_ALL_ENDS,
			NpcStringId.I_DON_T_FEEL_SO_GOOD_OH_MY_MIND_IS_VERY_TROUBLED
		}
	};
	
	private static final NpcStringId[] TAMED_TEXT =
	{
		NpcStringId.S1_SO_WHAT_DO_YOU_THINK_IT_IS_LIKE_TO_BE_TAMED,
		NpcStringId.S1_WHENEVER_I_SEE_SPICE_I_THINK_I_WILL_MISS_YOUR_HAND_THAT_USED_TO_FEED_IT_TO_ME,
		NpcStringId.S1_DON_T_GO_TO_THE_VILLAGE_I_DON_T_HAVE_THE_STRENGTH_TO_FOLLOW_YOU,
		NpcStringId.THANK_YOU_FOR_TRUSTING_ME_S1_I_HOPE_I_WILL_BE_HELPFUL_TO_YOU,
		NpcStringId.S1_WILL_I_BE_ABLE_TO_HELP_YOU,
		NpcStringId.I_GUESS_IT_S_JUST_MY_ANIMAL_MAGNETISM,
		NpcStringId.TOO_MUCH_SPICY_FOOD_MAKES_ME_SWEAT_LIKE_A_BEAST,
		NpcStringId.ANIMALS_NEED_LOVE_TOO
	};
	
	private static final Map<Integer, Integer> _feedInfo = new ConcurrentHashMap<>();
	private static final Map<Integer, GrowthCapableMob> GROWTH_CAPABLE_MOBS = new HashMap<>();
	
	// all mobs that grow by eating
	private static class GrowthCapableMob
	{
		private final int _growthLevel;
		private final int _chance;
		
		private final Map<Integer, int[][]> _spiceToMob = new ConcurrentHashMap<>();
		
		public GrowthCapableMob(int growthLevel, int chance)
		{
			_growthLevel = growthLevel;
			_chance = chance;
		}
		
		public void addMobs(int spice, int[][] Mobs)
		{
			_spiceToMob.put(spice, Mobs);
		}
		
		public Integer getMob(int spice, int mobType, int classType)
		{
			if (_spiceToMob.containsKey(spice))
			{
				return _spiceToMob.get(spice)[mobType][classType];
			}
			return null;
		}
		
		public Integer getRandomMob(int spice)
		{
			int[][] temp;
			temp = _spiceToMob.get(spice);
			final int rand = getRandom(temp[0].length);
			return temp[0][rand];
		}
		
		public Integer getChance()
		{
			return _chance;
		}
		
		public Integer getGrowthLevel()
		{
			return _growthLevel;
		}
	}
	
	private FeedableBeasts()
	{
		addKillId(FEEDABLE_BEASTS);
		addSkillSeeId(FEEDABLE_BEASTS);
		
		// TODO: no grendels?
		GrowthCapableMob temp;
		
		//@formatter:off
		final int[][] Kookabura_0_Gold = {{ 21452, 21453, 21454, 21455 }};
		final int[][] Kookabura_0_Crystal = {{ 21456, 21457, 21458, 21459 }};
		final int[][] Kookabura_1_Gold_1= {{ 21460, 21462 }};
		final int[][] Kookabura_1_Gold_2 = {{ 21461, 21463 }};
		final int[][] Kookabura_1_Crystal_1 = {{ 21464, 21466 }};
		final int[][] Kookabura_1_Crystal_2 = {{ 21465, 21467 }};
		final int[][] Kookabura_2_1 = {{ 21468, 21824}, { TRAINED_KOOKABURRA1, TRAINED_KOOKABURRA2 }};
		final int[][] Kookabura_2_2 = {{ 21469, 21825}, { TRAINED_KOOKABURRA1, TRAINED_KOOKABURRA2 }};
		
		final int[][] Buffalo_0_Gold = {{ 21471, 21472, 21473, 21474 }};
		final int[][] Buffalo_0_Crystal = {{ 21475, 21476, 21477, 21478 }};
		final int[][] Buffalo_1_Gold_1 = {{ 21479, 21481 }};
		final int[][] Buffalo_1_Gold_2 = {{ 21481, 21482 }};
		final int[][] Buffalo_1_Crystal_1 = {{ 21483, 21485 }};
		final int[][] Buffalo_1_Crystal_2 = {{ 21484, 21486 }};
		final int[][] Buffalo_2_1 = {{ 21487, 21826}, {TRAINED_BUFFALO1, TRAINED_BUFFALO2 }};
		final int[][] Buffalo_2_2 = {{ 21488, 21827}, {TRAINED_BUFFALO1, TRAINED_BUFFALO2 }};
		
		final int[][] Cougar_0_Gold = {{ 21490, 21491, 21492, 21493 }};
		final int[][] Cougar_0_Crystal = {{ 21494, 21495, 21496, 21497 }};
		final int[][] Cougar_1_Gold_1 = {{ 21498, 21500 }};
		final int[][] Cougar_1_Gold_2 = {{ 21499, 21501 }};
		final int[][] Cougar_1_Crystal_1 = {{ 21502, 21504 }};
		final int[][] Cougar_1_Crystal_2 = {{ 21503, 21505 }};
		final int[][] Cougar_2_1 = {{ 21506, 21828 }, { TRAINED_COUGAR1, TRAINED_COUGAR2 }};
		final int[][] Cougar_2_2 = {{ 21507, 21829 }, { TRAINED_COUGAR1, TRAINED_COUGAR2 }};
		//@formatter:on
		
		// Alpen Kookabura
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Kookabura_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_0_Crystal);
		GROWTH_CAPABLE_MOBS.put(21451, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_1);
		GROWTH_CAPABLE_MOBS.put(21452, temp);
		GROWTH_CAPABLE_MOBS.put(21454, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Kookabura_1_Gold_2);
		GROWTH_CAPABLE_MOBS.put(21453, temp);
		GROWTH_CAPABLE_MOBS.put(21455, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_1);
		GROWTH_CAPABLE_MOBS.put(21456, temp);
		GROWTH_CAPABLE_MOBS.put(21458, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_1_Crystal_2);
		GROWTH_CAPABLE_MOBS.put(21457, temp);
		GROWTH_CAPABLE_MOBS.put(21459, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_1);
		GROWTH_CAPABLE_MOBS.put(21460, temp);
		GROWTH_CAPABLE_MOBS.put(21462, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Kookabura_2_2);
		GROWTH_CAPABLE_MOBS.put(21461, temp);
		GROWTH_CAPABLE_MOBS.put(21463, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_1);
		GROWTH_CAPABLE_MOBS.put(21464, temp);
		GROWTH_CAPABLE_MOBS.put(21466, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Kookabura_2_2);
		GROWTH_CAPABLE_MOBS.put(21465, temp);
		GROWTH_CAPABLE_MOBS.put(21467, temp);
		
		// Alpen Buffalo
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Buffalo_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_0_Crystal);
		GROWTH_CAPABLE_MOBS.put(21470, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_1);
		GROWTH_CAPABLE_MOBS.put(21471, temp);
		GROWTH_CAPABLE_MOBS.put(21473, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Buffalo_1_Gold_2);
		GROWTH_CAPABLE_MOBS.put(21472, temp);
		GROWTH_CAPABLE_MOBS.put(21474, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_1);
		GROWTH_CAPABLE_MOBS.put(21475, temp);
		GROWTH_CAPABLE_MOBS.put(21477, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_1_Crystal_2);
		GROWTH_CAPABLE_MOBS.put(21476, temp);
		GROWTH_CAPABLE_MOBS.put(21478, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_1);
		GROWTH_CAPABLE_MOBS.put(21479, temp);
		GROWTH_CAPABLE_MOBS.put(21481, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Buffalo_2_2);
		GROWTH_CAPABLE_MOBS.put(21480, temp);
		GROWTH_CAPABLE_MOBS.put(21482, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_1);
		GROWTH_CAPABLE_MOBS.put(21483, temp);
		GROWTH_CAPABLE_MOBS.put(21485, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Buffalo_2_2);
		GROWTH_CAPABLE_MOBS.put(21484, temp);
		GROWTH_CAPABLE_MOBS.put(21486, temp);
		
		// Alpen Cougar
		temp = new GrowthCapableMob(0, 100);
		temp.addMobs(GOLDEN_SPICE, Cougar_0_Gold);
		temp.addMobs(CRYSTAL_SPICE, Cougar_0_Crystal);
		GROWTH_CAPABLE_MOBS.put(21489, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_1);
		GROWTH_CAPABLE_MOBS.put(21490, temp);
		GROWTH_CAPABLE_MOBS.put(21492, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(GOLDEN_SPICE, Cougar_1_Gold_2);
		GROWTH_CAPABLE_MOBS.put(21491, temp);
		GROWTH_CAPABLE_MOBS.put(21493, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_1);
		GROWTH_CAPABLE_MOBS.put(21494, temp);
		GROWTH_CAPABLE_MOBS.put(21496, temp);
		
		temp = new GrowthCapableMob(1, 40);
		temp.addMobs(CRYSTAL_SPICE, Cougar_1_Crystal_2);
		GROWTH_CAPABLE_MOBS.put(21495, temp);
		GROWTH_CAPABLE_MOBS.put(21497, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_1);
		GROWTH_CAPABLE_MOBS.put(21498, temp);
		GROWTH_CAPABLE_MOBS.put(21500, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(GOLDEN_SPICE, Cougar_2_2);
		GROWTH_CAPABLE_MOBS.put(21499, temp);
		GROWTH_CAPABLE_MOBS.put(21501, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_1);
		GROWTH_CAPABLE_MOBS.put(21502, temp);
		GROWTH_CAPABLE_MOBS.put(21504, temp);
		
		temp = new GrowthCapableMob(2, 25);
		temp.addMobs(CRYSTAL_SPICE, Cougar_2_2);
		GROWTH_CAPABLE_MOBS.put(21503, temp);
		GROWTH_CAPABLE_MOBS.put(21505, temp);
	}
	
	private void spawnNext(Npc npc, int growthLevel, PlayerInstance player, int food)
	{
		final int npcId = npc.getId();
		int nextNpcId = 0;
		
		// find the next mob to spawn, based on the current npcId, growthlevel, and food.
		if (growthLevel == 2)
		{
			// if tamed, the mob that will spawn depends on the class type (fighter/mage) of the player!
			if (getRandom(2) == 0)
			{
				if (player.getClassId().isMage())
				{
					nextNpcId = GROWTH_CAPABLE_MOBS.get(npcId).getMob(food, 1, 1);
				}
				else
				{
					nextNpcId = GROWTH_CAPABLE_MOBS.get(npcId).getMob(food, 1, 0);
				}
			}
			else
			{
				// if not tamed, there is a small chance that have "mad cow" disease.
				// that is a stronger-than-normal animal that attacks its feeder
				if (getRandom(5) == 0)
				{
					nextNpcId = GROWTH_CAPABLE_MOBS.get(npcId).getMob(food, 0, 1);
				}
				else
				{
					nextNpcId = GROWTH_CAPABLE_MOBS.get(npcId).getMob(food, 0, 0);
				}
			}
		}
		else
		{
			// all other levels of growth are straight-forward
			nextNpcId = GROWTH_CAPABLE_MOBS.get(npcId).getRandomMob(food);
		}
		
		// remove the feedinfo of the mob that got despawned, if any
		if (_feedInfo.containsKey(npc.getObjectId()))
		{
			if (_feedInfo.get(npc.getObjectId()) == player.getObjectId())
			{
				_feedInfo.remove(npc.getObjectId());
			}
		}
		// despawn the old mob
		// TODO: same code? FIXED?
		// @formatter:off
		/*
		 * if (_GrowthCapableMobs.get(npcId).getGrowthLevel() == 0)
			{
				npc.deleteMe();
			}
			else 
			{
		 */
		npc.deleteMe();
		// }
		// @formatter:on
		
		// if this is finally a trained mob, then despawn any other trained mobs that the
		// player might have and initialize the Tamed Beast.
		if (CommonUtil.contains(TAMED_BEASTS, nextNpcId))
		{
			for (TamedBeastInstance oldTrained : player.getTrainedBeasts())
			{
				oldTrained.deleteMe();
			}
			
			final TamedBeastInstance nextNpc = new TamedBeastInstance(nextNpcId, player, food - FOODSKILLDIFF, npc.getX(), npc.getY(), npc.getZ());
			nextNpc.setRunning();
			// TODO: Quest removed with Etina's Fate.
			// Q00020_BringUpWithLove.checkJewelOfInnocence(player);
			
			// Support for A Grand Plan for Taming Wild Beasts (655) quest.
			// Q00655_AGrandPlanForTamingWildBeasts.reward(player, nextNpc); TODO: Replace me?
			
			// also, perform a rare random chat
			if (getRandom(20) == 0)
			{
				final NpcStringId message = NpcStringId.getNpcStringId(getRandom(2024, 2029));
				npc.broadcastSay(ChatType.NPC_GENERAL, message, message.getParamCount() > 0 ? player.getName() : null);
			}
			// @formatter:off
			/*
			TODO: The tamed beast consumes one golden/crystal spice
			every 60 seconds with an initial delay of 60 seconds
			if (tamed beast exists and is alive)
			{
				if (player has 1+ golden/crystal spice)
				{
					take one golden/crystal spice;
					say random NpcString(getRandom(2029, 2038));
				}
			}
			*/
			// @formatter:on
		}
		else
		{
			// if not trained, the newly spawned mob will automatically be aggro against its feeder
			// (what happened to "never bite the hand that feeds you" anyway?!)
			final Attackable nextNpc = (Attackable) addSpawn(nextNpcId, npc);
			
			if (MAD_COW_POLYMORPH.containsKey(nextNpcId))
			{
				startQuestTimer("polymorph Mad Cow", 10000, nextNpc, player);
			}
			
			// register the player in the feedinfo for the mob that just spawned
			_feedInfo.put(nextNpc.getObjectId(), player.getObjectId());
			nextNpc.setRunning();
			nextNpc.addDamageHate(player, 0, 99999);
			nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if (event.equalsIgnoreCase("polymorph Mad Cow") && (npc != null) && (player != null))
		{
			if (MAD_COW_POLYMORPH.containsKey(npc.getId()))
			{
				// remove the feed info from the previous mob
				if (_feedInfo.get(npc.getObjectId()) == player.getObjectId())
				{
					_feedInfo.remove(npc.getObjectId());
				}
				// despawn the mad cow
				npc.deleteMe();
				// spawn the new mob
				final Attackable nextNpc = (Attackable) addSpawn(MAD_COW_POLYMORPH.get(npc.getId()), npc);
				
				// register the player in the feedinfo for the mob that just spawned
				_feedInfo.put(nextNpc.getObjectId(), player.getObjectId());
				nextNpc.setRunning();
				nextNpc.addDamageHate(player, 0, 99999);
				nextNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSkillSee(Npc npc, PlayerInstance caster, Skill skill, WorldObject[] targets, boolean isSummon)
	{
		// this behavior is only run when the target of skill is the passed npc (chest)
		// i.e. when the player is attempting to open the chest using a skill
		if (!CommonUtil.contains(targets, npc))
		{
			return super.onSkillSee(npc, caster, skill, targets, isSummon);
		}
		// gather some values on local variables
		final int npcId = npc.getId();
		final int skillId = skill.getId();
		// check if the npc and skills used are valid for this script. Exit if invalid.
		if ((skillId != SKILL_GOLDEN_SPICE) && (skillId != SKILL_CRYSTAL_SPICE))
		{
			return super.onSkillSee(npc, caster, skill, targets, isSummon);
		}
		
		// first gather some values on local variables
		final int objectId = npc.getObjectId();
		int growthLevel = 3; // if a mob is in FEEDABLE_BEASTS but not in _GrowthCapableMobs, then it's at max growth (3)
		if (GROWTH_CAPABLE_MOBS.containsKey(npcId))
		{
			growthLevel = GROWTH_CAPABLE_MOBS.get(npcId).getGrowthLevel();
		}
		
		// prevent exploit which allows 2 players to simultaneously raise the same 0-growth beast
		// If the mob is at 0th level (when it still listens to all feeders) lock it to the first feeder!
		if ((growthLevel == 0) && _feedInfo.containsKey(objectId))
		{
			return super.onSkillSee(npc, caster, skill, targets, isSummon);
		}
		
		_feedInfo.put(objectId, caster.getObjectId());
		
		int food = 0;
		if (skillId == SKILL_GOLDEN_SPICE)
		{
			food = GOLDEN_SPICE;
		}
		else if (skillId == SKILL_CRYSTAL_SPICE)
		{
			food = CRYSTAL_SPICE;
		}
		
		// display the social action of the beast eating the food.
		npc.broadcastSocialAction(2);
		
		// if this pet can't grow, it's all done.
		if (GROWTH_CAPABLE_MOBS.containsKey(npcId))
		{
			// do nothing if this mob doesn't eat the specified food (food gets consumed but has no effect).
			if (GROWTH_CAPABLE_MOBS.get(npcId).getMob(food, 0, 0) == null)
			{
				return super.onSkillSee(npc, caster, skill, targets, isSummon);
			}
			
			// rare random talk...
			if (getRandom(20) == 0)
			{
				final NpcStringId message = getRandomEntry(TEXT[growthLevel]);
				npc.broadcastSay(ChatType.NPC_GENERAL, message, message.getParamCount() > 0 ? caster.getName() : null);
			}
			
			if ((growthLevel > 0) && (_feedInfo.get(objectId) != caster.getObjectId()))
			{
				// check if this is the same player as the one who raised it from growth 0.
				// if no, then do not allow a chance to raise the pet (food gets consumed but has no effect).
				return super.onSkillSee(npc, caster, skill, targets, isSummon);
			}
			
			// Polymorph the mob, with a certain chance, given its current growth level
			if (getRandom(100) < GROWTH_CAPABLE_MOBS.get(npcId).getChance())
			{
				spawnNext(npc, growthLevel, caster, food);
			}
		}
		else if (CommonUtil.contains(TAMED_BEASTS, npcId) && (npc instanceof TamedBeastInstance))
		{
			final TamedBeastInstance beast = ((TamedBeastInstance) npc);
			if (skillId == beast.getFoodType())
			{
				beast.onReceiveFood();
				final NpcStringId message = getRandomEntry(TAMED_TEXT);
				npc.broadcastSay(ChatType.NPC_GENERAL, message, message.getParamCount() > 0 ? caster.getName() : null);
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isSummon)
	{
		// remove the feedinfo of the mob that got killed, if any
		if (_feedInfo.containsKey(npc.getObjectId()))
		{
			_feedInfo.remove(npc.getObjectId());
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new FeedableBeasts();
	}
}
