import java.util.*;
import java.io.*;
import java.lang.Double;

public class testRouter {
  public static void main(String[] args) {

    Map<String, Router> rMap = new HashMap<String, Router>();

    /* test case

         1
      /   \
     2    3
     \   /
      4

      each edge value is 4
    */
    String rIDA = "1";
    String nwA = "192.168.1.1";
    String rIDB = "2";
    String nwB = "192.168.1.2";
    String rIDC = "3";
    String nwC = "192.168.1.3";
    String rIDD = "4";
    String nwD = "192.168.1.4";

    Map<String, Double> networksA = new HashMap<String, Double>();
    Map<String, Double> costA = new HashMap<String, Double>();
    Map<String, String> networkRouterA = new HashMap<String, String>();
    networksA.put(nwA, 0.0);
    costA.put(rIDD, 4.0);
    costA.put(rIDB, 4.0);
    networkRouterA.put(nwA, rIDA);
    Router a = new Router(rIDA, nwA, costA, networksA, networkRouterA);

    Map<String, Double> networksB = new HashMap<String, Double>();
    Map<String, Double> costB = new HashMap<String, Double>();
    Map<String, String> networkRouterB = new HashMap<String, String>();
    networksB.put(nwB, 0.0);
    costB.put(rIDA, 4.0);
    costB.put(rIDC, 4.0);
    networkRouterB.put(nwB, rIDB);
    Router b = new Router(rIDB, nwB, costB, networksB, networkRouterB);

    Map<String, Double> networksC = new HashMap<String, Double>();
    Map<String, Double> costC = new HashMap<String, Double>();
    Map<String, String> networkRouterC = new HashMap<String, String>();
    networksC.put(nwC, 0.0);
    costC.put(rIDB, 4.0);
    costC.put(rIDD, 4.0);
    networkRouterC.put(nwC, rIDC);
    Router c = new Router(rIDC, nwC, costC, networksC, networkRouterC);

    Map<String, Double> networksD = new HashMap<String, Double>();
    Map<String, Double> costD = new HashMap<String, Double>();
    Map<String, String> networkRouterD = new HashMap<String, String>();
    networksD.put(nwD, 0.0);
    costD.put(rIDC, 4.0);
    costD.put(rIDA, 4.0);
    networkRouterD.put(nwD, rIDD);
    Router d = new Router(rIDD, nwD, costD, networksD, networkRouterD);

    Map<String, Router> routers = new HashMap<String, Router>();
    routers.put(rIDA, a);
    routers.put(rIDB, b);
    routers.put(rIDC, c);
    routers.put(rIDD, d);

    rMap = routers;
    // for (String key : rMap.keySet()) {
    //   rMap.get(key).printRoutingTable();
    // }

    // a.debugInfo();

    proto_process(rMap);

    for (String key : rMap.keySet()) {
      rMap.get(key).printRoutingTable();
    }

    proto_process(rMap);

    for (String key : rMap.keySet()) {
      rMap.get(key).printRoutingTable();
    }
  }

  public static void proto_process(Map<String, Router> m) {
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
