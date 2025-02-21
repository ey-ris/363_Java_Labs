package heapdb;

public class BitMap {
		
	byte[] bytes;  // 0 = free,  1= row
	
	public BitMap() {
		bytes = new byte[Constants.BLOCK_SIZE];
	}
	
	public BitMap(int size) {
		bytes = new byte[size];
	}
	
	public BitMap(byte[] bytes) {
		this.bytes = bytes;
	}
	
	/*
	 * get the value of the bit at position idx in the bitmap
	 */
	public boolean getBit(int idx) {
		// calculate index into the bytes array
		// there are 8 bits in each byte 
		// numbered from left to right 01234567
		int bidx = idx/8;
		int offset = idx%8;
		byte b = (byte)(bytes[bidx] << offset);
		// shift desired bit into the sign position and 
		// test for < 0, in other words is the bit a 1.
		return (b<0);
	}
	
	/*
	 * set the value of a bit to 1
	 */
	public void setBit(int idx) {
		int bidx = idx/8;
		int offset = idx%8;
		// create a bit mask of all zeros 
		// except a 1 in the desired bit position
		byte b = (byte)(1 << (7-offset));
		// perform bit-wise OR operation
		bytes[bidx] = (byte)(bytes[bidx] | b);
	}
	
	/*
	 * set the value of a bit to 0
	 */
	public void clearBit(int idx) {
		int bidx = idx/8;
		int offset = idx%8;
		// create a bit mask of all ones 
		// except a 0 in the desired bit position
		byte b = (byte)(~(1 << (7-offset)));
		// perform bit-wise AND operation
		bytes[bidx] = (byte)(bytes[bidx] & b);
	}
	
	/* 
	 * Find the first 0 bit in the bitmap.
	 * Return the bit index.
	 */
	public int findZero() {
		for (int i=0; i<bytes.length*8; i++) {
			if (! getBit(i)) return i;
		}
		throw new RuntimeException("No space.");
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	/*
	 * for debugging.
	 */
	public void diagnosticPrint() {
		int WIDTH=32;
		System.out.println("BIT MAP");
		for (int i=0; i<bytes.length; i++) {
			if (i%WIDTH == 0) {
				if (i>0) System.out.println();
				System.out.printf("%04x  ", i);
			}
			System.out.printf("%02x",  bytes[i]);
		}
		System.out.println();
		
	}
}
