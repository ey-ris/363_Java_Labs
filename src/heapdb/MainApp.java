package heapdb;

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

        // Step 7: Lookup an existing row using ID = 19803 and a non-existing ID = 12121
        Tuple foundTuple = table.lookup(19803);
        System.out.println("\nlookup 19803: " + (foundTuple != null ? foundTuple : "null"));

        Tuple notFoundTuple = table.lookup(12121);
        System.out.println("lookup 12121: " + (notFoundTuple != null ? notFoundTuple : "null"));

        // Step 8: Demonstrate lookup using column evaluation (e.g., department name)
        System.out.println("\neval dept_name=Comp. Sci.");
        Table compSciDept = (Table) table.lookup("dept_name", "Comp. Sci.");
        System.out.println(compSciDept);

        System.out.println("\neval ID=19803");
        Table idLookup = (Table) table.lookup("ID", 19803);
        System.out.println(idLookup);

        System.out.println("\neval ID=19802");
        Table idNotFound = (Table) table.lookup("ID", 19802);
        System.out.println(idNotFound);
    }
}