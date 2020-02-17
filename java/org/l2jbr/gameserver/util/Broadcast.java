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
package org.l2jbr.gameserver.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jbr.gameserver.enums.ChatType;
import org.l2jbr.gameserver.model.World;
import org.l2jbr.gameserver.model.actor.Creature;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.serverpackets.CharInfo;
import org.l2jbr.gameserver.network.serverpackets.CreatureSay;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jbr.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jbr.gameserver.network.serverpackets.RelationChanged;

/**
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class Broadcast
{
	private static Logger LOGGER = Logger.getLogger(Broadcast.class.getName());
	
	/**
	 * Send a packet to all PlayerInstance in the _KnownPlayers of the Creature that have the Character targeted.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * PlayerInstance in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the Creature, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param creature
	 * @param mov
	 */
	public static void toPlayersTargettingMyself(Creature creature, IClientOutgoingPacket mov)
	{
		World.getInstance().forEachVisibleObject(creature, PlayerInstance.class, player ->
		{
			if (player.getTarget() == creature)
			{
				player.sendPacket(mov);
			}
		});
	}
	
	/**
	 * Send a packet to all PlayerInstance in the _KnownPlayers of the Creature.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * PlayerInstance in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the Creature, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param creature
	 * @param mov
	 */
	public static void toKnownPlayers(Creature creature, IClientOutgoingPacket mov)
	{
		World.getInstance().forEachVisibleObject(creature, PlayerInstance.class, player ->
		{
			try
			{
				player.sendPacket(mov);
				if ((mov instanceof CharInfo) && (creature.isPlayer()))
				{
					final int relation = ((PlayerInstance) creature).getRelation(player);
					final Integer oldrelation = creature.getKnownRelations().get(player.getObjectId());
					if ((oldrelation != null) && (oldrelation != relation))
					{
						final RelationChanged rc = new RelationChanged();
						rc.addRelation((PlayerInstance) creature, relation, creature.isAutoAttackable(player));
						if (creature.hasSummon())
						{
							final Summon pet = creature.getPet();
							if (pet != null)
							{
								rc.addRelation(pet, relation, creature.isAutoAttackable(player));
							}
							if (creature.hasServitors())
							{
								creature.getServitors().values().forEach(s -> rc.addRelation(s, relation, creature.isAutoAttackable(player)));
							}
						}
						player.sendPacket(rc);
						creature.getKnownRelations().put(player.getObjectId(), relation);
					}
				}
			}
			catch (NullPointerException e)
			{
				LOGGER.log(Level.WARNING, e.getMessage(), e);
			}
		});
	}
	
	/**
	 * Send a packet to all PlayerInstance in the _KnownPlayers (in the specified radius) of the Creature.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * PlayerInstance in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the Creature, server just needs to go through _knownPlayers to send Server->Client Packet and check the distance between the targets.<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param creature
	 * @param mov
	 * @param radius
	 */
	public static void toKnownPlayersInRadius(Creature creature, IClientOutgoingPacket mov, int radius)
	{
		if (radius < 0)
		{
			radius = 1500;
		}
		
		World.getInstance().forEachVisibleObjectInRange(creature, PlayerInstance.class, radius, mov::sendTo);
	}
	
	/**
	 * Send a packet to all PlayerInstance in the _KnownPlayers of the Creature and to the specified character.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * PlayerInstance in the detection area of the Creature are identified in <B>_knownPlayers</B>.<BR>
	 * In order to inform other players of state modification on the Creature, server just need to go through _knownPlayers to send Server->Client Packet<BR>
	 * @param creature
	 * @param mov
	 */
	public static void toSelfAndKnownPlayers(Creature creature, IClientOutgoingPacket mov)
	{
		if (creature.isPlayer())
		{
			creature.sendPacket(mov);
		}
		
		toKnownPlayers(creature, mov);
	}
	
	// To improve performance we are comparing values of radius^2 instead of calculating sqrt all the time
	public static void toSelfAndKnownPlayersInRadius(Creature creature, IClientOutgoingPacket mov, int radius)
	{
		if (radius < 0)
		{
			radius = 600;
		}
		
		if (creature.isPlayer())
		{
			creature.sendPacket(mov);
		}
		
		World.getInstance().forEachVisibleObjectInRange(creature, PlayerInstance.class, radius, mov::sendTo);
	}
	
	/**
	 * Send a packet to all PlayerInstance present in the world.<BR>
	 * <B><U> Concept</U> :</B><BR>
	 * In order to inform other players of state modification on the Creature, server just need to go through _allPlayers to send Server->Client Packet<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SEND Server->Client packet to this Creature (to do this use method toSelfAndKnownPlayers)</B></FONT><BR>
	 * @param packet
	 */
	public static void toAllOnlinePlayers(IClientOutgoingPacket packet)
	{
		for (PlayerInstance player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	public static void toAllOnlinePlayers(String text)
	{
		toAllOnlinePlayers(text, false);
	}
	
	public static void toAllOnlinePlayers(String text, boolean isCritical)
	{
		toAllOnlinePlayers(new CreatureSay(0, isCritical ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, "", text));
	}
	
	public static void toAllOnlinePlayersOnScreen(String text)
	{
		toAllOnlinePlayers(new ExShowScreenMessage(text, 10000));
	}
}
