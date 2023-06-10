public class RECompile{
    //recursive descent parser
    //Top down
    String expression;
    int index;
    String operators;

    public RECompile(String p){
        expression = p;
        index = 0;
        //these are our operators
        //an easy way to define our literals is to create a list of the symbols with significance and check that a literal is NOT one of these
        //so, a string to use !contains on
        operators = "+|.*?()\\[]";
        
    }

    public static void main(String[] args) {
        if (!(args.length == 1)) {
            System.out.println("Usage:  RECompile <\"regularExpression\">");
            System.out.println("regularExpression:      A legal regular expression to compile into a finiste state machine for pattern matching");
            return;
        }
        String inExpression = args[0];
        RECompile rec = new RECompile(inExpression);
    }


    private void parse(){

    }

    private void compile(){

    }

    /////////////////////////// Here be dragons //////////////////////////////////////

    private void expression(){
        //call to term which calls factor which handles 1st, 2nd, 3rd precedence things
        term();
        //then comes back up to handle concatenation before descending to handle alternation, thus making concat 4th and alternation 5th in precedence
        if (isLiteral(expression.charAt(index)) || expression.charAt(index) == '(') expression();
    }
    private void term(){
        //this implements T -> F
        factor();
        //third precedence, repition/option
        //this implements T -> F*
        if (expression.charAt(index) == '*' || expression.charAt(index) == '+' || expression.charAt(index) == '?') index++;
        //this needs to be lowest precedence
        //by making this an else if, then hopefully should return back up to expression for concatenation so that things can happen
        //before returning for alternation
        //this implements T -> F|T  call to term is what confirms alternation
        else if (expression.charAt(index) == '|') {
            index++;
            term();
        }
        //this implements T -> [D]  
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
    }    
    private void factor(){
        //highest precedence
        //this implements F -> \S
        if (expression.charAt(index) == '\\'){
            //consume the \ and the following symbol
            index += 2;
        }
        //second highest precedence
        //this implements F -> (T)  call to expression denotes that E -> T so a call to expression is a call to a term
        else if (expression.charAt(index) == '('){
            index++;
            expression();
            if (expression.charAt(index) == ')') index++;
            else error();
        }
        //this implements F -> α, F-> .
        else if (isLiteral(expression.charAt(index)) || expression.charAt(index) == '.') index++;
        else error();
    }
    private boolean isLiteral(char c){
        //checks that the passed character is in fact a literal        
        return (!operators.contains(Character.toString(c)));
    }
    private void error(){
        //TODO error method
    }

}