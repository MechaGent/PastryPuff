package PastryPuff.Mk01;

import CustomExceptions.UnhandledEnum.Mk01.UnhandledEnumException;

public class Puffer
{
	/**
	 * maximum bits in a code
	 */
	private static final int MAXBITS = 15;

	/**
	 * maximum number of literal/length codes
	 */
	private static final int MAXLCODES = 286;

	/**
	 * maximum number of distance codes
	 */
	private static final int MAXDCODES = 30;

	/**
	 * maximum codes lengths to read
	 */
	private static final int MAXCODES = MAXLCODES + MAXDCODES;

	/**
	 * number of fixed literal/length codes
	 */
	private static final int FIXLCODES = 288;

	private static final int[] lens = new int[] { /* Size base for length codes 257..285 */
													3,
													4,
													5,
													6,
													7,
													8,
													9,
													10,
													11,
													13,
													15,
													17,
													19,
													23,
													27,
													31,
													35,
													43,
													51,
													59,
													67,
													83,
													99,
													115,
													131,
													163,
													195,
													227,
													258 };
	private static final int[] lext = new int[] { /* Extra bits for length codes 257..285 */
													0,
													0,
													0,
													0,
													0,
													0,
													0,
													0,
													1,
													1,
													1,
													1,
													2,
													2,
													2,
													2,
													3,
													3,
													3,
													3,
													4,
													4,
													4,
													4,
													5,
													5,
													5,
													5,
													0 };
	private static final int[] dists = new int[] { /* Offset base for distance codes 0..29 */
													1,
													2,
													3,
													4,
													5,
													7,
													9,
													13,
													17,
													25,
													33,
													49,
													65,
													97,
													129,
													193,
													257,
													385,
													513,
													769,
													1025,
													1537,
													2049,
													3073,
													4097,
													6145,
													8193,
													12289,
													16385,
													24577 };
	private static final int[] dext = new int[] { /* Extra bits for distance codes 0..29 */
													0,
													0,
													0,
													0,
													1,
													1,
													2,
													2,
													3,
													3,
													4,
													4,
													5,
													5,
													6,
													6,
													7,
													7,
													8,
													8,
													9,
													9,
													10,
													10,
													11,
													11,
													12,
													12,
													13,
													13 };
	
	private static final int[] order = new int[] {
													16,
													17,
													18,
													0,
													8,
													7,
													9,
													6,
													10,
													5,
													11,
													4,
													12,
													3,
													13,
													2,
													14,
													1,
													15 };

	//private static final boolean SLOW = true;
	private static final boolean INFLATE_ALLOW_INVALID_DISTANCE_TOOFAR_ARRR = true;

	private final State s;

	private boolean isVirgin;
	private HuffTable lengthCode;
	private HuffTable distCode;

	public Puffer(State inS)
	{
		this.s = inS;
		this.isVirgin = true;
		this.lengthCode = null;
		this.distCode = null;
	}

	public OutByteList puff()
	{
		boolean isLastBlock;

		do
		{
			isLastBlock = this.s.getBits(1) == 1;

			final BlockTypes type = BlockTypes.parse(this.s.getBits(2));
			
			//System.out.println("block triggered as " + type.toString());

			switch (type)
			{
				case Dynamic:
				{
					this.process_Dynamic();
					break;
				}
				case Fixed:
				{
					this.process_Fixed();
					break;
				}
				case Stored:
				{
					this.process_Stored();
					break;
				}
				default:
				{
					throw new UnhandledEnumException(type);
				}
			}
		} while (!isLastBlock);

		return this.s.getOutStream();
	}

	private void process_Stored()
	{
		this.s.clearBitBuffer();
		final int blockLength_Byte0 = s.getNextByte();
		final int blockLength_Byte1 = s.getNextByte() << 8;

		final int checkByte0 = (~s.getNextByte()) & 0xff;
		final int checkByte1 = (~s.getNextByte()) & 0xff;

		if ((blockLength_Byte0 != checkByte0) || (blockLength_Byte1 != checkByte1))
		{
			throw new IllegalArgumentException("Non-matching complements!");
		}

		final int blockLength = blockLength_Byte0 | blockLength_Byte1;

		for (int i = 0; i < blockLength; i++)
		{
			//result[i] = s.getNextByte();
			s.addWrittenByte(s.getNextByte());
		}
	}

	private void process_Fixed()
	{
		if (this.isVirgin)
		{
			int symbol = 0;
			final int[] lengths = new int[MAXLCODES];
			final int[] lencnt = new int[MAXBITS + 1];
			final int[] lensym = new int[FIXLCODES];

			final int[] distcnt = new int[MAXBITS + 1];
			final int[] distsym = new int[MAXDCODES];

			/* literal/length table */
			for (; symbol < 144; symbol++)
			{
				lengths[symbol] = 8;
			}

			for (; symbol < 256; symbol++)
			{
				lengths[symbol] = 9;
			}

			for (; symbol < 280; symbol++)
			{
				lengths[symbol] = 7;
			}

			for (; symbol < FIXLCODES; symbol++)
			{
				lengths[symbol] = 8;
			}

			constructHuffTable(lencnt, lensym, lengths, MAXLCODES);
			
			this.lengthCode = new HuffTable(lencnt, lensym);

			for (symbol = 0; symbol < MAXDCODES; symbol++)
			{
				lengths[symbol] = 5;
			}

			constructHuffTable(distcnt, distsym, lengths, MAXDCODES);
			this.distCode = new HuffTable(distcnt, distsym);

			this.isVirgin = false;
		}
		
		this.codes();
	}

	private void codes()
	{
		codes(this.s, this.lengthCode, this.distCode);
	}
	
	private static void codes(State s, HuffTable lencode, HuffTable distcode)
	{
		int symbol;

		do
		{
			symbol = decode(s, lencode);

			if (symbol < 256)
			{
				if (symbol < 0)
				{
					throw new IllegalArgumentException("invalid symbol: " + symbol);
				}

				// write out the literal
				s.getOutStream().add(symbol);
			}
			else if (symbol > 256)
			{
				// get and compute length *
				symbol -= 257;

				if (symbol >= 29)
				{
					throw new IllegalArgumentException("invalid symbol: " + symbol);
				}

				int length = lens[symbol] + s.getBits(lext[symbol]);

				// get and check distance *
				symbol = decode(s, distcode);

				if (symbol < 0)
				{
					throw new IllegalArgumentException("invalid symbol: " + symbol); // invalid symbol *
				}

				final int distance = dists[symbol] + s.getBits(dext[symbol]);

				if (INFLATE_ALLOW_INVALID_DISTANCE_TOOFAR_ARRR)
				{
					if (distance > s.getOutCount())
					{
						throw new IllegalArgumentException("Distance too far back: (" + distance + " of " + s.getOutCount() + ")"); // distance too far back *
					}
				}

				while (length-- > 0)
				{
					if (INFLATE_ALLOW_INVALID_DISTANCE_TOOFAR_ARRR && distance > s.getOutCount())
					{
						throw new IllegalArgumentException("tooooo faaaaar");
					}
					else
					{
						s.getOutStream().addIndirectly(s.getOutCount() - distance);
					}
				}
			}
		} while (symbol != 256);
	}

	private static int decode(State s, HuffTable h)
	{
		int numBitsInCode = 1;
		int code = 0;
		int first = 0;
		int index = 0;
		int bitbuffer = s.bitbuffer;
		int bitsRemaining = s.numBitsInBuffer;
		int next = 1;

		
		
		code = first = index = 0;
		numBitsInCode = 1;

		while (true)
		{
			while (bitsRemaining-- != 0)
			{
				code |= bitbuffer & 1;
				bitbuffer >>>= 1;
				final int count = h.getCountFor(next++);

				if (code - count < first)
				{
					s.bitbuffer = bitbuffer;
					s.numBitsInBuffer = (s.numBitsInBuffer - numBitsInCode) & 7;
					return h.getSymbolFor(index + (code - first));
				}
				else
				{
					index += count;
					first += count;
					first <<= 1;
					code <<= 1;
					numBitsInCode++;
				}
			}

			bitsRemaining = (MAXBITS + 1) - numBitsInCode;
			
			if (bitsRemaining == 0)
			{
				break;
			}

			bitbuffer = s.getNextByte();
			
			if (bitsRemaining > 8)
			{
				bitsRemaining = 8;
			}
		}

		return -10;
	}

	private void process_Dynamic()
	{
		/* number of lengths in descriptor */
		int nlen;
		int ndist;
		int ncode;

		int index; /* index of lengths[] */
		//int err; /* construct() return value */
		int[] lengths = new int[MAXCODES]; /* descriptor code lengths */

		/* lencode memory */
		int[] lencnt = new int[MAXBITS + 1];
		int[] lensym = new int[MAXLCODES];

		/* distcode memory */
		int[] distcnt = new int[MAXBITS + 1];
		int[] distsym = new int[MAXDCODES];

		/* length and distance codes */
		HuffTable lencode = new HuffTable(lencnt, lensym);
		HuffTable distcode = new HuffTable(distcnt, distsym);
		
		/* get number of lengths in each table, check lengths */
		nlen = this.s.getBits(5) + 257;
		ndist = this.s.getBits(5) + 1;
		ncode = this.s.getBits(4) + 4;
		
		if (nlen > MAXLCODES || ndist > MAXDCODES)
		{
			throw new IllegalArgumentException("bad counts!");
		}

		/* read code length code lengths (really), missing lengths are zero */
		for (index = 0; index < ncode; index++)
		{
			lengths[order[index]] = this.s.getBits(3);
		}
		for (; index < 19; index++)
		{
			lengths[order[index]] = 0;
		}

		/* build huffman table for code lengths codes (use lencode temporarily) */
		constructHuffTable(lencnt, lensym, lengths, 19);

		/* read length/literal and distance code length tables */
		index = 0;
		while (index < nlen + ndist)
		{
			int symbol; // * decoded value *
			int len; // * last length to repeat *

			symbol = decode(s, lencode);
			if (symbol < 0)
			{
				throw new IllegalArgumentException("invalid symbol: " + symbol); // invalid symbol
			}

			if (symbol < 16) // * length in 0..15 *
			{
				lengths[index++] = symbol;
			}
			else // * repeat instruction *
			{
				len = 0;

				// * assume repeating zeros *
				if (symbol == 16)
				{
					// * repeat last length 3..6 times *

					if (index == 0)
					{
						throw new IllegalArgumentException("no last length!"); // no last length!
					}

					len = lengths[index - 1]; // * last length *
					symbol = 3 + s.getBits(2);
				}
				else if (symbol == 17) // * repeat zero 3..10 times *
				{
					symbol = 3 + s.getBits(3);
				}
				else // * == 18, repeat zero 11..138 times *
				{
					symbol = 11 + s.getBits(7);
				}

				if (index + symbol > nlen + ndist)
				{
					throw new IllegalArgumentException("Too many lengths!"); // * too many lengths! *
				}

				while (symbol-- != 0) // * repeat last or zero symbol times *
				{
					lengths[index++] = len;
				}
			}
		}

		/* check for end-of-block code -- there better be one! */
		if (lengths[256] == 0)
		{
			throw new IllegalArgumentException("no end-of-block code!");
		}

		// build huffman table for literal/length codes
		constructHuffTable(lencnt, lensym, lengths, nlen);
		
		/*
		if (nlen != lencode.getCountFor(0) + lencode.getCountFor(1))
		{
			throw new IllegalArgumentException("incomplete code ok only for single length 1 code");
		}
		*/
		
		//* build huffman table for distance codes
		final int[] temp0 = helperMethod_getSubArray(lengths, nlen);
		constructHuffTable(distcnt, distsym, temp0, ndist);
		
		/*
		if (ndist != distcode.getCountFor(0) + distcode.getCountFor(1))
		{
			throw new IllegalArgumentException("incomplete code ok only for single length 1 code");
		}
		*/

		/* decode data until end-of-block code */
		codes(s, lencode, distcode);
	}
	
	private static final int[] helperMethod_getSubArray(int[] original, int startIndex)
	{
		final int[] result = new int[original.length - startIndex];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = original[startIndex++];
		}

		return result;
	}

	private static void constructHuffTable(int[] counts, int[] symbols, int[] lengths, int n)
	{
		int symbol;
		int length;

		int[] offs = new int[MAXBITS + 1];

		/* count number of codes of each length */
		for (length = 0; length <= MAXBITS; length++)
		{
			counts[length] = 0;
		}

		for (symbol = 0; symbol < n; symbol++)
		{
			/* assumes lengths are within bounds */
			(counts[lengths[symbol]])++;
		}

		if (counts[0] == n) // * no codes! *
		{
			/* complete, but decode() will fail */
			throw new IllegalArgumentException("complete, but decode will fail");
		}

		/* 
		 * check for an over-subscribed or incomplete set of lengths 
		 */

		int left = 1; /* one possible code of zero length */

		for (length = 1; length <= MAXBITS; length++)
		{
			left <<= 1; /* one more bit, double codes left */
			left -= counts[length]; /* deduct count from possible codes */
			if (left < 0)
			{
				/* over-subscribed--return negative */
				throw new IllegalArgumentException("You went too deep!");
			}

			/* left > 0 means incomplete */
		}

		/* generate offsets into symbol table for each length for sorting */
		offs[1] = 0;

		for (length = 1; length < MAXBITS; length++)
		{
			offs[length + 1] = offs[length] + counts[length];
		}

		/*
		 * put symbols in table sorted by length, by symbol order within each length
		 */
		for (symbol = 0; symbol < n; symbol++)
		{
			if (lengths[symbol] != 0)
			{
				symbols[offs[lengths[symbol]]++] = symbol;
			}
		}

		//return new HuffTable(counts, symbols);
	}

	private static enum BlockTypes
	{
		Stored,
		Fixed,
		Dynamic;

		public static BlockTypes parse(int rawType)
		{
			final BlockTypes result;

			switch (rawType)
			{
				case 0:
				{
					result = Stored;
					break;
				}
				case 1:
				{
					result = Fixed;
					break;
				}
				case 2:
				{
					result = Dynamic;
					break;
				}
				default:
				{
					throw new IllegalArgumentException("bad case: " + rawType);
				}
			}

			return result;
		}
	}
}
