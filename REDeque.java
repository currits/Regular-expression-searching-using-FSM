public class REDeque {
    
    Node head;
    int size;

    /**
     * Defines a Deque for the RESearch program
     */
    public REDeque(){
        this.size = 0;
    }

    /**
     * Pushes a value onto the top of the deque
     * @param value The value to push
     */
    public void push(int value){
        if(head == null){
            head = new Node(value);
            return;
        }

        Node newNode = new Node(value);

        head.setNext(newNode);

        head = newNode;
        size++;
    }

    /**
     * Puts a value at the bottom of the deque
     * @param value The value to put
     */
    public void put(int value){
        if(head == null){
            head = new Node(value);
            return;
        }

        Node curr = head;

        while(curr.getNext() != null){
            curr = curr.getNext();
        }

        Node newNode = new Node(value);

        curr.setNext(newNode);

        size++;
    }

    /**
     * Pops a value off the top of the deque
     * @return The head value of the deque
     */
    public Node pop(){
        if(head == null){
            return null;
        }


        Node node = head;

        head = head.getNext();

        size--;

        return node;
    }

    /**
     * Gets the size of the deque
     * @return The size of the deque
     */
    public int size(){
        return size;
    }
}
