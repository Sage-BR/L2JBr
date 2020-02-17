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

/**
 * This file contains all movies.
 * @author St3eT
 */
public enum Movie
{
	SC_LINDVIOR(1, true),
	SC_ECHMUS_OPENING(2, true),
	SC_ECHMUS_SUCCESS(3, true),
	SC_ECHMUS_FAIL(4, true),
	SC_BOSS_TIAT_OPENING(5, true),
	SC_BOSS_TIAT_ENDING_SUCCES(6, true),
	SC_BOSS_TIAT_ENDING_FAIL(7, true),
	SSQ_SUSPICIOUS_DEATHS(8, true),
	SSQ_DYING_MASSAGE(9, true),
	SSQ_CONTRACT_OF_MAMMON(10, true),
	SSQ_RITUAL_OF_PRIEST(11, true),
	SSQ_SEALING_EMPEROR_1ST(12, true),
	SSQ_SEALING_EMPEROR_2ND(13, true),
	SSQ_EMBRYO(14, true),
	SC_BOSS_FREYA_OPENING(15, false),
	SC_BOSS_FREYA_PHASECH_A(16, true),
	SC_BOSS_FREYA_PHASECH_B(17, true),
	SC_BOSS_KEGOR_INTRUSION(18, false),
	SC_BOSS_FREYA_ENDING_A(19, false),
	SC_BOSS_FREYA_ENDING_B(20, false),
	SC_BOSS_FREYA_FORCED_DEFEAT(21, true),
	SC_BOSS_FREYA_DEFEAT(22, true),
	SC_ICE_HEAVYKNIGHT_SPAWN(23, false),
	SSQ2_HOLY_BURIAL_GROUND_OPENING(24, true),
	SSQ2_HOLY_BURIAL_GROUND_CLOSING(25, true),
	SSQ2_SOLINA_TOMB_OPENING(26, false),
	SSQ2_SOLINA_TOMB_CLOSING(27, true),
	SSQ2_ELYSS_NARRATION(28, false),
	SSQ2_BOSS_OPENING(29, false),
	SSQ2_BOSS_CLOSING(30, false),
	SC_ISTINA_OPENING(31, true),
	SC_ISTINA_ENDING_A(32, false),
	SC_ISTINA_ENDING_B(33, true),
	SC_ISTINA_BRIDGE(34, true),
	SC_OCTABIS_OPENING(35, true),
	SC_OCTABIS_PHASECH_A(36, false),
	SC_OCTABIS_PHASECH_B(37, true),
	SC_OCTABIS_ENDING(38, true),
	SC_GD1_PROLOGUE(42, false),
	SC_TALKING_ISLAND_BOSS_OPENING(43, false),
	SC_TALKING_ISLAND_BOSS_ENDING(44, false),
	SC_AWAKENING_OPENING(45, false),
	SC_AWAKENING_BOSS_OPENING(46, false),
	SC_AWAKENING_BOSS_ENDING_A(47, false),
	SC_AWAKENING_BOSS_ENDING_B(48, false),
	SC_EARTHWORM_ENDING(49, false),
	SC_SPACIA_OPENING(50, true),
	SC_SPACIA_A(51, true),
	SC_SPACIA_B(52, true),
	SC_SPACIA_C(53, false),
	SC_SPACIA_ENDING(54, true),
	SC_AWAKENING_VIEW(55, true),
	SC_AWAKENING_OPENING_C(56, false),
	SC_AWAKENING_OPENING_D(57, false),
	SC_AWAKENING_OPENING_E(58, false),
	SC_AWAKENING_OPENING_F(59, false),
	SC_TAUTI_OPENING_B(69, true),
	SC_TAUTI_OPENING(70, true),
	SC_TAUTI_PHASE(71, false),
	SC_TAUTI_ENDING(72, true),
	SC_SOULISLAND_QUEST(73, false),
	SC_METUCELLAR_OPENING(74, false),
	SC_SUB_QUEST(75, false),
	SC_LIND_OPENING(76, false),
	SC_KATACOMB(77, true),
	SC_NECRO(78, true),
	SC_HELLBOUND(79, true),
	SC_HONORS(80, true),
	SC_KELBIM_OPENING(81, false),
	SC_NOBLE_OPENING(99, true),
	SC_NOBLE_ENDING(100, true),
	SI_ILLUSION_01_QUE(101, true),
	SI_ILLUSION_02_QUE(102, true),
	SI_ILLUSION_03_QUE(103, true),
	SI_ARKAN_ENTER(104, true),
	SI_BARLOG_OPENING(105, true),
	SI_BARLOG_STORY(106, true),
	SI_ILLUSION_04_QUE(107, false),
	SI_ILLUSION_05_QUE(108, false),
	SC_BLOODVEIN_OPENING(109, false),
	ERT_QUEST_A(110, false),
	ERT_QUEST_B(111, false),
	EPIC_FREYA_SLIDE(112, true),
	EPIC_KELBIM_SLIDE(113, true),
	EPIC_TAUTI_SLIDE(114, true),
	EPIC_FREYA_SCENE(115, true),
	EPIC_KELBIM_SCENE(116, false),
	EPIC_TAUTI_SCENE(117, false),
	SC_RAMONA_TRANS_A(119, true),
	SC_RAMONA_TRANS_B(120, true),
	SC_HELIOS_TRANS_A(121, true),
	SC_HELIOS_TRANS_B(122, true),
	// 152
	SC_KAIN_INTRO(123, true),
	SC_CAMILLE_INTRO(124, true),
	SC_CAMILLE_ENDING(125, true),
	SI_DEFENCE_SUCCESS(126, true),
	SI_DEFENCE_FAIL(127, true),
	SI_INZONE_FIN(128, true),
	SI_ENTER_CITADEL(129, true),
	SI_CHOICE_OF_KAIN_A(130, true),
	SI_CHOICE_OF_KAIN_B(131, true),
	EPIC_KAIN_SCENE(132, true),
	ARENA_START_BLUETEAM(133, true),
	ARENA_START_REDTEAM(134, true),
	SC_ETIS_VAN_ETINA_TRANS(135, true),
	SC_ETIS_VAN_ETINA_ENDING(136, true),
	SC_KAIN_BOSS_ENDING(137, true),
	SC_ANTARAS_INTRO(138, true),
	SC_ANTARAS_TRANS(139, true),
	EP5_ASTATINE_QST_START(140, true),
	EP5_ASTATINE_QST_END(141, true),
	EP5_HELIOS_QST_END(142, true),
	EP5_ETINA_QST_START_A(143, true),
	EP5_ETINA_QST_START_B(144, true),
	EP5_ETINA_QST_END(145, true),
	SC_INZONE_CAMILLE_INTRO(146, true),
	SC_INZONE_KAIN_INTRO(147, true),
	SC_INZONE_HELIOS_TRANS_A(148, true),
	SC_INZONE_HELIOS_TRANS_B(149, true),
	LAND_KSERTH_A(1000, true),
	LAND_KSERTH_B(1001, true),
	LAND_UNDEAD_A(1002, true),
	LAND_DISTRUCTION_A(1003, true),
	LAND_ANNIHILATION_A(1004, true),
	G_CARTIA_1_SIN(2001, false),
	G_CARTIA_2_SIN(2002, false);
	
	private final int _clientId;
	private final boolean _isEscapable;
	
	private Movie(int clientId, boolean isEscapable)
	{
		_clientId = clientId;
		_isEscapable = isEscapable;
	}
	
	/**
	 * @return the client id.
	 */
	public int getClientId()
	{
		return _clientId;
	}
	
	/**
	 * @return {@code true} if movie can be escaped (skipped), {@code false} otherwise.
	 */
	public boolean isEscapable()
	{
		return _isEscapable;
	}
	
	/**
	 * Finds the {@code Movie} by its clientId
	 * @param clientId the clientId
	 * @return the {@code Movie} if its found, {@code null} otherwise.
	 */
	public static Movie findByClientId(int clientId)
	{
		for (Movie movie : values())
		{
			if (movie.getClientId() == clientId)
			{
				return movie;
			}
		}
		return null;
	}
}