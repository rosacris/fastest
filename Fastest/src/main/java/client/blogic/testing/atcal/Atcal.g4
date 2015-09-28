grammar Atcal;

/************* GLOBAL SRUCTURE *************/
refinementRule : '@RRULE' ID
                 preamble?
                 datatypes?
                 laws
                 (plcode)?
                 uut
                 (epilogue)?;

preamble : '@PREAMBLE' ( plcode | ID '.@PREAMBLE' STMTEND  )+ ;

plcode : '@CODESTART' (.)*? '@CODEEND' ;

datatypes : '@DATATYPES' (typeDec)+ ;

typeDec : 'DATATYPE' ID '=' type STMTEND;

type : ID                                                       # NameType
     | 'INT'                                                    # IntType
     | 'FLOAT'                                                  # FloatType
     | 'STRING'                                                 # StringType
     | 'ARRAY' type '(' NUMBER ')'                              # ArrayType
     | 'ENUM' ID args                                           # EnumType
     | 'RECORD' ID '(' ID ':' type ( ',' ID ':' type )* ')'     # RecordType
     | 'CONSTRUCTOR' ID args 'SETTER' ID args 'GETTER' ID args  # ContractType
     ;

args : '(' ( ID ( ',' ID )* )? ')' ;

// Constant mappings for enumeration and given datatypes
constMapping: 'MAP' '[' constMap (',' constMap)* ']' ;
constMap: ID '->' ( ID | STRING | NUMBER ) ;

laws: '@LAWS'
      (law STMTEND)* ;

uut: '@UUT' ID args STMTEND ;

epilogue: '@EPILOGUE' ( plcode | ID '.@EPILOGUE')+ ;

/******************* LAWS *********************/
law: ( ID ':' )? ( lawRefinement );
lawRefinement : zExprs '==>' refinement ( ',' refinement )* ;

zExprs : zExpr ('==>' zExprs)? ;

refinement : lvalue 'AS' type constMapping? withRef?   # ImplRef
           ;

lvalue : ID                 # VarLValue
       | '[' NUMBER? ']'    # ArrayLValue
       | '.' ID             # FieldLValue
       ;

withRef : 'WITH' '[' lawRefinement ( ',' lawRefinement)* ']' ;

// Z expressions
zExpr : ID                                   # Ident
      | NUMBER                               # NumLiteral
      | STRING                               # StrLiteral
      | AUTO                                 # AutoExpr
      | ELEM                                 # ElemExpr
      | zExpr '.' TUPPROJ                    # ProdProj
      | '<' zExpr ( ',' zExpr )* '>'         # ProdCons
      | zExpr '.' NUMBER                     # SetElem
      | zExpr '.' DOM                        # SetDom
      | zExpr '.' RAN                        # SetRan
      | zExpr '.' PROJ                       # SetProj
      | zExpr INTER zExpr                    # SetInter
      | zExpr UNION zExpr                    # SetUnion
      | zExpr DIFF zExpr                     # SetDiff
      | '{' zExpr ( ',' zExpr )* '}'         # SetCons
      | zExpr '.' CARD                       # SetCard
      | zExpr MUL zExpr                      # NumMul
      | zExpr DIV zExpr                      # NumDiv
      | zExpr MOD zExpr                      # NumMod
      | zExpr PLUS zExpr                     # NumPlus
      | zExpr MINUS zExpr                    # NumMinus
      | zExpr '++' zExpr                     # StrConcat
      | '(' zExpr ')'                        # Group
      ;

/**************** LEXER *******************/
// Use semicolon as statement end
STMTEND : SEMICOLON+;
fragment SEMICOLON : ';';

// Match identifiers using C syntax
ID : ID_LETTER ( ID_LETTER | DIGIT )* ;
fragment ID_LETTER : 'a'..'z' | 'A'..'Z' | '_' ;
fragment DIGIT : '0'..'9' ;

// Numbers
NUMBER : DIGIT+ ;

// Z number operators
PLUS : '+' ;
MINUS : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
//     | 'div' ;

// Z dot set operators
DOM : '@DOM' ;
RAN : '@RAN' ;
PROJ : '@' NUMBER ;
TUPPROJ : '#' NUMBER ;
INTER : '/\\' ;
UNION : '\\/' ;
DIFF : '~' ;
ELEM : '@ELEM' ;
AUTO : '@AUTOFILL' ;
CARD : '@CARD' ;

// Match double-quoted strings
STRING : '"' ( ESC | . )*? '"' ;
fragment ESC : '\\' [btnr"\\] ;

// Match whitespace in lexer and remove it.
WS : [ \t\r\n]+ -> skip ;
