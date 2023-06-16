///Names: Kurtis-Rae Mokaraka 		Ethyn Gillies
///IDs:   1256115					1503149

To use the RECompile program, you must specify an input expression to build a Finite State Machine for. The program will display the FSM in Terminal, and write it to standard output for piping to another program.
To use the RESearch program, you must first call RECompile and pipe the output to RESearch. A filename to must also be specified for RESearch, and it will use the FSM output from RECompile to search the file and output lines that match the FSM

For example:
    java RECompile "regularExpression" //creates and writes a FSM for the passed regular expression.
	java RECompile "regularExpression" | java RESearch "file.text" // creates a FSM for the passed regular expression, and pipes it to RESearch, which then searches file.text for lines that contain text that satisfy the FSM.

Notes:
RECompile sanitises input; removing double occurences of * + | and ? thus a** etc is considered a legal expresssion (resolves to a*).
RECompile does not allow unbalanced brackets, for both () ie a(ab)), ab((a) are not legal.
RECompile considers ((a)) as legal input.
RECompile does not however consider [[abc]] as legal input.
RECompile considers a[[ab] as legal input, including the [ as part of the multi symbol alternation.
RECompile does not allow repeats of a symbol to occur within the multi-symbol alternation [], thus [aba] is NOT considered legal.

Phrase Structure Rules:
E -> T		Expression can be a term
E -> TE		Expression can be a term concatenated with another term
T -> F		Term can be a factor
T -> F?		Term can be a Factor occuring zero or one times
T -> F+		Term can be a Factor occuring one or more times
T -> F*		Term can be a Factor in occuring zero or more times
T -> F | E	Term can be a Factor in alternation with an Expression
F -> \S		Factor can be an escaped symbol
F -> (T)	Factor can be a bracketed Term
F -> α		Factor can be a literal
F -> .		Factor can be a wildcard (matches any literal)
F -> [D]	Factor can be a disjunctive list of literals