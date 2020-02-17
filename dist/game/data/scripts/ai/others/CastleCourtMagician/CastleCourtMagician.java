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
package ai.others.CastleCourtMagician;

import org.l2jbr.gameserver.data.xml.impl.MultisellData;
import org.l2jbr.gameserver.enums.CastleSide;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.clan.ClanPrivilege;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.network.clientpackets.RequestAcquireSkill;

import ai.AbstractNpcAI;
import handlers.effecthandlers.CallPc;

/**
 * Castle Court Magician AI.
 * @author St3eT
 */
public class CastleCourtMagician extends AbstractNpcAI
{
	// NPCs
	private static final int[] COURT_MAGICIAN =
	{
		35648, // Gludio
		35649, // Dion
		35650, // Giran
		35651, // Oren
		35652, // Aden
		35653, // Innadril
		35654, // Goddard
		35655, // Rune
		35656, // Schuttgart
	};
	// Skills
	private static final int CLAN_GATE = 3632; // Clan Gate
	private static final SkillHolder DISPLAY_CLAN_GATE = new SkillHolder(5109, 1); // Production - Clan Gate
	// Items
	private static final int CLOAK_OF_LIGHT = 34925;
	private static final int CLOAK_OF_DARK = 34926;
	private static final int EPAULETTE = 9912; // Knight's Epaulette
	private static final int RED_MEDITATION = 9931; // Red Talisman of Meditation
	private static final int BLUE_DIV_PROTECTION = 9932; // Blue Talisman - Divine Protection
	private static final int BLUE_EXPLOSION = 10416; // Blue Talisman - Explosion
	private static final int BLUE_M_EXPLOSION = 10417; // Blue Talisman - Magic Explosion
	private static final int RED_MIN_CLARITY = 9917; // Red Talisman of Minimum Clarity
	private static final int RED_MAX_CLARITY = 9918; // Red Talisman of Maximum Clarity
	private static final int RED_MENTAL_REG = 9928; // Red Talisman of Mental Regeneration
	private static final int BLUE_PROTECTION = 9929; // Blue Talisman of Protection
	private static final int BLUE_INVIS = 9920; // Blue Talisman of Invisibility
	private static final int BLUE_DEFENSE = 9916; // Blue Talisman of Defense
	private static final int BLACK_ESCAPE = 9923; // Black Talisman - Escape
	private static final int BLUE_HEALING = 9924; // Blue Talisman of Healing
	private static final int RED_RECOVERY = 9925; // Red Talisman of Recovery
	private static final int BLUE_DEFENSE2 = 9926; // Blue Talisman of Defense
	private static final int BLUE_M_DEFENSE = 9927; // Blue Talisman of Magic Defense
	private static final int RED_LIFE_FORCE = 10518; // Red Talisman - Life Force
	private static final int BLUE_GREAT_HEAL = 10424; // Blue Talisman - Greater Healing
	private static final int WHITE_FIRE = 10421; // White Talisman - Fire
	private static final int[] COMMON_TALISMANS =
	{
		9914, // Blue Talisman of Power
		9915, // Blue Talisman of Wild Magic
		9920, // Blue Talisman of Invisibility
		9921, // Blue Talisman - Shield Protection
		9922, // Black Talisman - Mending
		9933, // Yellow Talisman of Power
		9934, // Yellow Talisman of Violent Haste
		9935, // Yellow Talisman of Arcane Defense
		9936, // Yellow Talisman of Arcane Power
		9937, // Yellow Talisman of Arcane Haste
		9938, // Yellow Talisman of Accuracy
		9939, // Yellow Talisman of Defense
		9940, // Yellow Talisman of Alacrity
		9941, // Yellow Talisman of Speed
		9942, // Yellow Talisman of Critical Reduction
		9943, // Yellow Talisman of Critical Damage
		9944, // Yellow Talisman of Critical Dodging
		9945, // Yellow Talisman of Evasion
		9946, // Yellow Talisman of Healing
		9947, // Yellow Talisman of CP Regeneration
		9948, // Yellow Talisman of Physical Regeneration
		9949, // Yellow Talisman of Mental Regeneration
		9950, // Grey Talisman of Weight Training
		9952, // Orange Talisman - Hot Springs CP Potion
		9953, // Orange Talisman - Elixir of Life
		9954, // Orange Talisman - Elixir of Mental Strength
		9955, // Black Talisman - Vocalization
		9956, // Black Talisman - Arcane Freedom
		9957, // Black Talisman - Physical Freedom
		9958, // Black Talisman - Rescue
		9959, // Black Talisman - Free Speech
		9960, // White Talisman of Bravery
		9961, // White Talisman of Motion
		9962, // White Talisman of Grounding
		9963, // White Talisman of Attention
		9964, // White Talisman of Bandages
		9965, // White Talisman of Protection
		10418, // White Talisman - Storm
		10420, // White Talisman - Water
		10519, // White Talisman - Earth
		10422, // White Talisman - Light
		10423, // Blue Talisman - Self-Destruction
		10419, // White Talisman - Darkness
	};
	// Misc
	private static final CategoryType[] AWAKENED_CT =
	{
		CategoryType.SIXTH_SIGEL_GROUP,
		CategoryType.SIXTH_TIR_GROUP,
		CategoryType.SIXTH_OTHEL_GROUP,
		CategoryType.SIXTH_YR_GROUP,
		CategoryType.SIXTH_FEOH_GROUP,
		CategoryType.SIXTH_IS_GROUP,
		CategoryType.SIXTH_WYNN_GROUP,
		CategoryType.SIXTH_EOLH_GROUP,
	};
	
	private CastleCourtMagician()
	{
		addStartNpc(COURT_MAGICIAN);
		addTalkId(COURT_MAGICIAN);
		addFirstTalkId(COURT_MAGICIAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		if ((player.getClan() == null) && (player.getClanId() != npc.getCastle().getOwnerId()))
		{
			return "courtmagician-01.html";
		}
		
		String htmltext = null;
		int itemId = 0;
		switch (event)
		{
			case "courtmagician.html":
			case "courtmagician-03.html":
			case "courtmagician-07.html":
			case "courtmagician-11.html":
			{
				htmltext = event;
				break;
			}
			case "giveTalisman":
			{
				if (getQuestItemsCount(player, EPAULETTE) < 10)
				{
					htmltext = "courtmagician-06.html";
					break;
				}
				
				final int categoryChance = getRandom(100);
				if (categoryChance <= 5)
				{
					final int chance = getRandom(100);
					if (chance <= 25)
					{
						itemId = RED_MEDITATION;
					}
					else if (chance <= 50)
					{
						itemId = BLUE_DIV_PROTECTION;
					}
					else if (chance <= 75)
					{
						itemId = BLUE_EXPLOSION;
					}
					else
					{
						itemId = BLUE_M_EXPLOSION;
					}
				}
				else if (categoryChance <= 15)
				{
					final int chance = getRandom(100);
					if (chance <= 20)
					{
						itemId = RED_MIN_CLARITY;
					}
					else if (chance <= 40)
					{
						itemId = RED_MAX_CLARITY;
					}
					else if (chance <= 60)
					{
						itemId = RED_MENTAL_REG;
					}
					else if (chance <= 80)
					{
						itemId = BLUE_PROTECTION;
					}
					else
					{
						itemId = BLUE_INVIS;
					}
				}
				else if (categoryChance <= 30)
				{
					final int chance = getRandom(100);
					if (chance <= 12)
					{
						itemId = BLUE_DEFENSE;
					}
					else if (chance <= 25)
					{
						itemId = BLACK_ESCAPE;
					}
					else if (chance <= 37)
					{
						itemId = BLUE_HEALING;
					}
					else if (chance <= 50)
					{
						itemId = RED_RECOVERY;
					}
					else if (chance <= 62)
					{
						itemId = BLUE_DEFENSE2;
					}
					else if (chance <= 75)
					{
						itemId = BLUE_M_DEFENSE;
					}
					else if (chance <= 87)
					{
						itemId = RED_LIFE_FORCE;
					}
					else
					{
						itemId = BLUE_GREAT_HEAL;
					}
				}
				else
				{
					final int chance = getRandom(46);
					if (chance <= 42)
					{
						itemId = COMMON_TALISMANS[chance];
					}
					else
					{
						itemId = WHITE_FIRE;
					}
				}
				takeItems(player, EPAULETTE, 10);
				giveItems(player, itemId, 1);
				htmltext = "courtmagician-04.html";
				break;
			}
			case "giveTalismanR":
			{
				htmltext = player.isInCategory(CategoryType.SIXTH_CLASS_GROUP) ? "courtmagician-09.html" : "courtmagician-10.html";
				break;
			}
			case "giveTalismanR_classSpecific":
			{
				final int playerLevel = player.getLevel();
				if ((playerLevel >= 85) && (playerLevel < 90))
				{
					showClassSpecificMultisell(player, npc, 797);
				}
				else if ((playerLevel >= 90) && (playerLevel < 95))
				{
					showClassSpecificMultisell(player, npc, 805);
				}
				else if ((playerLevel >= 95) && (playerLevel <= 98))
				{
					showClassSpecificMultisell(player, npc, 813);
				}
				else if (playerLevel >= 99) // TODO: Glory Days AI use "else if (talker->level == 99)" but on IO is max level 106 - need be confirmed for 100+ chars
				{
					showClassSpecificMultisell(player, npc, 821);
				}
				break;
			}
			case "giveTalismanR_active":
			{
				final int playerLevel = player.getLevel();
				if ((playerLevel >= 85) && (playerLevel < 90))
				{
					MultisellData.getInstance().separateAndSend(789, player, npc, false);
				}
				else if ((playerLevel >= 90) && (playerLevel < 95))
				{
					MultisellData.getInstance().separateAndSend(794, player, npc, false);
				}
				else if ((playerLevel >= 95) && (playerLevel < 99))
				{
					MultisellData.getInstance().separateAndSend(795, player, npc, false);
				}
				else if (playerLevel >= 99) // TODO: Glory Days AI use "else if (talker->level == 99)" but on IO is max level 106 - need be confirmed for 100+ chars
				{
					MultisellData.getInstance().separateAndSend(796, player, npc, false);
				}
				break;
			}
			case "giveTalismanR_passive":
			{
				final int playerLevel = player.getLevel();
				if ((playerLevel >= 85) && (playerLevel < 90))
				{
					MultisellData.getInstance().separateAndSend(789, player, npc, false);
				}
				else if ((playerLevel >= 90) && (playerLevel < 95))
				{
					MultisellData.getInstance().separateAndSend(790, player, npc, false);
				}
				else if ((playerLevel >= 95) && (playerLevel < 99))
				{
					MultisellData.getInstance().separateAndSend(791, player, npc, false);
				}
				else if (playerLevel >= 99) // TODO: Glory Days AI use "else if (talker->level == 99)" but on IO is max level 106 - need be confirmed for 100+ chars
				{
					MultisellData.getInstance().separateAndSend(792, player, npc, false);
				}
				break;
			}
			case "squadSkill":
			{
				if (player.isClanLeader() || player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
				{
					RequestAcquireSkill.showSubUnitSkillList(player);
				}
				else
				{
					htmltext = "courtmagician-05.html";
				}
				break;
			}
			case "clanTeleport":
			{
				if (player.getClanId() == npc.getCastle().getOwnerId())
				{
					final PlayerInstance clanLeader = player.getClan().getLeader().getPlayerInstance();
					
					if ((clanLeader != null) && clanLeader.isAffectedBySkill(CLAN_GATE))
					{
						if (CallPc.checkSummonTargetStatus(player, clanLeader)) // TODO: Custom one, retail dont check it but for sure lets check same conditions like when summon player by skill.
						{
							npc.setTarget(player);
							npc.doCast(DISPLAY_CLAN_GATE.getSkill());
							player.teleToLocation(clanLeader.getLocation(), true);
						}
					}
					else
					{
						htmltext = "courtmagician-02.html";
					}
				}
				break;
			}
			case "giveCloak":
			{
				final int cloakId = npc.getCastle().getSide() == CastleSide.DARK ? CLOAK_OF_DARK : CLOAK_OF_LIGHT;
				
				if (hasQuestItems(player, cloakId))
				{
					htmltext = "courtmagician-08.html";
					break;
				}
				giveItems(player, cloakId, 1);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return ((player.getClan() != null) && (player.getClanId() == npc.getCastle().getOwnerId())) ? "courtmagician.html" : "courtmagician-01.html";
	}
	
	private void showClassSpecificMultisell(PlayerInstance player, Npc npc, int index)
	{
		for (CategoryType ct : AWAKENED_CT)
		{
			if (player.isInCategory(ct))
			{
				MultisellData.getInstance().separateAndSend(index, player, npc, false);
			}
			else
			{
				index++;
			}
		}
	}
	
	public static void main(String[] args)
	{
		new CastleCourtMagician();
	}
}
