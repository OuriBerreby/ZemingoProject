package il.co.lird.FS133.Projects.Zemingo;

import sun.awt.Mutex;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

public class QuickPopDataStructure<T> implements IQuickDataStructure<T>{
    private final Semaphore semaphore = new Semaphore(0);
    private final Mutex lock = new Mutex();
    private Node head = null;
    private final Comparator<T> comparator;

    public QuickPopDataStructure(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    private class Node {
        private final T data;
        private Node next;
        private Node prev;

        private Node(T data){
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    @Override
    public void push(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            return;
        }
        lock.lock();
        if (comparator.compare(newNode.data, head.data) <= 0) {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && comparator.compare(current.next.data, newNode.data) < 0) {
                current = current.next;
            }
            newNode.prev = current;
            newNode.next = current.next;
            if (current.next != null) {
                current.next.prev = newNode;
            }
            current.next = newNode;
        }
        lock.unlock();
        semaphore.release();
    }

    @Override
    public T pop(){
        if (null == head){
            throw new NoSuchElementException("There is no more elements");
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lock.lock();
        T returnValue = head.data;
        head = head.next;
        lock.unlock();

        return returnValue;
    }

    public T peek(){
        if (null != head){
            return head.data;
        }
        else return null;
    }
}
