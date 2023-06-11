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
        // TODO: NEEDS SET STATE STUFF
        if (expression.charAt(index) == '*' || expression.charAt(index) == '+' || expression.charAt(index) == '?') index++;

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
        // TODO: NEEDS SET STATE STUFF
        // Probably a bit like what is above but for each alternation within brackets?
        else if (expression.charAt(index) == '['){
            //consume the open bracket
            index++;
            //spec defines that for any [] list, if it includes ']' then it must be the first character, so
            if(expression.charAt(index) == ']') index++;
            //then consume any symbol until a ']' is reached, or until index is outside of string
            while(expression.charAt(index) != ']' && index < expression.length()){
                index++;
            }
            //if end of string reached, error
            if (index == expression.length()) error();
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