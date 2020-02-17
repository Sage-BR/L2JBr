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
final class BuilderContainer extends Builder
{
	private final Builder[] _builders;
	
	BuilderContainer(Builder[] builders)
	{
		_builders = builders;
	}
	
	@Override
	public String toString(Object param)
	{
		return toString(new Object[]
		{
			param
		});
	}
	
	@Override
	public String toString(Object... params)
	{
		final int buildersLength = _builders.length;
		final int paramsLength = params.length;
		final String[] builds = new String[buildersLength];
		
		Builder builder;
		String build;
		int i;
		int paramIndex;
		int buildTextLen = 0;
		if (paramsLength != 0)
		{
			for (i = buildersLength; i-- > 0;)
			{
				builder = _builders[i];
				paramIndex = builder.getIndex();
				build = (paramIndex != -1) && (paramIndex < paramsLength) ? builder.toString(params[paramIndex]) : builder.toString();
				buildTextLen += build.length();
				builds[i] = build;
			}
		}
		else
		{
			for (i = buildersLength; i-- > 0;)
			{
				build = _builders[i].toString();
				buildTextLen += build.length();
				builds[i] = build;
			}
		}
		
		final FastStringBuilder fsb = new FastStringBuilder(buildTextLen);
		for (i = 0; i < buildersLength; i++)
		{
			fsb.append(builds[i]);
		}
		return fsb.toString();
	}
	
	@Override
	public int getIndex()
	{
		return -1;
	}
}