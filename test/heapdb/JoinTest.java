package heapdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import heapdb.query.SelectQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JoinTest {
	
	private Table instTable, deptTable;
	private Schema instSchema, deptSchema;

	@BeforeEach
	void init() {

		// create schemas, tables and insert rows for test
		instSchema = new Schema();  // primary key ID int 
		instSchema.addKeyIntType("ID");
		instSchema.addVarCharType("name", 30);
		instSchema.addVarCharType("dept_name", 15);
		instSchema.addIntType("salary");
		Tuple[] tuples = new Tuple[]{
				new Tuple(instSchema, 22222, "Einstein", "Physics", 95000),
				new Tuple(instSchema, 12121, "Wu", "Finance", 90000),
				new Tuple(instSchema, 32343, "El Said", "History", 60000),
				new Tuple(instSchema, 45565, "Katz", "Comp. Sci.", 75000),
				new Tuple(instSchema, 98345, "Kim", "Elec. Eng.", 80000),
				new Tuple(instSchema, 10101, "Srinivasan", "Comp. Sci.", 65000),
				new Tuple(instSchema, 76766, "Crick", "Biology", 72000),
		};
		instTable = new Table(instSchema);
		for (int i = 0; i < tuples.length; i++) {
			instTable.insert(tuples[i]);
		}

		// department table
		deptSchema = new Schema();
		deptSchema.addKeyVarCharType("dept_name", 15);
		deptSchema.addVarCharType("building", 12);
		deptSchema.addIntType("budget");

		deptTable = new Table(deptSchema);
		Tuple[] deptTuples = new Tuple[]{
				new Tuple(deptSchema, "Biology", "Watson", 90000),
				new Tuple(deptSchema, "Comp. Sci.", "Taylor", 100000),
				new Tuple(deptSchema, "Elec. Eng.", "Taylor", 85000),
				new Tuple(deptSchema, "Finance", "Painter", 120000),
				new Tuple(deptSchema, "Music", "Packard", 80000),
				new Tuple(deptSchema, "History", "Painter", 50000),
				new Tuple(deptSchema, "Physics", "Watson", 70000),
		};

		for (int i = 0; i < deptTuples.length; i++) {
			deptTable.insert(deptTuples[i]);
		}
	}

	
	@Test
	void singleIntPrimaryKey() {
		
		// try to insert tuple with same ID as Einstein
		Tuple dupTuple = new Tuple(instSchema, 22222, "Royce", "Physics", 85000);
		int n = instTable.size();
		boolean result = instTable.insert(dupTuple);
		assertTrue((!result) && instTable.size() == n);
	}
	
	@Test
	void singleStringPrimaryKey() {
		
		// try to insert a duplicate Physics department.
		Tuple dupTuple = new Tuple(deptSchema, "Physics", "Champman", 120000 );
		int n = deptTable.size();
		boolean result = deptTable.insert(dupTuple);
		assertTrue(!result && deptTable.size() == n);
	}

	
	@Test
	void schemaJoin() {
		// test of Schema natural join 
		// result schema should have 6 columns 
		Schema joined = instSchema.naturaljoin(deptSchema);
		assertTrue(joined != null && joined.size() == 6);
	}
	
	@Test
	void tableJoin() {
		// test of natural join of two tables
		Table joined = SelectQuery.naturalJoin(instTable, deptTable);
		assertTrue(joined != null && joined.size() == instTable.size());
	}

	@Test
	void tableJoinMultipleColumns() {
		Schema schema1 = new Schema();
		schema1.addIntType("cola");
		schema1.addIntType("colb");
		schema1.addIntType("colc");
		schema1.addIntType("cold");
		Schema schema2 = new Schema();
		schema2.addIntType("cola");
		schema2.addIntType("cole");
		schema2.addIntType("colf");
		schema2.addIntType("colb");
		Table t1 = new Table(schema1);
		t1.insert(new Tuple(schema1, 10, 11, 12, 13));
		t1.insert(new Tuple(schema1, 10, 20, 21, 22));
		t1.insert(new Tuple(schema1, 30, 11, 12, 13));
		Table t2 = new Table(schema2);
		t2.insert(new Tuple(schema2, 10, 11, 12, 13));
		t2.insert(new Tuple(schema2, 10, 23, 24, 20));
		t2.insert(new Tuple(schema2, 10, 25, 25, 20));
		t2.insert(new Tuple(schema2, 40, 41, 42, 43));
		t2.insert(new Tuple(schema2, 0, 12, 23, 11));
		Table join = SelectQuery.naturalJoin(t1,t2);
		assertEquals(2, join.size());
		for (Tuple t: join){
			assertEquals(10, t.get("cola"));
			assertEquals(20, t.get("colb"));
		}
	}
}