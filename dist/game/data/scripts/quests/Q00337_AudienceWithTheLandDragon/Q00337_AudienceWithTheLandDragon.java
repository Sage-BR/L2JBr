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
package quests.Q00337_AudienceWithTheLandDragon;

import org.l2jbr.gameserver.ai.CtrlIntention;
import org.l2jbr.gameserver.enums.QuestSound;
import org.l2jbr.gameserver.model.actor.Attackable;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.quest.Quest;
import org.l2jbr.gameserver.model.quest.QuestState;
import org.l2jbr.gameserver.model.quest.State;

/**
 * Audience with the Land Dragon (337)
 * @URL https://l2wiki.com/Audience_with_the_Land_Dragon
 * @author Gigi
 */
public class Q00337_AudienceWithTheLandDragon extends Quest
{
	// NPCs
	private static final int GABRIELLE = 30753; // Town of Giran
	private static final int KENDRA = 30851; // Town of Aden
	private static final int ORVEN = 30857; // Town of Aden
	private static final int CHAKIRIS = 30705; // Hunters Village
	private static final int KAIENA = 30720; // Ivory Tower
	private static final int MOKE = 30498; // Gludin Village
	private static final int HELTON = 30678; // Town of Oren
	private static final int GILMORE = 30754; // Dragon Valley
	private static final int THEODRIC = 30755; // Antharas's Lair
	// Misc
	private static final int MIN_LEVEL = 85;
	// Items
	private static final int FEATHER_OF_GABRIELLE = 3852; // Gabrielle's Feather
	private static final int MARSH_DRAKE_TALONS = 3854; // Marsh Drake Talons
	private static final int MARSH_STALKER_HORN = 3853; // Marsh Stalker Horn
	private static final int KRANROT_SKIN = 3855; // Kranrot Skin
	private static final int HAMRUT_LEG = 3856; // Hamrut Leg
	private static final int REMAINS_OF_SACRIFIED = 3857; // Remains of Sacrifice
	private static final int MARA_FANG = 3862; // Mara Fang
	private static final int FIRST_FRAGMENT_OF_ABYSS_JEWEL = 3859; // 1st Fragment of Abyssal Jewel
	private static final int MARK_OF_WATCHMAN = 3864; // Mark of Watchman
	private static final int SECOND_FRAGMENT_OF_ABYSS_JEWEL = 3860; // 2nd Fragment of Abyssal Jewel
	private static final int MUSFEL_FANG = 3863; // Musfel Fang
	private static final int HERALD_OF_SLAYER = 3890; // Badge of slayer
	private static final int THIRD_FRAGMENT_OF_ABYSS_JEWEL = 3861; // 3rd Fragment of Abyssal Jewe
	private static final int TOTEM_OF_LAND_DRAGON = 3858; // Totem of Land Dragon
	private static final int PORTAL_STONE = 3865; // Portal Stone
	// Monsters
	private static final int HARIT_LIZARDMAN_ZEALOT = 27172; // Quest Monstr
	private static final int SACRIFICE_OF_THE_SACRIFICED = 27171; // Quest Monstr
	private static final int JEWEL_GUARDIAN_MARA = 27168; // Quest Monstr
	private static final int JEWEL_GUARDIAN_MUSFEL = 27169; // Quest Monstr
	private static final int JEWEL_GUARDIAN_PYTON = 27170; // Quest Monstr
	private static final int HARIT_LIZARDMAN_SHAMAN = 20644; // Forest of Mirrors
	private static final int MARSH_DRAKE = 20680; // Forsaken Plains
	private static final int MARSH_STALKER = 20679; // Forsaken Plains
	private static final int KRANROT = 20650; // Forsaken Plains
	private static final int HAMRUT = 20649; // Forsaken Plains
	private static final int BLOOD_QUEEN = 18001; // Watchers Tomb
	private static final int ABYSSAL_JEWEL_1 = 27165; // Windy Hill
	private static final int ABYSSAL_JEWEL_2 = 27166; // Sea of Spores
	private static final int ABYSSAL_JEWEL_3 = 27167; // Watchers Tomb,
	private static final int CAVE_MAIDEN = 20134; // Watchers Tomb,
	private static final int CAVE_KEEPER = 20246; // Watchers Tomb,
	// Spawn
	private static boolean jewel1 = false;
	private static boolean jewel2 = false;
	private static boolean jewel3 = false;
	// @formatter:off
	private static final int[][] DROPS_ON_KILL =
	{
		{SACRIFICE_OF_THE_SACRIFICED, 1, 1, 50, REMAINS_OF_SACRIFIED},
		{HARIT_LIZARDMAN_ZEALOT, 1, 2, 50, TOTEM_OF_LAND_DRAGON},
		{KRANROT, 1, 3, 50, KRANROT_SKIN},
		{HAMRUT, 1, 3, 50, HAMRUT_LEG},
		{MARSH_DRAKE, 1, 4, 50, MARSH_DRAKE_TALONS},
		{MARSH_STALKER, 1, 4, 50, MARSH_STALKER_HORN},
		{JEWEL_GUARDIAN_MARA, 2, 5, 50, MARA_FANG},
		{JEWEL_GUARDIAN_MUSFEL, 2, 6, 50, MUSFEL_FANG}
	};
	private static final int[][] DROP_ON_ATTACK =
	{
		{ABYSSAL_JEWEL_1, 2, 5, FIRST_FRAGMENT_OF_ABYSS_JEWEL, 20, JEWEL_GUARDIAN_MARA},
		{ABYSSAL_JEWEL_2, 2, 6, SECOND_FRAGMENT_OF_ABYSS_JEWEL, 20, JEWEL_GUARDIAN_MUSFEL},
		{ABYSSAL_JEWEL_3, 4, 7, THIRD_FRAGMENT_OF_ABYSS_JEWEL, 3, JEWEL_GUARDIAN_PYTON}
	};
	// @formatter:on
	
	public Q00337_AudienceWithTheLandDragon()
	{
		super(337);
		addStartNpc(GABRIELLE);
		addTalkId(GABRIELLE, ORVEN, KENDRA, CHAKIRIS, KAIENA, MOKE, HELTON, GILMORE, THEODRIC);
		addAttackId(ABYSSAL_JEWEL_1, ABYSSAL_JEWEL_2, ABYSSAL_JEWEL_3);
		addKillId(BLOOD_QUEEN, SACRIFICE_OF_THE_SACRIFICED, HARIT_LIZARDMAN_SHAMAN, HARIT_LIZARDMAN_ZEALOT, KRANROT, HAMRUT, MARSH_DRAKE, MARSH_STALKER, JEWEL_GUARDIAN_MARA, JEWEL_GUARDIAN_MUSFEL, CAVE_MAIDEN, CAVE_KEEPER, JEWEL_GUARDIAN_PYTON);
		registerQuestItems(FEATHER_OF_GABRIELLE, MARK_OF_WATCHMAN, REMAINS_OF_SACRIFIED, TOTEM_OF_LAND_DRAGON, KRANROT_SKIN, HAMRUT_LEG, MARSH_DRAKE_TALONS, MARSH_STALKER_HORN, FIRST_FRAGMENT_OF_ABYSS_JEWEL, MARA_FANG, SECOND_FRAGMENT_OF_ABYSS_JEWEL, MUSFEL_FANG, HERALD_OF_SLAYER, THIRD_FRAGMENT_OF_ABYSS_JEWEL);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30753-02.htm":
			case "30753-03.htm":
			case "30753-05.html":
			{
				htmltext = event;
				break;
			}
			case "30753-04.htm":
			{
				qs.startQuest();
				qs.set("drop1", "1");
				qs.set("drop2", "1");
				qs.set("drop3", "1");
				qs.set("drop4", "1");
				giveItems(player, FEATHER_OF_GABRIELLE, 1);
				htmltext = event;
				break;
			}
			case "30753-08.html":
			{
				qs.set("drop5", "2");
				qs.set("drop6", "2");
				takeItems(player, MARK_OF_WATCHMAN, 4);
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30755-05.html":
			{
				if (hasQuestItems(player, THIRD_FRAGMENT_OF_ABYSS_JEWEL))
				{
					takeItems(player, THIRD_FRAGMENT_OF_ABYSS_JEWEL, 1);
					takeItems(player, HERALD_OF_SLAYER, 1);
					giveItems(player, PORTAL_STONE, 1);
					qs.exitQuest(true, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, PlayerInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case GABRIELLE:
			{
				if (qs.isCreated())
				{
					htmltext = "30753-01.htm";
				}
				else if (qs.isCond(1) && (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 4))
				{
					htmltext = "30753-06.html";
				}
				else if (qs.isCond(1) && (getQuestItemsCount(player, MARK_OF_WATCHMAN) >= 4))
				{
					htmltext = "30753-07.html";
				}
				else if (qs.isCond(2) && (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 2))
				{
					htmltext = "30753-10.html";
				}
				else if (qs.isCond(2) && (getQuestItemsCount(player, MARK_OF_WATCHMAN) >= 2))
				{
					takeItems(player, FEATHER_OF_GABRIELLE, 1);
					takeItems(player, MARK_OF_WATCHMAN, 1);
					giveItems(player, HERALD_OF_SLAYER, 1);
					qs.setCond(3, true);
					htmltext = "30753-11.html";
				}
				else if (qs.isCond(3))
				{
					htmltext = "30753-12.html";
				}
				else if (qs.isCond(4))
				{
					htmltext = "30753-13.html";
				}
				else if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
			case KENDRA:
			{
				if (qs.isCond(1))
				{
					if (hasQuestItems(player, TOTEM_OF_LAND_DRAGON))
					{
						takeItems(player, TOTEM_OF_LAND_DRAGON, 1);
						giveItems(player, MARK_OF_WATCHMAN, 1);
						qs.unset("drop2");
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						htmltext = "30851-02.html";
					}
					else
					{
						htmltext = "30851-01.html";
					}
				}
				else if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 4)
				{
					htmltext = "30851-03.html";
				}
				else
				{
					htmltext = "30851-04.htm";
				}
				break;
			}
			case ORVEN:
			{
				if (qs.isCond(1))
				{
					if (qs.getInt("drop1") == 1)
					{
						if (hasQuestItems(player, REMAINS_OF_SACRIFIED))
						{
							takeItems(player, REMAINS_OF_SACRIFIED, 1);
							giveItems(player, MARK_OF_WATCHMAN, 1);
							qs.unset("drop1");
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							htmltext = "30857-02.html";
						}
						else
						{
							htmltext = "30857-01.html";
						}
					}
					else if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 4)
					{
						htmltext = "30857-03.html";
					}
					else
					{
						htmltext = "30857-04.html";
					}
				}
				break;
			}
			case CHAKIRIS:
			{
				if (qs.isCond(1))
				{
					if (qs.getInt("drop3") == 1)
					{
						if (hasQuestItems(player, KRANROT_SKIN) && hasQuestItems(player, HAMRUT_LEG))
						{
							takeItems(player, KRANROT_SKIN, 1);
							takeItems(player, HAMRUT_LEG, 1);
							giveItems(player, MARK_OF_WATCHMAN, 1);
							qs.unset("drop3");
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							htmltext = "30705-02.html";
						}
						else
						{
							htmltext = "30705-01.html";
						}
					}
					else if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 4)
					{
						htmltext = "30705-03.html";
					}
					else
					{
						htmltext = "30705-04.html";
					}
				}
				break;
			}
			case KAIENA:
			{
				if (qs.isCond(1))
				{
					if (qs.getInt("drop4") == 1)
					{
						if (hasQuestItems(player, MARSH_DRAKE_TALONS) && hasQuestItems(player, MARSH_STALKER_HORN))
						{
							takeItems(player, MARSH_DRAKE_TALONS, 1);
							takeItems(player, MARSH_STALKER_HORN, 1);
							giveItems(player, MARK_OF_WATCHMAN, 1);
							qs.unset("drop4");
							playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							htmltext = "30720-02.html";
						}
						else
						{
							htmltext = "30720-01.html";
						}
					}
					else if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 4)
					{
						htmltext = "30720-03.html";
					}
					else
					{
						htmltext = "30720-04.html";
					}
				}
				break;
			}
			case MOKE:
			{
				if (qs.isCond(2))
				{
					switch (qs.getInt("drop5"))
					{
						case 2:
						{
							qs.set("drop5", "1");
							htmltext = "30498-01.html";
							break;
						}
						case 1:
						{
							if (hasQuestItems(player, FIRST_FRAGMENT_OF_ABYSS_JEWEL) && hasQuestItems(player, MARA_FANG))
							{
								takeItems(player, FIRST_FRAGMENT_OF_ABYSS_JEWEL, 1);
								takeItems(player, MARA_FANG, 1);
								giveItems(player, MARK_OF_WATCHMAN, 1);
								qs.unset("drop5");
								playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								htmltext = "30498-03.html";
							}
							else
							{
								htmltext = "30498-02.html";
							}
							break;
						}
						case 0:
						{
							if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 2)
							{
								htmltext = "30498-04.html";
							}
							else
							{
								htmltext = "30498-05.html";
							}
							break;
						}
					}
				}
				break;
			}
			case HELTON:
			{
				if (qs.isCond(2))
				{
					switch (qs.getInt("drop6"))
					{
						case 2:
						{
							qs.set("drop6", "1");
							htmltext = "30678-01.html";
							break;
						}
						case 1:
						{
							if (hasQuestItems(player, SECOND_FRAGMENT_OF_ABYSS_JEWEL) && hasQuestItems(player, MUSFEL_FANG))
							{
								takeItems(player, SECOND_FRAGMENT_OF_ABYSS_JEWEL, 1);
								takeItems(player, MUSFEL_FANG, 1);
								giveItems(player, MARK_OF_WATCHMAN, 1);
								qs.unset("drop6");
								playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								htmltext = "30678-03.html";
							}
							else
							{
								htmltext = "30678-02.html";
							}
							break;
						}
						case 0:
						{
							if (getQuestItemsCount(player, MARK_OF_WATCHMAN) < 2)
							{
								htmltext = "30678-04.html";
							}
							else
							{
								htmltext = "30678-05.html";
							}
							break;
						}
					}
				}
				break;
			}
			case GILMORE:
			{
				if (qs.isCond(2) || qs.isCond(1))
				{
					htmltext = "30754-01.html";
				}
				else if (qs.isCond(3))
				{
					qs.set("drop7", "1");
					qs.setCond(4, true);
					htmltext = "30754-02.html";
				}
				else if (qs.isCond(4))
				{
					if (hasQuestItems(player, THIRD_FRAGMENT_OF_ABYSS_JEWEL))
					{
						htmltext = "30754-05.html";
					}
					else
					{
						htmltext = "30754-04.html";
					}
				}
				break;
			}
			case THEODRIC:
			{
				if (qs.isCond(1) || qs.isCond(2))
				{
					htmltext = "30755-01.html";
				}
				else if (qs.isCond(3))
				{
					htmltext = "30755-02.html";
				}
				else if (qs.isCond(4))
				{
					if (hasQuestItems(player, THIRD_FRAGMENT_OF_ABYSS_JEWEL))
					{
						htmltext = "30755-04.html";
					}
					else
					{
						htmltext = "30755-03.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, PlayerInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs == null) || (qs.getCond() < 2))
		{
			return null;
		}
		final int npcId = npc.getId();
		for (int[] npcInfo : DROP_ON_ATTACK)
		{
			if (npcInfo[0] == npcId)
			{
				final double percentHp = ((npc.getCurrentHp() + damage) * 100.0D) / npc.getMaxHp();
				
				if (percentHp < 33.0D)
				{
					final int itemId = npcInfo[3];
					if (!hasQuestItems(attacker, itemId))
					{
						giveItems(attacker, itemId, 1);
						playSound(attacker, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				
				if (percentHp < 66.0D)
				{
					boolean spawn;
					if (npcId == ABYSSAL_JEWEL_3)
					{
						spawn = jewel3;
					}
					else if (npcId == ABYSSAL_JEWEL_2)
					{
						spawn = jewel2;
					}
					else
					{
						spawn = jewel1;
					}
					if (spawn)
					{
						for (int i = 0; i < npcInfo[4]; i++)
						{
							final Npc mob = addSpawn(npcInfo[5], npc.getX() + getRandom(-150, 150), npc.getY() + getRandom(-150, 150), npc.getZ(), npc.getHeading(), true, 60000, false);
							mob.setRunning();
							((Attackable) mob).addDamageHate(attacker, 0, 500);
							mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
						}
						
						if (npcId == ABYSSAL_JEWEL_3)
						{
							jewel3 = false;
						}
						else if (npcId == ABYSSAL_JEWEL_2)
						{
							jewel2 = false;
						}
						else
						{
							jewel1 = false;
						}
					}
					break;
				}
				if (npcId == ABYSSAL_JEWEL_3)
				{
					jewel3 = true;
					break;
				}
				if (npcId == ABYSSAL_JEWEL_2)
				{
					jewel2 = true;
					break;
				}
				if (npcId == ABYSSAL_JEWEL_1)
				{
					jewel1 = true;
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, PlayerInstance player, boolean isSummon)
	{
		final QuestState qs = player.getQuestState(getName());
		if ((qs == null) || (qs.getState() != State.STARTED))
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case HAMRUT:
			case KRANROT:
			case MARSH_STALKER:
			case MARSH_DRAKE:
			case JEWEL_GUARDIAN_MARA:
			case JEWEL_GUARDIAN_MUSFEL:
			case SACRIFICE_OF_THE_SACRIFICED:
			case HARIT_LIZARDMAN_ZEALOT:
			{
				for (int[] npcInfo : DROPS_ON_KILL)
				{
					if (npcInfo[0] == npc.getId())
					{
						if ((npcInfo[1] != qs.getCond()) || (getRandom(100) >= npcInfo[3]))
						{
							break;
						}
						final int itemId = npcInfo[4];
						if (!hasQuestItems(player, itemId))
						{
							giveItems(player, itemId, 1);
							playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						break;
					}
				}
				break;
			}
			case BLOOD_QUEEN:
			{
				if (qs.isCond(1) && (getRandom(100) < 70) && (qs.getInt("drop1") == 1) && !hasQuestItems(player, REMAINS_OF_SACRIFIED))
				{
					for (int i = 0; i < 10; i++)
					{
						addSpawn(SACRIFICE_OF_THE_SACRIFICED, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), npc.getHeading(), true, 60000, false);
					}
				}
				break;
			}
			case HARIT_LIZARDMAN_SHAMAN:
			{
				if (qs.isCond(1) && (getRandom(100) < 70) && (qs.getInt("drop2") == 1) && !hasQuestItems(player, TOTEM_OF_LAND_DRAGON))
				{
					for (int i = 0; i < 3; i++)
					{
						addSpawn(HARIT_LIZARDMAN_ZEALOT, npc.getX() + getRandom(-100, 100), npc.getY() + getRandom(-100, 100), npc.getZ(), npc.getHeading(), true, 60000, false);
					}
				}
				break;
			}
			case CAVE_MAIDEN:
			case CAVE_KEEPER:
			{
				if (qs.isCond(4) && (getRandom(100) < 50) && !hasQuestItems(player, THIRD_FRAGMENT_OF_ABYSS_JEWEL))
				{
					addSpawn(ABYSSAL_JEWEL_3, npc.getX() + getRandom(-50, 50), npc.getY() + getRandom(-50, 50), npc.getZ(), npc.getHeading(), true, 60000, false);
				}
				break;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
}