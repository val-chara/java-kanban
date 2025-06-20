package manager;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {

        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private void removeNode(Node node) {

        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node existingNode = nodeMap.get(task.getId());

        if (existingNode != null) {
            removeNode(existingNode);
            nodeMap.remove(task.getId());
        }

        Node newNode = new Node(task);
        linkLast(newNode);
        nodeMap.put(task.getId(), newNode);
    }

    private void linkLast (Node node){
            if (tail == null) {
                head = node;
            } else {
                tail.next = node;
                node.prev = tail;
            }
            tail = node;
        }

    @Override
    public void remove(int id) {

        Node node = nodeMap.remove(id);

        if (node == null) {
            return;
        }

        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {

        List<Task> history = new ArrayList<>();
        Node current = head;

        while (current != null) {

            history.add(current.task);
            current = current.next;
        }

        return history;
    }
}