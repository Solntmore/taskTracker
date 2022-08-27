package managers;

import interfaces.HistoryManager;
import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();
    private final Map<Integer, CustomLinkedList.Node> mapForCustomLinkList = new HashMap<>();


    @Override
    public void addTask(Task task) {
        customLinkedList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        if (mapForCustomLinkList.containsKey(id)) {
            customLinkedList.removeNode(mapForCustomLinkList.get(id));
            mapForCustomLinkList.remove(id);
        }
    }

    @Override
    public ArrayList<Integer> getIdFromMapForCustomLinkList() {
        ArrayList<Integer> tasksId = new ArrayList<>();
        for (Integer id : mapForCustomLinkList.keySet()) {
            tasksId.add(id);
        }
        return tasksId;
    }


    public class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        public void linkLast(T element) {
            Task task = (Task) element;
            int taskId = task.getMainTaskId();
            if (mapForCustomLinkList.containsKey(taskId)) {
                removeNode(mapForCustomLinkList.get(taskId));
            }
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            mapForCustomLinkList.put(taskId, newNode);
            tail = newNode;
            if (head == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        public List<Task> getTasks() {
            List<Task> browsingList = new ArrayList<>();
            Node<T> oldHead = head;
            if (oldHead != null) {
                browsingList.add((Task) head.data);
                for (int i = 0; i <= mapForCustomLinkList.size(); i++) {
                    if (oldHead.next != null) {
                        oldHead = oldHead.next;
                        if (oldHead.data != null) {
                            browsingList.add((Task) oldHead.data);
                        }
                    }
                }
            }
            return browsingList;
        }

        public void removeNode(Node node) {
            Node<T> nodeToRemove = node;
            Node<T> prevNodeToRemove = nodeToRemove.prev;
            Node<T> nextNodeToRemove = nodeToRemove.next;
            if (prevNodeToRemove != null) {
                prevNodeToRemove.next = nextNodeToRemove;
            }
            if (nextNodeToRemove != null) {
                nextNodeToRemove.prev = prevNodeToRemove;
            }
            if (head == nodeToRemove) {
                head = nodeToRemove.next;
            }
        }

        public class Node<E> {
            public E data;
            public Node<E> next;
            public Node<E> prev;

            public Node(Node<E> prev, E data, Node<E> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }
    }
}
