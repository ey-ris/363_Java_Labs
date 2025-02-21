package heapdb;

import heapdb.query.Condition;
import heapdb.query.EqCondition;
import heapdb.query.SelectQuery;

public class MainApp {
    public static void main(String[] args) {
        // Step 1: Define the schema for the Instructor table
        Schema instructorSchema = new Schema();
        instructorSchema.addKeyIntType("ID");
        instructorSchema.addVarCharType("name", 25);
        instructorSchema.addVarCharType("dept_name", 25);
        instructorSchema.addIntType("salary");

        // Step 2: Create a Table using the defined schema
        Table table = new Table(instructorSchema);

        // Step 3: Insert 5 rows into the table
        table.insert(new Tuple(instructorSchema, 12121, "Kim", "Elect. Engr.", 65000));
        table.insert(new Tuple(instructorSchema, 19803, "Wisneski", "Comp. Sci.", 46000));
        table.insert(new Tuple(instructorSchema, 24734, "Bruns", "Comp. Sci.", 70000));
        table.insert(new Tuple(instructorSchema, 55552, "Scott", "Math", 80000));
        table.insert(new Tuple(instructorSchema, 12321, "Tao", "Comp. Sci.", 95000));

        // Step 4: Print the table after inserting 5 rows
        System.out.println("Table after inserting 5 instructors:");
        System.out.println(table);

        // Step 5: Delete an instructor (e.g., ID = 12121) and print the table again
        boolean deleted = table.delete(12121);
        System.out.println("\ndelete 12121:" + deleted);
        System.out.println(table);

        // Step 6: Attempt to delete the same row again and print the result
        boolean deleteAgain = table.delete(12121);
        System.out.println("\ndelete 12121:" + deleteAgain);

        // Step 7: Lookup using SelectQuery and Condition (ID = 19803)
        Condition condition1 = new EqCondition("ID", 19803);
        SelectQuery selectQuery1 = new SelectQuery(condition1);
        ITable foundTable1 = selectQuery1.eval(table);  // Use eval() on the table
        System.out.println("\nlookup 19803 (SelectQuery):");
        System.out.println(foundTable1);  // Print the resulting table

        // Lookup using SelectQuery and Condition (ID = 12121 - not found)
        Condition condition2 = new EqCondition("ID", 12121);
        SelectQuery selectQuery2 = new SelectQuery(condition2);
        ITable notFoundTable2 = selectQuery2.eval(table);
        System.out.println("\nlookup 12121 (SelectQuery):");
        System.out.println(notFoundTable2); // Print the resulting table (should be empty)

        // Step 8: Demonstrate lookup using column evaluation with SelectQuery
        Condition condition3 = new EqCondition("dept_name", "Comp. Sci.");
        SelectQuery selectQuery3 = new SelectQuery(condition3);
        ITable compSciDept = selectQuery3.eval(table);
        System.out.println("\neval dept_name=Comp. Sci. (SelectQuery):");
        System.out.println(compSciDept);

        Condition condition4 = new EqCondition("ID", 19803);
        SelectQuery selectQuery4 = new SelectQuery(condition4);
        ITable idLookup = selectQuery4.eval(table);
        System.out.println("\neval ID=19803 (SelectQuery):");
        System.out.println(idLookup);

        Condition condition5 = new EqCondition("ID", 19802);
        SelectQuery selectQuery5 = new SelectQuery(condition5);
        ITable idNotFound = selectQuery5.eval(table);
        System.out.println("\neval ID=19802 (SelectQuery):");
        System.out.println(idNotFound);
    }
}