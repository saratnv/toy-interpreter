package interpreter;
import interpreter.controller.Controller;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import interpreter.view.ExitCommand;
import interpreter.view.RunExampleCommand;
import interpreter.view.TextMenu;

public class Main {
    private static Controller makeController(IStmt program, String logFile){
        PrgState state = PrgState.of(program);
        Repository repo = new Repository(state, logFile);
        return new Controller(repo, true);
    }

    public static void main(String[] args) {
        TextMenu menu = new TextMenu();

        menu.addCommand(new ExitCommand("0", "exit"));

        menu.addCommand(new RunExampleCommand("1", "Run Example 1", makeController(Examples.EX1, "log1.txt")));
        menu.addCommand(new RunExampleCommand("2", "Run Example 2", makeController(Examples.EX2, "log2.txt")));
        menu.addCommand(new RunExampleCommand("3", "Run Example 3", makeController(Examples.EX3, "log3.txt")));

        menu.addCommand(new RunExampleCommand("4", "Run file operations example", makeController(Examples.ex4(), "log4.txt")));
        menu.addCommand(new RunExampleCommand("5", "Run relational expression example", makeController(Examples.ex5(), "log5.txt")));

        menu.addCommand(new RunExampleCommand("6", "Run heap allocation example", makeController(Examples.ex6(), "log6.txt")));
        menu.addCommand(new RunExampleCommand("7", "Run heap reading example", makeController(Examples.ex7(), "log7.txt")));
        menu.addCommand(new RunExampleCommand("8", "Run heap writing example", makeController(Examples.ex8(), "log8.txt")));
        menu.addCommand(new RunExampleCommand("9", "Run safe garbage collector example", makeController(Examples.ex9(), "log9.txt")));
        menu.addCommand(new RunExampleCommand("10", "Run while statement example", makeController(Examples.ex10(), "log10.txt")));

        menu.show();
    }
}
