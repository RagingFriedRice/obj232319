import java.io.*;
import java.util.*;

public class linkState {
  public static final int initTTL = 10;

  public static void main(String[] args) throws FileNotFoundException {
    Scanner s = new Scanner(new File("infile.dat"));
    Map<String, Router> rMap = initRouter(s);
    shell(rMap);
  }

  // TODO: needs to double check this
  private static Map<String, Router> initRouter(Scanner s) {
    Map<String, Router> rMap = new HashMap<String, Router>();
    Queue<String> buffer = new LinkedList<String>();
    while (s.hasNextLine()) {
      buffer.add(s.nextLine());
    }
    String r = buffer.remove();

    while (!buffer.isEmpty()) {
      String[] temp = r.split(" ");
      String rID = temp[0];
      String network = temp[1];
      Map<String, Double> cost = new HashMap<String, Double>();
      Map<String, Double> networks = new HashMap<String, Double>();
      Map<String, String> networkRouter = new HashMap<String, String>();

      // network of itself
      networks.put(network, 0.0);
      networkRouter.put(network, rID);

      String tempStr = buffer.remove();
      while (tempStr.charAt(0) == ' ') {
        temp = tempStr.split(" ");
        String rout = temp[1];
        double c = Double.parseDouble(temp[2]);
        cost.put(rout, c);
        tempStr = buffer.remove();
      }
      Router tempRouter = new Router(rID, network, cost, networks, networkRouter);
      rMap.put(rID, tempRouter);
      r = tempStr;
    }
    return rMap;
  }

  /**
   * the shell prompt for this hw
   * @param Map<String, Router> rMap map of routers we have.
   */
  private static void shell(Map<String, Router> rMap) {
    Scanner s = new Scanner(System.in);
    while(true) {
      System.out.println("C, Q, P, S, T: ");
      String[] input = s.nextLine().split(" ");

      switch(input[0]) {
        case "Q":
          return;
        case "C":
          proto_process(rMap);
          break;
        case "P":
          rMap.get(input[1]).printRoutingTable();
          break;
        case "S":
          rMap.get(input[1]).shutdown();
          break;
        case "T":
          rMap.get(input[1]).boot();
          break;
        default:
          System.out.println("wrong command");
      }
    }
  }

  /**
   * processing the lsp protocol by starting to generate and send lsp packet from
   * each router. this method make sure that lsp with higher ttl get sent first.
   * @param Map<String, Router> m   all the routers.
   */
  private static void proto_process(Map<String, Router> m) {
    Queue<LSP> fringe = new LinkedList<LSP>();
    for (String key : m.keySet()) {
      Queue<LSP> tmp = m.get(key).originatePacket();
      while(!tmp.isEmpty()) {
        fringe.add(tmp.remove());
      }
    }
    while(!fringe.isEmpty()) {
      LSP p = fringe.remove();
      Queue<LSP> tmp = m.get(p.target).receivePacket(p);
      while(tmp != null && !tmp.isEmpty()) {
        fringe.add(tmp.remove());
      }
    }
  }
}
