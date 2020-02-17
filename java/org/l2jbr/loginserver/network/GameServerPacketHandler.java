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
package org.l2jbr.loginserver.network;

import java.util.logging.Logger;

import org.l2jbr.commons.network.BaseRecievePacket;
import org.l2jbr.loginserver.GameServerThread;
import org.l2jbr.loginserver.network.gameserverpackets.BlowFishKey;
import org.l2jbr.loginserver.network.gameserverpackets.ChangeAccessLevel;
import org.l2jbr.loginserver.network.gameserverpackets.ChangePassword;
import org.l2jbr.loginserver.network.gameserverpackets.GameServerAuth;
import org.l2jbr.loginserver.network.gameserverpackets.PlayerAuthRequest;
import org.l2jbr.loginserver.network.gameserverpackets.PlayerInGame;
import org.l2jbr.loginserver.network.gameserverpackets.PlayerLogout;
import org.l2jbr.loginserver.network.gameserverpackets.PlayerTracert;
import org.l2jbr.loginserver.network.gameserverpackets.ReplyCharacters;
import org.l2jbr.loginserver.network.gameserverpackets.RequestTempBan;
import org.l2jbr.loginserver.network.gameserverpackets.ServerStatus;
import org.l2jbr.loginserver.network.loginserverpackets.LoginServerFail;

/**
 * @author mrTJO
 */
public class GameServerPacketHandler
{
	protected static Logger LOGGER = Logger.getLogger(GameServerPacketHandler.class.getName());
	
	public enum GameServerState
	{
		CONNECTED,
		BF_CONNECTED,
		AUTHED
	}
	
	public static BaseRecievePacket handlePacket(byte[] data, GameServerThread server)
	{
		BaseRecievePacket msg = null;
		final int opcode = data[0] & 0xff;
		final GameServerState state = server.getLoginConnectionState();
		switch (state)
		{
			case CONNECTED:
			{
				switch (opcode)
				{
					case 0x00:
					{
						msg = new BlowFishKey(data, server);
						break;
					}
					default:
					{
						LOGGER.warning("Unknown Opcode (" + Integer.toHexString(opcode).toUpperCase() + ") in state " + state.name() + " from GameServer, closing connection.");
						server.forceClose(LoginServerFail.NOT_AUTHED);
						break;
					}
				}
				break;
			}
			case BF_CONNECTED:
			{
				switch (opcode)
				{
					case 0x01:
					{
						msg = new GameServerAuth(data, server);
						break;
					}
					default:
					{
						LOGGER.warning("Unknown Opcode (" + Integer.toHexString(opcode).toUpperCase() + ") in state " + state.name() + " from GameServer, closing connection.");
						server.forceClose(LoginServerFail.NOT_AUTHED);
						break;
					}
				}
				break;
			}
			case AUTHED:
			{
				switch (opcode)
				{
					case 0x02:
					{
						msg = new PlayerInGame(data, server);
						break;
					}
					case 0x03:
					{
						msg = new PlayerLogout(data, server);
						break;
					}
					case 0x04:
					{
						msg = new ChangeAccessLevel(data, server);
						break;
					}
					case 0x05:
					{
						msg = new PlayerAuthRequest(data, server);
						break;
					}
					case 0x06:
					{
						msg = new ServerStatus(data, server);
						break;
					}
					case 0x07:
					{
						msg = new PlayerTracert(data);
						break;
					}
					case 0x08:
					{
						msg = new ReplyCharacters(data, server);
						break;
					}
					case 0x09:
					{
						// msg = new RequestSendMail(data);
						break;
					}
					case 0x0A:
					{
						msg = new RequestTempBan(data);
						break;
					}
					case 0x0B:
					{
						new ChangePassword(data);
						break;
					}
					default:
					{
						LOGGER.warning("Unknown Opcode (" + Integer.toHexString(opcode).toUpperCase() + ") in state " + state.name() + " from GameServer, closing connection.");
						server.forceClose(LoginServerFail.NOT_AUTHED);
						break;
					}
				}
				break;
			}
		}
		return msg;
	}
}
