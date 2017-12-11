import java.io.*;
import java.util.*;
import java.lang.Double;

public class linkState {
  public static final int initTTL = 10;

  public static void main(String[] args) throws FileNotFoundException {
    Scanner s = new Scanner(new File("infile.dat"));
    Map<String, Router> rMap = initRouter(s);
    shell(rMap);
  }

  // TODO: paste the right version here
  private static Map<String, Router> initRouter(Scanner s) {
    Map<String, Router> rM = new HashMap<String, Router>();
    String rt = "";
    while(s.hasNextLine()) {
      String str = s.nextLine();
      String[] l = str.split(" ");
      if (str.charAt(0) != ' ') {
        rt = l[0];
        rM.put(rt, new Router(rt, l[1], new HashMap<String, Double>()));
      } else {
        if (l.length > 2) {
          rM.get(rt).costRef.put(l[1], Double.parseDouble(l[2]));
        } else {
          rM.get(rt).costRef.put(l[1], 1.0);
        }
      }
    }
    return rM;
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
 			LSP tmp = m.get(key).originatePacket();
 			if (tmp != null) {
 				fringe.add(tmp);
 			}
 		}
 		while(!fringe.isEmpty()) {
 			LSP p = fringe.remove();
 			for (String r : m.get(p.sender).costRef.keySet()) {
 				LSP tmp = m.get(r).receivePacket(p);
 				if (tmp != null) {
 					fringe.add(tmp);
 				}
 			}
 		}
 	}
}
