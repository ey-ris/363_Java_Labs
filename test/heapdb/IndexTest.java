package heapdb;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

public class IndexTest {
	
	@Test 
	public void testAscendingKeys() {
		OrderedIndex index = new OrderedIndex();
		for (int key=0; key<100; key++ ) {
			boolean b = index.insert(key,  key+100);
			assertTrue(b);
		}
		
		for (int key=0; key<100; key++) {
			int value = index.lookupOne(key);
			assertEquals(100+key, value);
		}	
	}
	
	
	@Test 
	public void testDescendingKeys() {
		OrderedIndex index = new OrderedIndex();
		for (int key=99; key>=0; key-- ) {
			boolean b = index.insert(key,  key+100);
			assertTrue(b);
		}
		
		for (int key=0; key<100; key++) {
			int value = index.lookupOne(key);
			assertEquals(100+key, value);
		}	
	}
	/*
	 * test random inserts and deletes
	 */
	@Test
	public void testIndexUnique() {
		int[] keys = new int[100];
		OrderedIndex index = new OrderedIndex("testIndex");
		Random gen = new Random();
		for (int i=0; i<10000; i++) {
			int key = gen.nextInt(100);
			if (keys[key]==0) {
				keys[key]=1;
				boolean b = index.insert(key, key+1);
				assertTrue(b);
			} else {
				keys[key]=0;
				int value = index.lookupOne(key);
				assertEquals(key+1, value);
				index.delete(key,  key+1);
			}
		}
	}
	
	/*
	 * test random inserts and deletes
	 */
	@Test
	public void testIndexLookUpMany() {
		int[] keys = new int[100];
		OrderedIndex index = new OrderedIndex("testNonUniqueIndex");
		Random gen = new Random();
		for (int i=0; i<10000; i++) {
			// choose random key
			int key = gen.nextInt(100);
			// choose random insert or delete operation
			int op = gen.nextInt(2);
			if (op==0) {
				// do insert
				ArrayList<Integer> rows = index.lookupMany(key);
				assertEquals(keys[key], rows.size());
				index.insert(key, i);
				keys[key]++;
				rows = index.lookupMany(key);
				assertEquals(keys[key], rows.size());				
			} else {
				// do delete
				ArrayList<Integer> rows = index.lookupMany(key);
				assertEquals(keys[key], rows.size());
				if (rows.size()==0) {
					boolean b = index.delete(key, 1);
					assertFalse(b);
				} else {
					boolean b = index.delete(key, rows.get(0));
					assertTrue(b);
					keys[key]--;
					rows = index.lookupMany(key);
					assertEquals(keys[key], rows.size());		
				}
			}
		}
	}

}