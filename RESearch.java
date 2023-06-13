import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class RESearch {

    static String[] ch;
    static int[] n1, n2;

    public static void main(String[] args) throws IOException{
        
        // TODO: Get input from RECompile, currently just using example from tony lecture for testing

        ch = new String[]{
            "", "A", "", "B", "", "A", "C", "D"
        };

        n1 = new int[]{
            2, 2, 1, 6, 5, 6, 7, 8
        };

        n2 = new int[]{
            2, 2, 4, 6, 3, 6, 7, 8
        };

        if(args.length != 1){
            System.err.println("Usage: java RECompile <\"regularExpression\"> | java RESearch <file>");
            return;
        }

        File file = new File(args[0]);

        if(!file.exists()){
            System.err.println("Please specify an existing file to search");
            return;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;

        while((line = reader.readLine()) != null){
            boolean match = searchLine(line);

            //If theres at all a match, output the whole line
            if(match) System.out.println(line);
        }

        // Be a tidy kiwi!
        reader.close();
    }

    /**
     * Runs the current FSM against a line of text
     * @param line The line to check the FSM against
     * @return True if the FSM finds a match, false if not
     */
    private static boolean searchLine(String line){

        // Array for checks
        boolean[] checked = new boolean[ch.length];
        Arrays.fill(checked, false);

        // Pointers for line
        int mark = 0, pointer = 0;

        REDeque deque = new REDeque();
        // -1 is SCAN
        deque.put(-1);
        // Push start state
        deque.push(n1[0]);

        while (true){
            //Get the current possible state
            int state = deque.pop();

            // No more input means FAIL so return false
            if(pointer >= line.length()){
                return false;
            }

            // Scan matched
            if(state == -1){
                // No possible next states so increase mark and start again
                if(deque.size == 0){
                    // Increase mark
                    mark++;
                    pointer = mark;

                    Arrays.fill(checked, false);

                    // Push start state
                    deque.push(n1[0]);
                }
                
                // Reset checks
                Arrays.fill(checked, false);

                // -1 is SCAN
                deque.put(-1);
                // Check the next character
                pointer++;

                continue;
            }

            // Mark State
            checked[state] = true;

            // Match if we are at final state! SUCCESS
            if(state == ch.length - 1){
                return true;
            }

            // Branch matched
            if(ch[state].isEmpty()){
                // Pushing both possible states only if they are not checked already
                if(!checked[n1[state]])
                    deque.push(n1[state]);

                if(n2[state] != n1[state] && !checked[n2[state]])
                    deque.push(n2[state]);

                continue;
            }

            // Literal matched so put its next possible state on the deque
            if(ch[state].contains(String.valueOf(line.charAt(pointer)))){
                deque.put(n1[state]);
            }
        }
    }
}
