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
package handlers.effecthandlers;

import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.model.stats.Stats;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;

/**
 * Focus Energy effect implementation.
 * @author DS
 */
public class FocusMomentum extends AbstractEffect
{
	private final int _amount;
	private final int _maxCharges;
	
	public FocusMomentum(StatsSet params)
	{
		_amount = params.getInt("amount", 1);
		_maxCharges = params.getInt("maxCharges", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (!effected.isPlayer())
		{
			return;
		}
		
		final PlayerInstance player = effected.getActingPlayer();
		final int currentCharges = player.getCharges();
		final int maxCharges = Math.min(_maxCharges, (int) effected.getStat().getValue(Stats.MAX_MOMENTUM, 0));
		
		if (currentCharges >= maxCharges)
		{
			if (!skill.isTriggeredSkill())
			{
				player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			}
			return;
		}
		
		final int newCharge = Math.min(currentCharges + _amount, maxCharges);
		
		player.setCharges(newCharge);
		
		if (newCharge == maxCharges)
		{
			player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
		}
		else
		{
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1);
			sm.addInt(newCharge);
			player.sendPacket(sm);
		}
		
		player.sendPacket(new EtcStatusUpdate(player));
	}
}