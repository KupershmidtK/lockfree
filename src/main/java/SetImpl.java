import java.util.concurrent.atomic.AtomicMarkableReference;

public class SetImpl<T extends Comparable<T>> implements Set<T> {
    private final Node head, tail;

    SetImpl() {
        head = new Node(null);
        tail = new Node(null);
        head.next = new AtomicMarkableReference<>(tail, false);
        tail.next = null;
    }

    @Override
    public boolean add(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        while (true) {
            LockFrame frame = find(value);
            Node pred = frame.pred;
            Node curr = frame.curr;
            if (value.equals(curr.item)) {
                return false;
            } else {
                Node node = new Node(value);
                node.next = new AtomicMarkableReference<>(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    @Override
    public boolean remove(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        boolean snip;
        while (true) {
            LockFrame frame = find(value);
            Node pred = frame.pred;
            Node curr = frame.curr;
            if (!value.equals(curr.item)) {
                return false;
            } else {
                Node succ = curr.next.getReference();
                snip = curr.next.attemptMark(succ, true);
                if (!snip)
                    continue;
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    @Override
    public boolean contains(T value) {
        boolean[] marked = {false};
        Node curr = head;
        while (curr.next != null) {
            if (value.equals(curr.item) && !marked[0])
                return true;

            curr = curr.next.get(marked);
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return head.next.getReference() == tail;
    }


    private LockFrame find(T value) {
        if (isEmpty())
            return new LockFrame(head, tail);

        Node pred, curr, succ;
        boolean[] marked = {false};
        boolean snip;

        retry:
        while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(marked);
                while (marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if (!snip) continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }

                assert curr.item != null;
                if (value.compareTo(curr.item) >= 0)
                    return new LockFrame(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    private class Node {
        final T item;
        AtomicMarkableReference<Node> next;

        public Node(T item) {
            this.item = item;
        }
    }

    private class LockFrame {
        public Node pred, curr;

        LockFrame(Node pred, Node curr) {
            this.pred = pred;
            this.curr = curr;
        }
    }
}
