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
package org.l2jbr.loginserver.ui;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * @author Mobius
 */
public class frmAbout
{
	private JFrame frmAbout;
	private static final String URL = "www.fb.me/l2jsages";
	final URI uri;
	
	public frmAbout()
	{
		initialize();
		uri = createURI(URL);
		frmAbout.setVisible(true);
	}
	
	private void initialize()
	{
		frmAbout = new JFrame();
		frmAbout.setResizable(false);
		frmAbout.setTitle("About");
		frmAbout.setBounds(100, 100, 297, 197);
		frmAbout.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frmAbout.setType(Type.UTILITY);
		frmAbout.getContentPane().setLayout(null);
		
		JLabel lblLjmobius = new JLabel("L2JBr");
		lblLjmobius.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblLjmobius.setHorizontalAlignment(SwingConstants.CENTER);
		lblLjmobius.setBounds(10, 11, 271, 39);
		frmAbout.getContentPane().add(lblLjmobius);
		
		JLabel lblData = new JLabel("2019-" + Calendar.getInstance().get(Calendar.YEAR));
		lblData.setHorizontalAlignment(SwingConstants.CENTER);
		lblData.setBounds(10, 44, 271, 14);
		frmAbout.getContentPane().add(lblData);
		
		JLabel lblLoginServer = new JLabel("Login Server");
		lblLoginServer.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblLoginServer.setBounds(10, 86, 271, 23);
		frmAbout.getContentPane().add(lblLoginServer);
		
		JLabel site = new JLabel(URL);
		site.setText("<HTML><FONT color=\"#000099\"><U>" + URL + "</U></FONT></HTML>");
		site.setHorizontalAlignment(SwingConstants.CENTER);
		site.setBounds(76, 148, 140, 14);
		site.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(uri);
					}
					catch (IOException e)
					{
					}
				}
			}
		});
		frmAbout.getContentPane().add(site);
		
		// Center frame to screen.
		frmAbout.setLocationRelativeTo(null);
	}
	
	private static URI createURI(String str)
	{
		try
		{
			return new URI(str);
		}
		catch (URISyntaxException x)
		{
			throw new IllegalArgumentException(x.getMessage(), x);
		}
	}
}
