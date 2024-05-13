import java.util.concurrent.atomic.AtomicMarkableReference;

public class SetImpl<T extends Comparable<T>> implements Set<T> {
    private final Node head;

    SetImpl() {
        head = new Node(null);
        Node tail = new Node(null);
        head.next = new AtomicMarkableReference<>(tail, false);
        tail.next = new AtomicMarkableReference<>(null, false);
    }

    @Override
    public boolean add(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        while (true) {
            LockFrame frame = find(value);
            Node pred = frame.pred;
            Node curr = frame.curr;

            if (curr.item != null && curr.item.equals(value)) {
                return false;
            }

            Node node = new Node(value);
            node.next = new AtomicMarkableReference<>(curr, false);
            if (pred.next.compareAndSet(curr, node, false, false)) {
                return true;
            }
        }
    }

    @Override
    public boolean remove(T value) {
        if (value == null)
            throw new IllegalArgumentException();

        while (true) {
            LockFrame frame = find(value);
            Node pred = frame.pred;
            Node curr = frame.curr;

            if (curr.item == null || !curr.item.equals(value)) {
                return false;
            }

            Node succ = curr.next.getReference();
            if (!curr.next.compareAndSet(succ, succ,false, true))
                continue;

            pred.next.compareAndSet(curr, succ, false, false);
            return true;
        }
    }

    @Override
    public boolean contains(T value) {
        Node current = head.next.getReference();
        while (current.item != null && current.item.compareTo(value) < 0) {
            current = current.next.getReference();
        }
        return current.item != null && current.item.equals(value) && !current.next.isMarked();
    }

    @Override
    public boolean isEmpty() {
        LockFrame frame = find(null);
        return frame.curr.item == null;
    }


    private LockFrame find(T value) {
        Node pred, curr, succ;
        boolean[] marked = {false};

        retry: while (true) {
            pred = head;
            curr = pred.next.getReference();
            while (true) {
                succ = curr.next.get(marked);
                while (marked[0]) {
                    if (!pred.next.compareAndSet(curr, succ, false, false))
                        continue retry;
                    curr = succ;
                    succ = curr.next.get(marked);
                }

                if (curr.item == null || curr.item.compareTo(value) >= 0)
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
