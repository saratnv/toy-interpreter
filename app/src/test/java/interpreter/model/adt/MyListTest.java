package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void addAndGetWorkCorrectly() throws ADTException {
        list.add(100);
        assertEquals(1, list.size());
        assertEquals(100, list.get(0));
    }

    @Test
    void getOutOfBoundsThrowsException(){
        assertThrows(ADTException.class, () -> list.get(0));
    }
}
