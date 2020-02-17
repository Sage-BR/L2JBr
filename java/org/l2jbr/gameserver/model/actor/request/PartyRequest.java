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
package org.l2jbr.gameserver.model.actor.request;

import java.util.Objects;

import org.l2jbr.gameserver.model.Party;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author UnAfraid
 */
public class PartyRequest extends AbstractRequest
{
	private final PlayerInstance _targetPlayer;
	private final Party _party;
	
	public PartyRequest(PlayerInstance player, PlayerInstance targetPlayer, Party party)
	{
		super(player);
		Objects.requireNonNull(targetPlayer);
		Objects.requireNonNull(party);
		_targetPlayer = targetPlayer;
		_party = party;
	}
	
	public PlayerInstance getTargetPlayer()
	{
		return _targetPlayer;
	}
	
	public Party getParty()
	{
		return _party;
	}
	
	@Override
	public boolean isUsing(int objectId)
	{
		return false;
	}
	
	@Override
	public void onTimeout()
	{
		super.onTimeout();
		getActiveChar().removeRequest(getClass());
		_targetPlayer.removeRequest(getClass());
	}
}
