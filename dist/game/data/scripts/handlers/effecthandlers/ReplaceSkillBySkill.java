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

import org.l2jbr.commons.concurrent.ThreadPool;
import org.l2jbr.gameserver.data.xml.impl.SkillData;
import org.l2jbr.gameserver.enums.ShortcutType;
import org.l2jbr.gameserver.model.Shortcut;
import org.l2jbr.gameserver.model.StatsSet;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.effects.AbstractEffect;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.serverpackets.ShortCutInit;
import org.l2jbr.gameserver.network.serverpackets.ShortCutRegister;

/**
 * @author Mobius
 */
public class ReplaceSkillBySkill extends AbstractEffect
{
	private final SkillHolder _existingSkill;
	private final SkillHolder _replacementSkill;
	
	public ReplaceSkillBySkill(StatsSet params)
	{
		_existingSkill = new SkillHolder(params.getInt("existingSkillId"), params.getInt("existingSkillLevel", -1));
		_replacementSkill = new SkillHolder(params.getInt("replacementSkillId"), params.getInt("replacementSkillLevel", -1));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, ItemInstance item)
	{
		if (effected.isPlayer())
		{
			final PlayerInstance player = effected.getActingPlayer();
			final Skill knownSkill = player.getKnownSkill(_existingSkill.getSkillId());
			if ((knownSkill == null) || (knownSkill.getLevel() < _existingSkill.getSkillLevel()))
			{
				return;
			}
			
			final Skill addedSkill = SkillData.getInstance().getSkill(_replacementSkill.getSkillId(), _replacementSkill.getSkillLevel() < 1 ? knownSkill.getLevel() : _replacementSkill.getSkillLevel(), knownSkill.getSubLevel());
			player.addSkill(addedSkill, false);
			for (Shortcut shortcut : player.getAllShortCuts())
			{
				if ((shortcut.getType() == ShortcutType.SKILL) && (shortcut.getId() == knownSkill.getId()) && (shortcut.getLevel() == knownSkill.getLevel()))
				{
					final int slot = shortcut.getSlot();
					final int page = shortcut.getPage();
					final int characterType = shortcut.getCharacterType();
					player.deleteShortCut(slot, page);
					final Shortcut newShortcut = new Shortcut(slot, page, ShortcutType.SKILL, addedSkill.getId(), addedSkill.getLevel(), addedSkill.getSubLevel(), characterType);
					player.registerShortCut(newShortcut);
					player.sendPacket(new ShortCutRegister(newShortcut));
				}
			}
			
			player.removeSkill(knownSkill, false);
			player.sendSkillList();
			ThreadPool.schedule(() ->
			{
				player.sendPacket(new ShortCutInit(player));
			}, 1100);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		final PlayerInstance player = effected.getActingPlayer();
		final Skill knownSkill = player.getKnownSkill(_replacementSkill.getSkillId());
		if (knownSkill == null)
		{
			return;
		}
		
		final Skill addedSkill = SkillData.getInstance().getSkill(_existingSkill.getSkillId(), _existingSkill.getSkillLevel() < 1 ? knownSkill.getLevel() : _existingSkill.getSkillLevel(), knownSkill.getSubLevel());
		player.addSkill(addedSkill, false);
		for (Shortcut shortcut : player.getAllShortCuts())
		{
			if ((shortcut.getType() == ShortcutType.SKILL) && (shortcut.getId() == knownSkill.getId()) && (shortcut.getLevel() == knownSkill.getLevel()))
			{
				final int slot = shortcut.getSlot();
				final int page = shortcut.getPage();
				final int characterType = shortcut.getCharacterType();
				player.deleteShortCut(slot, page);
				final Shortcut newShortcut = new Shortcut(slot, page, ShortcutType.SKILL, addedSkill.getId(), addedSkill.getLevel(), addedSkill.getSubLevel(), characterType);
				player.registerShortCut(newShortcut);
				player.sendPacket(new ShortCutRegister(newShortcut));
			}
		}
		
		player.removeSkill(knownSkill, false);
		player.sendSkillList();
		ThreadPool.schedule(() ->
		{
			player.sendPacket(new ShortCutInit(player));
		}, 1100);
	}
}
