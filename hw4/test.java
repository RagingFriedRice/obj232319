import java.io.*;
import java.util.*;

public class test {

  public static void main(String[] args) throws FileNotFoundException {

    String[] arr = {"a", "an", "the", "and", "or", "but"};
    Set<String> banList = new HashSet<String>(Arrays.asList(arr));
    String articleName = "articletest.dat";
    String companyName = "companytest.dat";
    Scanner as = new Scanner(new File(articleName));
    Scanner cs = new Scanner(new File(companyName));

    HashMap<String, String> companys = processCompany(cs);
    Trie t = trieBuilder(companys);
    int totalWord = processArticle(t, banList, as);
    HashMap<String, Integer> companyWordCount = processTrie(t, companys);

    printResult(companyWordCount, totalWord);
  }

  /**
   * Read the company file and produce a HashMap.
   * @param  Scanner cs            Scanner of the company file.
   * @return         HashMap with each name of the company as key and the
   *                 primary name of the company as value.
   */
  private static HashMap<String, String> processCompany(Scanner cs) {
    HashMap<String, String> result = new HashMap<String, String>();
    while (cs.hasNextLine()) {
      String line = cs.nextLine();
      if (line.matches("^[.]+$")) {
        continue;
      }
      String[] arr = line.split("\t");
      String primaryName = arr[0];
      for (int i = 0; i < arr.length; i++) {
        String temp = arr[i].replaceAll("[^A-Za-z0-9]|[ ]", ""); // strip input
        result.put(temp, primaryName);
      }
    }
    return result;
  }

  /**
   * Using the comapny name to build a trie.
   * @param  HashMap<String, String>  cs  HashMap of company name, key is all the
   *                                      name we have while the value is the
   *                                      correspond primary name of the company
   *                                      that key is related to.
   * @return                 A trie of company names.
   */
  private static Trie trieBuilder(HashMap<String, String> cs) {
    Trie result = new Trie();
    for (String k : cs.keySet()) {
      result.insert(k);
    }
    return result;
  }

  /**
   * Process the article with the comapny name trie and count the appeareance of
   * company name.
   * @param  Trie        t             The company name trie.
   * @param  Set<String> bl            Banlist of word which we don't count.
   * @param  Scanner     as            Scanner of article file
   * @return             Total word in this article. The node of trie will be marked
   *                     visited and the visited counter will be change.
   */
  private static int processArticle(Trie t, Set<String> bl, Scanner as) {
    int wordCount = 0;
    TrieNode currentNode = t.root;
    while(as.hasNext()) {
      String s = as.next();
      s = s.replaceAll("[^A-Za-z0-9 ]", "");  // trim the string, get rid of special symbol
      if (bl.contains(s.toLowerCase())) { // ignore the banned word
        currentNode = t.root;
        continue;
      }
      wordCount++;
      for (int i = 0; i < s.length(); i++) {
        TrieNode next = currentNode.children.get(s.charAt(i));
        if (next == null) {
          currentNode = t.root;
          break;
        }
        currentNode = next;
      }
      if (currentNode.isWord) {
        // System.out.println("hitting: " + currentNode.word + "count: " + (currentNode.visited + 1));
        currentNode.visited++;
        currentNode = t.root;
      }
    }
    return wordCount;
  }

  /**
   * Process the trie after run the article on it to get the count of each company.
   * Using BFS.
   * @param  Trie            t             trie of comapny name.
   * @param  HashMap<String, String>       c             hashmap of company name.
   * @return                 hashmap with primary name of company as key and
   *                         word count of comapny as value.
   */
  private static HashMap<String, Integer> processTrie(Trie t, HashMap<String, String> c) {
    HashMap<String, Integer> result = new HashMap<String, Integer>();
    Queue<TrieNode> fringe = new LinkedList<TrieNode>(); // BFS
    fringe.add(t.root);
    while (!fringe.isEmpty()) {
      TrieNode n = fringe.remove();
      for (char key : n.children.keySet()) {
        fringe.add(n.children.get(key));
      }
      if (n.isWord) {
        // System.out.println("using: " + n.word);
        String k = c.get(n.word); // use the primary name of company
        int count = n.visited;
        if (result.get(k) == null) {
          result.put(k, 0);
        }
        result.put(k, result.get(k) + count);
        System.out.println("real: " + n.word + " count: " + n.visited);
      }
    }
    return result;
  }

  /**
   * Print the result in table format
   * @param HashMap<String, Integer> res           Company and word count
   * @param int             tw       total word count
   */
  private static void printResult(HashMap<String, Integer> res, int tw) {
    System.out.format("%-30s%-30s%-30s\n", "Company", "Word Count", "Relevance");
    int ca = 0;
    for (String k : res.keySet()) {
      if (k == null) { // for some reasone there is null
        continue;
      }
      int wc = res.get(k);
      ca += wc;
      double rel = 100.0 * wc / tw;
      System.out.format("%-30s%-30d%f%%\n", k, wc, rel);
    }
    System.out.format("%-30s%-30d%f%%\n", "Total", ca, 100.0 * ca / tw);
    System.out.format("%-30s%-30d\n", "Total Words", tw);
  }

  /**
   * Trie node class used to build trie
   */
  private static class TrieNode {
    public String word;                                // the word on this node
    public boolean isWord;                             // if this node is a word
    public int visited;                                // how many time this node has been visited
    public HashMap<Character, TrieNode> children;      // children of this node

    public TrieNode() {
      this("");
    }

    public TrieNode(String s) {
      this.word = s;
      this.isWord = false;
      this.visited = 0;
      this.children = new HashMap<Character, TrieNode>();
    }
  }

  /**
   * Trie structure to store the dictionary
   */
  private static class Trie {
    public TrieNode root;                              // root node

    public Trie() {
      this.root = new TrieNode();
    }

    /**
     * Insert a word into this trie.
     * @param String s The word to insert.
     */
    public void insert(String s) {
      s = s.replaceAll("[^A-Za-z0-9 ]", "");  // trim the string, get rid of special symbol
      TrieNode current = this.root;
      for (int i = 0; i < s.length(); i++) {
        if (current.children.get(s.charAt(i)) == null) {
          current.children.put(s.charAt(i), new TrieNode(current.word + s.charAt(i)));
        }
        current = current.children.get(s.charAt(i));
      }
      current.isWord = true;
      // System.out.println("build: " + current.word);
    }
  }
}
