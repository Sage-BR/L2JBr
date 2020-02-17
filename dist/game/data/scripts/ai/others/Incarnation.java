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
package ai.others;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.model.WorldObject;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.Id;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureAttack;
import org.l2jbr.gameserver.model.events.impl.creature.OnCreatureSkillFinishCast;
import org.l2jbr.gameserver.model.events.impl.creature.npc.OnNpcSpawn;
import org.l2jbr.gameserver.model.events.listeners.ConsumerEventListener;

import ai.AbstractNpcAI;

/**
 * @author Nik
 */
public class Incarnation extends AbstractNpcAI
{
	public Incarnation()
	{
	}
	
	@RegisterEvent(EventType.ON_NPC_SPAWN)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(13302)
	@Id(13303)
	@Id(13304)
	@Id(13305)
	@Id(13455)
	@Id(13456)
	@Id(13457)
	@Id(13578)
	@Id(13579)
	public void onNpcSpawn(OnNpcSpawn event)
	{
		final Npc npc = event.getNpc();
		if (npc.getSummoner() != null)
		{
			npc.getSummoner().addListener(new ConsumerEventListener(npc, EventType.ON_CREATURE_ATTACK, (OnCreatureAttack e) -> onOffense(npc, e.getAttacker(), e.getTarget()), this));
			npc.getSummoner().addListener(new ConsumerEventListener(npc, EventType.ON_CREATURE_SKILL_FINISH_CAST, (OnCreatureSkillFinishCast e) -> onOffense(npc, e.getCaster(), e.getTarget()), this));
		}
	}
	
	public void onOffense(Npc npc, Creature attacker, WorldObject target)
	{
		if ((attacker == target) || (npc.getSummoner() == null))
		{
			return;
		}
		
		// Attack target of summoner
		npc.setRunning();
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
	}
	
	public static void main(String[] args)
	{
		new Incarnation();
	}
}
