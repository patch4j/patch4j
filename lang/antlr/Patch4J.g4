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
    : patchClassDecl  # TopLevelPatchClassDeclaration
    | makeDecl        # TopLevelMakeDeclaration
    | patchMethodDecl # TopLevelPatchMethodDeclaration
    | patchFieldDecl  # TopLevelPatchFieldDeclaration
    ;

makeDecl
    : MAKE CLASS qualifiedName modifierShortcut+ #MakeClassDeclaration
    | MAKE fieldName           modifierShortcut+ #MakeFieldDeclaration
    | MAKE methodName          modifierShortcut+ #MakeMethodDeclaration
    ;

modifierShortcut
    : modifier    #AccessModifierShortcut
    | finalToggle #FinalToggleModifierShortcut
    ;

patchClassDecl
    : PATCH CLASS qualifiedName patchClassBlock
    ;

patchClassBlock
    : LBRACE classModifier* patchClassLevelStatement* RBRACE
    ;

// Class level

classModifier
    : accessModifier # ClassAccessModifier
    | finalModifier  # ClassFinalModifier
    ;

methodModifier
    : accessModifier # MethodAccessModifier
    | finalModifier  # MethodFinalModifier
    ;

fieldModifier
    : accessModifier # FieldAccessModifier
    | finalModifier  # FieldFinalModifier
    | valueModifier  # FieldValueModifier
    ;

patchClassLevelStatement
    : patchFieldDecl       # ClassLevelPatchFieldDeclaration
    | patchMethodDecl      # ClassLevelPatchMethodDeclaration
    | patchConstructorDecl # ClassLevelPatchConstructorDeclaration
    | patchClinitDecl      # ClassLevelPatchClinitDeclaration
    | patchExtendsDecl     # ClassLevelPatchExtendsDeclaration
    | makeDecl             # ClassLevelMakeDeclaration
    | replaceConstantDecl  # ClassLevelReplaceConstantDeclaration
    | patchClassDecl       # ClassLevelPatchClassDeclaration
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
    : PATCH fieldName patchFieldBlock
    ;

patchMethodDecl
    : PATCH methodName patchMethodBlock
    ;

methodName
    : (METHOD | STATIC_METHOD) qualifiedName LPAREN params=parameterList? RPAREN (COLON returnType=typeName)?
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
    : (FIELD | STATIC_FIELD) qualifiedName (COLON typeName)?
    ;

patchFieldBlock
    : LBRACE fieldModifier* RBRACE
    ;

patchMethodBlock
    : LBRACE methodModifier* RBRACE
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

finalToggle
    : BANG? FINAL
    ;

finalModifier
    : FINAL BOOLEAN
    ;

literal
    : STRING  # StringLiteral
    | INT     # IntLiteral
    | BOOLEAN # BooleanLiteral
    ;

primitiveType
    : VOID | BOOLEAN_T | BYTE | CHAR | SHORT | INT_T | LONG | FLOAT_T | DOUBLE
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

VOID      : 'void'    ;
BOOLEAN_T : 'boolean' ;
BYTE      : 'byte'    ;
CHAR      : 'char'    ;
SHORT     : 'short'   ;
INT_T     : 'int'     ;
LONG      : 'long'    ;
FLOAT_T   : 'float'   ;
DOUBLE    : 'double'  ;

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
BOOLEAN     : 'true' | 'false' ;

// Identifiers
ID          : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Whitespace & comments
WS              : [ \t\r\n]+        -> skip ;
LINE_COMMENT    : '//' ~[\r\n]*     -> skip ;
BLOCK_COMMENT   : '/*' .*? '*/'     -> skip ;
