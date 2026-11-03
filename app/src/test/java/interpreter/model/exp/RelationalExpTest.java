package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationalExpTest {
    private MyDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void evaluateAllOperatorsToTrue() throws MyException {
        IExp left = new ValueExp(new IntValue(5));
        IExp right = new ValueExp(new IntValue(10));
        IExp equalLeft = new ValueExp(new IntValue(10));

        assertEquals(new BoolValue(true), new RelationalExp(RelOp.LT, left, right).eval(symTable));
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.LE, left, right).eval(symTable));
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.EQ, right, equalLeft).eval(symTable));
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.NE, left, right).eval(symTable));
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.GT, right, left).eval(symTable));
        assertEquals(new BoolValue(true), new RelationalExp(RelOp.GE, right, equalLeft).eval(symTable));
    }

    @Test
    void evaluatesAllOperatorsToFalse() throws MyException{
        IExp left = new ValueExp(new IntValue(5));
        IExp right = new ValueExp(new IntValue(10));
        IExp equalLeft = new ValueExp(new IntValue(10));

        assertEquals(new BoolValue(false), new RelationalExp(RelOp.GT, left, right).eval(symTable));
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.GE, left, right).eval(symTable));
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.EQ, left, right).eval(symTable));
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.NE, right, equalLeft).eval(symTable));
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.LT, right, left).eval(symTable));
        assertEquals(new BoolValue(false), new RelationalExp(RelOp.LE, right, left).eval(symTable));
    }

    @Test
    void nonIntegerOperandThrowsExpressionException() throws MyException {
        IExp validInt = new ValueExp(new IntValue(5));
        IExp invalidString = new ValueExp(new StringValue("test"));

        ExpressionException ex1 = assertThrows(ExpressionException.class,
                () -> new RelationalExp(RelOp.LT, invalidString, validInt).eval(symTable));
        assertTrue(ex1.getMessage().contains("first operand is not an integer"));

        ExpressionException ex2 = assertThrows(ExpressionException.class,
                () ->new RelationalExp(RelOp.LE, validInt, invalidString).eval(symTable));
        assertTrue(ex2.getMessage().contains("second operand is not an integer"));
    }
}
