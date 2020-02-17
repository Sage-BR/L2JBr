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
package ai.others.VillageMasters.ClassTransferTalk;

import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.gameserver.enums.Race;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;

import ai.AbstractNpcAI;

/**
 * This script is not fully retail like because of NCSoft<br>
 * laziness to update every npc dialog (still using old format for class transfer).<br>
 * So some parts of script are done by custom way, so we don't need<br>
 * to copy retail issues/bugs/typos.
 * @author Gladicek
 */
public class ClassTransferTalk extends AbstractNpcAI
{
	// NPCs
	// Talking Island Village (Administrative office)
	private static final int FRANCO = 32153;
	private static final int RIVIAN = 32147;
	private static final int DEVON = 32160;
	private static final int TOOK = 32150;
	private static final int KAKAI = 30565;
	private static final int MOKA = 32157;
	private static final int VALFAR = 32146;
	// @formatter:off
	private static final int[] FIRST_CLASS_TOWNS =
	{
		// Town of Gludio
		30289, 30288, 30297, 30505, 30504, 30503, 32196,
		// Town of Dion
		30070, 30066, 30462, 30508, 30595, 30594, 32199,
		// Gludin Village
		30290, 30499, 30498, 30500, 32193, 30373, 30037,
		// Town of Schuttgard  
		32095, 32098, 32094, 32092, 32093, 32097, 32202
	};
	private static final int[] SECOND_CLASS_TOWNS =
	{
		// Town of Goddard
		31279, 31755, 31276, 32226, 32225, 31272, 31269, 31288, 31285,
		// Town of Aden
		30865, 30862, 32221, 32222, 30854, 30857, 30849, 30847, 30845,
		// Town of Giran
		30512, 30474, 32213, 32214, 30513, 30511, 30109, 30120, 30115,
		// Town of Rune
		31317, 31314, 31324, 31321, 31326, 31336, 31334, 31328, 31331,
		// Town of Oren
		30681, 30677, 30676, 30187, 30191, 30195, 32205, 32206,
		// Heine
		30905, 30900, 30894, 30897, 32210, 32209, 30913, 30910,
		// Hunters Village
		30687, 30685, 32218, 32217, 30694, 30689, 30699, 30704,
		// Ivory Tower
		30174, 30175, 30176
	};
	// @formatter:on
	
	private ClassTransferTalk()
	{
		addStartNpc(FRANCO, RIVIAN, DEVON, TOOK, KAKAI, MOKA, VALFAR);
		addStartNpc(FIRST_CLASS_TOWNS);
		addStartNpc(SECOND_CLASS_TOWNS);
		addTalkId(FRANCO, RIVIAN, DEVON, TOOK, KAKAI, MOKA, VALFAR);
		addTalkId(FIRST_CLASS_TOWNS);
		addTalkId(SECOND_CLASS_TOWNS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "30565-02.html":
			case "30565-03.html":
			case "30565-04.html":
			case "30565-05.html":
			case "30565-06.html":
			case "30565-07.html":
			case "30565-08.html":
			case "30565-09.html":
			case "32147-02.html":
			case "32147-03.html":
			case "32147-04.html":
			case "32147-05.html":
			case "32147-06.html":
			case "32147-07.html":
			case "32147-08.html":
			case "32147-09.html":
			case "32147-10.html":
			case "32147-11.html":
			case "32150-02.html":
			case "32150-03.html":
			case "32150-04.html":
			case "32150-05.html":
			case "32150-06.html":
			case "32150-07.html":
			case "32150-08.html":
			case "32150-09.html":
			case "32157-01.html":
			case "32157-02.html":
			case "32157-03.html":
			case "32157-04.html":
			case "32160-02.html":
			case "32160-03.html":
			case "32160-04.html":
			case "32160-05.html":
			case "32160-06.html":
			case "32160-07.html":
			case "32160-08.html":
			case "32160-09.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		String htmltext = null;
		
		// Custom... retail still use old class transfer way for quest
		if (CommonUtil.contains(FIRST_CLASS_TOWNS, npc.getId()))
		{
			htmltext = "first_class_transfer.html";
		}
		else if (CommonUtil.contains(SECOND_CLASS_TOWNS, npc.getId()))
		{
			htmltext = "second_class_transfer.html";
		}
		else
		{
			switch (npc.getId())
			{
				case FRANCO:
				{
					if (player.getRace() == Race.HUMAN)
					{
						if (player.isMageClass())
						{
							htmltext = "32153-02.html";
						}
						else
						{
							// Custom (missing html for fighters?!)
							htmltext = "first_class_transfer.html";
						}
						break;
					}
					htmltext = "32153-01.html";
					break;
				}
				case RIVIAN:
				{
					if (player.getRace() == Race.ELF)
					{
						if (player.isMageClass())
						{
							htmltext = "32147-07.html";
						}
						else
						{
							htmltext = "32147-02.html";
						}
						break;
					}
					htmltext = "32147-01.html";
					break;
				}
				case DEVON:
				{
					if (player.getRace() == Race.DARK_ELF)
					{
						if (player.isMageClass())
						{
							htmltext = "32160-02.html";
						}
						else
						{
							htmltext = "32160-07.html";
						}
						break;
					}
					htmltext = "32160-01.html";
					break;
				}
				case TOOK:
				{
					if (player.getRace() == Race.ORC)
					{
						if (player.isMageClass())
						{
							htmltext = "32150-07.html";
						}
						else
						{
							htmltext = "32150-02.html";
						}
						break;
					}
					htmltext = "32150-01.html";
					break;
				}
				case KAKAI:
				{
					if (player.getRace() == Race.ORC)
					{
						if (player.isMageClass())
						{
							htmltext = "30565-07.html";
						}
						else
						{
							htmltext = "30565-02.html";
						}
						break;
					}
					htmltext = "30565-01.html";
					break;
				}
				case MOKA:
				{
					// Custom because on retail you can access this with every race...
					if (player.getRace() == Race.DWARF)
					{
						htmltext = "32157-01.html";
					}
					else
					{
						htmltext = "32157-05.html";
					}
					break;
				}
				case VALFAR:
				{
					// Retail like
					htmltext = "first_class_transfer.html";
					break;
				}
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new ClassTransferTalk();
	}
}
