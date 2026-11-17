package interpreter.model.type;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import interpreter.model.value.IValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RefTypeTest {
    @Test
    void defaultValueOfRefTypeIsAddressZeroWithInnerType(){
        IType intType = new IntType();
        RefType refInt= new RefType(intType);
        IValue defaultVal = refInt.defaultValue();
        assertEquals(new RefValue(0,intType), defaultVal);
    }

    @Test
    void nestedRefTypesEqualEachOther(){
        RefType nestedRef1 = new RefType(new RefType(new IntType()));
        RefType nestedRef2 = new RefType(new RefType(new IntType()));
        assertEquals(nestedRef1, nestedRef2);
    }

    @Test
    void nestedRefTypeDoesNotEqualShallowRefType(){
        RefType deepRef = new RefType(new RefType(new IntType()));
        RefType shallowRef = new RefType(new IntType());
        assertNotEquals(deepRef, shallowRef);
    }
}
