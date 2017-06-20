package PastryPuff.Mk01;

public class HuffTable
{
	private final int[] counts;
	private final int[] symbols;
	
	public HuffTable(int[] inCounts, int[] inSymbols)
	{
		this.counts = inCounts;
		this.symbols = inSymbols;
	}
	
	public int getCountFor(int index)
	{
		return this.counts[index];
	}
	
	public int getSymbolFor(int index)
	{
		return this.symbols[index];
	}
}
