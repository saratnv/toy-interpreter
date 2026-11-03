package interpreter.model.type;

import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringTest {
    @Test
    void stringTypeDefaultValueIsEmptyString(){
        StringType type =  new StringType();
        assertEquals(new StringValue(""), type.defaultValue());
    }

    @Test
    void equalStringValuesHaveSameEqualsAndHashCode(){
        StringValue val1 = new StringValue("test");
        StringValue val2 = new StringValue("test");

        assertEquals(val1, val2);
        assertEquals(val1.hashCode(), val2.hashCode());
    }
}
