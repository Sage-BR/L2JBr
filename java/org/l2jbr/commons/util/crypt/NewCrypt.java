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
package org.l2jbr.commons.util.crypt;

/**
 * Class to use a blowfish cipher with ECB processing.<br>
 * Static methods are present to append/check the checksum of<br>
 * packets exchanged between the following partners:<br>
 * Login Server <-> Game Client<br>
 * Login Server <-> Game Server<br>
 * Also a static method is provided for the initial xor encryption between Login Server <-> Game Client.
 */
public class NewCrypt
{
	private final BlowfishEngine _cipher;
	
	/**
	 * @param blowfishKey
	 */
	public NewCrypt(byte[] blowfishKey)
	{
		_cipher = new BlowfishEngine();
		_cipher.init(blowfishKey);
	}
	
	public NewCrypt(String key)
	{
		this(key.getBytes());
	}
	
	/**
	 * Equivalent to calling {@link #verifyChecksum(byte[], int, int)} with parameters (raw, 0, raw.length)
	 * @param raw data array to be verified
	 * @return true when the checksum of the data is valid, false otherwise
	 */
	public static boolean verifyChecksum(byte[] raw)
	{
		return verifyChecksum(raw, 0, raw.length);
	}
	
	/**
	 * Method to verify the checksum of a packet received by login server from game client.<br>
	 * This is also used for game server <-> login server communication.
	 * @param raw data array to be verified
	 * @param offset at which offset to start verifying
	 * @param size number of bytes to verify
	 * @return true if the checksum of the data is valid, false otherwise
	 */
	public static boolean verifyChecksum(byte[] raw, int offset, int size)
	{
		// check if size is multiple of 4 and if there is more then only the checksum
		if (((size & 3) != 0) || (size <= 4))
		{
			return false;
		}
		
		long chksum = 0;
		final int count = size - 4;
		long check = -1;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			check = raw[i] & 0xff;
			check |= (raw[i + 1] << 8) & 0xff00;
			check |= (raw[i + 2] << 0x10) & 0xff0000;
			check |= (raw[i + 3] << 0x18) & 0xff000000;
			
			chksum ^= check;
		}
		
		check = raw[i] & 0xff;
		check |= (raw[i + 1] << 8) & 0xff00;
		check |= (raw[i + 2] << 0x10) & 0xff0000;
		check |= (raw[i + 3] << 0x18) & 0xff000000;
		
		return check == chksum;
	}
	
	/**
	 * Equivalent to calling {@link #appendChecksum(byte[], int, int)} with parameters (raw, 0, raw.length)
	 * @param raw data array to compute the checksum from
	 */
	public static void appendChecksum(byte[] raw)
	{
		appendChecksum(raw, 0, raw.length);
	}
	
	/**
	 * Method to append packet checksum at the end of the packet.
	 * @param raw data array to compute the checksum from
	 * @param offset offset where to start in the data array
	 * @param size number of bytes to compute the checksum from
	 */
	public static void appendChecksum(byte[] raw, int offset, int size)
	{
		long chksum = 0;
		final int count = size - 4;
		long ecx;
		int i;
		
		for (i = offset; i < count; i += 4)
		{
			ecx = raw[i] & 0xff;
			ecx |= (raw[i + 1] << 8) & 0xff00;
			ecx |= (raw[i + 2] << 0x10) & 0xff0000;
			ecx |= (raw[i + 3] << 0x18) & 0xff000000;
			
			chksum ^= ecx;
		}
		
		ecx = raw[i] & 0xff;
		ecx |= (raw[i + 1] << 8) & 0xff00;
		ecx |= (raw[i + 2] << 0x10) & 0xff0000;
		ecx |= (raw[i + 3] << 0x18) & 0xff000000;
		
		raw[i] = (byte) (chksum & 0xff);
		raw[i + 1] = (byte) ((chksum >> 0x08) & 0xff);
		raw[i + 2] = (byte) ((chksum >> 0x10) & 0xff);
		raw[i + 3] = (byte) ((chksum >> 0x18) & 0xff);
	}
	
	/**
	 * Packet is first XOR encoded with <code>key</code> then, the last 4 bytes are overwritten with the the XOR "key".<br>
	 * Thus this assume that there is enough room for the key to fit without overwriting data.
	 * @param raw The raw bytes to be encrypted
	 * @param key The 4 bytes (int) XOR key
	 */
	public static void encXORPass(byte[] raw, int key)
	{
		encXORPass(raw, 0, raw.length, key);
	}
	
	/**
	 * Packet is first XOR encoded with <code>key</code> then, the last 4 bytes are overwritten with the the XOR "key".<br>
	 * Thus this assume that there is enough room for the key to fit without overwriting data.
	 * @param raw The raw bytes to be encrypted
	 * @param offset The beginning of the data to be encrypted
	 * @param size Length of the data to be encrypted
	 * @param key The 4 bytes (int) XOR key
	 */
	static void encXORPass(byte[] raw, int offset, int size, int key)
	{
		final int stop = size - 8;
		int pos = 4 + offset;
		int edx;
		int ecx = key; // Initial xor key
		
		while (pos < stop)
		{
			edx = raw[pos] & 0xFF;
			edx |= (raw[pos + 1] & 0xFF) << 8;
			edx |= (raw[pos + 2] & 0xFF) << 16;
			edx |= (raw[pos + 3] & 0xFF) << 24;
			
			ecx += edx;
			
			edx ^= ecx;
			
			raw[pos++] = (byte) (edx & 0xFF);
			raw[pos++] = (byte) ((edx >> 8) & 0xFF);
			raw[pos++] = (byte) ((edx >> 16) & 0xFF);
			raw[pos++] = (byte) ((edx >> 24) & 0xFF);
		}
		
		raw[pos++] = (byte) (ecx & 0xFF);
		raw[pos++] = (byte) ((ecx >> 8) & 0xFF);
		raw[pos++] = (byte) ((ecx >> 16) & 0xFF);
		raw[pos++] = (byte) ((ecx >> 24) & 0xFF);
	}
	
	/**
	 * Method to decrypt using Blowfish-Blockcipher in ECB mode.<br>
	 * The results will be directly placed inside {@code raw} array.<br>
	 * This method does not do any error checking, since the calling code<br>
	 * should ensure sizes.
	 * @param raw the data array to be decrypted
	 * @param offset the offset at which to start decrypting
	 * @param size the number of bytes to be decrypted
	 */
	public void decrypt(byte[] raw, int offset, int size)
	{
		for (int i = offset; i < (offset + size); i += 8)
		{
			_cipher.decryptBlock(raw, i);
		}
	}
	
	/**
	 * Method to encrypt using Blowfish-Blockcipher in ECB mode.<br>
	 * The results will be directly placed inside {@code raw} array.<br>
	 * This method does not do any error checking, since the calling code should ensure sizes.
	 * @param raw the data array to be decrypted
	 * @param offset the offset at which to start decrypting
	 * @param size the number of bytes to be decrypted
	 */
	public void crypt(byte[] raw, int offset, int size)
	{
		for (int i = offset; i < (offset + size); i += 8)
		{
			_cipher.encryptBlock(raw, i);
		}
	}
}
