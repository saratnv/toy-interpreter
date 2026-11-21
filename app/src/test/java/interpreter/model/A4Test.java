package interpreter.model;

import interpreter.controller.Controller;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.exp.*;
import interpreter.model.stmt.*;
import interpreter.model.type.*;
import interpreter.model.value.*;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
class A4Test {
    @TempDir
    Path tempDir;

    private PrgState runProgram(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        Repository repo = new Repository(state, tempDir.resolve("test.log").toString());
        Controller controller = new Controller(repo, false);
        controller.allStep();
        return state;
    }
}
