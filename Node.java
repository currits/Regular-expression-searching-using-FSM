public class Node {
    
    int state;
    Node next;

    public Node(int state){
        this.state = state;
    }

    /**
     * Gets the state of this node
     * @return The state of the node
     */
    public int getState(){
        return state;
    }

    /**
     * Gets the next node
     * @return The next node
     */
    public Node getNext(){
        return next;
    }

    /**
     * Sets the next node
     * @param next The node to set next to
     */
    public void setNext(Node next){
        this.next = next; 
    }
}
