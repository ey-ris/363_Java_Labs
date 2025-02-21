package heapdb;

import java.util.ArrayList;

public interface Index {
	
	/*
	 * insert (key, row_no) entry  
	 * return true
	 * return false if unsuccessful
	 */
	boolean insert(int key, int row_no);
	
	/*
	 * delete (key, row_no) entry.
	 * return true
	 * return false if not found.
	 */
	boolean delete(int key, int row_no);
	
	/*
	 * unique key lookup
	 * if key is not found, return -1
	 */
	int lookupOne(int key);
	
	/*
	 * non-unique key lookup
	 * if key not found, return empty ArrayList
	 */
	ArrayList<Integer> lookupMany(int key);

}
