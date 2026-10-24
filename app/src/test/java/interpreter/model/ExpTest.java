package interpreter.model;

import interpreter.exceptions.ADTException;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.exp.*;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void testValueExp() throws MyException {
        IExp exp = new ValueExp(new IntValue(10));
    }

    @Test
    void testVarExpAndUndefinedVariable() throws MyException{
        symTable.put("a", new IntValue(5));
        IExp exp = new VarExp("a");
        assertEquals(new IntValue(5), exp.eval(symTable));

        IExp missing = new VarExp("b");
        assertThrows(ADTException.class,() -> missing.eval(symTable));
    }

    @Test
    void testArithExpEveryOperator() throws MyException {
        IExp e1 = new ValueExp(new IntValue(10));
        IExp e2 = new ValueExp(new IntValue(5));

        assertEquals(new IntValue(15), new ArithExp(ArithOp.ADD, e1, e2).eval(symTable));
        assertEquals(new IntValue(5), new ArithExp(ArithOp.SUB, e1, e2).eval(symTable));
        assertEquals(new IntValue(50), new ArithExp(ArithOp.MUL, e1, e2).eval(symTable));
        assertEquals(new IntValue(2), new ArithExp(ArithOp.DIV, e1, e2).eval(symTable));
    }

    @Test
    void testArithExpDivisionByZero(){
        IExp e1 = new ValueExp(new IntValue(10));
        IExp e2 = new ValueExp(new IntValue(0));
        ExpressionException ex = assertThrows(ExpressionException.class,() -> new ArithExp(ArithOp.DIV, e1, e2).eval(symTable));
        assertEquals(ex.getMessage(),"division by zero");
    }


    @Test
    void testWrongOperandTypes(){
        IExp e1 = new ValueExp(new IntValue(10));
        IExp e2 = new ValueExp(new BoolValue(true));
        assertThrows(ExpressionException.class, () -> new ArithExp(ArithOp.ADD, e1, e2).eval(symTable));
        assertThrows(ExpressionException.class, () -> new LogicExp(LogicOp.AND, e1, e2).eval(symTable));
    }
}
