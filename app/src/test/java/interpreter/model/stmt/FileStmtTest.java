package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private ValueExp file;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");

        file = new ValueExp(new StringValue(f.toString()));
        state = PrgState.of(new NopStmt());

        new VarDeclStmt("x", new IntType()).execute(state);
        new VarDeclStmt("s", new StringType()).execute(state);
    }

    @Test
    void readGivesTheNextNumberAndThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));

        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,() -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void openingMissingFileThrows() {
        ValueExp missingFile  = new ValueExp(new StringValue(dir.resolve("missing.in").toString()));
        assertThrows(StatementException.class,() -> new OpenRFileStmt(missingFile).execute(state));
    }

    @Test
    void readFileVariableNotDeclaredThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,() -> new ReadFileStmt(file, "y").execute(state));
    }

    @Test
    void readFileVariablNotIntThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class,() -> new ReadFileStmt(file, "s").execute(state));
    }

    @Test
    void closeRemovesFileFromTable() throws MyException{
        new OpenRFileStmt(file).execute(state);
        StringValue fileName = (StringValue) file.eval(state.getSymTable());
        assertTrue(state.getFileTable().isDefined(fileName));

        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void closeUnopenedFileThrows(){
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @AfterEach
    void tearDown() {
        state.getFileTable().toMap().values().forEach(reader ->{
            try {
                reader.close();
            } catch (Exception ignored) {}
        });
    }
}
