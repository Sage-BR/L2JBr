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
package ai.others.MentorGuide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.l2jbr.Config;
import org.l2jbr.commons.util.IXmlReader;
import org.l2jbr.gameserver.enums.CategoryType;
import org.l2jbr.gameserver.enums.MailType;
import org.l2jbr.gameserver.instancemanager.MailManager;
import org.l2jbr.gameserver.instancemanager.MentorManager;
import org.l2jbr.gameserver.model.Mentee;
import org.l2jbr.gameserver.model.actor.Npc;
import org.l2jbr.gameserver.model.actor.instance.PlayerInstance;
import org.l2jbr.gameserver.model.entity.Message;
import org.l2jbr.gameserver.model.events.EventType;
import org.l2jbr.gameserver.model.events.ListenerRegisterType;
import org.l2jbr.gameserver.model.events.annotations.RegisterEvent;
import org.l2jbr.gameserver.model.events.annotations.RegisterType;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerLevelChanged;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeAdd;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeLeft;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeRemove;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMenteeStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerMentorStatus;
import org.l2jbr.gameserver.model.events.impl.creature.player.OnPlayerProfessionChange;
import org.l2jbr.gameserver.model.holders.SkillHolder;
import org.l2jbr.gameserver.model.itemcontainer.Mail;
import org.l2jbr.gameserver.model.skills.BuffInfo;
import org.l2jbr.gameserver.model.skills.Skill;
import org.l2jbr.gameserver.network.SystemMessageId;
import org.l2jbr.gameserver.network.serverpackets.SystemMessage;
import org.l2jbr.gameserver.network.serverpackets.mentoring.ExMentorList;
import org.l2jbr.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * Mentor Guide AI.
 * @author Gnacik, UnAfraid
 */
public class MentorGuide extends AbstractNpcAI implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(MentorGuide.class.getName());
	
	// NPCs
	private static final int MENTOR_GUIDE = 33587;
	// Items
	private static final int MENTEE_CERT = 33800;
	private static final int MENTEE_MARK = 33804;
	private static final int MENTEE_HEADPHONE = 34759;
	private static final int DIPLOMA = 33805;
	// Skills
	private static final SkillHolder[] MENTEE_BUFFS =
	{
		new SkillHolder(9233, 1), // Mentor's Guidance
	};
	// Skills
	private static final SkillHolder[] MENTEE_BUFFS_WITHOUT_MENTOR_ONLINE =
	{
		new SkillHolder(9227, 1), // Horn Melody - Mentor
		new SkillHolder(9228, 1), // Drum Melody - Mentor
		new SkillHolder(9230, 1), // Pipe Organ Melody - Mentor
		new SkillHolder(9231, 1), // Guitar Melody - Mentor
		new SkillHolder(17082, 1), // Mentor's Prevailing Sonata
		new SkillHolder(17083, 1), // Daring Sonata - Mentor
		new SkillHolder(17084, 1), // Refreshing Sonata - Mentor
		new SkillHolder(18593, 1), // Mentor's Harmony
	};
	protected static final SkillHolder[] MENTOR_BUFFS =
	{
		new SkillHolder(9256, 1), // Mentee's Appreciation;
	};
	private static final SkillHolder MENTEE_MENTOR_SUMMON = new SkillHolder(9379, 1); // Mentee's Mentor Summon
	private static final SkillHolder MENTOR_ART_OF_SEDUCTION = new SkillHolder(18594, 1); // Mentor's Art of Seduction
	// Misc
	private static final int MAX_LEVEL = 85;
	private static final String LEVEL_UP_TITLE = "Mentee coin from Mentee leveling";
	private static final String LEVEL_UP_BODY = "Your mentee %s has reached level %d, so you are receiving some Mentee Coin. After Mentee Coin has successfully been removed and placed into your inventory please be sure to delete this letter. If your mailbox is full when any future letters are sent to you cannot be delivered and you will not receive these items.";
	private static final String MENTEE_ADDED_TITLE = "Congratulations on becoming a mentee.";
	private static final String MENTEE_ADDED_BODY = "Greetings. This is the Mentor Guide.\n\nYou will experience a world of unlimited adventures with your mentor, Exciting, isn't it?\n\nWhen you graduate from mentee status (upon awakening at level 85), you will receive a Mentee Certificate. If you bring it to me, I will give you a Diploma that you can exchange for R-grade equipment.";
	private static final String MENTEE_GRADUATE_TITLE = "Congratulations on your graduation";
	private static final String MENTEE_GRADUATE_BODY = "Greetings! This is the Mentor Guide.\nCongratulations!  Did you enjoy the time with a mentor? Here is a Mentee Certificate for graduating.\n\nFind me in town, and I'll give you a Diploma if you show me your Mentee Certificatee. You'll also get a small graduation gift!\n\nNow, on to your next Adventure!";
	private static final Map<Integer, Integer> MENTEE_COINS = new HashMap<>();
	
	@Override
	public void load()
	{
		parseDatapackFile("config/MentorCoins.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + MENTEE_COINS.size() + " mentee coins");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("mentee".equalsIgnoreCase(d.getNodeName()))
					{
						final int level = parseInteger(d.getAttributes(), "level");
						final int coins = parseInteger(d.getAttributes(), "coins");
						MENTEE_COINS.put(level, coins);
					}
				}
			}
		}
	}
	
	private MentorGuide()
	{
		addFirstTalkId(MENTOR_GUIDE);
		addStartNpc(MENTOR_GUIDE);
		addTalkId(MENTOR_GUIDE);
		load();
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, PlayerInstance player)
	{
		String htmltext = event;
		
		if (event.equalsIgnoreCase("exchange"))
		{
			if (hasQuestItems(player, MENTEE_CERT) && (player.getLevel() >= MAX_LEVEL) && player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
			{
				takeItems(player, MENTEE_CERT, 1);
				giveItems(player, DIPLOMA, 40);
				return null;
			}
			htmltext = "33587-04.htm";
		}
		else if (event.startsWith("REMOVE_BUFFS"))
		{
			final String[] params = event.split(" ");
			if (Util.isDigit(params[1]))
			{
				final int objectId = Integer.valueOf(params[1]);
				MentorManager.getInstance().getMentees(objectId).stream().filter(Objects::nonNull).filter(Mentee::isOnline).forEach(mentee ->
				{
					final PlayerInstance menteePlayer = mentee.getPlayerInstance();
					if (menteePlayer != null)
					{
						for (SkillHolder holder : MENTEE_BUFFS)
						{
							menteePlayer.stopSkillEffects(holder.getSkill());
						}
					}
					mentee.sendPacket(new ExMentorList(mentee.getPlayerInstance()));
				});
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, PlayerInstance player)
	{
		return "33587-01.htm";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MENTEE_ADD)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onMenteeAdded(OnPlayerMenteeAdd event)
	{
		// Starting buffs for Mentor
		for (SkillHolder sk : MENTOR_BUFFS)
		{
			sk.getSkill().applyEffects(event.getMentor(), event.getMentor());
		}
		
		// Starting buffs for Mentee when mentor is online
		for (SkillHolder sk : MENTEE_BUFFS)
		{
			sk.getSkill().applyEffects(event.getMentee(), event.getMentee());
		}
		
		// Starting buffs for Mentee
		for (SkillHolder sk : MENTEE_BUFFS_WITHOUT_MENTOR_ONLINE)
		{
			sk.getSkill().applyEffects(event.getMentee(), event.getMentee());
		}
		
		// Update mentor list
		event.getMentor().sendPacket(new ExMentorList(event.getMentor()));
		
		// Add the mentee skill
		handleMenteeSkills(event.getMentee());
		
		// Give mentor's buffs only if he didn't had them.
		handleMentorSkills(event.getMentor());
		
		// Send mail with the headphone
		sendMail(event.getMentee().getObjectId(), MENTEE_ADDED_TITLE, MENTEE_ADDED_BODY, MENTEE_HEADPHONE, 1);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MENTEE_STATUS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerMenteeStatus(OnPlayerMenteeStatus event)
	{
		final PlayerInstance player = event.getMentee();
		
		if (event.isMenteeOnline())
		{
			final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
			if (mentor != null)
			{
				// Starting buffs for Mentee
				for (SkillHolder sk : MENTEE_BUFFS_WITHOUT_MENTOR_ONLINE)
				{
					sk.getSkill().applyEffects(player, player);
				}
				
				if (mentor.isOnline())
				{
					//@formatter:off
					final long mentorBuffs = mentor.getPlayerInstance().getEffectList().getEffects()
						.stream()
						.map(BuffInfo::getSkill)
						.filter(Skill::isMentoring)
						.count();
					//@formatter:on
					
					if (mentorBuffs != MENTOR_BUFFS.length)
					{
						// Starting buffs for Mentor
						for (SkillHolder sk : MENTOR_BUFFS)
						{
							sk.getSkill().applyEffects(mentor.getPlayerInstance(), mentor.getPlayerInstance());
						}
					}
					
					// Starting buffs for Mentee
					for (SkillHolder sk : MENTEE_BUFFS)
					{
						sk.getSkill().applyEffects(player, player);
					}
					
					// Add the mentee skill
					handleMenteeSkills(player);
					
					mentor.sendPacket(new SystemMessage(SystemMessageId.YOUR_MENTEE_S1_HAS_CONNECTED).addString(player.getName()));
					mentor.sendPacket(new ExMentorList(mentor.getPlayerInstance()));
				}
			}
			player.sendPacket(new ExMentorList(player));
		}
		else
		{
			final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
			if ((mentor != null) && mentor.isOnline())
			{
				if (MentorManager.getInstance().isAllMenteesOffline(mentor.getObjectId(), player.getObjectId()))
				{
					MentorManager.getInstance().cancelAllMentoringBuffs(mentor.getPlayerInstance());
				}
				
				mentor.sendPacket(new SystemMessage(SystemMessageId.YOUR_MENTEE_S1_HAS_DISCONNECTED).addString(player.getName()));
				mentor.sendPacket(new ExMentorList(mentor.getPlayerInstance()));
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MENTOR_STATUS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerMentorStatus(OnPlayerMentorStatus event)
	{
		final PlayerInstance player = event.getMentor();
		
		if (event.isMentorOnline())
		{
			// stop buffs removal task
			cancelQuestTimer("REMOVE_BUFFS " + player.getObjectId(), null, null);
			
			MentorManager.getInstance().getMentees(player.getObjectId()).stream().filter(Objects::nonNull).filter(Mentee::isOnline).forEach(mentee ->
			{
				//@formatter:off
				final long menteeBuffs = mentee.getPlayerInstance().getEffectList().getEffects()
					.stream()
					.map(BuffInfo::getSkill)
					.filter(Skill::isMentoring)
					.count();
				//@formatter:on
				
				if (menteeBuffs != MENTEE_BUFFS.length)
				{
					// Starting buffs for Mentee
					for (SkillHolder sk : MENTEE_BUFFS)
					{
						sk.getSkill().applyEffects(mentee.getPlayerInstance(), mentee.getPlayerInstance());
					}
				}
				
				mentee.sendPacket(new SystemMessage(SystemMessageId.YOUR_MENTOR_S1_HAS_CONNECTED).addString(player.getName()));
				mentee.sendPacket(new ExMentorList(mentee.getPlayerInstance()));
			});
				
			if (MentorManager.getInstance().hasOnlineMentees(player.getObjectId()))
			{
				// Starting buffs for Mentor
				for (SkillHolder sk : MENTOR_BUFFS)
				{
					sk.getSkill().applyEffects(player, player);
				}
			}
			
			// Give mentor's buffs only if he didn't had them.
			handleMentorSkills(player);
			
			player.sendPacket(new ExMentorList(player));
		}
		else
		{
			startQuestTimer("REMOVE_BUFFS " + player.getObjectId(), 5 * 60 * 1000, null, null);
			MentorManager.getInstance().getMentees(player.getObjectId()).stream().filter(Objects::nonNull).filter(Mentee::isOnline).forEach(mentee ->
			{
				mentee.sendPacket(new SystemMessage(SystemMessageId.YOUR_MENTOR_S1_HAS_DISCONNECTED).addString(player.getName()));
				mentee.sendPacket(new ExMentorList(mentee.getPlayerInstance()));
			});
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onProfessionChange(OnPlayerProfessionChange event)
	{
		final PlayerInstance player = event.getPlayer();
		
		if (player.isMentor())
		{
			// Give mentor's buffs only if he didn't had them.
			handleMentorSkills(player);
			return;
		}
		
		// Not a mentee
		if (!player.isMentee())
		{
			return;
		}
		
		handleMenteeSkills(player);
		
		if (player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			handleGraduateMentee(player);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onLevelIncreased(OnPlayerLevelChanged event)
	{
		final PlayerInstance player = event.getPlayer();
		
		// Not a mentee
		if (!player.isMentee())
		{
			return;
		}
		
		checkLevelForReward(player); // Checking level to send a mail if is necessary
		
		if (player.getLevel() > MAX_LEVEL)
		{
			handleGraduateMentee(player);
		}
		else
		{
			final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
			if ((mentor != null) && mentor.isOnline())
			{
				mentor.sendPacket(new ExMentorList(mentor.getPlayerInstance()));
			}
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MENTEE_LEFT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onMenteeLeft(OnPlayerMenteeLeft event)
	{
		final PlayerInstance player = event.getMentee();
		final PlayerInstance mentor = event.getMentor().getPlayerInstance();
		// Remove the mentee skills
		player.removeSkill(MENTEE_MENTOR_SUMMON.getSkill(), true);
		
		// If player does not have any mentees anymore remove mentor skills.
		if ((mentor != null) && (MentorManager.getInstance().getMentees(mentor.getObjectId()) == null))
		{
			mentor.removeSkill(MENTOR_ART_OF_SEDUCTION.getSkill(), true);
			
			// Clear the mentee
			mentor.sendPacket(new ExMentorList(mentor));
		}
		
		// Clear mentee status
		player.sendPacket(new ExMentorList(player));
	}
	
	@RegisterEvent(EventType.ON_PLAYER_MENTEE_REMOVE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onMenteeRemove(OnPlayerMenteeRemove event)
	{
		final Mentee mentee = event.getMentee();
		final PlayerInstance mentor = event.getMentor();
		final PlayerInstance player = mentee.getPlayerInstance();
		
		if (player != null)
		{
			// Remove the mentee skills
			player.removeSkill(MENTEE_MENTOR_SUMMON.getSkill(), true);
			
			// Clear mentee status
			player.sendPacket(new ExMentorList(player));
		}
		
		// If player does not have any mentees anymore remove mentor skills.
		if (MentorManager.getInstance().getMentees(mentor.getObjectId()) == null)
		{
			mentor.removeSkill(MENTOR_ART_OF_SEDUCTION.getSkill(), true);
		}
		
		// Remove mentee from the list
		event.getMentor().sendPacket(new ExMentorList(mentor));
	}
	
	private void handleMenteeSkills(PlayerInstance player)
	{
		// Give mentee's buffs only if he didn't had them.
		if (player.getKnownSkill(MENTEE_MENTOR_SUMMON.getSkillId()) == null)
		{
			// Add the mentee skills
			player.addSkill(MENTEE_MENTOR_SUMMON.getSkill(), false);
		}
	}
	
	private void handleMentorSkills(PlayerInstance player)
	{
		// Give mentor's buffs only if he didn't had them.
		if (player.getKnownSkill(MENTOR_ART_OF_SEDUCTION.getSkillId()) == null)
		{
			// Add the mentor skills
			player.addSkill(MENTOR_ART_OF_SEDUCTION.getSkill(), false);
		}
	}
	
	private void handleGraduateMentee(PlayerInstance player)
	{
		MentorManager.getInstance().cancelAllMentoringBuffs(player);
		final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
		if (mentor != null)
		{
			MentorManager.getInstance().setPenalty(mentor.getObjectId(), Config.MENTOR_PENALTY_FOR_MENTEE_COMPLETE);
			MentorManager.getInstance().deleteMentor(mentor.getObjectId(), player.getObjectId());
			
			if (mentor.isOnline())
			{
				mentor.sendPacket(new SystemMessage(SystemMessageId.S1_HAS_AWAKENED_AND_THE_MENTOR_MENTEE_RELATIONSHIP_HAS_ENDED_THE_MENTOR_CANNOT_OBTAIN_ANOTHER_MENTEE_FOR_ONE_DAY_AFTER_THE_MENTEE_S_GRADUATION).addPcName(player));
				
				if (MentorManager.getInstance().isAllMenteesOffline(mentor.getObjectId(), player.getObjectId()))
				{
					MentorManager.getInstance().cancelAllMentoringBuffs(mentor.getPlayerInstance());
				}
				mentor.sendPacket(new ExMentorList(mentor.getPlayerInstance()));
			}
			
			// Remove the mentee skills
			player.removeSkill(MENTEE_MENTOR_SUMMON.getSkill(), true);
			
			// Clear mentee status
			player.sendPacket(new ExMentorList(player));
			
			player.sendPacket(new SystemMessage(SystemMessageId.YOUR_MENTOR_MENTEE_RELATIONSHIP_WITH_YOUR_MENTOR_S1_HAS_ENDED_AS_YOU_ARE_AN_AWAKENED_CHARACTER_OF_LV_85_OR_ABOVE_YOU_CAN_NO_LONGER_BE_PAIRED_WITH_A_MENTOR).addPcName(player));
			
			sendMail(player.getObjectId(), MENTEE_GRADUATE_TITLE, MENTEE_GRADUATE_BODY, MENTEE_CERT, 1);
		}
	}
	
	/**
	 * Verifies if player is mentee and if his current level should reward his mentor and if so sends a mail with reward.
	 * @param player
	 */
	private void checkLevelForReward(PlayerInstance player)
	{
		if (!MENTEE_COINS.containsKey(player.getLevel()))
		{
			return;
		}
		
		final Mentee mentor = MentorManager.getInstance().getMentor(player.getObjectId());
		if (mentor == null)
		{
			return;
		}
		
		final int amount = MENTEE_COINS.get(player.getLevel());
		if (amount > 0)
		{
			sendMail(mentor.getObjectId(), LEVEL_UP_TITLE, String.format(LEVEL_UP_BODY, player.getName(), player.getLevel()), MENTEE_MARK, amount);
		}
	}
	
	private void sendMail(int objectId, String title, String body, int itemId, long amount)
	{
		final Message msg = new Message(MENTOR_GUIDE, objectId, title, body, MailType.MENTOR_NPC);
		final Mail attachments = msg.createAttachments();
		attachments.addItem(getName(), itemId, amount, null, null);
		MailManager.getInstance().sendMessage(msg);
	}
	
	public static void main(String[] args)
	{
		new MentorGuide();
	}
}