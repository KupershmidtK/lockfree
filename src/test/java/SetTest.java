import org.junit.Test;

import static org.junit.Assert.*;

public class SetTest {

    @Test
    public void add() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.add(1));
        assertFalse(set.add(1));
    }

    @Test
    public void remove() {
        SetImpl<Integer> set = new SetImpl<>();
        assertFalse(set.remove(1));

        set.add(1);
        assertTrue(set.remove(1));

        assertFalse(set.remove(1));
    }

    @Test
    public void contains() {
        SetImpl<Integer> set = new SetImpl<>();
        set.add(1);
        assertTrue(set.contains(1));
        assertFalse(set.contains(4));
    }

    @Test
    public void isEmpty() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.isEmpty());

        set.add(1);
        assertFalse(set.isEmpty());
    }
}
