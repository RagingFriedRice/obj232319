import java.util.*;

// the lsp object, this implementation using adjacency list structure.
public class LSP {
  public String master;
  public int sequenceNum;
  public int ttl;
  public Map<String, Double> networks;
  public String sender;
  public String target;

  public LSP(String master,
             int sequenceNum,
             int ttl,
             Map<String, Double> networks,
             String sender,
             String target) {
    this.master = master;
    this.sequenceNum = sequenceNum;
    this.ttl = ttl;
    this.networks = networks;
    this.sender = sender;
    this.target = target;
  }
}
