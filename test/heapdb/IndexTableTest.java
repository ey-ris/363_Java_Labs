package heapdb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IndexTableTest {
	
	private TableHeap table;
	private Schema schema;

	@BeforeEach
	void init() {
		schema = new Schema();
		schema.addKeyIntType("ID"); // primary key ID int
		schema.addVarCharType("name", 30);
		schema.addVarCharType("dept_name", 30);
		schema.addIntType("salary");
		Tuple[] tuples = new Tuple[] { new Tuple(schema, 22222, "Einstein", "Physics", 95000),
				new Tuple(schema, 12121, "Wu", "Finance", 90000), 
				new Tuple(schema, 32343, "El Said", "History", 60000),
				new Tuple(schema, 45565, "Katz", "Comp. Sci.", 72000),
				new Tuple(schema, 98345, "Kim", "Elec. Eng.", 80000),
				new Tuple(schema, 10101, "Srinivasan", "Comp. Sci.", 65000),
				new Tuple(schema, 76766, "Crick", "Biology", 72000), };
		table = new TableHeap("test.db", schema);
		for (int i = 0; i < tuples.length; i++) {
			table.insert(tuples[i]);
		}
		table.createIndex("ID");
		table.createIndex("salary");
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
		ITable tuples = table.lookup("ID", 22222);
		assertEquals( 1, tuples.size(), "lookup result should be 1 tuple.");
		Tuple t = table.lookup(22222);
		assertNotNull(t);
	}
	
	@Test
	void lookupMissingTuple() {
		table.createIndex("ID");
		ITable tuples = table.lookup("ID", 11111);
		assertEquals(0, tuples.size(), "lookup result should be empty.");
		Tuple t = table.lookup(11111);
		assertNull(t);
	}
	
	@Test
	void lookupNonKeyColumn() {
		ITable tuples = table.lookup("salary", 72000);
		assertEquals(2, tuples.size(), "Table lookup should return 2 tuples.");
		tuples = table.lookup("salary", 71100);
		assertEquals(0, tuples.size(), "Expected result set size should be 0.");
	}

}

