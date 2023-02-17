# boolean-logic-interpreter
The syntax of the Boolean logic expressions that this interpreter can evaluate is based on a combination of the standard symbols used in Boolean algebra and some additional symbols:

true or T for the Boolean value true

false or F for the Boolean value false

Variables consisting of one or more alphabetic characters (e.g., x, y, foo)

The operators ^ for AND, v for OR, and ~ for NOT

Parentheses for grouping expressions
The operator precedence rules follow the standard Boolean algebra convention, where NOT is evaluated first, followed by AND, and then OR. To specify a different order of evaluation, parentheses can be used to group expressions.

Here are some examples of valid Boolean logic expressions:

T ^ F evaluates to false
x v y evaluates to true if x and/or y are true
~T evaluates to false
~(x ^ y) v (y v F) evaluates to true if x is false and y is true
In the last example, the expression is evaluated as follows:

x ^ y is evaluated to false since x is false
~(x ^ y) is evaluated to true since ~ inverts the result
y v F is evaluated to true since y is true
The final expression ~(x ^ y) v (y v F) is evaluated to true since true v true is true
