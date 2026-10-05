grammar Patch4J;

patchFile
    : targetDecl? importDecl* topLevelDecl* EOF
    ;

targetDecl
    : TARGET STRING
    ;

importDecl
    : IMPORT qualifiedName
    ;

// Top level

topLevelDecl
    : patchClassDecl
    | makeDecl
    | patchMethodDecl
    | patchFieldDecl
    ;

makeDecl
    : MAKE CLASS         qualifiedName modifierShortcut+
    | MAKE FIELD         fieldName     modifierShortcut+
    | MAKE STATIC_FIELD  fieldName     modifierShortcut+
    | MAKE METHOD        methodName    modifierShortcut+
    | MAKE STATIC_METHOD methodName    modifierShortcut+
    ;

modifierShortcut
    : modifier
    | finalToggle
    ;

patchClassDecl
    : PATCH CLASS qualifiedName patchClassBlock
    ;

patchClassBlock
    : LBRACE classModifier* patchClassLevelStatement* RBRACE
    ;

// Class level

classModifier
    : accessModifier
    | finalModifier
    ;

patchClassLevelStatement
    : patchFieldDecl
    | patchMethodDecl
    | patchConstructorDecl
    | patchClinitDecl
    | patchExtendsDecl
    | makeDecl
    | replaceConstantDecl
    ;

patchExtendsDecl
    : PATCH EXTENDS qualifiedName
    ;

patchConstructorDecl
    : PATCH CONSTRUCTOR LPAREN parameterList? RPAREN patchMethodBlock
    ;

patchClinitDecl
    : PATCH CLINIT patchMethodBlock
    ;

replaceConstantDecl
    : REPLACE CONSTANT literal WITH literal
    ;

patchFieldDecl
    : PATCH FIELD fieldName (COLON typeName)? patchFieldBlock
    ;

patchMethodDecl
    : PATCH METHOD methodName patchMethodBlock
    ;

methodName
    : qualifiedName LPAREN params=parameterList? RPAREN (COLON returnType=typeName)?
    ;

parameterList
    : parameter (COMMA parameter)*
    ;

parameter
    : type=typeName (name=ID)?
    ;

typeName
    : (primitiveType | qualifiedName) (LBRACKET RBRACKET)*
    ;

fieldName
    : qualifiedName
    | STRING
    ;

patchFieldBlock
    : LBRACE fieldModifier* RBRACE
    ;

patchMethodBlock
    : LBRACE methodModifier* RBRACE
    ;

methodModifier
    : accessModifier
    | finalModifier
    ;

fieldModifier
    : accessModifier
    | finalModifier
    | valueModifier
    ;

valueModifier
    : VALUE literal
    ;

// Common

qualifiedName
    : ID (DOT ID)*
    ;

accessModifier
    : ACCESS modifier
    ;

modifier
    : PUBLIC | PRIVATE | PROTECTED
    ;

finalModifier
    : FINAL booleanLiteral
    ;

finalToggle
    : BANG? FINAL
    ;

booleanLiteral
    : TRUE | FALSE
    ;

literal
    : STRING | INT | booleanLiteral
    ;

primitiveType
    : VOID | BOOLEAN | BYTE | CHAR | SHORT | INT_T | LONG | FLOAT_T | DOUBLE
    ;

// Keywords
TARGET        : 'target'      ;
IMPORT        : 'import'      ;
PATCH         : 'patch'       ;
CLASS         : 'class'       ;
METHOD        : 'method'      ;
STATIC_METHOD : 'smethod'     ;
STATIC_FIELD  : 'sfield'      ;
FIELD         : 'field'       ;
MAKE          : 'make'        ;
VALUE         : 'value'       ;
EXTENDS       : 'extends'     ;
CONSTRUCTOR   : 'constructor' ;
CLINIT        : 'clinit'      ;
REPLACE       : 'replace'     ;
CONSTANT      : 'constant'    ;
WITH          : 'with'        ;
ACCESS        : 'access'      ;
PUBLIC        : 'public'      ;
PRIVATE       : 'private'     ;
PROTECTED     : 'protected'   ;
FINAL         : 'final'       ;
STATIC        : 'static'      ;
TRUE          : 'true'        ;
FALSE         : 'false'       ;
AFTER         : 'after'       ;
BEFORE        : 'before'      ;
EVERY         : 'every'       ;
INSERT        : 'insert'      ;
ANCHOR        : 'anchor'      ;
START         : 'start'       ;
END           : 'end'         ;
AND           : 'and'         ;
REMOVE        : 'remove'      ;
USING         : 'using'       ;

VOID    : 'void'    ;
BOOLEAN : 'boolean' ;
BYTE    : 'byte'    ;
CHAR    : 'char'    ;
SHORT   : 'short'   ;
INT_T   : 'int'     ;
LONG    : 'long'    ;
FLOAT_T : 'float'   ;
DOUBLE  : 'double'  ;

// Punctuation
LBRACE      : '{' ;
RBRACE      : '}' ;
LPAREN      : '(' ;
RPAREN      : ')' ;
LBRACKET    : '[' ;
RBRACKET    : ']' ;
DOT         : '.' ;
COMMA       : ',' ;
COLON       : ':' ;
SEMICOLON   : ';' ;
BANG        : '!' ;

VARIABLE : '$' ID;

// Literals
STRING      : '"' (~["\\\r\n] | '\\' .)* '"' ;
INT         : [0-9]+ ;

// Identifiers
ID          : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Whitespace & comments
WS              : [ \t\r\n]+        -> skip ;
LINE_COMMENT    : '//' ~[\r\n]*     -> skip ;
BLOCK_COMMENT   : '/*' .*? '*/'     -> skip ;
