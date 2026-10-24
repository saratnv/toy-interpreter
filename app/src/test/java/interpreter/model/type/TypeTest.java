package interpreter.model.type;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    @Test
    void testIntTypeDefaultValue() {
        IType intType = new IntType();
        assertEquals(new IntValue(0), intType.defaultValue());
    }

    @Test
    void testBoolTypeDefaultValue() {
        IType boolType = new BoolType();
        assertEquals(new BoolValue(false), boolType.defaultValue());
    }

    @Test
    void testTypeEquality() {
        IType intType1 = new IntType();
        IType intType2 = new IntType();
        IType boolType1 = new BoolType();

        // Records provide equals() for free
        assertEquals(intType1, intType2);
        assertNotEquals(intType1, boolType1);
    }
}