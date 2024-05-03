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
    public void contains2() {
        SetImpl<Integer> set = new SetImpl<>();

        Thread th1 = new Thread(() -> {
            for (int i = 0; i < 100; i = i+2) {
                set.add(i);
            }
        });

        Thread th2 = new Thread(() -> {
            for (int i = 1; i < 100; i = i+2) {
                set.add(i);
            }
        });

        th1.start();
        th2.start();
        try {
            th1.join();
            th2.join();
        } catch (Exception ignore) {}

        for (int i = 0; i < 100; i++) {
            assertTrue(set.contains(i));
        }
    }

    @Test
    public void isEmpty() {
        SetImpl<Integer> set = new SetImpl<>();
        assertTrue(set.isEmpty());

        set.add(1);
        assertFalse(set.isEmpty());
    }

    @Test
    public void isEmpty2() {
        SetImpl<Integer> set = new SetImpl<>();

        Thread th1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                set.add(i);
            }
        });

        Thread th2 = new Thread(() -> {
            for (int i = 0; i < 60; i++) {
                set.remove(i);
            }
        });

        Thread th3 = new Thread(() -> {
            for (int i = 40; i < 100; i++) {
                set.remove(i);
            }
        });

        th1.start();
        try { th1.join(); } catch (Exception ignore) {}

        th2.start();
        th3.start();
        try {
            th2.join();
            th3.join();
        } catch (Exception ignore) {}

        assertTrue(set.isEmpty());
    }

    @Test
    public void isEmpty3() {
        SetImpl<Integer> set = new SetImpl<>();


        Thread th1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                set.add(i);
            }
        });
        th1.start();

        for (int i = 40; i < 100; i++) {
            set.add(i);
        }

        Thread th2 = new Thread(() -> {
            for (int i = 0; i < 100; i += 2) {
                set.remove(i);
            }
        });

        Thread th3 = new Thread(() -> {
            for (int i = 1; i < 100; i += 2) {
                set.remove(i);
            }
        });

        try { th1.join(); } catch (Exception ignore) {}

        th2.start();
        th3.start();
        try {
            th2.join();
            th3.join();
        } catch (Exception ignore) {}

        assertTrue(set.isEmpty());
    }
}

