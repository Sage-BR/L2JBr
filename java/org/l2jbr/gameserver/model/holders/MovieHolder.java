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
package org.l2jbr.gameserver.model.holders;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jbr.gameserver.enums.Movie;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

/**
 * @author St3eT
 */
public class MovieHolder
{
	private final Movie _movie;
	private final List<PlayerInstance> _players;
	private final Collection<PlayerInstance> _votedPlayers = ConcurrentHashMap.newKeySet();
	
	public MovieHolder(List<PlayerInstance> players, Movie movie)
	{
		_players = players;
		_movie = movie;
		
		_players.forEach(p -> p.playMovie(this));
	}
	
	public Movie getMovie()
	{
		return _movie;
	}
	
	public void playerEscapeVote(PlayerInstance player)
	{
		if (_votedPlayers.contains(player) || !_players.contains(player) || !_movie.isEscapable())
		{
			return;
		}
		
		_votedPlayers.add(player);
		
		if (((_votedPlayers.size() * 100) / _players.size()) >= 50)
		{
			_players.forEach(PlayerInstance::stopMovie);
		}
	}
	
	public List<PlayerInstance> getPlayers()
	{
		return _players;
	}
	
	public Collection<PlayerInstance> getVotedPlayers()
	{
		return _votedPlayers;
	}
}