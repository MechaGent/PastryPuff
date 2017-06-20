package PastryPuff.PuffWrapper;

/**
 * 
 * input and output state
 *
 */
class state
{
	/*
	 * output state
	 */

	int[] out; // (was unsigned char*) /* output buffer */
	long outlen; // (was unsigned long, where long_c == int_java) /* available space at out */
	int outcnt; // (was unsigned long, where long_c == int_java) /* bytes written to out so far */

	/*
	 * input state
	 */

	final int[] in; // (was unsigned char*) /* input buffer */
	long inlen; // (was unsigned long, where long_c == int_java) /* available input at in */
	int incnt; // (was unsigned long, where long_c == int_java) /* bytes read so far */
	int bitbuf; // () /* bit buffer */
	int bitcnt; // () /* number of bits in bit buffer */

	Object env; // (was jmp_buf) /* input limit error return state for bits() and decode() */

	public state(int[] inIn)
	{
		this.out = new int[0];
		this.outlen = 0;
		this.outcnt = 0;

		this.in = inIn;
		this.inlen = 0;
		this.incnt = 0;
		this.bitbuf = 0;
		this.bitcnt = 0;

		this.env = null;
	}
}