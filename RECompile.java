import java.util.ArrayList;
import java.util.List;

public class RECompile{
    //recursive descent parser
    //Top down
    String expression;
    int index;
    String operators;
    int state;

    List<Character> ch;
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

        setState(state, ' ', 0, 0);

        // TODO: Output 3 arrays to std out with initial as start state below

    }

    /////////////////////////// Here be dragons //////////////////////////////////////

    private int expression(){
        int r = 0;

        //call to term which calls factor which handles 1st, 2nd, 3rd precedence things
        r = term();
        //then comes back up to handle concatenation before descending to handle alternation, thus making concat 4th and alternation 5th in precedence
        if (isLiteral(expression.charAt(index)) || expression.charAt(index) == '(') expression();

        return r;
    }

    private int term(){
        int r, t1, t2, f;

        f = state-1;

        //this implements T -> F
        r = t1 = factor();

        //third precedence, repition/option
        //this implements T -> F*
        if (expression.charAt(index) == '*') {
            //this implements the zero or more state
            //sets r to this branch state, so that the factor can be skipped (match 0 times)
            index++;
            //from 22/3 lecture
            setState(state, ' ', state+1, t1);
            r = state; state++;
        }
        else if (expression.charAt(index) == '+'){
            //this implements the one or more state
            //similar to above, but does NOT set r to this state, as the factor must be matched before branching
            index++;
            setState(state, ' ', state+1, t1);
            state++;
        }
        else if (expression.charAt(index) == '?'){
            //this implements the zero or once state
            index++;
            //create the branch pointing to the factor state and the next state
            setState(state, ' ', state+1, t1); 
            //now make the previous state (the state created by the factor) point to the next state after this one
            if(next1.get(f).equals(next2.get(f))){
                next2.set(f, state+1);
            }
            next1.set(f, state+1);
            //thus making a zero or once machine
            r = state; state++;
        }

        //this needs to be lowest precedence
        //by making this an else if, then hopefully should return back up to expression for concatenation so that things can happen
        //before returning for alternation
        //this implements T -> F|T  call to term is what confirms alternation
        else if (expression.charAt(index) == '|') {
            if(next1.get(f).equals(next2.get(f))){
                next2.set(f, state);
            }

            next1.set(f, state);
            f = state-1;

            index++;
            r = state;
            state++;

            t2 = term();
            setState(r, ' ', t1, t2);

            if(next1.get(f).equals(next2.get(f))){
                next2.set(f, state);
            }

            next1.set(f, state);
        }
        //this implements T -> [D]  
        // TODO: Figure how moving this to factor alters precedence, then find a way to get this handled properly
        // TODO: clean these comments oml
        else if (expression.charAt(index) == '['){
            //consume the open bracket, dont need a new state for this symbol consumption
            index++;
            //set the entry to the whole machine as the next machine to be created (will be a branching state)
            r = state;
            //spec defines that for any [] list, if it includes ']' then it must be the first character, so
            if(expression.charAt(index) == ']'){
                //create a branch state that points to the state for the ] character, and the next branching machine after
                setState(state, ' ', state+1, state+2);
                state++;
                //state for matching the ]
                //branch numbers are placeholder
                setState(state, expression.charAt(index), state+1, state+1);
                //consume the character
                index++;
                state++;
            }
            //then consume any symbol until a ']' is reached, or until index is outside of string
            while(index < expression.length() && expression.charAt(index) != ']'){
                //create a branch state that points to the state for this loop's character, and the next branching machine after
                setState(state, ' ', state+1, state+2);
                state++; //new state
                //make a state for matching the character for this loop
                //branch numbers are placeholder, will be iterated over after all the machines are made
                setState(state, expression.charAt(index), state+1, state+1);
                state++;
                //consume the character
                index++;
            }
            //if end of string reached, error
            if (index == expression.length()) error();
            //TODO: see if the last disjunctive set state can be removed
            //point the branch numbers of the final loop iteration branch state to prevent leaving the alternation (effectively turns that branch state into a dud unconditional pass-through type state, can try doing without but would be intense)
            //state-2 as the loop makes branch state first then character match state
            //so branch state is 2 back
            next1.set(state-2, state-1);
            next2.set(state-2, state-1);
            //consume the close bracket
            index++;
            //dont need to make state, can just make all created states point to whatever is created next
            //now we need to join the end points of all the character matching states we have just built, to the new state value
            //r currently stores the value of state before entering this whole loop, so we can use it as our reference point
            //the first state we need to change is r + 1, then every second state after, so
            for(int i = r + 1; i < state; i+=2){
                //make the character matching states all point to the current state value
                //meaning if any match, machine jumps to the end
                //alternation
                next1.set(i, state);
                next2.set(i, state);
            }
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

    private void setState(int s, char c, int n1, int n2){
        ch.add(s,c);
        next1.add(s, n1);
        next2.add(s, n2);
    }

}