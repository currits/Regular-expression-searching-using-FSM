import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class RECompile{
    //recursive descent parser
    //Top down
    String expression;
    int index;
    String operators;
    int state;

    List<String> ch;
    List<Integer> next1, next2;

    public RECompile(String p){
        ch = new ArrayList<>();
        next1 = new ArrayList<>(); 
        next2 = new ArrayList<>();

        expression = p;
        index = 0;
        state = 0;

        //these are our operators
        //an easy way to define our literals is to create a list of the symbols with significance and check that a literal is NOT one of these
        //so, a string to use !contains on
        operators = "+|.*?()\\[]";
        
        parse();
    }

    public static void main(String[] args) {
        if (!(args.length == 1)) {
            System.err.println("Usage:  RECompile <\"regularExpression\">");
            System.err.println("regularExpression:      A legal regular expression to compile into a finiste state machine for pattern matching");
            return;
        }
        String inExpression = args[0];
        RECompile rec = new RECompile(inExpression);
    }


    private void parse(){
        int initial = expression();

        setState(state, " ", 0, 0);

        // TODO: Output 3 arrays to std out with initial as start state below

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            String str = "s   ch 1 2";
            writer.write(str);
            writer.newLine();
            str = "---+--+-+-+";
            writer.write(str);
            writer.newLine();
            for(int i = 0; i < ch.size(); i++){
                str = String.format("%02d | %s %d %d", i, ch.get(i), next1.get(i), next2.get(i));
                writer.write(str);
                writer.newLine();
            }
            str = "start state: " + initial;
            writer.write(str);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing results to output");
            e.printStackTrace();
        }

    }

    /////////////////////////// Here be dragons //////////////////////////////////////

    private int expression(){
        int r = 0;

        //call to term which calls factor which handles 1st, 2nd, 3rd precedence things
        r = term();
        if (index == expression.length())
            return r;
        //then comes back up to handle concatenation before descending to handle alternation, thus making concat 4th and alternation 5th in precedence
        if (isLiteral(expression.charAt(index)) || expression.charAt(index) == '(' || expression.charAt(index) == '[' || expression.charAt(index) == '\\') expression();

        return r;
    }

    private int term(){
        int r, t1, t2, f;

        f = state-1;

        //this implements T -> F
        r = t1 = factor();
        if (index == expression.length())
            return r;

        //third precedence, repition/option
        //this implements T -> F*
        if (expression.charAt(index) == '*') {
            //TODO a*b* not directing a* branch to b* branch
            //this implements the zero or more state
            //sets r to this branch state, so that the factor can be skipped (match 0 times)
            index++;
            //from 22/3 lecture
            setState(state, " ", state+1, t1);
            r = state; state++;
        }
        else if (expression.charAt(index) == '+'){
            //this implements the one or more state
            //similar to above, but does NOT set r to this state, as the factor must be matched before branching
            index++;
            setState(state, " ", state+1, t1);
            state++;
        }
        else if (expression.charAt(index) == '?'){
            //TODO a?b? not directing a? branch to b? branch
            //this implements the zero or once state
            index++;
            //create the branch pointing to the factor state and the next state
            setState(state, " ", state+1, t1);
            //now make the previous state (the state created by the factor) point to the next state after this one
            if(next1.get(r).equals(next2.get(r))){
                next2.set(r, state+1);
            }
            next1.set(r, state+1);
            //thus making a zero or once machine
            r = state; state++;
        }

        //this needs to be lowest precedence
        //by making this an else if, then hopefully should return back up to expression for concatenation so that things can happen
        //before returning for alternation
        //this implements T -> F|T  call to term is what confirms alternation
        //TODO (a|b)|(c|d) a state not branching to end state
        //Need to insert non matching end state for alternation machines to point to, then set that state's endpoint
        else if (expression.charAt(index) == '|') {
            //best I can figure, this is for making the most previously created state (whatever has been created before the call to term) point to the about-to-be-made alternation
            // ie for "(abcd)a|b" it takes the end of the (abcd) machine and points it to the branch machine, the |
            //but using f here is fucking things up when the | is early in the term (the second symbol, "a|b") because when f is assigned, state is 0, so f becomes -1, and the algorithm tries to grab a state that doesnt exist
            //its from moodle though, so moodle code must assume state starts at 1??
            //temp fix for now
            if (f != -1){
                if(next1.get(f).equals(next2.get(f))){
                    next2.set(f, state);
                }
                next1.set(f, state);
            }
            f = state-1;

            index++;
            r = state;
            setState(state, " ", 0 , 0); //the dummy
            state++;

            //we need to create a dummy state first, then overwrite it. The dummy state will be ther branch state, to the beginning of the machines made on either side of the |
            //currently the lists prevent leaving null indexes
            //the dummy state is above, before the state increment
            t2 = term();
            //we shall now set it to the values we need: setState(r, " ", t1, t2);
            next1.set(r, t1); next2.set(r, t2);

            //and this sets the end state of the machine that comes after the | to the final state beyond it
            if(next1.get(f).equals(next2.get(f))){
                next2.set(f, state);
            }
            next1.set(f, state);

        }
        return r;
    }    

    private int factor(){
        int r = 0;

        //highest precedence
        //this implements F -> \S
        if (expression.charAt(index) == '\\'){
            setState(state, expression.charAt(index+1), state+1, state+1);
            //consume the \ and the following symbol
            index += 2;
            r = state;
            state++;
        }
        //second highest precedence
        //this implements F -> (T)  call to expression denotes that E -> T so a call to expression is a call to a term
        else if (expression.charAt(index) == '('){
            index++;
            r = expression();
            if (expression.charAt(index) == ')') index++;
            else error();
        }
        //this implements F -> α, F-> .
        else if (isLiteral(expression.charAt(index)) || expression.charAt(index) == '.'){
            setState(state, expression.charAt(index), state+1, state+1);
            index++;
            r = state;
            state++;
        }
        //this implements F -> [D]  
        else if (expression.charAt(index) == '['){
            //consume the open bracket, dont need a new state for this symbol consumption
            index++;
            //new string to store the array of characters that can be matched
            String disjuntiveList = "";
            //set the entry to the whole machine as the next machine to be created (will be a branching state)
            r = state;
            //spec defines that for any [] list, if it includes ']' then it must be the first character, so
            if(expression.charAt(index) == ']'){
                //append the ]
                disjuntiveList += expression.charAt(index);
                //consume the character
                index++;
            }
            //then consume any symbol until a ']' is reached, or until index is outside of string
            while(index < expression.length() && expression.charAt(index) != ']'){
                //append the character
                disjuntiveList += expression.charAt(index);
                //consume the character
                index++;
            }
            //if end of string reached, error
            if (index == expression.length()) error();
            //otherwise, we have built our string, so create state with it
            setState(state, disjuntiveList, state+1, state+1);
            state++;
            //and consume the close bracket
            index++;
        }
        else error();

        return r;
    }

    private boolean isLiteral(char c){
        //checks that the passed character is in fact a literal        
        return (!operators.contains(Character.toString(c)));
    }

    private void error(){
        System.err.println("Failed to parse expression!");
        System.exit(0);
    }

    private void setState(int s, String c, int n1, int n2){
        ch.add(s,c);
        next1.add(s, n1);
        next2.add(s, n2);
    }

    //overload method so we can handle passing characters here instead of casting in every method call
    private void setState(int s, char c, int n1, int n2){
        String str = Character.toString(c);
        ch.add(s,str);
        next1.add(s, n1);
        next2.add(s, n2);
    }

}