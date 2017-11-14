import java.util.*;
import java.io.*;

public class TrieA {

  public static void main(String[] args) throws FileNotFoundException {
    // word that not count toward final result
    HashMap<String, Integer> banList = new HashMap<String, Integer>(); //= {"a", "an", "the", "and", "or", "but"};
    banList.put("a", 1);
    banList.put("an", 1);
    banList.put("the", 1);
    banList.put("and", 1);
    banList.put("or", 1);
    banList.put("but", 1);

    String articleName = "articletest.dat";
    String companyList = "companytest.dat";

    Scanner articleScanner = new Scanner(new File(articleName));
    Scanner companyScanner = new Scanner(new File(companyList));

    Trie t = buildTrie(articleScanner, banList);
    output(t, companyScanner);
  }

  public static Trie buildTrie(Scanner s, HashMap<String, Integer> bl) {
    Trie result = new Trie();
    while (s.hasNextLine()) {
      String[] arr = s.nextLine().split(" ");
      for (int i = 0; i < arr.length; i++) {
        String str = arr[i];
        if (bl.containsKey(str.toLowerCase())) {
          continue;
        }
        str = str.replaceAll("[^a-zA-Z]", "");
        result.insert(str);
      }
    }
    return result;
  }

  // System.out.format("%-15s%-15s%-15s\n", s1, s2, s3);
  public static void output(Trie t, Scanner cl) {
    System.out.format("%-15s%-15s%-15s\n", "Comapny", "Hit Count", "Relavance");
    double totalWord = t.totalWord();
    int totalCompany = 0;
    while (cl.hasNextLine()) {
      String s = cl.nextLine();
      // System.out.println(s);
      if (s.matches("^[.]+$")) {
        continue;
      }
      String[] arr = s.split("\t");
      String company = arr[0];
      int wordCount = 0;
      for (int i = 0; i< arr.length; i++) {
        // System.out.println()
        wordCount += t.search(arr[i]);
        // System.out.println(wordCount);
      }
      double rel = 1.0 * wordCount / totalWord;
      totalCompany += wordCount;
      System.out.format("%-15s%-15d%-15f\n", company, wordCount, rel);
    }
    double totalRel = 1.0 * totalCompany / totalWord;
    System.out.format("%-15s%-15d%-15f\n", "Total", totalCompany, totalRel);
    System.out.format("%-15s%-30d\n", "TotalWords", totalWord);
  }

  /**
   * TrieNode Object,
   */
  private static class TrieNode {
    public TrieNode[] childrens;
    public boolean isWord;
    public int count;
    public int wordCount;

    public TrieNode() {
      this.childrens = new TrieNode[58];
      this.count = 0;
      this.wordCount = 0;
    }
  }

  private static class Trie {

    private TrieNode root;

    public Trie() {
      this.root = new TrieNode();
    }

    public void insert(String s) {
      TrieNode current = this.root;
      for (int i = 0; i < s.length(); i++) {
        int index = s.charAt(i) - 'A';
        if (current.childrens[index] == null) {
          current.childrens[index] = new TrieNode();
        }
        current.count += 1;
        current = current.childrens[index];
      }
      current.isWord = true;
      current.wordCount += 1;
    }

    public int search(String s) {
      TrieNode current = this.root;
      for (int i = 0; i < s.length(); i++) {
        int index = s.charAt(i) - 'A';
        System.out.println(s.charAt(i));
        if (current.childrens[index] == null) {
          return 0;
        }
        current = current.childrens[index];
      }
      if (!current.isWord) {
        return 0;
      }
      return current.wordCount;
    }

    public int totalWord() {
      return root.count;
    }
  }
}
