package interpreter.model;

import interpreter.controller.Controller;
import interpreter.exceptions.ADTException;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyHeap;
import interpreter.model.exp.*;
import interpreter.model.stmt.*;
import interpreter.model.type.*;
import interpreter.model.value.*;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Ref;

import static org.junit.jupiter.api.Assertions.*;

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

    //REFERENCE TYPES----------------------------------
    @Test
    void testRefTypDefaultValueAndEquals() throws MyException{
        RefType refInt = new RefType(new IntType());
        RefType refRefInt1 = new RefType(new RefType( new IntType() ));
        RefType refRefInt2 = new RefType(new RefType( new IntType() ));

        assertEquals(new RefValue(0, new IntType()), refInt.defaultValue());
        assertEquals(refRefInt1, refRefInt2);
        assertNotEquals(refRefInt1, refInt);

        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", refInt). execute(state);
        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("v"));
    }

    //HEAP----------------------------------------------
    void testHeapAllocateAndMissingAddressThrows() throws MyException{
        IHeap heap = new MyHeap();
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));

        assertEquals(new IntValue(20), heap.get(2));
        heap.update(2, new IntValue(99));
        assertEquals(new IntValue(99), heap.get(2));

        assertThrows(ADTException.class, () -> heap.get(999));
        assertThrows(ADTException.class, () -> heap.update(999, new IntValue(1)));
    }

    //NewStmt----------------------------------------------
    @Test
    void testNewAllocatesAndUpdatesVariables() throws MyException{
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void testNewErrors() throws MyException{
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);

        // Variable not declared
        assertThrows(StatementException.class,
                () -> new NewStmt("missing", new ValueExp(new IntValue(10))).execute(state));

        // Variable not a reference
        assertThrows(StatementException.class,
                () -> new NewStmt("x", new ValueExp(new IntValue(10))).execute(state));

        // Value of wrong type
        assertThrows(StatementException.class,
                () -> new NewStmt("v", new ValueExp(new BoolValue(true))).execute(state));
    }


    //ReadHeadExp-----------------------------------------
    @Test
    void testReadHeapAndErrors() throws MyException{
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new VarDeclStmt("nullRef", new RefType(new IntType())).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(42))).execute(state);

        // Valid read
        assertEquals(new IntValue(42),
                new ReadHeapExp(new VarExp("v")).eval(state.getSymTable(), state.getHeap()));

        // Non-reference expression
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new ValueExp(new IntValue(10))).eval(state.getSymTable(), state.getHeap()));

        // Address not in heap (0 / null)
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("nullRef")).eval(state.getSymTable(), state.getHeap()));
    }

    //WriteHeapStmt
    @Test
    void testWriteHeapAndErrors() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);
        new VarDeclStmt("nullRef", new RefType(new IntType())).execute(state);
        new NewStmt("v", new ValueExp(new IntValue(20))).execute(state);

        // Valid write
        new WriteHeapStmt("v", new ValueExp(new IntValue(30))).execute(state);
        assertEquals(new IntValue(30), state.getHeap().get(1));

        // Variable not declared
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("missing", new ValueExp(new IntValue(10))).execute(state));

        // Variable not a reference
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("x", new ValueExp(new IntValue(10))).execute(state));

        // Address not in heap (0 / null)
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("nullRef", new ValueExp(new IntValue(10))).execute(state));

        // Value of wrong type
        assertThrows(StatementException.class,
                () -> new WriteHeapStmt("v", new ValueExp(new BoolValue(false))).execute(state));
    }

    //WhileStmt----------------------------------------------------
    @Test
    void testWhileRunsZeroAndOneTime() throws MyException {
        // 0 iterations: int v; v = 0; while (v > 0) (print(v); v = v - 1)
        IStmt zeroTimes = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new WhileStmt(
                        new RelationalExp(RelOp.GT, new VarExp("v"), new ValueExp(new IntValue(0))),
                        new CompStmt(
                                new PrintStmt(new VarExp("v")),
                                new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1))))
                        )
                )
        );
        PrgState state0 = runProgram(zeroTimes);
        assertEquals("[]", state0.getOut().toString());
        assertEquals(new IntValue(0), state0.getSymTable().lookup("v"));

        // 1 iteration: int v; v = 1; while (v > 0) (print(v); v = v - 1)
        IStmt oneTime = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ValueExp(new IntValue(1))),
                        new WhileStmt(
                                new RelationalExp(RelOp.GT, new VarExp("v"), new ValueExp(new IntValue(0))),
                                new CompStmt(
                                        new PrintStmt(new VarExp("v")),
                                        new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1))))
                                )
                        )
                )
        );
        PrgState state1 = runProgram(oneTime);
        assertEquals("[1]", state1.getOut().toString());
        assertEquals(new IntValue(0), state1.getSymTable().lookup("v"));
    }

    @Test
    void testWhileNonBooleanConditionThrows() {
        IStmt invalidWhile = new WhileStmt(new ValueExp(new IntValue(1)), new NopStmt());
        assertThrows(StatementException.class, () -> runProgram(invalidWhile));
    }

    //Controller----------------------------------------------------
    @Test
    void testEx6HeapAllocation() throws MyException {
        PrgState state = runProgram(Examples.ex6());
        assertEquals("[(1, int), (2, Ref(int))]", state.getOut().toString());
        assertEquals(new IntValue(20), state.getHeap().getContent().get(1));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().getContent().get(2));
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new RefValue(2, new RefType(new IntType())), state.getSymTable().lookup("a"));
    }

    @Test
    void testEx7HeapReading() throws MyException {
        PrgState state = runProgram(Examples.ex7());
        assertEquals("[20, 25]", state.getOut().toString());
    }

    @Test
    void testEx8HeapWriting() throws MyException {
        PrgState state = runProgram(Examples.ex8());
        assertEquals("[20, 35]", state.getOut().toString());
        assertEquals(new IntValue(30), state.getHeap().getContent().get(1));
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void testEx9SafeGarbageCollector() throws MyException {
        PrgState state = runProgram(Examples.ex9());
        assertEquals("[20]", state.getOut().toString());
    }

    @Test
    void testEx10WhileMultipleIterations() throws MyException {
        PrgState state = runProgram(Examples.ex10());
        assertEquals("[4, 3, 2, 1, 0]", state.getOut().toString());
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

}
