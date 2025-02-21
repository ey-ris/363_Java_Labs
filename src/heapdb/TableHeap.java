package heapdb;

import java.util.Iterator;
import java.nio.ByteBuffer;

public class TableHeap implements ITable {
	
	BlockedFile bfile;
	Schema schema;
	BitMap bitmap;  //0=free, 1=row
	int rows_per_block;
	Index[] indexes;
	
	public TableHeap(String filename, Schema schema) {
		this.schema = schema;
		// create a new file
		// write schema to block 0
		// write free space bitmap to block 1  
		// data rows start at block 2
		bfile = new BlockedFile(filename, true);
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);
		buffer.position(8);
		schema.serialize(buffer);
		// set bytes used in the buffer.
		buffer.putInt(0, buffer.position());
		bfile.writeBlock(0, buffer);
		// write out BitMap of all 0's
		bitmap = new BitMap();
		bfile.writeBlock(1, bitmap.getBytes());
		rows_per_block = (4096-8)/schema.getTupleSizeInBytes();
		indexes = new Index[schema.size()];
	}
	
	public TableHeap(String filename) {
		// open existing file
		bfile = new BlockedFile(filename, false);
		// read the schema from block 0
		ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);
		bfile.readBlock(0,  buffer);
		buffer.limit(buffer.getInt(0));
		buffer.position(8);
		schema = Schema.deserialize(buffer);
		// read the bitmap from block 1
		byte[] bytes = new byte[4096];
		bfile.readBlock(1, bytes);
		bitmap = new BitMap(bytes);
		rows_per_block = (4096-8)/schema.getTupleSizeInBytes();
		indexes = new Index[schema.size()];
	}
	
	@Override
	public void close() {
		if (bfile.isOpen()) {
		// write the bitmap
			bfile.writeBlock(1,  bitmap.getBytes());
			bfile.close();
		}
	}

	@Override
	public Iterator<Tuple> iterator() {
		return new TupleIterator();
	}
	
	public class TupleIterator implements Iterator<Tuple> {
		
		private int row_no = -1;
		private int current_block_no = 0;
		private ByteBuffer buffer = ByteBuffer.wrap(new byte[4096]);

		
		@Override
		public boolean hasNext() {
			for (row_no=row_no+1; row_no<4096*8; row_no++) {
				if (bitmap.getBit(row_no)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Tuple next() {
			// read row 
			int block_no = row_no/rows_per_block + 2;
			int offset = (row_no%rows_per_block) * schema.getTupleSizeInBytes();
			// read the block
			if (current_block_no != block_no) {
				// read the block
				bfile.readBlock(block_no, buffer);
				current_block_no = block_no;
			}
			// get the tuple
			buffer.position(offset);
			Tuple t = Tuple.deserialize(schema,  buffer);
			return t;
		}
		
		private int getRowNo() {
			return row_no;
		}
		
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

	/* 
	 * return the number of rows in table
	 */
	@Override
	public int size() {
		int size=0;
		for (int i=0; i<Constants.BLOCK_SIZE*8; i++) {
			if (bitmap.getBit(i)) size++;
		}
		return size;
	}

	@Override
	public boolean insert(Tuple rec) {
		// check primary key
		if (schema.getKey()!=null) {
			Tuple t = lookup(rec.getKey());
			if (t != null) {
				return false;  // duplicate primary key
			}
		}

		// Find empty space for row
		int row_no = -1;
		for (int i = 0; i < Constants.BLOCK_SIZE * 8; i++) {
			if (!bitmap.getBit(i)) {
				row_no = i;
				break;
			}
		}

		if (row_no == -1) {
			return false; // No space available
		}

		// Write the tuple to the file
		writeTuple(row_no, rec);

		// Set space occupied in the bitmap
		bitmap.setBit(row_no);
		
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != null) {
				// TODO in lab14. 
			}
		}
		return true;
	}

	@Override
	public boolean delete(Object key) {
		String keyColumnName = schema.getKey();
		if (keyColumnName == null)
			throw new RuntimeException("Cannot delete when schema does not have a key.");

		int row_no = -1;
		TupleIterator it = new TupleIterator();
		while (it.hasNext()) {
			Tuple t = it.next();
			if (t.get(keyColumnName).equals(key)) { // Compare by key
				row_no = it.getRowNo();
				break; // exit the loop once found
			}
		}

		if (row_no == -1) {
			return false; // Tuple not found
		}

		// Mark the space free in the bitmap
		bitmap.clearBit(row_no);

		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] != null) {
				// TODO in lab14.
			}
		}

		return true;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Tuple lookup(Object key) {
		String keyColumnName = schema.getKey();
		if (keyColumnName == null) throw new RuntimeException("Cannot lookup by key when schema does not have a key.");

		TupleIterator it = new TupleIterator();
		while (it.hasNext()) {
			Tuple t = it.next();
			if (t.get(keyColumnName).equals(key)) {
				return t;
			}
		}

		return null; // Tuple not found
		//throw new UnsupportedOperationException();
	}

	@Override
	public ITable lookup(String colname, Object value) {
	    ITable result = new Table(schema);

		TupleIterator it = new TupleIterator();
		while (it.hasNext()) {
			Tuple t = it.next();
			if (t.get(colname).equals(value)) {
				result.insert(t);
			}
		}

		return result;
	    //throw new UnsupportedOperationException();
	}


	
	public boolean createIndex(String columnName) {
		int col_index = schema.getColumnIndex(columnName);
		if (col_index < 0) {
			throw new RuntimeException("Can not create index.  Column does not exist. "+columnName);
		}
		if ( !(schema.getType(col_index) instanceof TypeInt)) {
			throw new RuntimeException("Indexes are only supported on integer columns. "+columnName);
		}
		Index index = new OrderedIndex();
		indexes[col_index] = index;
		TupleIterator it = new TupleIterator();
		while (it.hasNext() ) {
			Tuple t = it.next();
			int key = t.getInt(col_index);
			if (!index.insert(key, it.getRowNo())) {
				throw new RuntimeException("Create index insert failed. "+key+" "+it.getRowNo());
			}
		}
		return true;
		
	}
	
	public void dropIndex(String columnName) {
		int col_index = schema.getColumnIndex(columnName);
		if (col_index < 0) {
			throw new RuntimeException("Can not drop index.  Column does not exist. "+columnName);
		}
		indexes[col_index]=null;
	}
	
	private Tuple readTuple(int row_no) {
		int block_no = 2 + row_no/rows_per_block;
		ByteBuffer buffer = ByteBuffer.wrap(new byte[Constants.BLOCK_SIZE]);
		bfile.readBlock(block_no, buffer);
		int offset = (row_no%rows_per_block)*schema.getTupleSizeInBytes();
		buffer.position(offset);
		Tuple t = Tuple.deserialize(schema,  buffer);
		return t;
	}
	
	private void writeTuple(int row_no, Tuple t) {
		int block_no = 2 + row_no/rows_per_block;
		int offset = (row_no%rows_per_block)*schema.getTupleSizeInBytes();
		ByteBuffer buffer = ByteBuffer.wrap(new byte[Constants.BLOCK_SIZE]);
		if (block_no <= bfile.getHighestBlockNo()) {
			bfile.readBlock(block_no, buffer);
		}
		buffer.position(offset);
		t.serialize(buffer);
		bfile.writeBlock(block_no,  buffer);
	}

	public void printDiagnostic() {
		// schema
		System.out.println(schema.toString());
		// bitmap
		bitmap.diagnosticPrint();
		// data blocks
		ByteBuffer buffer = ByteBuffer.wrap(new byte[Constants.BLOCK_SIZE]);
		int row_no=0;

		for (int blockno=2; blockno <= bfile.getHighestBlockNo(); blockno++) {
			int free_rows=0;
			bfile.readBlock(blockno,  buffer);
			System.out.println("DATA BLOCK "+blockno);
			for (int row=0; row< rows_per_block; row++) {
				if (bitmap.getBit(row_no)) {
					if (free_rows>0) {
						if (free_rows ==1) {
							System.out.printf("%5d freespace\n", row_no-1);
							free_rows=0;
						} else {
							// more than 1 free row
							System.out.printf("%5d-%5d freespace\n", row_no-free_rows, row_no-1);
							free_rows=0;
						}
					}
					int offset = row*schema.getTupleSizeInBytes();
					buffer.position(offset);
					Tuple t = Tuple.deserialize(schema,  buffer);
					System.out.printf("%5d %s\n", row_no, t.toString());
				} else {
					free_rows++;
				}
				row_no++;
			}
			if (free_rows>0) {
				if (free_rows ==1) {
					System.out.printf("%5d freespace\n", row_no-1);
					free_rows=0;
				} else {
					// more than 1 free row
					System.out.printf("%5d-%d freespace\n", row_no-free_rows, row_no-1);
				}

			}
		}
		
	}

}
