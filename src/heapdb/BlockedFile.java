package heapdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import static heapdb.Constants.BLOCK_SIZE;

/*
 * Provide random read and write block operations for a file.
 *
 */
public class BlockedFile  {
	
	protected RandomAccessFile file; 
	protected int highBlockNo;
	protected boolean isOpen ;
	
	/*
	 * open a file.  
	 * create=true, a new empty file is created.
	 * If the file exists, then it is first deleted.
	 * 
	 * create=false, the file is opened.
	 */
	BlockedFile(String filename, boolean create) {
		if (create) {
			File f = new File(filename);
			boolean b = f.exists();
			if (b) {
				b=f.delete();
				if (!b) {
					throw new RuntimeException("Unable to create file.  Cannot delete old file. "+filename);
				}
			}
			try {
				file = new RandomAccessFile(filename, "rw");
				highBlockNo =   -1;
				isOpen = true;
			} catch (Exception e) {
				throw new RuntimeException( e.getMessage() );
			}
		} else {
			try {
				file = new RandomAccessFile(filename, "rw");
				highBlockNo =   (int)(file.length()/heapdb.Constants.BLOCK_SIZE) -1 ;
				isOpen = true;
			} catch (Exception e) {
				throw new RuntimeException( e.getMessage() );
			}
		}
	}
	
	
	/*
	 * return the highest block number for the file.
	 * The first block in the file is block 0. 
	 * If the file is empty, the highest block number will be -1. 
	 */
	int getHighestBlockNo() { 
		return highBlockNo;
	}
	
	boolean isOpen() {
		return isOpen;
	}
	

	/*
	 * read a block from the file into the buffer.
	 */
	void readBlock(int blockNo, ByteBuffer buffer) {
		readBlock(blockNo, buffer.array());
	}
	
	void readBlock(int blockNo, byte[] bytes) {
		int nobytes = 0;
		try {
			file.seek(blockNo * BLOCK_SIZE);
			nobytes = file.read(bytes);
			if (nobytes == BLOCK_SIZE) {
				return;
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		if (nobytes != BLOCK_SIZE) {
			throw new RuntimeException("Error: BlockedFile read returned truncated block " + blockNo + " " + nobytes);
		}
	}
	
	/* 
	 * close the file.
	 */
	void close() {
		try {
			file.close();
			isOpen=false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * write a block from the byte buffer.
	 */
	void writeBlock(int blockNo, ByteBuffer buffer) {
		writeBlock(blockNo, buffer.array());
	}
	
	void writeBlock(int blockNo, byte[] bytes) {
		try {
			if (blockNo > highBlockNo) {
				highBlockNo = blockNo;
			}
			file.seek(blockNo*BLOCK_SIZE);
			file.write(bytes);
		} catch (Exception e) {
			throw new RuntimeException( e.getMessage() );
		}
	}
	
	/* 
	 * for debugging.
	 */
	public static void printBlock(byte[] block) {
		int len = block.length;
		for (int i=0; i< len; i++) {
		if (i>0 && i%64==0) System.out.println();
		   System.out.printf("%02x", block[i]);
		   
		}
		System.out.println();
	}
}
