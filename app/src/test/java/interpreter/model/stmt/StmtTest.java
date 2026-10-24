package interpreter.model.stmt;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.*;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.print.PrinterGraphics;
import java.lang.ref.PhantomReference;
import java.sql.Statement;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class StmtTest {
    private IStack<IStmt> exeStack;
    private IDictionary<String, IValue> symTable;
    private IList<IValue> out;
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        exeStack = new MyStack<>();
        symTable = new MyDictionary<>();
        out = new MyList<>();
        state = new PrgState(exeStack, symTable,out, new NopStmt());
        exeStack.pop();
    }

    //COMP

    @Test
    void testCompStmtPushesInCorrectOrder() throws MyException {
        IStmt s1 = new NopStmt();
        IStmt s2 = new NopStmt();
        IStmt comp = new CompStmt(s1,s2);

        comp.execute(state);

        assertEquals(s1, exeStack.pop());
        assertEquals(s2, exeStack.pop());
    }

    //ASIIGN

    @Test
    void testAssignStmtValid() throws MyException {
        symTable.put("v",new IntValue(0));
        IStmt assign = new AssignStmt("v", new ValueExp(new IntValue(5)));

        assign.execute(state);
        assertEquals(new IntValue(5), symTable.lookup("v"));
    }

    @Test
    void testAssignStmtUndeclaredThrows(){
        IStmt assign = new AssignStmt("v", new ValueExp(new IntValue(5)));
        StatementException ex = assertThrows(StatementException.class,()->assign.execute(state));
        assertTrue(ex.getMessage().contains("was not declared"));
    }

    @Test
    void testAssignStmtTypeMismatch() throws MyException {
        symTable.put("v",new IntValue(0));
        IStmt assign = new AssignStmt("v", new ValueExp(new BoolValue(true)));
        StatementException ex = assertThrows(StatementException.class,()->assign.execute(state));
        assertTrue(ex.getMessage().contains("do not match"));
    }

    //VARDECL

    @Test
    void testVarDeclStmtValid() throws MyException {
        IStmt decl = new VarDeclStmt("v", new IntType());
        decl.execute(state);
        assertTrue(symTable.isDefined("v"));
        assertEquals(new IntValue(0), symTable.lookup("v"));
    }

    @Test
    void testVarDeclStmtRedeclaredThrows(){
        symTable.put("v",new IntValue(0));
        IStmt decl = new VarDeclStmt("v", new IntType());
        StatementException ex = assertThrows(StatementException.class,()->decl.execute(state));
        assertTrue(ex.getMessage().contains("already declared"));
    }

    //PRINT

    @Test
    void testPrintStmt() throws MyException {
        IStmt print = new PrintStmt(new ValueExp(new IntValue(10)));
        print.execute(state);
        assertEquals(new IntValue(10), out.get(0));
    }

    //IF

    @Test
    void testIfStmtTrueCondition() throws MyException {
        IStmt thenS = new VarDeclStmt("a", new IntType());
        IStmt elseS = new NopStmt();
        IStmt ifStmt = new IfStmt(new ValueExp(new BoolValue(true)),thenS,elseS);

        ifStmt.execute(state);

        assertEquals(thenS, exeStack.pop());
    }

    @Test
    void testIfStmtFalseCondition() throws MyException {
        IStmt thenS = new VarDeclStmt("a", new IntType());
        IStmt elseS = new NopStmt();
        IStmt ifStmt = new IfStmt(new ValueExp(new BoolValue(false)),thenS,elseS);

        ifStmt.execute(state);

        assertEquals(elseS, exeStack.pop());
    }

    @Test
    void testIfStmtNonBooleanThrows() throws MyException {
        IStmt ifStmt = new IfStmt(new ValueExp(new IntValue(1)), new NopStmt(), new NopStmt());

        StatementException ex = assertThrows(StatementException.class,()->ifStmt.execute(state));
        assertTrue(ex.getMessage().contains("not a boolean"));
    }

    @Test
    void testNopStmtLeavesStateUnchanged() throws MyException {
        IStmt nopStmt = new NopStmt();
        nopStmt.execute(state);
        assertTrue(exeStack.isEmpty());
    }
}
