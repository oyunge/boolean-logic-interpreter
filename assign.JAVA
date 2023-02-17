import java.util.HashMap;
import java.util.Map;

public class BooleanInterpreter {
    private Map<String, Boolean> variables;

    public BooleanInterpreter() {
        this.variables = new HashMap<>();
    }

    public boolean eval(String expression) {
        Token[] tokens = tokenize(expression);
        AstNode ast = parse(tokens, 0, tokens.length);
        return evaluate(ast);
    }

    private Token[] tokenize(String expression) {
        Token[] tokens = new Token[expression.length()];
        int i = 0;
        int j = 0;
        while (j < expression.length()) {
            char c = expression.charAt(j);
            if (c == 'T' || c == 'F') {
                tokens[i++] = new Token(TokenType.VALUE, c == 'T');
                j++;
            } else if (Character.isLetter(c)) {
                int k = j;
                while (k < expression.length() && Character.isLetter(expression.charAt(k))) {
                    k++;
                }
                String variable = expression.substring(j, k);
                tokens[i++] = new Token(TokenType.VARIABLE, variable);
                j = k;
            } else if (c == '^' || c == 'v' || c == '~' || c == '(' || c == ')') {
                tokens[i++] = new Token(TokenType.OPERATOR, c);
                j++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }
        Token[] result = new Token[i];
        System.arraycopy(tokens, 0, result, 0, i);
        return result;
    }

    private AstNode parse(Token[] tokens, int start, int end) {
        if (start == end) {
            throw new IllegalArgumentException("Invalid syntax");
        }
        int i = start;
        AstNode left = parseTerm(tokens, i, end);
        i += left.size();
        while (i < end && tokens[i].type == TokenType.OPERATOR) {
            Operator operator = Operator.fromChar(tokens[i].value);
            if (operator == Operator.NOT) {
                AstNode right = parseTerm(tokens, i + 1, end);
                left = new UnaryOpNode(operator, left, right);
                i += right.size() + 1;
            } else {
                AstNode right = parseTerm(tokens, i + 1, end);
                left = new BinaryOpNode(operator, left, right);
                i += right.size() + 1;
            }
        }
        if (i != end) {
            throw new IllegalArgumentException("Invalid syntax");
        }
        return left;
    }

    private AstNode parseTerm(Token[] tokens, int start, int end) {
        if (start == end) {
            throw new IllegalArgumentException("Invalid syntax");
        }
        if (tokens[start].type == TokenType.VALUE) {
            return new ValueNode(tokens[start].value);
        } else if (tokens[start].type == TokenType.VARIABLE) {
            return new VariableNode(tokens[start].value, variables);
        } else if (tokens[start].type == TokenType.OPERATOR && tokens[start].value == '(') {
            AstNode node = parse(tokens, start + 1, end);
            if (node.size() + start + 1 >= end || tokens[node.size() + start + 1].type != TokenType.OPERATOR || tokens[node.size() + start + 1].value != ')') {
                throw new IllegalArgumentException("Missing closing parenthesis");
            }
            return node;
        } else {
            throw new IllegalArgumentException("Invalid syntax");
        }
    }

private boolean evaluate(AstNode node) {
    if (node instanceof ValueNode) {
        return ((ValueNode) node).getValue();
    } else if (node instanceof VariableNode) {
        return ((VariableNode) node).getValue();
    } else if (node instanceof UnaryOpNode) {
        UnaryOpNode unaryOpNode = (UnaryOpNode) node;
        if (unaryOpNode.getOperator() == Operator.NOT) {
            return !evaluate(unaryOpNode.getOperand());
        } else {
            throw new IllegalArgumentException("Invalid operator: " + unaryOpNode.getOperator().getSymbol());
        }
    } else if (node instanceof BinaryOpNode) {
        BinaryOpNode binaryOpNode = (BinaryOpNode) node;
        Operator operator = binaryOpNode.getOperator();
        if (operator == Operator.AND) {
            return evaluate(binaryOpNode.getLeft()) && evaluate(binaryOpNode.getRight());
        } else if (operator == Operator.OR) {
            return evaluate(binaryOpNode.getLeft()) || evaluate(binaryOpNode.getRight());
        } else {
            throw new IllegalArgumentException("Invalid operator: " + operator.getSymbol());
        }
    } else {
        throw new IllegalArgumentException("Invalid syntax");
    }
}

private static class Token {
    private TokenType type;
    private Object value;

    private Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }
}

private enum TokenType {
    VALUE,
    VARIABLE,
    OPERATOR
}

private static class AstNode {
    public int size() {
        return 1;
    }
}

private static class ValueNode extends AstNode {
    private boolean value;

    public ValueNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}

private static class VariableNode extends AstNode {
    private String name;
    private Map<String, Boolean> variables;

    public VariableNode(String name, Map<String, Boolean> variables) {
        this.name = name;
        this.variables = variables;
    }

    public boolean getValue() {
        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Undefined variable: " + name);
        }
        return variables.get(name);
    }
}

private static class UnaryOpNode extends AstNode {
    private Operator operator;
    private AstNode operand;

    public UnaryOpNode(Operator operator, AstNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    public AstNode getOperand() {
        return operand;
    }

    @Override
    public int size() {
        return 1 + operand.size();
    }
}

private static class BinaryOpNode extends AstNode {
    private Operator operator;
    private AstNode left;
    private AstNode right;

    public BinaryOpNode(Operator operator, AstNode left, AstNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public AstNode getLeft() {
        return left;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public int size() {
        return 1 + left.size() + right.size();
    }
}
private enum Operator {
    AND('^'),
    OR('v'),
    NOT('~');

    private char symbol;

    private Operator(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static Operator fromChar(char c) {
        switch (c) {
            case '^':
                return AND;
            case 'v':
                return OR;
            case '~':
                return NOT;
            default:
                throw new IllegalArgumentException("Invalid operator: " + c);
        }
    }
}
