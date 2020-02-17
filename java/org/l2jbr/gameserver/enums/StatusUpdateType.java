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
package org.l2jbr.gameserver.enums;

import java.util.function.Function;

import org.l2jbr.gameserver.model.actor.Creature;

/**
 * @author UnAfraid
 */
public enum StatusUpdateType
{
	LEVEL(0x01, Creature::getLevel),
	EXP(0x02, creature -> (int) creature.getStat().getExp()),
	STR(0x03, Creature::getSTR),
	DEX(0x04, Creature::getDEX),
	CON(0x05, Creature::getCON),
	INT(0x06, Creature::getINT),
	WIT(0x07, Creature::getWIT),
	MEN(0x08, Creature::getMEN),
	
	CUR_HP(0x09, creature -> (int) creature.getCurrentHp()),
	MAX_HP(0x0A, Creature::getMaxHp),
	CUR_MP(0x0B, creature -> (int) creature.getCurrentMp()),
	MAX_MP(0x0C, Creature::getMaxMp),
	
	P_ATK(0x11, Creature::getPAtk),
	ATK_SPD(0x12, Creature::getPAtkSpd),
	P_DEF(0x13, Creature::getPDef),
	EVASION(0x14, Creature::getEvasionRate),
	ACCURACY(0x15, Creature::getAccuracy),
	CRITICAL(0x16, creature -> (int) creature.getCriticalDmg(1)),
	M_ATK(0x17, Creature::getMAtk),
	CAST_SPD(0x18, Creature::getMAtkSpd),
	M_DEF(0x19, Creature::getMDef),
	PVP_FLAG(0x1A, creature -> (int) creature.getPvpFlag()),
	REPUTATION(0x1B, creature -> creature.isPlayer() ? creature.getActingPlayer().getReputation() : 0),
	
	CUR_CP(0x21, creature -> (int) creature.getCurrentCp()),
	MAX_CP(0x22, Creature::getMaxCp);
	
	private int _clientId;
	private Function<Creature, Integer> _valueSupplier;
	
	StatusUpdateType(int clientId, Function<Creature, Integer> valueSupplier)
	{
		_clientId = clientId;
		_valueSupplier = valueSupplier;
	}
	
	public int getClientId()
	{
		return _clientId;
	}
	
	public int getValue(Creature creature)
	{
		return _valueSupplier.apply(creature);
	}
}
