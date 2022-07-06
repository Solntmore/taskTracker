package managers;

//хорошая ли идея переместить node в пакет task и переименовать пакет в что-то типо Objects или HelpClasses?
// Или пускай лежит в managers
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
