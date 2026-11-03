package interpreter;
import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import interpreter.view.Examples;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println("\nTOY INTERPRETER MENU");
            System.out.println("1. Run Example 1: " + Examples.EX1);
            System.out.println("2. Run Example 2: " + Examples.EX2);
            System.out.println("3. Run Example 3: " + Examples.EX3);
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            if (!scanner.hasNextInt()){
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            if(choice == 0){
                System.out.println("Exiting...");
                break;
            }

            IStmt selectedProgram = null;
            switch(choice){
                case 1 -> selectedProgram = Examples.EX1;
                case 2 -> selectedProgram = Examples.EX2;
                case 3 -> selectedProgram = Examples.EX3;
                default -> System.out.println("Invalid choice. Please try again.");
            }

            if(selectedProgram != null){
                runProgram(selectedProgram);
            }

        }

        scanner.close();
    }

    private static void runProgram(IStmt program){
        try{
            //each run gets a fresh memory state
            PrgState state =PrgState.of( program);
            Repository repo = new Repository(state);
            Controller controller = new Controller(repo, true);

            System.out.println("\n - EXECUTION TRACE -");
            controller.allStep();
            System.out.println(" - EXECUTION FINISHED - ");

        }catch (MyException e){
            System.out.println("Interpreter error: " + e.getMessage());
        } catch (Exception e){
            System.out.println("Unexpected error: " + e.getMessage());
        }

    }
}
