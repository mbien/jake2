/*
 * Com.java
 * Copyright (C) 2003
 * 
 * $Id: Com.java,v 1.2.2.2 2004-09-06 19:39:19 hzi Exp $
 */
/*
Copyright (C) 1997-2001 Id Software, Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package jake2.qcommon;

import jake2.Defines;
import jake2.Globals;
import jake2.client.CL;
import jake2.client.Console;
import jake2.game.Cmd;
import jake2.server.SV_MAIN;
import jake2.sys.Sys;
import jake2.util.*;

import java.io.*;

/**
 * Com
 *
 */
public final class Com
{

	static int com_argc;
	static String[] com_argv= new String[Defines.MAX_NUM_ARGVS];

	public abstract static class RD_Flusher
	{
		public abstract void rd_flush(int target, byte[] buffer);
	}

	static int rd_target;
	static byte[] rd_buffer;
	static int rd_buffersize;
	static RD_Flusher rd_flusher;

	public static void BeginRedirect(int target, byte[] buffer, int buffersize, RD_Flusher flush)
	{
		if (0 == target || null == buffer || 0 == buffersize || null == flush)
			return;

		rd_target= target;
		rd_buffer= buffer;
		rd_buffersize= buffersize;
		rd_flusher= flush;

		rd_buffer= null;
	}

	public static void EndRedirect()
	{
		rd_flusher.rd_flush(rd_target, rd_buffer);

		rd_target= 0;
		rd_buffer= null;
		rd_buffersize= 0;
		rd_flusher= null;
	}

	static boolean recursive= false;

	static String msg= "";

	// helper class to replace the pointer-pointer
	public static class ParseHelp
	{
		public ParseHelp(String in)
		{
			if (in == null)
			{
				data= null;
			}
			else
			{
				data= in.toCharArray();
			}
			index= 0;
		}

		public ParseHelp(char in[])
		{
			this(in, 0);
		}

		public ParseHelp(char in[], int offset)
		{
			data= in;
			index= offset;
		}

		public char getchar()
		{
			// faster than if
			try
			{
				return data[index];
			}
			catch (Exception e)
			{
				data= null;
				// last char
				return 0;
			}
		}

		public char nextchar()
		{
			// faster than if
			try
			{
				index++;
				return data[index];
			}
			catch (Exception e)
			{
				data= null;
				// avoid int wraps;
				index--;
				// last char
				return 0;
			}
		}
		
		public char prevchar() {
			if (index > 0) 
			{
				index--;
				return data[index];
			}
			return 0;					
		}

		public boolean isEof()
		{
			return data == null;
		}

		public int index;
		public char data[];

		public char skipwhites()
		{
			char c;
			while (((c= getchar()) <= ' ') && c != 0)
				index++;
			return c;
		}

		public char skipwhitestoeol()
		{
			char c;
			while (((c= getchar()) <= ' ') && c != '\n' && c != 0)
				index++;
			return c;
		}

		public char skiptoeol()
		{
			char c;
			while ((c= getchar()) != '\n' && c != 0)
				index++;
			return c;
		}
	}

	public static char com_token[]= new char[Defines.MAX_TOKEN_CHARS];

	// See GameSpanw.ED_ParseEdict() to see how to use it now.
	public static String Parse(ParseHelp hlp) {
		int c;
		int len = 0;

		if (hlp.data == null) {
			return "";
		}

		while (true) {
			//	   skip whitespace
			hlp.skipwhites();
			if (hlp.isEof())
				return "";

			//	   skip // comments
			if (hlp.getchar() == '/') {
				if (hlp.nextchar() == '/') {
					hlp.skiptoeol();
					return "";
				} else {
					hlp.prevchar();
					break;
				}
			} else
				break;
		}

		//	   handle quoted strings specially
		if (hlp.getchar() == '\"') {
			hlp.nextchar();
			while (true) {
				c = hlp.getchar();
				hlp.nextchar();
				if (c == '\"' || c == 0) {
					return new String(com_token, 0, len);
				}
				if (len < Defines.MAX_TOKEN_CHARS) {
					com_token[len] = (char) c;
					len++;
				}
			}
		}

		//	   parse a regular word
		c = hlp.getchar();
		do {
			if (len < Defines.MAX_TOKEN_CHARS) {
				com_token[len] = (char) c;
				len++;
			}
			c = hlp.nextchar();
		} while (c > 32);

		if (len == Defines.MAX_TOKEN_CHARS) {
			Com.Printf("Token exceeded " + Defines.MAX_TOKEN_CHARS + " chars, discarded.\n");
			len = 0;
		}

		return new String(com_token, 0, len);
	}

	public static xcommand_t Error_f= new xcommand_t()
	{
		public void execute() throws longjmpException
		{
			Error(Globals.ERR_FATAL, Cmd.Argv(1));
		}
	};

	public static void Error(int code, String fmt) throws longjmpException
	{
		Error(code, fmt, null);
	}

	public static void Error(int code, String fmt, Vargs vargs) throws longjmpException
	{
		// va_list argptr;
		// static char msg[MAXPRINTMSG];

		if (recursive)
		{
			Sys.Error("recursive error after: " + msg);
		}
		recursive= true;

		msg= sprintf(fmt, vargs);

		if (code == Defines.ERR_DISCONNECT)
		{
			CL.Drop();
			recursive= false;
			throw new longjmpException();
		}
		else if (code == Defines.ERR_DROP)
		{
			Com.Printf("********************\nERROR: " + msg + "\n********************\n");
			SV_MAIN.SV_Shutdown("Server crashed: " + msg + "\n", false);
			CL.Drop();
			recursive= false;
			throw new longjmpException();
		}
		else
		{
			SV_MAIN.SV_Shutdown("Server fatal crashed: %s" + msg + "\n", false);
			CL.Shutdown();
		}

		Sys.Error(msg);
	}

	/**
	 * Com_InitArgv checks the number of command line arguments
	 * and copies all arguments with valid length into com_argv.
	 */
	static void InitArgv(String[] args) throws longjmpException
	{

		if (args.length > Globals.MAX_NUM_ARGVS)
		{
			Com.Error(Globals.ERR_FATAL, "argc > MAX_NUM_ARGVS");
		}

		Com.com_argc= args.length;
		for (int i= 0; i < Com.com_argc; i++)
		{
			if (args[i].length() >= Globals.MAX_TOKEN_CHARS)
				Com.com_argv[i]= "";
			else
				Com.com_argv[i]= args[i];
		}
	}

	public static void DPrintf(String fmt)
	{
		DPrintf(fmt, null);
	}

	public static void Printf(String fmt)
	{
		Printf(fmt, null);
	}

	public static void DPrintf(String fmt, Vargs vargs)
	{
		if (Globals.developer == null || Globals.developer.value == 0)
			return; // don't confuse non-developers with techie stuff...

		Printf(fmt, vargs);
	}

	public static void Printf(String fmt, Vargs vargs)
	{
		// TODO Com.Printf ist nur zum testen
		String msg= sprintf(fmt, vargs);

		if (rd_target != 0)
		{
			if ((msg.length() + Lib.strlen(rd_buffer)) > (rd_buffersize - 1))
			{
				rd_flusher.rd_flush(rd_target, rd_buffer);
				// *rd_buffer = 0;
				rd_buffer[rd_buffersize]= '\0';
			}
			// TODO handle rd_buffer
			// strcat(rd_buffer, msg);
			return;
		}

		Console.Print(msg);

		// also echo to debugging console
		Sys.ConsoleOutput(msg);

		// logfile
		if (Globals.logfile_active != null && Globals.logfile_active.value != 0)
		{
			String name;

			if (Globals.logfile == null)
			{
				name= FS.Gamedir() + "/qconsole.log";
				if (Globals.logfile_active.value > 2)
					try
					{
						Globals.logfile= new RandomAccessFile(name, "a");
					}
					catch (FileNotFoundException e)
					{
						// TODO: do quake2 error handling!
						e.printStackTrace();
					}
				else
					try
					{
						Globals.logfile= new RandomAccessFile(name, "rw");
					}
					catch (FileNotFoundException e1)
					{
						// TODO: do quake2 error handling!
						e1.printStackTrace();
					}
			}
			if (Globals.logfile != null)
				try
				{
					Globals.logfile.writeChars(msg);
				}
				catch (IOException e)
				{
					// TODO: do quake2 error handling!
					e.printStackTrace();
				}
			if (Globals.logfile_active.value > 1); // do nothing
			// fflush (logfile);		// force it to save every time
		}
	}

	public static void Println(String fmt)
	{
		Printf(fmt);
		Printf("\n");
	}

	public static void p(String fmt)
	{
		Printf(fmt);
		Printf("\n");
	}

	public static String sprintf(String fmt, Vargs vargs)
	{
		String msg= "";
		if (vargs == null || vargs.size() == 0)
		{
			msg= fmt;
		}
		else
		{
			msg= new PrintfFormat(fmt).sprintf(vargs.toArray());
		}
		return msg;
	}

	public static int Argc()
	{
		return Com.com_argc;
	}

	public static String Argv(int arg)
	{
		if (arg < 0 || arg >= Com.com_argc || Com.com_argv[arg].length() < 1)
			return "";
		return Com.com_argv[arg];
	}

	public static void ClearArgv(int arg)
	{
		if (arg < 0 || arg >= Com.com_argc || Com.com_argv[arg].length() < 1)
			return;
		Com.com_argv[arg]= "";
	}

	public static void Quit()
	{
		SV_MAIN.SV_Shutdown("Server quit\n", false);
		CL.Shutdown();

		if (Globals.logfile != null)
		{
			try
			{
				Globals.logfile.close();
			}
			catch (IOException e)
			{
			}
			Globals.logfile= null;
		}

		Sys.Quit();
	}

	public static void SetServerState(int i)
	{
		Globals.server_state= i;
	}

	public static int BlockChecksum(byte[] buf, int length)
	{
		return MD4.Com_BlockChecksum(buf, length);
	}

	public static String StripExtension(String string) {
		int i = string.lastIndexOf('.');
		if (i < 0)
			return string;
		return string.substring(0, i);
	}

	/**
	 * CRC table. 
	 */
	static int chktbl[]=
		{
			0x84,
			0x47,
			0x51,
			0xc1,
			0x93,
			0x22,
			0x21,
			0x24,
			0x2f,
			0x66,
			0x60,
			0x4d,
			0xb0,
			0x7c,
			0xda,
			0x88,
			0x54,
			0x15,
			0x2b,
			0xc6,
			0x6c,
			0x89,
			0xc5,
			0x9d,
			0x48,
			0xee,
			0xe6,
			0x8a,
			0xb5,
			0xf4,
			0xcb,
			0xfb,
			0xf1,
			0x0c,
			0x2e,
			0xa0,
			0xd7,
			0xc9,
			0x1f,
			0xd6,
			0x06,
			0x9a,
			0x09,
			0x41,
			0x54,
			0x67,
			0x46,
			0xc7,
			0x74,
			0xe3,
			0xc8,
			0xb6,
			0x5d,
			0xa6,
			0x36,
			0xc4,
			0xab,
			0x2c,
			0x7e,
			0x85,
			0xa8,
			0xa4,
			0xa6,
			0x4d,
			0x96,
			0x19,
			0x19,
			0x9a,
			0xcc,
			0xd8,
			0xac,
			0x39,
			0x5e,
			0x3c,
			0xf2,
			0xf5,
			0x5a,
			0x72,
			0xe5,
			0xa9,
			0xd1,
			0xb3,
			0x23,
			0x82,
			0x6f,
			0x29,
			0xcb,
			0xd1,
			0xcc,
			0x71,
			0xfb,
			0xea,
			0x92,
			0xeb,
			0x1c,
			0xca,
			0x4c,
			0x70,
			0xfe,
			0x4d,
			0xc9,
			0x67,
			0x43,
			0x47,
			0x94,
			0xb9,
			0x47,
			0xbc,
			0x3f,
			0x01,
			0xab,
			0x7b,
			0xa6,
			0xe2,
			0x76,
			0xef,
			0x5a,
			0x7a,
			0x29,
			0x0b,
			0x51,
			0x54,
			0x67,
			0xd8,
			0x1c,
			0x14,
			0x3e,
			0x29,
			0xec,
			0xe9,
			0x2d,
			0x48,
			0x67,
			0xff,
			0xed,
			0x54,
			0x4f,
			0x48,
			0xc0,
			0xaa,
			0x61,
			0xf7,
			0x78,
			0x12,
			0x03,
			0x7a,
			0x9e,
			0x8b,
			0xcf,
			0x83,
			0x7b,
			0xae,
			0xca,
			0x7b,
			0xd9,
			0xe9,
			0x53,
			0x2a,
			0xeb,
			0xd2,
			0xd8,
			0xcd,
			0xa3,
			0x10,
			0x25,
			0x78,
			0x5a,
			0xb5,
			0x23,
			0x06,
			0x93,
			0xb7,
			0x84,
			0xd2,
			0xbd,
			0x96,
			0x75,
			0xa5,
			0x5e,
			0xcf,
			0x4e,
			0xe9,
			0x50,
			0xa1,
			0xe6,
			0x9d,
			0xb1,
			0xe3,
			0x85,
			0x66,
			0x28,
			0x4e,
			0x43,
			0xdc,
			0x6e,
			0xbb,
			0x33,
			0x9e,
			0xf3,
			0x0d,
			0x00,
			0xc1,
			0xcf,
			0x67,
			0x34,
			0x06,
			0x7c,
			0x71,
			0xe3,
			0x63,
			0xb7,
			0xb7,
			0xdf,
			0x92,
			0xc4,
			0xc2,
			0x25,
			0x5c,
			0xff,
			0xc3,
			0x6e,
			0xfc,
			0xaa,
			0x1e,
			0x2a,
			0x48,
			0x11,
			0x1c,
			0x36,
			0x68,
			0x78,
			0x86,
			0x79,
			0x30,
			0xc3,
			0xd6,
			0xde,
			0xbc,
			0x3a,
			0x2a,
			0x6d,
			0x1e,
			0x46,
			0xdd,
			0xe0,
			0x80,
			0x1e,
			0x44,
			0x3b,
			0x6f,
			0xaf,
			0x31,
			0xda,
			0xa2,
			0xbd,
			0x77,
			0x06,
			0x56,
			0xc0,
			0xb7,
			0x92,
			0x4b,
			0x37,
			0xc0,
			0xfc,
			0xc2,
			0xd5,
			0xfb,
			0xa8,
			0xda,
			0xf5,
			0x57,
			0xa8,
			0x18,
			0xc0,
			0xdf,
			0xe7,
			0xaa,
			0x2a,
			0xe0,
			0x7c,
			0x6f,
			0x77,
			0xb1,
			0x26,
			0xba,
			0xf9,
			0x2e,
			0x1d,
			0x16,
			0xcb,
			0xb8,
			0xa2,
			0x44,
			0xd5,
			0x2f,
			0x1a,
			0x79,
			0x74,
			0x87,
			0x4b,
			0x00,
			0xc9,
			0x4a,
			0x3a,
			0x65,
			0x8f,
			0xe6,
			0x5d,
			0xe5,
			0x0a,
			0x77,
			0xd8,
			0x1a,
			0x14,
			0x41,
			0x75,
			0xb1,
			0xe2,
			0x50,
			0x2c,
			0x93,
			0x38,
			0x2b,
			0x6d,
			0xf3,
			0xf6,
			0xdb,
			0x1f,
			0xcd,
			0xff,
			0x14,
			0x70,
			0xe7,
			0x16,
			0xe8,
			0x3d,
			0xf0,
			0xe3,
			0xbc,
			0x5e,
			0xb6,
			0x3f,
			0xcc,
			0x81,
			0x24,
			0x67,
			0xf3,
			0x97,
			0x3b,
			0xfe,
			0x3a,
			0x96,
			0x85,
			0xdf,
			0xe4,
			0x6e,
			0x3c,
			0x85,
			0x05,
			0x0e,
			0xa3,
			0x2b,
			0x07,
			0xc8,
			0xbf,
			0xe5,
			0x13,
			0x82,
			0x62,
			0x08,
			0x61,
			0x69,
			0x4b,
			0x47,
			0x62,
			0x73,
			0x44,
			0x64,
			0x8e,
			0xe2,
			0x91,
			0xa6,
			0x9a,
			0xb7,
			0xe9,
			0x04,
			0xb6,
			0x54,
			0x0c,
			0xc5,
			0xa9,
			0x47,
			0xa6,
			0xc9,
			0x08,
			0xfe,
			0x4e,
			0xa6,
			0xcc,
			0x8a,
			0x5b,
			0x90,
			0x6f,
			0x2b,
			0x3f,
			0xb6,
			0x0a,
			0x96,
			0xc0,
			0x78,
			0x58,
			0x3c,
			0x76,
			0x6d,
			0x94,
			0x1a,
			0xe4,
			0x4e,
			0xb8,
			0x38,
			0xbb,
			0xf5,
			0xeb,
			0x29,
			0xd8,
			0xb0,
			0xf3,
			0x15,
			0x1e,
			0x99,
			0x96,
			0x3c,
			0x5d,
			0x63,
			0xd5,
			0xb1,
			0xad,
			0x52,
			0xb8,
			0x55,
			0x70,
			0x75,
			0x3e,
			0x1a,
			0xd5,
			0xda,
			0xf6,
			0x7a,
			0x48,
			0x7d,
			0x44,
			0x41,
			0xf9,
			0x11,
			0xce,
			0xd7,
			0xca,
			0xa5,
			0x3d,
			0x7a,
			0x79,
			0x7e,
			0x7d,
			0x25,
			0x1b,
			0x77,
			0xbc,
			0xf7,
			0xc7,
			0x0f,
			0x84,
			0x95,
			0x10,
			0x92,
			0x67,
			0x15,
			0x11,
			0x5a,
			0x5e,
			0x41,
			0x66,
			0x0f,
			0x38,
			0x03,
			0xb2,
			0xf1,
			0x5d,
			0xf8,
			0xab,
			0xc0,
			0x02,
			0x76,
			0x84,
			0x28,
			0xf4,
			0x9d,
			0x56,
			0x46,
			0x60,
			0x20,
			0xdb,
			0x68,
			0xa7,
			0xbb,
			0xee,
			0xac,
			0x15,
			0x01,
			0x2f,
			0x20,
			0x09,
			0xdb,
			0xc0,
			0x16,
			0xa1,
			0x89,
			0xf9,
			0x94,
			0x59,
			0x00,
			0xc1,
			0x76,
			0xbf,
			0xc1,
			0x4d,
			0x5d,
			0x2d,
			0xa9,
			0x85,
			0x2c,
			0xd6,
			0xd3,
			0x14,
			0xcc,
			0x02,
			0xc3,
			0xc2,
			0xfa,
			0x6b,
			0xb7,
			0xa6,
			0xef,
			0xdd,
			0x12,
			0x26,
			0xa4,
			0x63,
			0xe3,
			0x62,
			0xbd,
			0x56,
			0x8a,
			0x52,
			0x2b,
			0xb9,
			0xdf,
			0x09,
			0xbc,
			0x0e,
			0x97,
			0xa9,
			0xb0,
			0x82,
			0x46,
			0x08,
			0xd5,
			0x1a,
			0x8e,
			0x1b,
			0xa7,
			0x90,
			0x98,
			0xb9,
			0xbb,
			0x3c,
			0x17,
			0x9a,
			0xf2,
			0x82,
			0xba,
			0x64,
			0x0a,
			0x7f,
			0xca,
			0x5a,
			0x8c,
			0x7c,
			0xd3,
			0x79,
			0x09,
			0x5b,
			0x26,
			0xbb,
			0xbd,
			0x25,
			0xdf,
			0x3d,
			0x6f,
			0x9a,
			0x8f,
			0xee,
			0x21,
			0x66,
			0xb0,
			0x8d,
			0x84,
			0x4c,
			0x91,
			0x45,
			0xd4,
			0x77,
			0x4f,
			0xb3,
			0x8c,
			0xbc,
			0xa8,
			0x99,
			0xaa,
			0x19,
			0x53,
			0x7c,
			0x02,
			0x87,
			0xbb,
			0x0b,
			0x7c,
			0x1a,
			0x2d,
			0xdf,
			0x48,
			0x44,
			0x06,
			0xd6,
			0x7d,
			0x0c,
			0x2d,
			0x35,
			0x76,
			0xae,
			0xc4,
			0x5f,
			0x71,
			0x85,
			0x97,
			0xc4,
			0x3d,
			0xef,
			0x52,
			0xbe,
			0x00,
			0xe4,
			0xcd,
			0x49,
			0xd1,
			0xd1,
			0x1c,
			0x3c,
			0xd0,
			0x1c,
			0x42,
			0xaf,
			0xd4,
			0xbd,
			0x58,
			0x34,
			0x07,
			0x32,
			0xee,
			0xb9,
			0xb5,
			0xea,
			0xff,
			0xd7,
			0x8c,
			0x0d,
			0x2e,
			0x2f,
			0xaf,
			0x87,
			0xbb,
			0xe6,
			0x52,
			0x71,
			0x22,
			0xf5,
			0x25,
			0x17,
			0xa1,
			0x82,
			0x04,
			0xc2,
			0x4a,
			0xbd,
			0x57,
			0xc6,
			0xab,
			0xc8,
			0x35,
			0x0c,
			0x3c,
			0xd9,
			0xc2,
			0x43,
			0xdb,
			0x27,
			0x92,
			0xcf,
			0xb8,
			0x25,
			0x60,
			0xfa,
			0x21,
			0x3b,
			0x04,
			0x52,
			0xc8,
			0x96,
			0xba,
			0x74,
			0xe3,
			0x67,
			0x3e,
			0x8e,
			0x8d,
			0x61,
			0x90,
			0x92,
			0x59,
			0xb6,
			0x1a,
			0x1c,
			0x5e,
			0x21,
			0xc1,
			0x65,
			0xe5,
			0xa6,
			0x34,
			0x05,
			0x6f,
			0xc5,
			0x60,
			0xb1,
			0x83,
			0xc1,
			0xd5,
			0xd5,
			0xed,
			0xd9,
			0xc7,
			0x11,
			0x7b,
			0x49,
			0x7a,
			0xf9,
			0xf9,
			0x84,
			0x47,
			0x9b,
			0xe2,
			0xa5,
			0x82,
			0xe0,
			0xc2,
			0x88,
			0xd0,
			0xb2,
			0x58,
			0x88,
			0x7f,
			0x45,
			0x09,
			0x67,
			0x74,
			0x61,
			0xbf,
			0xe6,
			0x40,
			0xe2,
			0x9d,
			0xc2,
			0x47,
			0x05,
			0x89,
			0xed,
			0xcb,
			0xbb,
			0xb7,
			0x27,
			0xe7,
			0xdc,
			0x7a,
			0xfd,
			0xbf,
			0xa8,
			0xd0,
			0xaa,
			0x10,
			0x39,
			0x3c,
			0x20,
			0xf0,
			0xd3,
			0x6e,
			0xb1,
			0x72,
			0xf8,
			0xe6,
			0x0f,
			0xef,
			0x37,
			0xe5,
			0x09,
			0x33,
			0x5a,
			0x83,
			0x43,
			0x80,
			0x4f,
			0x65,
			0x2f,
			0x7c,
			0x8c,
			0x6a,
			0xa0,
			0x82,
			0x0c,
			0xd4,
			0xd4,
			0xfa,
			0x81,
			0x60,
			0x3d,
			0xdf,
			0x06,
			0xf1,
			0x5f,
			0x08,
			0x0d,
			0x6d,
			0x43,
			0xf2,
			0xe3,
			0x11,
			0x7d,
			0x80,
			0x32,
			0xc5,
			0xfb,
			0xc5,
			0xd9,
			0x27,
			0xec,
			0xc6,
			0x4e,
			0x65,
			0x27,
			0x76,
			0x87,
			0xa6,
			0xee,
			0xee,
			0xd7,
			0x8b,
			0xd1,
			0xa0,
			0x5c,
			0xb0,
			0x42,
			0x13,
			0x0e,
			0x95,
			0x4a,
			0xf2,
			0x06,
			0xc6,
			0x43,
			0x33,
			0xf4,
			0xc7,
			0xf8,
			0xe7,
			0x1f,
			0xdd,
			0xe4,
			0x46,
			0x4a,
			0x70,
			0x39,
			0x6c,
			0xd0,
			0xed,
			0xca,
			0xbe,
			0x60,
			0x3b,
			0xd1,
			0x7b,
			0x57,
			0x48,
			0xe5,
			0x3a,
			0x79,
			0xc1,
			0x69,
			0x33,
			0x53,
			0x1b,
			0x80,
			0xb8,
			0x91,
			0x7d,
			0xb4,
			0xf6,
			0x17,
			0x1a,
			0x1d,
			0x5a,
			0x32,
			0xd6,
			0xcc,
			0x71,
			0x29,
			0x3f,
			0x28,
			0xbb,
			0xf3,
			0x5e,
			0x71,
			0xb8,
			0x43,
			0xaf,
			0xf8,
			0xb9,
			0x64,
			0xef,
			0xc4,
			0xa5,
			0x6c,
			0x08,
			0x53,
			0xc7,
			0x00,
			0x10,
			0x39,
			0x4f,
			0xdd,
			0xe4,
			0xb6,
			0x19,
			0x27,
			0xfb,
			0xb8,
			0xf5,
			0x32,
			0x73,
			0xe5,
			0xcb,
			0x32, 
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	static byte chkb[] = new byte [60 + 4];
	
	/**
	 * Calculates a crc checksum-sequence over an array.
	 */
	public static byte BlockSequenceCRCByte(byte base[], int offset, int length, int sequence)
	{
		int n;
		int p_ndx;
		short x;
		
		short crc;

		if (sequence < 0)
			Sys.Error("sequence < 0, this shouldn't happen\n");

		//p_ndx = (sequence % (sizeof(chktbl) - 4));
		p_ndx = (sequence % (1024 - 4));
		
		//memcpy(chkb, base, length);
		System.arraycopy(base, offset , chkb, 0, Math.max(60, length));
		
		chkb[length]= (byte) chktbl[p_ndx + 0];
		chkb[length + 1]= (byte) chktbl[p_ndx + 1];
		chkb[length + 2]= (byte) chktbl[p_ndx + 2];
		chkb[length + 3]= (byte) chktbl[p_ndx + 3];
		
		
		length += 4;

		crc = CRC.CRC_Block(chkb, length);

		for (x= 0, n= 0; n < length; n++)
			x += chkb[n];

		crc ^= x;

		return (byte)(crc & 0xFF);
	}

}