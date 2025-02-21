package heapdb;

import java.util.ArrayList;

public class OrderedIndex implements Index {
	
	private String name;
	
	/*
	 * Each index value is represented by an IndexEntry
	 * containing the key value and the list of row numbers.
	 */
	private class IndexEntry {
		int key;
		ArrayList<Integer> rows = new ArrayList<>();
	}
	
	private ArrayList<IndexEntry> entries = new ArrayList<>();
	
	public OrderedIndex() {
		name="";
	}
	
	public OrderedIndex(String indexName) {
		this.name = indexName;
	}
	
	@Override
	public boolean insert(int key, int row_no) {
	
		// TODO 
		
		// add a new IndexEntry or 
		// add the row_no to an existing entry
		
		throw new UnsupportedOperationException();

	}

	@Override
	public int lookupOne(int key) {
		// TO DO 
		// return the row number of the key.
		// -1 if the key does not exist
		// if there are multiple row numbers, return the first one.
		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ArrayList<Integer> lookupMany(int key){
		// TODO 
		// return the list of row number for the key.
		// if the key does not exist, return an empty list.
		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean delete(int key, int row_no) {
		
		// TODO 
		// delete row_number from the list of row numbers for the key
		// if the key is not found, return false
		// if the list of row_number is empty, delete the index entry
		
		throw new UnsupportedOperationException();
	}
	
	/*
	 * return -1 if the key not found in entries
	 * return index of entries equal to key
	 */
	private int searchEQ(int key) {
		// TODO 
		// perform binary search for key value
		throw new UnsupportedOperationException();
	}
	
	/*
	 * return index of entry equal to key 
	 * or where to add the new entry
	 */
	private int searchGE(int key) {
		// TODO 
		// perform binary search 
		throw new UnsupportedOperationException();
	}
	
	
	public void diagnosticPrint() {
		System.out.println(name);
		for (IndexEntry entry : entries) {
			System.out.printf("%d, %s\n", entry.key, entry.rows.toString());
		}
	}

}
