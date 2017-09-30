import java.util.*;

public class test {

  public static void main(String[] args) {

    Operant op = new Operant();
    Scanner sc = new Scanner(System.in);

    while(true) {
      System.out.println("Input the equation: ");
      String exp = sc.nextLine();
      Node root = treeBuilder(exp, op);
      System.out.println("Postfix form: ");
      int result = printExp(root);
      System.out.println();
      System.out.println("The result is: " + result);
      System.out.println();
    }
  }

  private static class Operant {
    private Map<String, Integer> op;
    public Operant() {
      this.op = new HashMap<String, Integer>();
      this.op.put(" ", 1);
      this.op.put("+", 2);
      this.op.put("-", 2);
      this.op.put("*", 3);
      this.op.put("/", 3);
      this.op.put("%", 3);
      this.op.put("POW", 4);
    }

    public int compare(String op1, String op2) {
      // System.out.println(op1);
      // System.out.println(op2);
      return this.op.get(op1) - this.op.get(op2);
    }
  }

  private static class Node {
    public String symbol;
    public Node leftc;
    public Node rightc;

    public Node(String val) {
      this.symbol = val;
      this.leftc = null;
      this.rightc = null;
    }

    public Node() {
      this(" ");
    }

    public boolean isLeaf() {
      return this.leftc == null && this.rightc == null;
    }
  }

  // bulid a AST tree from expression
  private static Node treeBuilder(String exp, Operant op) {
    exp = exp.replaceAll("\\s+", "");
    String[] symbols = exp.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)|(?<=\\D)(?<=\\D)");
    Stack<Node> ops = new Stack<Node>();
    Stack<String> opts = new Stack<String>();
    int i = 0;
    String prevOpt = " ";
    while (i < symbols.length) {
      String symbol = symbols[i];
      if (symbol.matches("^[0-9]+$")) {
        // System.out.println(symbol);
        ops.push(new Node(symbol));
      } else {
        if (symbol.equals("(")) {
          opts.push(symbol);
        } else if (symbol.equals(")")) {
          while (!opts.peek().equals("(")) {
            // System.out.println(ops.size());
            String sym = opts.pop();
            Node tmp = new Node(sym);
            tmp.rightc = ops.pop();
            tmp.leftc = ops.pop();
            ops.push(tmp);
          }
          opts.pop();
        } else {
          // System.out.println(symbol);
          while (!opts.empty() && !opts.peek().equals("(") && op.compare(opts.peek(), symbol) >= 0) {
            // System.out.println("wut");
            String sym = opts.pop();
            Node tmp = new Node(sym);
            tmp.rightc = ops.pop();
            tmp.leftc = ops.pop();
            ops.push(tmp);
          }
          opts.push(symbol);
        }
      }
      i++;
    }
    while (!opts.empty()) {
      String sym = opts.pop();
      Node temp = new Node(sym);
      temp.rightc = ops.pop();
      temp.leftc = ops.pop();
      ops.push(temp);
    }
    return ops.pop();
  }

  // print the exp in postfix form and calculate the result
  private static int printExp(Node n) {
    if (n == null) {
      return 0;
    } else {
      int left = printExp(n.leftc);
      int right = printExp(n.rightc);
      System.out.print(n.symbol + " ");
      int returnVal = 0;
      switch(n.symbol) {
        case "+":
          returnVal = left + right;
          break;
        case "-":
          returnVal = left - right;
          break;
        case "*":
          returnVal = left * right;
          break;
        case "/":
          returnVal = left / right;
          break;
        case "%":
          returnVal = left % right;
          break;
        default:
          returnVal = Integer.parseInt(n.symbol);
      }
      return returnVal;
    }
  }
}
