package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    private static PrgState run(IStmt program) throws MyException {
        PrgState state =PrgState.of( program);
        new Controller(new Repository(state, "test_log.txt"), false).allStep();
        return state;
    }
    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException{
        //init a state and clear the stack to test the error
        PrgState state = PrgState.of(Examples.EX1);
        state.getExeStack().pop();

        Controller controller = new Controller(new Repository(state, "test_log.txt"), false);
        StatementException ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertTrue(ex.getMessage().contains("stack is empty"));
    }

    @TempDir
    Path dir;

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        IStmt program = new PrintStmt(new ValueExp(new IntValue(5)));
        PrgState state = PrgState.of(program);

        Path logPath =  dir.resolve("log.txt");
        Repository repo = new Repository(state, logPath.toString());
        Controller controller = new Controller(repo, false);

        controller.allStep();
        //read the generated log file
        String log = Files.readString(logPath);

        int a = log.indexOf("ExeStack:");
        int b = log.indexOf("SymTable:");
        int c = log.indexOf("Out:");
        int d = log.indexOf("FileTable:");

        assertTrue(0 <= a && a < b && b < c && c < d);
    }
}
