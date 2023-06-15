///Name:
///ID:
///Name: Ethyn Gillies
///ID: 1503149

import java.io.*;
import java.util.*;

public class RESearch {

    static String[] ch;
    static int[] n1, n2;
    final static String delimiter = " ";

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java RECompile <\"regularExpression\"> | java RESearch <file path>");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.err.println("Please specify an existing file to search");
            return;
        }

        // Get input from standard in
        readInput();

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;

        System.out.println("Lines containing pattern:");

        while ((line = reader.readLine()) != null) {
            // If theres at all a match, output the whole line
            if (searchLine(line))
                System.out.println(line);
        }

        // Be a tidy kiwi!
        reader.close();
    }

    /**
     * Gets finite state machines from standard input from the output of RECompile
     * 
     * @throws IOException
     */
    private static void readInput() throws IOException {
        BufferedReader fsmReader = new BufferedReader(new InputStreamReader(System.in));
        List<String> stateMachines = new ArrayList<>();
        String str = "";

        while ((str = fsmReader.readLine()) != null) {
            stateMachines.add(str);
        }

        if (stateMachines.isEmpty()) {
            System.err.println("Error reading state machine from standard input");
            return;
        }

        // -1 as the last line is an empty line
        final int machineNum = stateMachines.size() - 1;

        ch = new String[machineNum];
        n1 = new int[machineNum];
        n2 = new int[machineNum];

        for (int i = 0; i < machineNum; i++) {
            String[] tuple = stateMachines.get(i).split(delimiter);

            ch[i] = tuple[0];
            n1[i] = Integer.parseInt(tuple[1]);
            n2[i] = Integer.parseInt(tuple[2]);
        }
    }

    /**
     * Runs the current FSM against a line of text
     * 
     * @param line The line to check the FSM against
     * @return True if the FSM finds a match, false if not
     */
    private static boolean searchLine(String line) {

        // Array for checks
        boolean[] checked = new boolean[ch.length];
        Arrays.fill(checked, false);

        // Pointers for line
        int mark = 0, pointer = 0;

        // -1 as SCAN because state can never be -1
        final int SCAN = -1;

        REDeque deque = new REDeque();
        // -1 is SCAN
        deque.put(SCAN);
        // Push start state
        deque.push(n1[0]);

        // Main FSM loop
        while (true) {
            // Get the current possible state
            int state = deque.pop();

            // No more input means FAIL so return false
            if (pointer >= line.length()) {
                return false;
            }

            // Scan matched
            if (state == SCAN) {
                // No possible next states so increase mark and start again
                if (deque.size == 0) {
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
                deque.put(SCAN);
                // Check the next character
                pointer++;

                continue;
            }

            // Mark State
            checked[state] = true;

            // Match if we are at final state! SUCCESS
            if (state == ch.length - 1) {
                return true;
            }

            // Branch matched
            if (ch[state].isEmpty()) {
                // Pushing both possible states only if they are not checked already
                if (!checked[n1[state]])
                    deque.push(n1[state]);

                if (n2[state] != n1[state] && !checked[n2[state]])
                    deque.push(n2[state]);

                continue;
            }

            // Literal matched so put its next possible state on the deque
            if (ch[state].contains(String.valueOf(line.charAt(pointer)))) {
                deque.put(n1[state]);
            }
        }
    }
}
