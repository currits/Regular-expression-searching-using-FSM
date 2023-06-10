public class RECompile{
    //deque - heap and stack combined, bottom end is heap, top end is stack - should make another implemenation for that

    public static void main(String[] args) {
        if (!(args.length == 1)) {
            System.out.println("Usage:  RECompile <\"regularExpression\">");
            System.out.println("regularExpression:      A legal regular expression to compile into a finiste state machine for pattern matching");
            return;
        }
        String expression = args[0];
        
    }
}