package PastryPuff.PuffWrapper;

/**
 * Huffman code decoding tables. count[1..MAXBITS] is the number of symbols of
 * each length, which for a canonical code are stepped through in order.
 * symbol[] are the symbol values in canonical order, where the number of
 * entries is the sum of the counts in count[]. The decoding process can be
 * seen in the function decode() below.
 */
class huffman
{
	int[] count; // (was originally short*) /* number of symbols of each length */
	int[] symbol; // (was originally short*) /* canonically ordered symbols */

	public huffman(int[] inCount, int[] inSymbol)
	{
		this.count = inCount;
		this.symbol = inSymbol;
	}
}