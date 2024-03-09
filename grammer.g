expression     → comma ;
comma          → ternary ( "," ternary )* ;
ternary        → ( equality "?" expression ":" )* equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;

#######################

expr → expr PART1
expr → IDENTIFIER
expr → NUMBER

PART1 → "(" PART3 ")"
PART1 → "." IDENTIFIER
PART1 → PART1 PART1

PART2 → "," expr
PART2 → PART2 PART2
PART2 → ""

PART3 → expr PART2
PART3 → ""

1()
