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
package instances.MithrilMine;

import java.util.List;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.instancezone.Instance;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.network.NpcStringId;

import instances.AbstractInstance;
import quests.Q10284_AcquisitionOfDivineSword.Q10284_AcquisitionOfDivineSword;

/**
 * Mithril Mine instance zone.
 * @author Adry_85
 */
public class MithrilMine extends AbstractInstance
{
	// NPCs
	private static final int KEGOR = 18846;
	private static final int MITHRIL_MILLIPEDE = 22766;
	private static final int KRUN = 32653;
	private static final int TARUN = 32654;
	// Item
	private static final int COLD_RESISTANCE_POTION = 15514;
	// Skill
	private static SkillHolder BLESS_OF_SWORD = new SkillHolder(6286, 1);
	// Misc
	private static final int TEMPLATE_ID = 138;
	
	public MithrilMine()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(KEGOR);
		addKillId(KEGOR, MITHRIL_MILLIPEDE);
		addStartNpc(TARUN, KRUN);
		addTalkId(TARUN, KRUN, KEGOR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		switch (event)
		{
			case "BUFF":
			{
				if ((player != null) && npc.isInsideRadius3D(player, 1000) && npc.isScriptValue(1) && !player.isDead())
				{
					npc.setTarget(player);
					npc.doCast(BLESS_OF_SWORD.getSkill());
				}
				startQuestTimer("BUFF", 30000, npc, player);
				break;
			}
			case "TIMER":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final List<Npc> npcs = world.spawnGroup("attackers");
					for (Npc n : npcs)
					{
						n.setScriptValue(1);
						n.setRunning();
						n.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
						((Attackable) n).addDamageHate(npc, 0, 999999);
					}
				}
				break;
			}
			case "FINISH":
			{
				World.getInstance().forEachVisibleObject(npc, Creature.class, knownChar ->
				{
					if (knownChar.getId() == KEGOR)
					{
						final Npc kegor = (Npc) knownChar;
						kegor.setScriptValue(2);
						kegor.setWalking();
						kegor.setTarget(player);
						kegor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
						kegor.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_CAN_FINALLY_TAKE_A_BREATHER_BY_THE_WAY_WHO_ARE_YOU_HMM_I_THINK_I_KNOW_WHO_SENT_YOU);
					}
				});
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
		
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = player.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
		if ((qs != null))
		{
			if (qs.isMemoState(2))
			{
				return npc.isScriptValue(0) ? "18846.html" : "18846-01.html";
			}
			else if (qs.isMemoState(3))
			{
				finishInstance(player, 0);
				giveAdena(player, 296425, true);
				addExpAndSp(player, 921805, 82230);
				qs.exitQuest(false, true);
				return "18846-03.html";
			}
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (npc.getId() == KEGOR)
			{
				npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.HOW_COULD_I_FALL_IN_A_PLACE_LIKE_THIS);
				world.finishInstance(1);
			}
			else if (npc.isScriptValue(1))
			{
				final int count = world.getParameters().getInt("count", 0) + 1;
				world.setParameter("count", count);
				if (count >= 5)
				{
					final QuestState qs = player.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
					if ((qs != null) && qs.isMemoState(2))
					{
						cancelQuestTimer("BUFF", npc, player);
						qs.setMemoState(3);
						qs.setCond(6, true);
						startQuestTimer("FINISH", 3000, npc, player);
						world.finishInstance();
					}
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance talker)
	{
		switch (npc.getId())
		{
			case TARUN:
			case KRUN:
			{
				final QuestState qs = talker.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
				if ((qs != null) && qs.isMemoState(2))
				{
					if (!hasQuestItems(talker, COLD_RESISTANCE_POTION))
					{
						giveItems(talker, COLD_RESISTANCE_POTION, 1);
					}
					qs.setCond(4, true);
					enterInstance(talker, npc, TEMPLATE_ID);
				}
				break;
			}
			case KEGOR:
			{
				final QuestState qs = talker.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
				if ((qs != null) && qs.isMemoState(2) && hasQuestItems(talker, COLD_RESISTANCE_POTION) && npc.isScriptValue(0))
				{
					takeItems(talker, COLD_RESISTANCE_POTION, -1);
					qs.setCond(5, true);
					npc.setScriptValue(1);
					startQuestTimer("TIMER", 3000, npc, talker);
					startQuestTimer("BUFF", 3500, npc, talker);
					return "18846-02.html";
				}
				break;
			}
		}
		return super.onTalk(npc, talker);
	}
	
	public static void main(String[] args)
	{
		new MithrilMine();
	}
}