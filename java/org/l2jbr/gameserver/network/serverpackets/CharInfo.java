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

import java.util.Set;

import org.l2jbr.Config;
import org.l2jbr.commons.network.PacketWriter;
import org.l2jbr.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jbr.gameserver.model.VariationInstance;
import org.l2jbr.gameserver.model.actor.instance.DecoyInstance;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2jbr.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2jbr.gameserver.model.clan.Clan;
import org.l2jbr.gameserver.model.interfaces.ILocational;
import org.l2jbr.gameserver.model.itemcontainer.Inventory;
import org.l2jbr.gameserver.model.skills.AbnormalVisualEffect;
import org.l2jbr.gameserver.model.zone.ZoneId;
import org.l2jbr.gameserver.network.OutgoingPackets;

public class CharInfo implements IClientOutgoingPacket
{
	private final PlayerInstance _player;
	private final Clan _clan;
	private int _objId;
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	private final float _attackSpeedMultiplier;
	private int _enchantLevel = 0;
	private int _armorEnchant = 0;
	private int _vehicleId = 0;
	private final boolean _gmSeeInvis;
	
	private static final int[] PAPERDOLL_ORDER = new int[]
	{
		Inventory.PAPERDOLL_UNDER,
		Inventory.PAPERDOLL_HEAD,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_LHAND,
		Inventory.PAPERDOLL_GLOVES,
		Inventory.PAPERDOLL_CHEST,
		Inventory.PAPERDOLL_LEGS,
		Inventory.PAPERDOLL_FEET,
		Inventory.PAPERDOLL_CLOAK,
		Inventory.PAPERDOLL_RHAND,
		Inventory.PAPERDOLL_HAIR,
		Inventory.PAPERDOLL_HAIR2
	};
	
	public CharInfo(PlayerInstance player, boolean gmSeeInvis)
	{
		_player = player;
		_objId = player.getObjectId();
		_clan = player.getClan();
		if ((_player.getVehicle() != null) && (_player.getInVehiclePosition() != null))
		{
			_x = _player.getInVehiclePosition().getX();
			_y = _player.getInVehiclePosition().getY();
			_z = _player.getInVehiclePosition().getZ();
			_vehicleId = _player.getVehicle().getObjectId();
		}
		else
		{
			_x = _player.getX();
			_y = _player.getY();
			_z = _player.getZ();
		}
		_heading = _player.getHeading();
		_mAtkSpd = _player.getMAtkSpd();
		_pAtkSpd = _player.getPAtkSpd();
		_attackSpeedMultiplier = (float) _player.getAttackSpeedMultiplier();
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
		_enchantLevel = player.getInventory().getWeaponEnchant();
		_armorEnchant = player.getInventory().getArmorMinEnchant();
		_gmSeeInvis = gmSeeInvis;
	}
	
	public CharInfo(DecoyInstance decoy, boolean gmSeeInvis)
	{
		this(decoy.getActingPlayer(), gmSeeInvis); // init
		_objId = decoy.getObjectId();
		_x = decoy.getX();
		_y = decoy.getY();
		_z = decoy.getZ();
		_heading = decoy.getHeading();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.CHAR_INFO.writeId(packet);
		final CeremonyOfChaosEvent event = _player.getEvent(CeremonyOfChaosEvent.class);
		final CeremonyOfChaosMember cocPlayer = event != null ? event.getMember(_player.getObjectId()) : null;
		packet.writeC(0x00); // Grand Crusade
		packet.writeD(_x); // Confirmed
		packet.writeD(_y); // Confirmed
		packet.writeD(_z); // Confirmed
		packet.writeD(_vehicleId); // Confirmed
		packet.writeD(_objId); // Confirmed
		packet.writeS(_player.getAppearance().getVisibleName()); // Confirmed
		
		packet.writeH(_player.getRace().ordinal()); // Confirmed
		packet.writeC(_player.getAppearance().isFemale() ? 0x01 : 0x00); // Confirmed
		packet.writeD(_player.getBaseClass()); // Confirmed
		
		for (int slot : getPaperdollOrder())
		{
			packet.writeD(_player.getInventory().getPaperdollItemDisplayId(slot)); // Confirmed
		}
		
		for (int slot : getPaperdollOrderAugument())
		{
			final VariationInstance augment = _player.getInventory().getPaperdollAugmentation(slot);
			packet.writeD(augment != null ? augment.getOption1Id() : 0); // Confirmed
			packet.writeD(augment != null ? augment.getOption2Id() : 0); // Confirmed
		}
		
		packet.writeC(_armorEnchant);
		
		for (int slot : getPaperdollOrderVisualId())
		{
			packet.writeD(_player.getInventory().getPaperdollItemVisualId(slot));
		}
		
		packet.writeC(_player.getPvpFlag());
		packet.writeD(_player.getReputation());
		
		packet.writeD(_mAtkSpd);
		packet.writeD(_pAtkSpd);
		
		packet.writeH(_runSpd);
		packet.writeH(_walkSpd);
		packet.writeH(_swimRunSpd);
		packet.writeH(_swimWalkSpd);
		packet.writeH(_flyRunSpd);
		packet.writeH(_flyWalkSpd);
		packet.writeH(_flyRunSpd);
		packet.writeH(_flyWalkSpd);
		packet.writeF(_moveMultiplier);
		packet.writeF(_attackSpeedMultiplier);
		
		packet.writeF(_player.getCollisionRadius());
		packet.writeF(_player.getCollisionHeight());
		
		packet.writeD(_player.getVisualHair());
		packet.writeD(_player.getVisualHairColor());
		packet.writeD(_player.getVisualFace());
		
		packet.writeS(_gmSeeInvis ? "Invisible" : _player.getAppearance().getVisibleTitle());
		
		packet.writeD(_player.getAppearance().getVisibleClanId());
		packet.writeD(_player.getAppearance().getVisibleClanCrestId());
		packet.writeD(_player.getAppearance().getVisibleAllyId());
		packet.writeD(_player.getAppearance().getVisibleAllyCrestId());
		
		packet.writeC(_player.isSitting() ? 0x00 : 0x01); // Confirmed
		packet.writeC(_player.isRunning() ? 0x01 : 0x00); // Confirmed
		packet.writeC(_player.isInCombat() ? 0x01 : 0x00); // Confirmed
		
		packet.writeC(!_player.isInOlympiadMode() && _player.isAlikeDead() ? 0x01 : 0x00); // Confirmed
		
		packet.writeC(_player.isInvisible() ? 0x01 : 0x00);
		
		packet.writeC(_player.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		packet.writeC(_player.getPrivateStoreType().getId()); // Confirmed
		
		packet.writeH(_player.getCubics().size()); // Confirmed
		_player.getCubics().keySet().forEach(packet::writeH);
		
		packet.writeC(_player.isInMatchingRoom() ? 0x01 : 0x00); // Confirmed
		
		packet.writeC(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
		packet.writeH(_player.getRecomHave()); // Confirmed
		packet.writeD(_player.getMountNpcId() == 0 ? 0 : _player.getMountNpcId() + 1000000);
		
		packet.writeD(_player.getClassId().getId()); // Confirmed
		packet.writeD(0x00); // TODO: Find me!
		packet.writeC(_player.isMounted() ? 0 : _enchantLevel); // Confirmed
		
		packet.writeC(_player.getTeam().getId()); // Confirmed
		
		packet.writeD(_player.getClanCrestLargeId());
		packet.writeC(_player.getNobleLevel()); // Confirmed
		packet.writeC(_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2?
		
		packet.writeC(_player.isFishing() ? 1 : 0); // Confirmed
		
		final ILocational baitLocation = _player.getFishing().getBaitLocation();
		packet.writeD(baitLocation.getX()); // Confirmed
		packet.writeD(baitLocation.getY()); // Confirmed
		packet.writeD(baitLocation.getZ()); // Confirmed
		
		packet.writeD(_player.getAppearance().getNameColor()); // Confirmed
		
		packet.writeD(_heading); // Confirmed
		
		packet.writeC(_player.getPledgeClass());
		packet.writeH(_player.getPledgeType());
		
		packet.writeD(_player.getAppearance().getTitleColor()); // Confirmed
		
		packet.writeC(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
		
		packet.writeD(_clan != null ? _clan.getReputationScore() : 0);
		packet.writeD(_player.getTransformationDisplayId()); // Confirmed
		packet.writeD(_player.getAgathionId()); // Confirmed
		
		packet.writeC(0x00); // TODO: Find me!
		
		packet.writeD((int) Math.round(_player.getCurrentCp())); // Confirmed
		packet.writeD(_player.getMaxHp()); // Confirmed
		packet.writeD((int) Math.round(_player.getCurrentHp())); // Confirmed
		packet.writeD(_player.getMaxMp()); // Confirmed
		packet.writeD((int) Math.round(_player.getCurrentMp())); // Confirmed
		
		packet.writeC(0x00); // TODO: Find me!
		final Set<AbnormalVisualEffect> abnormalVisualEffects = _player.getEffectList().getCurrentAbnormalVisualEffects();
		packet.writeD(abnormalVisualEffects.size() + (_gmSeeInvis ? 1 : 0)); // Confirmed
		for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects)
		{
			packet.writeH(abnormalVisualEffect.getClientId()); // Confirmed
		}
		if (_gmSeeInvis)
		{
			packet.writeH(AbnormalVisualEffect.STEALTH.getClientId());
		}
		packet.writeC(cocPlayer != null ? cocPlayer.getPosition() : _player.isTrueHero() ? 100 : 0);
		packet.writeC(_player.isHairAccessoryEnabled() ? 0x01 : 0x00); // Hair accessory
		packet.writeC(_player.getAbilityPointsUsed()); // Used Ability Points
		return true;
	}
	
	@Override
	public int[] getPaperdollOrder()
	{
		return PAPERDOLL_ORDER;
	}
}
