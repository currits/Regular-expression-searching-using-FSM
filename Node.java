public class Node {
    
    int state;
    Node next, last;

    public Node(int state){
        this.state = state;
    }

    public int getState(){
        return state;
    }

    public Node getNext(){
        return next;
    }

    public Node getLast(){
        return last;
    }

    public void setNext(Node next){
        this.next = next; 
    }

    public void setLast(Node last){
        this.last = last;
    }
}
