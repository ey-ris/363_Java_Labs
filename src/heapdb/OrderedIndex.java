package heapdb;

import java.util.ArrayList;
import java.util.Collections;

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
		int index = searchGE(key); // Find where to insert

		if (index < entries.size() && entries.get(index).key == key) {
			// Key exists, add row_no and keep the list sorted
			IndexEntry entry = entries.get(index);
			entry.rows.add(row_no);
			Collections.sort(entry.rows); // Maintain sorted order of row numbers
		} else {
			// Key doesn't exist, create a new entry and insert it at the correct position
			IndexEntry newEntry = new IndexEntry();
			newEntry.key = key;
			newEntry.rows.add(row_no);
			entries.add(index, newEntry); // Insert at the correct sorted position
		}
		return true;
	}

	@Override
	public int lookupOne(int key) {
		int index = searchEQ(key);
		if (index != -1) {
			return entries.get(index).rows.get(0); // Return the first row number
		}
		return -1;
	}

	@Override
	public ArrayList<Integer> lookupMany(int key) {
		int index = searchEQ(key);
		if (index != -1) {
			return entries.get(index).rows;
		}
		return new ArrayList<>(); // Return an empty list if key doesn't exist
	}

	@Override
	public boolean delete(int key, int row_no) {
		int index = searchEQ(key);
		if (index == -1) {
			return false; // Key not found
		}

		IndexEntry entry = entries.get(index);
		if (entry.rows.remove(Integer.valueOf(row_no))) { // Use Integer.valueOf() to remove by value
			if (entry.rows.isEmpty()) {
				entries.remove(index); // Delete the entry if the list is empty
			}
			return true;
		}

		return false; // Row number not found for the key
	}

	private int searchEQ(int key) {
		int low = 0;
		int high = entries.size() - 1;

		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (entries.get(mid).key == key) {
				return mid;
			} else if (entries.get(mid).key < key) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		return -1; // Key not found
	}

	private int searchGE(int key) {
		int low = 0;
		int high = entries.size() - 1;

		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (entries.get(mid).key >= key) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return low; // Index where the key should be inserted or the index of the first greater element
	}
	
	public void diagnosticPrint() {
		System.out.println(name);
		for (IndexEntry entry : entries) {
			System.out.printf("%d, %s\n", entry.key, entry.rows.toString());
		}
	}

}
