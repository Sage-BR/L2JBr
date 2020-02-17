/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ai.areas.GardenOfGenesis.ApherusLookout;

import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.network.NpcStringId;
import org.l2jbr.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * @author Gigi
 */
public class ApherusLookout extends AbstractNpcAI
{
	private static final int APHERUS_LOOKOUT = 22964;
	private static final int APHERUS_PACKAGE = 19001;
	private static final int APHERUS_PACKAGE1 = 19002;
	private static final int APHERUS_PACKAGE2 = 19003;
	private static final int APERUS_KEY = 17373;
	
	public ApherusLookout()
	{
		addKillId(APHERUS_LOOKOUT);
		addFirstTalkId(APHERUS_PACKAGE, APHERUS_PACKAGE1, APHERUS_PACKAGE2);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		if (event.equals("open_bag"))
		{
			if (getRandom(100) < 7)
			{
				npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.MOVED_TO_APHERUS_DIMENSION, ExShowScreenMessage.TOP_CENTER, 3000, true));
				giveItems(player, APERUS_KEY, 1);
			}
			npc.deleteMe();
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance killer, boolean isPet)
	{
		Npc aPackage = addSpawn(APHERUS_PACKAGE, npc.getX(), npc.getY(), npc.getZ(), 0, true, 120000, false);
		aPackage.setIsImmobilized(true);
		Npc bPackage = addSpawn(APHERUS_PACKAGE1, npc.getX(), npc.getY(), npc.getZ(), 0, true, 120000, false);
		bPackage.setIsImmobilized(true);
		Npc cPackage = addSpawn(APHERUS_PACKAGE2, npc.getX(), npc.getY(), npc.getZ(), 0, true, 120000, false);
		cPackage.setIsImmobilized(true);
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "19001.html";
	}
	
	public static void main(String[] args)
	{
		new ApherusLookout();
	}
}