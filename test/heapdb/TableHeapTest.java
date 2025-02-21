package heapdb;

import java.util.Iterator;


import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableHeapTest {

	private ITable table;
	private Schema schema;

	@BeforeEach
	void init() {
		// create schema, table and test data 
		schema = new Schema();
		schema.addKeyIntType("ID"); // primary key ID int
		schema.addVarCharType("name", 30);
		schema.addVarCharType("dept_name", 30);
		schema.addIntType("salary");
		Tuple[] tuples = new Tuple[] { new Tuple(schema, 22222, "Einstein", "Physics", 95000),
				new Tuple(schema, 12121, "Wu", "Finance", 90000), 
				new Tuple(schema, 32343, "El Said", "History", 60000),
				new Tuple(schema, 45565, "Katz", "Comp. Sci.", 75000),
				new Tuple(schema, 98345, "Kim", "Elec. Eng.", 80000),
				new Tuple(schema, 10101, "Srinivasan", "Comp. Sci.", 65000),
				new Tuple(schema, 76766, "Crick", "Biology", 72000), };
		table = new TableHeap("test.db", schema);
		for (int i = 0; i < tuples.length; i++) {
			table.insert(tuples[i]);
		}
		
	}
	
	@AfterEach
	void cleanup() {
		table.close();
	}

	@Test
	void insertOneTuple() {
		Tuple newTup = new Tuple(schema, 11111, "Molina", "Music", 70000);
		// insert should succeed if the key value is not already in the table
		boolean insertSucceeded = table.insert(newTup);
		assertTrue(insertSucceeded);
		Tuple t = table.lookup(11111);
		assertNotNull(t);
		
	}

	@Test
	void insertDuplicateTuple() {
		Tuple oldTup = new Tuple(schema, 22222, "Einstein", "Physics", 95000);
		// insert should fail if the key value *is* already in the table
		boolean insertSucceeded = table.insert(oldTup);
		assertFalse(insertSucceeded);
	}

	@Test
	void lookupExistingTuple() {
		 
		// lookup by ID should succeed if the ID is in the table
		ITable tuples = table.lookup("ID", 22222);
		assertEquals( 1, tuples.size(), "lookup result should be 1 tuple.");
		Iterator<Tuple> iterator = tuples.iterator();
		assertTrue(iterator.hasNext());
		Tuple tuple = tuples.iterator().next();
		assertEquals( 22222, tuple.getInt(0), "incorrect tuple returned.");
		 
	}
	
	@Test
	void lookupMissingTuple() {
		 
		// lookup by ID should fail if the ID is not in the table
		ITable tuples = table.lookup("ID", 11111);
		assertEquals( 0, tuples.size(), "lookup result should be empty.");
		 
	}
	
	@Test
	void deleteTest() {
		// delete row by primary key.
		Tuple t = table.lookup(22222);
		assertNotNull(t);
		boolean rc = table.delete(22222);
		assertTrue(rc);
		t = table.lookup(22222);
		assertNull(t);
		 
	}
	
	@Test
	void deleteAllTest() {
		// delete all rows
		for (Tuple t: table) {
			table.delete(t.getKey());
		}
		assertEquals(0, table.size());
		 
	}
	
	@Test
	void lookupNonKeyColumn() {
		// lookup by ID should fail if the ID is not in the table
		ITable tuples = table.lookup("dept_name", "Comp. Sci.");
		assertEquals( 2, tuples.size(), "Table lookup should return 2 tuples.");
		 
	}
	
  	@Test
	void schemaWithoutKeyTest() {
		// primary key column is optional 
  		table.close();
		Schema s = new Schema();
		s.addIntType("cola");
		s.addIntType("colb");
		table = new TableHeap("test.db", s);
		table.insert( new Tuple(s, 5, 10));
		table.insert( new Tuple(s, 5, 11));
		assertEquals(2, table.size());
		ITable result = table.lookup("cola", 5);
		assertEquals(2, result.size());
	}

}
