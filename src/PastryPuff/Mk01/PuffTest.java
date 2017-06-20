package PastryPuff.Mk01;

import HandyStuff.FileParser;

public class PuffTest
{
	public static void main(String[] args)
	{
		// createCompressedFile();
		runTest();
		// DB 8E DB 36
		// System.out.println(Integer.toBinaryString(0xdb) + " " + Integer.toBinaryString(0x8e) + " " + Integer.toBinaryString(0xdb) + " " + Integer.toBinaryString(0x36) );//+ " " + Integer.toBinaryString(0xdb));
	}

	public static void runTest()
	{
		int[] source = getRawFile(getSourceDir());

		final State s = new State(source);
		final Puffer parser = new Puffer(s);

		final byte[] output = parser.puff().toByteArray();

		for (int i = 0; i < output.length; i++)
		{
			System.out.println(Character.toString((char) output[i]));
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

		// result = "H:/Users/Thrawnboo/Documents/GAME STUFF/zlib-1.2.11 Source Code/contrib/puff/zeros.raw"; // uncompressed: 260 bytes
		result = "H:/Users/Thrawnboo/Documents/GAME STUFF/zlib-1.2.11 Source Code/contrib/puff/deflatedTestDoc.txt";

		return result;
	}
}
