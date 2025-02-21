package heapdb;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class IndexPerformance {
	
	@Test
	public void lookupTime() {
		
		/*
		 * create table with data
		 * compare time to do lookups without index to 
		 * lookups using index
		 * if index is working correctly, the time 
		 * for lookup with index should be significantly less.
		 */
		
		int numRecords = 10000;
		int numLookups = 5000;
		long time0, time1;

		Schema s = new Schema();
		s.addKeyIntType("ID");
		s.addVarCharType("name", 10 );
		s.addVarCharType("dept_name", 20 );
		s.addIntType("salary");
		
		TableHeap table = new TableHeap("insertMany", s);

		String[] depts = {"Biology", "Chemistry", "Physics", "Comp. Sci.", "Mathematics", "Statistics", "Pschology", "Spanish", "Latin", "Greek", 
				"English", "Japanese", "Chinese", "Communication", "Cinematography", "Art"};
		Tuple t = new Tuple(s);
		Random gen = new Random(123789);

		for (int rkey=1; rkey <= numRecords; rkey++) {
			t.set(0, rkey);
			t.set(1, "Name"+rkey);
			t.set(2, depts[gen.nextInt(depts.length)]);  // pick random department
			t.set(3, gen.nextInt(50000)+50000);  // random salary
			boolean result = table.insert(t);
			assertTrue(result);
		}
		
		table.createIndex("ID");
		time0 = System.currentTimeMillis();
		
		for (int i=1; i<=numLookups; i++) {
			int key = gen.nextInt(numRecords)+1;
			Tuple r = table.lookup(key);
			assertNotNull(r);
		}
		
		time1 = System.currentTimeMillis();
		
		System.out.printf("elapsed time for %d lookups table size %d using index %.2f secs. \n" , numLookups, numRecords, (time1-time0)/1000. );
		
		table.dropIndex("ID");

		
		for (int i=1; i<=numLookups; i++) {
			int key = gen.nextInt(numRecords)+1;
			Tuple r = table.lookup(key);
			assertNotNull(r);
		}
		
		long time2 = System.currentTimeMillis();
		
		System.out.printf("elapsed time for %d lookups table size %d no index %.2f secs. \n" , numLookups, numRecords, (time2-time1)/1000. );

		long ratio = (time2-time1)/(time1-time0);
		System.out.println("Improvement ratio "+ratio);
		assertTrue( ratio > 100, "index time has not improved.  Double check that TableHeap uses index on lookup methods.");
	
	}

}
