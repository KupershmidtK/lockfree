import java.util.concurrent.atomic.AtomicMarkableReference;

public class SetImpl2<T extends Comparable<T>> implements Set<T> {
    private final Node head, tail;

    SetImpl2() {
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference<>(tail, false);
    }

    @Override
    public boolean add(T value) {
        int key = value.hashCode();
        while (true) {
            LockFrame frame = find(head, key);
            Node pred = frame.pred;
            Node curr = frame.curr;
            if (curr.key == key) {
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
        int key = value.hashCode();
        boolean snip;
        while (true) {
            LockFrame frame = find(head, key);
            Node pred = frame.pred, curr = frame.curr;
            if (curr.key != key) {
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
        int key = value.hashCode();
        Node curr = head;
        while (curr.key < key) {
            curr = curr.next.get(marked);
        }
        return (curr.key == key && !marked[0]);
    }

    @Override
    public boolean isEmpty() {
        return head.next.getReference() == tail;
    }


    private LockFrame find(Node head, int key) {
        if (isEmpty())
            return new LockFrame(this.head, tail);

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
                if (curr.key >= key)
                    return new LockFrame(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    private class Node {
        final T item;
        final int key;
        AtomicMarkableReference<Node> next;

        public Node(T item) {
            this.item = item;
            key = item != null ? item.hashCode() : 0;
        }

        public Node(int key) {
            this.item = null;
            this.key = key;
        }
    }

    private class LockFrame {
        public Node pred, curr;

        LockFrame(Node myPred, Node myCurr) {
            pred = myPred;
            curr = myCurr;
        }
    }
}

