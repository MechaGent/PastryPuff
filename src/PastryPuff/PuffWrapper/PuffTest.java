package PastryPuff.PuffWrapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import HandyStuff.FileParser;

public class PuffTest
{
	public static void main(String[] args)
	{
		//createCompressedFile();
		runTest();
		//DB 8E DB 36
		//System.out.println(Integer.toBinaryString(0xdb) + " " + Integer.toBinaryString(0x8e) + " " + Integer.toBinaryString(0xdb) + " " + Integer.toBinaryString(0x36) );//+ " " + Integer.toBinaryString(0xdb));
	}

	public static void runTest()
	{
		int ret = 0;
		int put = 0;
		int fail = 0;

		int skip = 0;

		int[] arg = null;
		int[] name = null;

		int[] source = getRawFile(getSourceDir());
		int[] dest = new int[2876];

		int len = 0;

		int sourcelen;
		int destlen = dest.length;

		ret = Puff.puff(dest, destlen, source, source.length);

		if (ret != 0)
		{
			System.out.println("puff failed, with return code " + ret);
		}
		else
		{
			for (int i = 0; i < destlen; i++)
			{
				System.out.println(Character.toString((char) dest[i]));
			}
		}
	}

	private static int[] getRawFile(String pathDir)
	{
		final byte[] raw = FileParser.parseFileAsByteArray(pathDir);
		final int[] result = new int[raw.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = Byte.toUnsignedInt(raw[i]);
		}

		return result;
	}

	private static String getSourceDir()
	{
		final String result;

		//result = "H:/Users/Thrawnboo/Documents/GAME STUFF/zlib-1.2.11 Source Code/contrib/puff/zeros.raw"; // uncompressed: 260 bytes
		result = "H:/Users/Thrawnboo/Documents/GAME STUFF/zlib-1.2.11 Source Code/contrib/puff/deflatedTestDoc.txt";

		return result;
	}

	private static void createCompressedFile()
	{
		final String base = "H:/Users/Thrawnboo/Documents/GAME STUFF/zlib-1.2.11 Source Code/contrib/puff/";
		final String uncomp = "testDoc.txt";
		final String compr = "deflatedTestDoc.txt";
		
		try
		{
			createCompressedFile_base(base, uncomp, compr);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createCompressedFile_base(String pathDir_base, String pathDir_uncompressed, String pathDir_compressed) throws Exception
	{
		
		final FileInputStream fis = new FileInputStream(pathDir_base + pathDir_uncompressed);
		FileOutputStream fos = new FileOutputStream(pathDir_base + pathDir_compressed);
		final Deflater def = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
		DeflaterOutputStream dos = new DeflaterOutputStream(fos, def);
		
		doCopy(fis, dos);
		
		/*
		FileInputStream fis2 = new FileInputStream(pathDir_base + pathDir_compressed);
		InflaterInputStream iis = new InflaterInputStream(fis2);
		FileOutputStream fos2 = new FileOutputStream(pathDir_base + "inflated.txt");

		doCopy(iis, fos2);
		*/
	}

	private static void doCopy(InputStream is, OutputStream os) throws Exception
	{
		int oneByte;
		while ((oneByte = is.read()) != -1)
		{
			os.write(oneByte);
		}
		os.close();
		is.close();
	}
}
