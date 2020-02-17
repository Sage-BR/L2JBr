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
package org.l2jbr.loginserver;

/**
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:30:09 $
 */
public class HackingException extends Exception
{
	String _ip;
	private final int _connects;
	
	public HackingException(String ip, int connects)
	{
		_ip = ip;
		_connects = connects;
	}
	
	/**
	 * @return
	 */
	public String getIP()
	{
		return _ip;
	}
	
	public int getConnects()
	{
		return _connects;
	}
}
