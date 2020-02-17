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

/**
 * @author Mobius
 */
public class TeleporterQuestRecommendationHolder
{
	private final int _npcId;
	private final String _questName;
	private final int[] _conditions; // -1 = all conditions
	private final String _html;
	
	public TeleporterQuestRecommendationHolder(int npcId, String questName, int[] conditions, String html)
	{
		_npcId = npcId;
		_questName = questName;
		_conditions = conditions;
		_html = html;
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
	
	public String getQuestName()
	{
		return _questName;
	}
	
	public int[] getConditions()
	{
		return _conditions;
	}
	
	public String getHtml()
	{
		return _html;
	}
}