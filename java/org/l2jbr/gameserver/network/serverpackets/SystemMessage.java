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
package org.l2jbr.gameserver.network.serverpackets;

import java.util.Arrays;

import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.datatables.ItemTable;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.Summon;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.actor.templates.NpcTemplate;
import org.l2jbr.gameserver.model.items.Item;
import org.l2jbr.gameserver.model.items.instance.ItemInstance;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.OutgoingPackets;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.SystemMessageId.SMLocalisation;

/**
 * @author Forsaiken
 */
public class SystemMessage implements IClientOutgoingPacket
{
	private static final SMParam[] EMPTY_PARAM_ARRAY = new SMParam[0];
	
	public class SMParam
	{
		private final byte _type;
		private final Object _value;
		
		public SMParam(byte type, Object value)
		{
			_type = type;
			_value = value;
		}
		
		public byte getType()
		{
			return _type;
		}
		
		public Object getValue()
		{
			return _value;
		}
		
		public String getStringValue()
		{
			return (String) _value;
		}
		
		public int getIntValue()
		{
			return ((Integer) _value).intValue();
		}
		
		public long getLongValue()
		{
			return ((Long) _value).longValue();
		}
		
		public int[] getIntArrayValue()
		{
			return (int[]) _value;
		}
	}
	
	public static final byte TYPE_FACTION_NAME = 24; // c(short), faction id.
	// id 22 d (shared with 1-3,17,22
	// id 21 h
	// id 20 c
	// id 19 c
	// id 18 Q (read same as 6)
	// id 17 shared with 1-3,17,22
	public static final byte TYPE_BYTE = 20;
	public static final byte TYPE_POPUP_ID = 16;
	public static final byte TYPE_CLASS_ID = 15;
	// id 14 dSSSSS
	public static final byte TYPE_SYSTEM_STRING = 13;
	public static final byte TYPE_PLAYER_NAME = 12;
	public static final byte TYPE_DOOR_NAME = 11;
	public static final byte TYPE_INSTANCE_NAME = 10;
	public static final byte TYPE_ELEMENT_NAME = 9;
	// id 8 - ddd
	public static final byte TYPE_ZONE_NAME = 7;
	public static final byte TYPE_LONG_NUMBER = 6;
	public static final byte TYPE_CASTLE_NAME = 5;
	public static final byte TYPE_SKILL_NAME = 4;
	public static final byte TYPE_ITEM_NAME = 3;
	public static final byte TYPE_NPC_NAME = 2;
	public static final byte TYPE_INT_NUMBER = 1;
	public static final byte TYPE_TEXT = 0;
	
	private SMParam[] _params;
	private final SystemMessageId _smId;
	private int _paramIndex;
	private String _lang;
	
	public SystemMessage(int id)
	{
		_smId = SystemMessageId.getSystemMessageId(id);
		_params = _smId.getParamCount() > 0 ? new SMParam[_smId.getParamCount()] : EMPTY_PARAM_ARRAY;
	}
	
	public SystemMessage(SystemMessageId smId)
	{
		if (smId == null)
		{
			throw new NullPointerException("SystemMessageId cannot be null!");
		}
		_smId = smId;
		_params = smId.getParamCount() > 0 ? new SMParam[smId.getParamCount()] : EMPTY_PARAM_ARRAY;
	}
	
	public SystemMessage(String text)
	{
		if (text == null)
		{
			throw new NullPointerException();
		}
		_smId = SystemMessageId.getSystemMessageId(SystemMessageId.S1_2.getId());
		_params = new SMParam[1];
		addString(text);
	}
	
	public int getId()
	{
		return _smId.getId();
	}
	
	public SystemMessageId getSystemMessageId()
	{
		return _smId;
	}
	
	private void append(SMParam param)
	{
		if (_paramIndex >= _params.length)
		{
			_params = Arrays.copyOf(_params, _paramIndex + 1);
			_smId.setParamCount(_paramIndex + 1);
			// Mobius: With additional on-screen damage param (popup), length is increased.
			if (param.getType() != TYPE_POPUP_ID)
			{
				LOGGER.info("Wrong parameter count '" + (_paramIndex + 1) + "' for SystemMessageId: " + _smId);
			}
		}
		
		_params[_paramIndex++] = param;
	}
	
	public SystemMessage addString(String text)
	{
		append(new SMParam(TYPE_TEXT, text));
		return this;
	}
	
	/**
	 * Appends a Castle name parameter type, the name will be read from CastleName-e.dat.<br>
	 * <ul>
	 * <li>1-9 Castle names</li>
	 * <li>21 Fortress of Resistance</li>
	 * <li>22-33 Clan Hall names</li>
	 * <li>34 Devastated Castle</li>
	 * <li>35 Bandit Stronghold</li>
	 * <li>36-61 Clan Hall names</li>
	 * <li>62 Rainbow Springs</li>
	 * <li>63 Wild Beast Reserve</li>
	 * <li>64 Fortress of the Dead</li>
	 * <li>81-89 Territory names</li>
	 * <li>90-100 null</li>
	 * <li>101-121 Fortress names</li>
	 * </ul>
	 * @param number the conquerable entity
	 * @return the system message with the proper parameter
	 */
	public SystemMessage addCastleId(int number)
	{
		append(new SMParam(TYPE_CASTLE_NAME, number));
		return this;
	}
	
	public SystemMessage addInt(int number)
	{
		append(new SMParam(TYPE_INT_NUMBER, number));
		return this;
	}
	
	public SystemMessage addLong(long number)
	{
		append(new SMParam(TYPE_LONG_NUMBER, number));
		return this;
	}
	
	public SystemMessage addPcName(PlayerInstance pc)
	{
		append(new SMParam(TYPE_PLAYER_NAME, pc.getAppearance().getVisibleName()));
		return this;
	}
	
	/**
	 * ID from doorData.xml
	 * @param doorId
	 * @return
	 */
	public SystemMessage addDoorName(int doorId)
	{
		append(new SMParam(TYPE_DOOR_NAME, doorId));
		return this;
	}
	
	public SystemMessage addNpcName(Npc npc)
	{
		return addNpcName(npc.getTemplate());
	}
	
	public SystemMessage addNpcName(Summon npc)
	{
		return addNpcName(npc.getId());
	}
	
	public SystemMessage addNpcName(NpcTemplate template)
	{
		if (template.isUsingServerSideName())
		{
			return addString(template.getName());
		}
		return addNpcName(template.getId());
	}
	
	public SystemMessage addNpcName(int id)
	{
		append(new SMParam(TYPE_NPC_NAME, 1000000 + id));
		return this;
	}
	
	public SystemMessage addItemName(ItemInstance item)
	{
		return addItemName(item.getId());
	}
	
	public SystemMessage addItemName(Item item)
	{
		return addItemName(item.getId());
	}
	
	public SystemMessage addItemName(int id)
	{
		final Item item = ItemTable.getInstance().getTemplate(id);
		if (item.getDisplayId() != id)
		{
			return addString(item.getName());
		}
		
		append(new SMParam(TYPE_ITEM_NAME, id));
		return this;
	}
	
	public SystemMessage addZoneName(int x, int y, int z)
	{
		append(new SMParam(TYPE_ZONE_NAME, new int[]
		{
			x,
			y,
			z
		}));
		return this;
	}
	
	public SystemMessage addSkillName(Skill skill)
	{
		if (skill.getId() != skill.getDisplayId())
		{
			return addString(skill.getName());
		}
		return addSkillName(skill.getId(), skill.getLevel(), skill.getSubLevel());
	}
	
	public SystemMessage addSkillName(int id)
	{
		return addSkillName(id, 1, 0);
	}
	
	public SystemMessage addSkillName(int id, int lvl, int subLvl)
	{
		append(new SMParam(TYPE_SKILL_NAME, new int[]
		{
			id,
			lvl,
			subLvl
		}));
		return this;
	}
	
	/**
	 * Elemental name - 0(Fire) ...
	 * @param type
	 * @return
	 */
	public SystemMessage addAttribute(int type)
	{
		append(new SMParam(TYPE_ELEMENT_NAME, type));
		return this;
	}
	
	/**
	 * ID from sysstring-e.dat
	 * @param type
	 * @return
	 */
	public SystemMessage addSystemString(int type)
	{
		append(new SMParam(TYPE_SYSTEM_STRING, type));
		return this;
	}
	
	/**
	 * ID from ClassInfo-e.dat
	 * @param type
	 * @return
	 */
	public SystemMessage addClassId(int type)
	{
		append(new SMParam(TYPE_CLASS_ID, type));
		return this;
	}
	
	public SystemMessage addFactionName(int factionId)
	{
		append(new SMParam(TYPE_FACTION_NAME, factionId));
		return this;
	}
	
	public SystemMessage addPopup(int target, int attacker, int damage)
	{
		append(new SMParam(TYPE_POPUP_ID, new int[]
		{
			target,
			attacker,
			damage
		}));
		return this;
	}
	
	public SystemMessage addByte(int time)
	{
		append(new SMParam(TYPE_BYTE, time));
		return this;
	}
	
	/**
	 * Instance name from instantzonedata-e.dat
	 * @param type id of instance
	 * @return
	 */
	public SystemMessage addInstanceName(int type)
	{
		append(new SMParam(TYPE_INSTANCE_NAME, type));
		return this;
	}
	
	public SMParam[] getParams()
	{
		return _params;
	}
	
	public void setLang(String lang)
	{
		_lang = lang;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SYSTEM_MESSAGE.writeId(packet);
		
		// Localisation related.
		if (_lang != null)
		{
			final SMLocalisation sml = _smId.getLocalisation(_lang);
			if (sml != null)
			{
				final Object[] params = new Object[_paramIndex];
				for (int i = 0; i < _paramIndex; i++)
				{
					params[i] = _params[i].getValue();
				}
				
				packet.writeH(SystemMessageId.S1_2.getId());
				packet.writeC(0x01);
				packet.writeC(TYPE_TEXT);
				packet.writeS(sml.getLocalisation(params));
				return true;
			}
		}
		
		packet.writeH(getId());
		packet.writeC(_params.length);
		for (SMParam param : _params)
		{
			packet.writeC(param.getType());
			switch (param.getType())
			{
				case TYPE_ELEMENT_NAME:
				case TYPE_BYTE:
				case TYPE_FACTION_NAME:
				{
					packet.writeC(param.getIntValue());
					break;
				}
				case TYPE_CASTLE_NAME:
				case TYPE_SYSTEM_STRING:
				case TYPE_INSTANCE_NAME:
				case TYPE_CLASS_ID:
				{
					packet.writeH(param.getIntValue());
					break;
				}
				case TYPE_ITEM_NAME:
				case TYPE_INT_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_DOOR_NAME:
				{
					packet.writeD(param.getIntValue());
					break;
				}
				case TYPE_LONG_NUMBER:
				{
					packet.writeQ(param.getLongValue());
					break;
				}
				case TYPE_TEXT:
				case TYPE_PLAYER_NAME:
				{
					packet.writeS(param.getStringValue());
					break;
				}
				case TYPE_SKILL_NAME:
				{
					final int[] array = param.getIntArrayValue();
					packet.writeD(array[0]); // skill id
					packet.writeH(array[1]); // skill level
					packet.writeH(array[2]); // skill sub level
					break;
				}
				case TYPE_POPUP_ID:
				case TYPE_ZONE_NAME:
				{
					final int[] array = param.getIntArrayValue();
					packet.writeD(array[0]); // x
					packet.writeD(array[1]); // y
					packet.writeD(array[2]); // z
					break;
				}
			}
		}
		return true;
	}
}
