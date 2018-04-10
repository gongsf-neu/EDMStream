package D_Stream;

public class Node<E> {
	public E e;
	public Node<E> next;
	
	public Node(E e){
		this.e = e;
		next = null;
	}
	
}
