package PastryPuff.Mk01;

import SingleLinkedList.Mk01.SingleLinkedList;

public class State
{
	private final OutByteList outStream;

	private final Reader reader;

	int bitbuffer;
	int numBitsInBuffer;

	public State(SingleLinkedList<int[]> inInStream, int inTotalReadBytesRemaining)
	{
		this(Reader.getInstance(inInStream, inTotalReadBytesRemaining));
	}
	
	public State(int[] inInStream)
	{
		this(Reader.getInstance(inInStream));
	}
	
	private State(Reader stream)
	{
		this.outStream = new OutByteList();

		this.reader = stream;

		this.bitbuffer = 0;
		this.numBitsInBuffer = 0;
	}

	public int getBits(int numBits)
	{
		while (this.numBitsInBuffer < numBits)
		{
			this.bitbuffer |= this.reader.getNextByte() << this.numBitsInBuffer;
			this.numBitsInBuffer += 8;
		}

		final int result = this.bitbuffer & getMaskForBits(numBits);
		
		this.bitbuffer >>>= numBits;
		this.numBitsInBuffer -= numBits;
		
		return result;
	}
	
	public int getNextByte()
	{
		return this.reader.getNextByte();
	}
	
	public void clearBitBuffer()
	{
		this.bitbuffer = 0;
		this.numBitsInBuffer = 0;
	}
	
	public int getOutCount()
	{
		return this.outStream.getCurrentSize();
	}
	
	public void addWrittenChunk(int[] chunk)
	{
		this.outStream.add(chunk);
	}
	
	public void addWrittenByte(int in)
	{
		this.outStream.add(in);
	}

	public OutByteList getOutStream()
	{
		return this.outStream;
	}

	private static int getMaskForBits(int numBits)
	{
		final int result;

		switch (numBits)
		{
			case 0:
			{
				result = 0b0;
				break;
			}
			case 1:
			{
				result = 0b1;
				break;
			}
			case 2:
			{
				result = 0b11;
				break;
			}
			case 3:
			{
				result = 0b111;
				break;
			}
			case 4:
			{
				result = 0b1111;
				break;
			}
			case 5:
			{
				result = 0b11111;
				break;
			}
			case 6:
			{
				result = 0b111111;
				break;
			}
			case 7:
			{
				result = 0b1111111;
				break;
			}
			case 8:
			{
				result = 0b11111111;
				break;
			}
			case 9:
			{
				result = 0b111111111;
				break;
			}
			case 10:
			{
				result = 0b1111111111;
				break;
			}
			case 11:
			{
				result = 0b11111111111;
				break;
			}
			case 12:
			{
				result = 0b111111111111;
				break;
			}
			case 13:
			{
				result = 0b1111111111111;
				break;
			}
			case 14:
			{
				result = 0b11111111111111;
				break;
			}
			case 15:
			{
				result = 0b111111111111111;
				break;
			}
			case 16:
			{
				result = 0b1111111111111111;
				break;
			}
			case 17:
			{
				result = 0b11111111111111111;
				break;
			}
			case 18:
			{
				result = 0b111111111111111111;
				break;
			}
			case 19:
			{
				result = 0b1111111111111111111;
				break;
			}
			case 20:
			{
				result = 0b11111111111111111111;
				break;
			}
			default:
			{
				throw new IllegalArgumentException("bad case: " + numBits);
			}
		}

		return result;
	}
}
