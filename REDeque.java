///Name: Ethyn Gillies
///ID: 1503149
///Name: Kurtis-Rae Mokaraka
///ID: 1256115

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
            size++;
            return;
        }

        Node newNode = new Node(value);

        newNode.setNext(head);

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
            size++;
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
    public int pop(){
        if(head == null){
            return -2;
        }

        Node node = head;

        if(size() == 1){
            head = null;
        }
        else{
            head = head.getNext();
        }

        size--;

        return node.getState();
    }

    /**
     * Gets the size of the deque
     * @return The size of the deque
     */
    public int size(){
        return size;
    }
}
