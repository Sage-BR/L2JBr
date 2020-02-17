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
package org.l2jbr.tools.gsregistering;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.l2jbr.Config;
import org.l2jbr.commons.database.DatabaseFactory;
import org.l2jbr.commons.enums.ServerMode;
import org.l2jbr.commons.util.CommonUtil;
import org.l2jbr.loginserver.GameServerTable;

/**
 * The Class BaseGameServerRegister.
 * @author KenM
 */
public abstract class BaseGameServerRegister
{
	private boolean _loaded = false;
	
	/**
	 * The main method.
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		boolean interactive = true;
		boolean force = false;
		boolean fallback = false;
		BaseTask task = null;
		
		String arg;
		for (int i = 0; i < args.length; i++)
		{
			arg = args[i];
			
			// --force : Forces GameServer register operations to overwrite a server if necessary
			if (arg.equals("-f") || arg.equals("--force"))
			{
				force = true;
			}
			// --fallback : If an register operation fails due to ID already being in use it will then try to register first available ID
			else if (arg.equals("-b") || arg.equals("--fallback"))
			{
				fallback = true;
			}
			// --register <id> <hexid_dest_dir> : Register GameServer with ID <id> and output hexid on <hexid_dest_dir>
			// Fails if <id> already in use, unless -force is used (overwrites)
			else if (arg.equals("-r") || arg.equals("--register"))
			{
				interactive = false;
				final int id = Integer.parseInt(args[++i]);
				final String dir = args[++i];
				
				task = new RegisterTask(id, dir, force, fallback);
			}
			// --unregister <id> : Removes GameServer denoted by <id>
			else if (arg.equals("-u") || arg.equals("--unregister"))
			{
				interactive = false;
				final String gsId = args[++i];
				if (gsId.equalsIgnoreCase("all"))
				{
					task = new UnregisterAllTask();
				}
				else
				{
					try
					{
						final int id = Integer.parseInt(gsId);
						task = new UnregisterTask(id);
					}
					catch (NumberFormatException e)
					{
						System.out.printf("wrong argument for GameServer removal, specify a numeric ID or \"all\" without quotes to remove all." + Config.EOL, gsId);
						System.exit(1);
					}
				}
			}
			// --help : Prints usage/arguments/credits
			else if (arg.equals("-h") || arg.equals("--help"))
			{
				interactive = false;
				
				printHelp();
			}
		}
		
		try
		{
			if (interactive)
			{
				startCMD();
			}
			else
			{
				// if there is a task, do it, else the app has already finished
				if (task != null)
				{
					task.run();
				}
			}
		}
		catch (HeadlessException e)
		{
			startCMD();
		}
	}
	
	/**
	 * Prints the help.
	 */
	private static void printHelp()
	{
		final String[] help =
		{
			"Allows to register/remove GameServers from LoginServer.",
			"",
			"Options:",
			"-b, --fallback\t\t\t\tIf during the register operation the specified GameServer ID is in use, an attempt with the first available ID will be made.",
			"-c, --cmd\t\t\t\tForces this application to run in console mode, even if GUI is supported.",
			"-f, --force\t\t\t\tForces GameServer register operation to overwrite a previous registration on the specified ID, if necessary.",
			"-h, --help\t\t\t\tShows this help message and exits.",
			"-r, --register <id> <hexid_dest_dir>\tRegisters a GameServer on ID <id> and saves the hexid.txt file on <hexid_dest_dir>.",
			"\t\t\t\t\tYou can provide a negative value for <id> to register under the first available ID.",
			"\t\t\t\t\tNothing is done if <id> is already in use, unless --force or --fallback is used.",
			"",
			"-u, --unregister <id>|all\t\tRemoves the GameServer specified by <id>, use \"all\" to remove all currently registered GameServers.",
			"",
			"© 2008-2009 L2J Team. All rights reserved.",
			"Bug Reports: http://www.l2jserver.com"
		};
		
		for (String str : help)
		{
			System.out.println(str);
		}
	}
	
	/**
	 * Start the CMD.
	 */
	private static void startCMD()
	{
		final GameServerRegister cmdUi = new GameServerRegister();
		try
		{
			cmdUi.consoleUI();
		}
		catch (IOException e)
		{
			cmdUi.showError("I/O exception trying to get input from keyboard.", e);
		}
	}
	
	/**
	 * Load.
	 */
	public void load()
	{
		Config.load(ServerMode.LOGIN);
		DatabaseFactory.init();
		GameServerTable.getInstance();
		
		_loaded = true;
	}
	
	/**
	 * Checks if is loaded.
	 * @return true, if is loaded
	 */
	public boolean isLoaded()
	{
		return _loaded;
	}
	
	/**
	 * Show the error.
	 * @param msg the msg.
	 * @param t the t.
	 */
	public abstract void showError(String msg, Throwable t);
	
	/**
	 * Unregister the game server.
	 * @param id the game server id.
	 * @throws SQLException the SQL exception.
	 */
	public static void unregisterGameServer(int id) throws SQLException
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM gameservers WHERE server_id = ?"))
		
		{
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		GameServerTable.getInstance().getRegisteredGameServers().remove(id);
	}
	
	/**
	 * Unregister all game servers.
	 * @throws SQLException the SQL exception
	 */
	public static void unregisterAllGameServers() throws SQLException
	{
		try (Connection con = DatabaseFactory.getConnection();
			Statement s = con.createStatement())
		{
			s.executeUpdate("DELETE FROM gameservers");
		}
		GameServerTable.getInstance().getRegisteredGameServers().clear();
	}
	
	/**
	 * Register a game server.
	 * @param id the id of the game server.
	 * @param outDir the out dir.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void registerGameServer(int id, String outDir) throws IOException
	{
		final byte[] hexId = CommonUtil.generateHex(16);
		GameServerTable.getInstance().registerServerOnDB(hexId, id, "");
		
		final Properties hexSetting = new Properties();
		final File file = new File(outDir, "hexid.txt");
		// Create a new empty file only if it doesn't exist
		file.createNewFile();
		try (OutputStream out = new FileOutputStream(file))
		{
			hexSetting.setProperty("ServerID", String.valueOf(id));
			hexSetting.setProperty("HexID", new BigInteger(hexId).toString(16));
			hexSetting.store(out, "The HexId to Auth into LoginServer");
		}
	}
	
	/**
	 * Register first available.
	 * @param outDir the out dir
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static int registerFirstAvailable(String outDir) throws IOException
	{
		for (Entry<Integer, String> e : GameServerTable.getInstance().getServerNames().entrySet())
		{
			if (!GameServerTable.getInstance().hasRegisteredGameServerOnId(e.getKey()))
			{
				registerGameServer(e.getKey(), outDir);
				return e.getKey();
			}
		}
		return -1;
	}
	
	/**
	 * The Class BaseTask.
	 */
	protected static abstract class BaseTask implements Runnable
	{
		protected ResourceBundle _bundle;
		
		/**
		 * Sets the bundle.
		 * @param bundle The bundle to set.
		 */
		public void setBundle(ResourceBundle bundle)
		{
			_bundle = bundle;
		}
		
		/**
		 * Gets the bundle.
		 * @return Returns the bundle.
		 */
		public ResourceBundle getBundle()
		{
			return _bundle;
		}
		
		/**
		 * Show the error.
		 * @param msg the msg
		 * @param t the t
		 */
		public void showError(String msg, Throwable t)
		{
			String title;
			if (_bundle != null)
			{
				title = _bundle.getString("error");
				msg += Config.EOL + _bundle.getString("reason") + ' ' + t.getLocalizedMessage();
			}
			else
			{
				title = "Error";
				msg += Config.EOL + "Cause: " + t.getLocalizedMessage();
			}
			System.out.println(title + ": " + msg);
		}
	}
	
	/**
	 * The Class RegisterTask.
	 */
	private static class RegisterTask extends BaseTask
	{
		private final int _id;
		private final String _outDir;
		private boolean _force;
		private boolean _fallback;
		
		/**
		 * Instantiates a new register task.
		 * @param id the id.
		 * @param outDir the out dir.
		 * @param force the force.
		 * @param fallback the fallback.
		 */
		public RegisterTask(int id, String outDir, boolean force, boolean fallback)
		{
			_id = id;
			_outDir = outDir;
			_force = force;
			_fallback = fallback;
		}
		
		/**
		 * Sets the actions.
		 * @param force the force.
		 * @param fallback the fallback.
		 */
		@SuppressWarnings("unused")
		public void setActions(boolean force, boolean fallback)
		{
			_force = force;
			_fallback = fallback;
		}
		
		@Override
		public void run()
		{
			try
			{
				if (_id < 0)
				{
					final int registeredId = registerFirstAvailable(_outDir);
					
					if (registeredId < 0)
					{
						System.out.println(_bundle.getString("noFreeId"));
					}
					else
					{
						System.out.printf(_bundle.getString("registrationOk") + Config.EOL, registeredId);
					}
				}
				else
				{
					System.out.printf(_bundle.getString("checkingIdInUse") + Config.EOL, _id);
					if (GameServerTable.getInstance().hasRegisteredGameServerOnId(_id))
					{
						System.out.println(_bundle.getString("yes"));
						if (_force)
						{
							System.out.printf(_bundle.getString("forcingRegistration") + Config.EOL, _id);
							unregisterGameServer(_id);
							registerGameServer(_id, _outDir);
							System.out.printf(_bundle.getString("registrationOk") + Config.EOL, _id);
						}
						else if (_fallback)
						{
							System.out.println(_bundle.getString("fallingBack"));
							final int registeredId = registerFirstAvailable(_outDir);
							
							if (registeredId < 0)
							{
								System.out.println(_bundle.getString("noFreeId"));
							}
							else
							{
								System.out.printf(_bundle.getString("registrationOk") + Config.EOL, registeredId);
							}
						}
						else
						{
							System.out.println(_bundle.getString("noAction"));
						}
					}
					else
					{
						System.out.println(_bundle.getString("no"));
						registerGameServer(_id, _outDir);
					}
				}
			}
			catch (SQLException e)
			{
				showError(_bundle.getString("sqlErrorRegister"), e);
			}
			catch (IOException e)
			{
				showError(_bundle.getString("ioErrorRegister"), e);
			}
		}
	}
	
	/**
	 * The Class UnregisterTask.
	 */
	private static class UnregisterTask extends BaseTask
	{
		private final int _id;
		
		/**
		 * Instantiates a new unregister task.
		 * @param id the task id.
		 */
		public UnregisterTask(int id)
		{
			_id = id;
			
		}
		
		@Override
		public void run()
		{
			System.out.printf(_bundle.getString("removingGsId") + Config.EOL, _id);
			try
			{
				unregisterGameServer(_id);
			}
			catch (SQLException e)
			{
				showError(_bundle.getString("sqlErrorRegister"), e);
			}
		}
	}
	
	/**
	 * The Class UnregisterAllTask.
	 */
	protected static class UnregisterAllTask extends BaseTask
	{
		@Override
		public void run()
		{
			try
			{
				unregisterAllGameServers();
			}
			catch (SQLException e)
			{
				showError(_bundle.getString("sqlErrorUnregisterAll"), e);
			}
		}
	}
}
