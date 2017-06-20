package PastryPuff.Mk01;

import SingleLinkedList.Mk01.SingleLinkedList;

abstract class Reader
{
	protected int totalReadBytesRemaining;

	private Reader(int inTotalReadBytesRemaining)
	{
		this.totalReadBytesRemaining = inTotalReadBytesRemaining;
	}
	
	public static Reader getInstance(SingleLinkedList<int[]> inInStream, int inTotalReadBytesRemaining)
	{
		return new ListReader(inInStream, inTotalReadBytesRemaining);
	}
	
	public static Reader getInstance(int[] inStream)
	{
		return new ArrReader(inStream);
	}

	public abstract int getNextByte();
	
	private static class ListReader extends Reader
	{
		private final SingleLinkedList<int[]> inStream;
		private int[] currentRead;
		private int subIndex;
		
		public ListReader(SingleLinkedList<int[]> inInStream, int inTotalReadBytesRemaining)
		{
			super(inTotalReadBytesRemaining);
			this.inStream = inInStream;
			this.currentRead = this.inStream.pop();
			this.subIndex = 0;
		}

		@Override
		public int getNextByte()
		{
			if (this.subIndex >= this.currentRead.length)
			{
				this.currentRead = this.inStream.pop();
				this.subIndex = 0;
			}

			this.totalReadBytesRemaining--;

			return this.currentRead[this.subIndex++];
		}
	}
	
	private static class ArrReader extends Reader
	{
		private final int[] core;
		private int index;
		
		public ArrReader(int[] inCore)
		{
			super(inCore.length);
			this.core = inCore;
			this.index = 0;
		}

		@Override
		public int getNextByte()
		{
			return this.core[this.index++];
		}
	}
}