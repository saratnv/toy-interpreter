package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putAndLookupWorkCorrectly() throws ADTException {
        dict.put("v", 5);
        assertTrue(dict.isDefined("v"));
        assertEquals(5, dict.lookup("v"));
    }

    @Test
    void lookupMissingKeyThrowsException() {
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }

    @Test
    void updateChangesValue() throws ADTException {
        dict.put("a", 10);
        dict.update("a", 20);
        assertEquals(20, dict.lookup("a"));
    }
}
