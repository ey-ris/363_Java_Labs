package heapdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {
	
	private Table table;
	private Tuple oldTup, newTup;
	
	@BeforeEach
	void init() {
		// create schema, table and insert rows for tests
		Schema schema = new Schema();  
		schema.addKeyIntType("ID");   // primary key ID int 
		schema.addVarCharType("name", 30);
		schema.addVarCharType("dept_name", 30);
		schema.addIntType("salary");
		Tuple[] tuples = new Tuple[] {
				new Tuple(schema, 22222, "Einstein",    "Physics", 95000),
				new Tuple(schema, 12121, "Wu",          "Finance", 90000),
				new Tuple(schema, 32343, "El Said" ,    "History", 60000),
				new Tuple(schema, 45565, "Katz",        "Comp. Sci.", 75000),
				new Tuple(schema, 98345, "Kim",         "Elec. Eng.", 80000),
				new Tuple(schema, 10101, "Srinivasan" , "Comp. Sci.", 65000),
				new Tuple(schema, 76766, "Crick" ,      "Biology", 72000),
		};
		table = new Table(schema);
        for (Tuple tuple : tuples) {
            table.insert(tuple);
        }
		
		oldTup = new Tuple(schema, 22222, "Einstein",    "Physics", 95000 );
		newTup = new Tuple(schema, 11111, "Molina",      "Music",   70000 );
	}

	@Test
	void insertOneTuple() {
		// insert should succeed when the key value is not already in the table
		boolean insertSucceeded = table.insert(newTup);
		assertTrue(insertSucceeded);
		Tuple t = table.lookup(newTup.get("ID"));
		assertNotNull(t);
	}
	
	@Test
	void insertDuplicateTuple() {
		// insert should fail if the key value *is* already in the table
		boolean insertSucceeded = table.insert(oldTup);
		assertFalse(insertSucceeded);
	}
	
	@Test
	void lookupExistingTuple() {
		// lookup by ID should succeed if the ID is in the table
		int ID = oldTup.getInt(0);
		ITable tuples = table.lookup("ID", ID);
		assertEquals( 1, tuples.size(), "lookup result should be 1 tuple.");
		Tuple tuple = tuples.iterator().next();
		assertEquals( ID, tuple.getInt(0), "incorrect tuple returned.");
	}
	
	@Test
	void lookupMissingTuple() {
		// lookup by ID should fail if the ID is not in the table
		ITable tuples = table.lookup("ID", 11111);
		assertEquals( 0, tuples.size(), "lookup result should be empty.");
	}
	
	@Test
	void lookupNonKeyColumn() {
		// lookup by ID should fail if the ID is not in the table
		ITable tuples = table.lookup("dept_name", "Comp. Sci.");
		assertEquals(2, tuples.size(), "Table lookup should return 2 tuples.");
	}
  
  	@Test
	void schemaWithoutKeyTest() {
		// a primary key column is optional for a schemas
		Schema s = new Schema();
		s.addIntType("cola");
		s.addIntType("colb");
		Table t = new Table(s);
		t.insert( new Tuple(s, 5, 10));
		t.insert( new Tuple(s, 5, 11));
		assertEquals(2, t.size());
		ITable result = t.lookup("cola", 5);
		assertEquals(2, result.size());
	}
	
	/*
	 * table insert should add a copy of the tuple to the ArrayList of Tuples.
	 * this is to prevent modification of the tuple after insert.
	 */
	@Test
	void copyTupleTest() {
		Schema s = new Schema();
		s.addKeyIntType("keya");
		s.addIntType("colb");
		Table t = new Table(s);
		t.insert( new Tuple(s, 5, 10));
		Tuple tuple =  new Tuple(s, 6, 10);
		t.insert( tuple);
		tuple.set(1, 12);
		assertEquals(2, t.size());
		ITable result = t.lookup("colb", 10);
		assertEquals(2, result.size(), "Table insert method must add a copy of tuple to arraylist.");
	}
}
