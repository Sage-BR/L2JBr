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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Path;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

/**
 * @author HorridoJoho
 */
final class ScriptingOutputFileObject implements JavaFileObject
{
	private final Path _sourcePath;
	private final String _javaName;
	private final String _javaSimpleName;
	private final ByteArrayOutputStream _out;
	
	public ScriptingOutputFileObject(Path sourcePath, String javaName, String javaSimpleName)
	{
		_sourcePath = sourcePath;
		_javaName = javaName;
		_javaSimpleName = javaSimpleName;
		_out = new ByteArrayOutputStream();
	}
	
	public Path getSourcePath()
	{
		return _sourcePath;
	}
	
	public String getJavaName()
	{
		return _javaName;
	}
	
	public String getJavaSimpleName()
	{
		return _javaSimpleName;
	}
	
	public byte[] getJavaData()
	{
		return _out.toByteArray();
	}
	
	@Override
	public URI toUri()
	{
		return null;
	}
	
	@Override
	public String getName()
	{
		return null;
	}
	
	@Override
	public InputStream openInputStream()
	{
		return null;
	}
	
	@Override
	public OutputStream openOutputStream()
	{
		return _out;
	}
	
	@Override
	public Reader openReader(boolean ignoreEncodingErrors)
	{
		return null;
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
	{
		return null;
	}
	
	@Override
	public Writer openWriter()
	{
		return null;
	}
	
	@Override
	public long getLastModified()
	{
		return 0;
	}
	
	@Override
	public boolean delete()
	{
		return false;
	}
	
	@Override
	public Kind getKind()
	{
		return Kind.CLASS;
	}
	
	@Override
	public boolean isNameCompatible(String simpleName, Kind kind)
	{
		return (kind == Kind.CLASS) && (_javaSimpleName == simpleName);
	}
	
	@Override
	public NestingKind getNestingKind()
	{
		return NestingKind.TOP_LEVEL;
	}
	
	@Override
	public Modifier getAccessLevel()
	{
		return null;
	}
}
