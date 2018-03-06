grammar Query;

// This puts "package formula;" at the top of the output Java files.
@header {
package formula;
}

// Adds code to the generated lexer and parser
@members {
    // This method makes the lexer or parser stop running if it encounters
    // invalid input and throw a RuntimeException.
    public void reportErrorsAsExceptions() {
        //removeErrorListeners();
        
        addErrorListener(new ExceptionThrowingErrorListener());
    }
    
    private static class ExceptionThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new RuntimeException(msg);
        }
    }
}

/*
 * These are the lexical rules. They define the tokens used by the lexer.
 *   *** Antlr requires tokens to be CAPITALIZED, like START_ITALIC, END_ITALIC, and TEXT.
 */
 
AND : '&&' ;

OR : '||' ;

WHITESPACE : [ \t\r\n]+ -> skip ;

LPAREN : '(' ;

RPAREN : ')' ;

IN : 'in' ;

CAT : 'category' ;

RAT : 'rating' ;

PR : 'price' ;

NAME : 'name' ;

STR: ('a'..'z' | 'A'..'Z' | '.')+ ;

DIGIT : '1'..'5' ;

RANGE : DIGIT '..' DIGIT ;

/*
 * These are the parser rules. They define the structures used by the parser.
 *    *** Antlr requires grammar nonterminals to be lowercase, like html, normal, and italic.
 */

query: expr EOF ;

expr: andexpr | orexpr ;

orexpr: andexpr (OR andexpr)* ;

andexpr: atom AND atom* ;

atom: in | category | rating | price | name | (LPAREN orexpr RPAREN) ;

in : IN LPAREN STR RPAREN ;

category : CAT LPAREN STR RPAREN ;

name : NAME LPAREN STR RPAREN ;

rating : RAT LPAREN RANGE RPAREN ;

price : PR LPAREN RANGE RPAREN ;
