package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    private static PrgState run(IStmt program) throws MyException {
        PrgState state =PrgState.of( program);
        new Controller(new Repository(state), false).allStep();
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

        Controller controller = new Controller(new Repository(state), false);
        StatementException ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertTrue(ex.getMessage().contains("stack is empty"));
    }
}
