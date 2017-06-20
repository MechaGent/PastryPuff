package PastryPuff.Mk01;

import SingleLinkedList.Mk01.SingleLinkedList;

public class OutByteList
{
	private Node head;
	private Node tail;
	private int size;
	
	public OutByteList()
	{
		this.head = null;
		this.tail = null;
		this.size = 0;
	}
	
	public void add(int in)
	{
		this.add_internal(new SingleNode(in), 1);
	}
	
	public void add(int[] in)
	{
		switch(in.length)
		{
			case 0:
			{
				break;
			}
			case 1:
			{
				this.add_internal(new SingleNode(in[0]), 1);
				break;
			}
			default:
			{
				this.add_internal(new arrNode(in), in.length);
				break;
			}
		}
	}
	
	public void addIndirectly(int sourceIndex)
	{
		this.add_internal(new IndirectNode(sourceIndex), 1);
	}
	
	private void add_internal(Node in, int nodeLength)
	{
		if(this.size == 0)
		{
			this.head = in;
			this.tail = in;
			this.size = nodeLength;
		}
		else
		{
			this.tail.next = in;
			this.tail = in;
			this.size += nodeLength;
		}
	}
	
	public byte[] toByteArray()
	{
		final ToArrayContainer result = new ToArrayContainer(this.size);
		Node current = this.head;
		
		while(current != null)
		{
			current.copySelfToArray(result);
			current = current.next;
		}
		
		for(int[] indi: result.indirectsStack)
		{
			result.array[indi[0]] = result.array[indi[1]];
		}
		
		return result.array;
	}
	
	public int getCurrentSize()
	{
		return this.size;
	}
	
	private static abstract class Node
	{
		protected Node next;
		
		public Node()
		{
			this.next = null;
		}
		
		public abstract void copySelfToArray(ToArrayContainer in);
	}
	
	private static class SingleNode extends Node
	{
		protected final int cargo;
		
		public SingleNode(int inCargo)
		{
			super();
			this.cargo = inCargo;
		}
		
		@Override
		public void copySelfToArray(ToArrayContainer in)
		{
			in.array[in.currentIndex++] = (byte) this.cargo;
		}
	}
	
	private static class IndirectNode extends SingleNode
	{
		public IndirectNode(int copyFromIndex)
		{
			super(copyFromIndex);
		}
		
		@Override
		public void copySelfToArray(ToArrayContainer in)
		{
			if(this.cargo < in.currentIndex)
			{
				in.array[in.currentIndex++] = in.array[this.cargo];
			}
			else
			{
				in.indirectsStack.add(new int[]{in.currentIndex++, this.cargo});
			}
		}
	}
	
	private static class arrNode extends Node
	{
		private final int[] cargo;

		public arrNode(int[] inCargo)
		{
			this.cargo = inCargo;
		}

		@Override
		public void copySelfToArray(ToArrayContainer in)
		{
			for(int i = 0; i < this.cargo.length; i++)
			{
				in.array[in.currentIndex++] = (byte) this.cargo[i];
			}
		}
	}
	
	private static class ToArrayContainer
	{
		private final byte[] array;
		private int currentIndex;
		private SingleLinkedList<int[]> indirectsStack;
		
		public ToArrayContainer(int arrLength)
		{
			this.array = new byte[arrLength];
			this.currentIndex = 0;
			this.indirectsStack = new SingleLinkedList<int[]>();
		}
	}
}
