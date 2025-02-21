package heapdb.query;


import java.util.ArrayList;
import java.util.List;

import heapdb.ITable;
import heapdb.Schema;
import heapdb.Table;
import heapdb.Tuple;

import static heapdb.Tuple.joinTuple;

/**
 * A simple select query of the form:
 * select column, column, . . . from table where condition
 * 
 * @author Glenn
 *
 */

public class SelectQuery  {
	
	private Condition cond;
	private String[] colNames;	   // a value of null means return all columns of the table
	
	/**
	 * A query that contains both a where condition and a projection of columns
	 * @param colNames are the columns to return
	 * @param cond is the where clause
	 */
	public SelectQuery(String[] colNames, Condition cond) {
		this.colNames = colNames;
		this.cond = cond;
	}
	
	/**
	 * A query that contains both a where condition.  All columns 
	 * of the Tuples are returned.
	 * @param cond is the where clause
	 */
	public SelectQuery(Condition cond) {
		this(null, cond);
	}
	
	
	public static Table naturalJoin(ITable table1, ITable table2) {
		Schema resultSchema = table1.getSchema().naturaljoin(table2.getSchema());
		Table result = new Table(resultSchema);

		List<String> joinColumns = new ArrayList<>();

		// Find common columns (join columns)
		for (int i = 0; i < table1.getSchema().size(); i++) {
			String colName1 = table1.getSchema().getName(i);
			if (table2.getSchema().getColumnIndex(colName1) >= 0) {
				joinColumns.add(colName1);
			}
		}

		// Nested loop join
		for (Tuple t1 : table1) {
			for (Tuple t2 : table2) {
				boolean match = true;
				for (String joinColumn : joinColumns) {
					Object value1 = t1.get(joinColumn);
					Object value2 = t2.get(joinColumn);

					if (value1 == null && value2 == null) continue; // handles null == null

					if (value1 == null || value2 == null || !value1.equals(value2)) {
						match = false;
						break;  // No need to check further for this tuple combination
					}
				}

				if (match) {
					Tuple joinedTuple = joinTuple(resultSchema, t1, t2); // Use resultSchema here
					result.insert(joinedTuple);
				}
			}
		}

		return result;
	}
	
	public ITable eval(ITable table) {
		Schema s;
		// if there is a projection operation, make the new schema
		if (colNames==null) s = table.getSchema();
		else s = table.getSchema().project(colNames);
		
		Table result = new Table(s);
		for (Tuple t: table) {
			// if tuple t satisfies the condition, insert t into the result table.
			if (cond.eval(t)) {
				result.insert(t.project(s));
			}
		}
		return result;
	}

	@Override
	public String toString() {
	    String proj_columns;
	    if (colNames != null) {
	    	proj_columns = String.join(",", colNames);
	    } else {
	    	proj_columns = "*";
	    }
	    return "select " + proj_columns + " where " + cond;
	}

}
