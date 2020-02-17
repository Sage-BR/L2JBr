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
package org.l2jbr.gameserver.scripting.java;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * @author Mobius
 */
public class JavaScriptingEngine
{
	private static final Logger LOGGER = Logger.getLogger(JavaScriptingEngine.class.getName());
	
	private final static Map<String, String> _properties = new HashMap<>();
	private final static JavaCompiler _compiler = ToolProvider.getSystemJavaCompiler();
	
	public JavaScriptingEngine()
	{
		// Load config.
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream("config/ScriptEngine.ini"))
		{
			props.load(fis);
		}
		catch (Exception e)
		{
			LOGGER.warning("Could not load ScriptEngine.ini: " + e.getMessage());
		}
		
		// Set properties.
		for (Entry<Object, Object> prop : props.entrySet())
		{
			_properties.put((String) prop.getKey(), (String) prop.getValue());
		}
	}
	
	public JavaExecutionContext createExecutionContext()
	{
		return new JavaExecutionContext(this);
	}
	
	public String getProperty(String key)
	{
		return _properties.get(key);
	}
	
	public JavaCompiler getCompiler()
	{
		return _compiler;
	}
}