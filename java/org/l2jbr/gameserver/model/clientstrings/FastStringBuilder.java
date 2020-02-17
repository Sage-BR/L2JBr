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
package org.l2jbr.gameserver.model.clientstrings;

/**
 * @author Forsaiken
 */
final class FastStringBuilder
{
	private final char[] _array;
	private int _len;
	
	public FastStringBuilder(int capacity)
	{
		_array = new char[capacity];
	}
	
	public void append(String text)
	{
		text.getChars(0, text.length(), _array, _len);
		_len += text.length();
	}
	
	@Override
	public String toString()
	{
		return new String(_array);
	}
}