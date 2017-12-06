import java.util.*;
import java.lang.Double;

public class Router {

  private static final int initTTL = 10;
  // basic info
  private String routerID;
  private String network;                       // network it carries
  public Map<String, Double> cost;              // routerID, cost(real, may change)
  private int tickCounter;                      // how many tick
  private int sequenceNum;                      // how many LSP sent
  private boolean isAlive;                      // if the router is shutdown

  // visiter and neighbor status
  private Map<String, Integer> visiter;         // originate router, sequenceNum
  private Map<String, Integer> neighbor;        // routerID, tickCounter

  // routing table
  private Map<String, Double> networks;         // network, cost
  private Map<String, String> networkRouter;    // network, routerID

  public Router(String routerID,
                String network,
                Map<String, Double> cost,
                Map<String, Double> networks,
                Map<String, String> networkRouter) {
    this.routerID = routerID;
    this.cost = new HashMap<String, Double>(cost);
    this.network = network;
    this.tickCounter = 0;
    this.sequenceNum = 1;
    this.isAlive = true;
    this.networks = networks;
    this.networkRouter = networkRouter;
    this.neighbor = new HashMap<String, Integer>();
    this.visiter = new HashMap<String, Integer>();
  }

  /**
   * shutdown the router
   */
  public void shutdown() {
    this.isAlive = false;
  }

  /**
   * startup the router
   */
  public void boot() {
    this.isAlive = true;
  }

  /**
   * originatePacket from this router, does nothing if the router is shutdown.
   * @return a queue of packets which needs to be send out
   */
  public Queue<LSP> originatePacket() {
    if (!this.isAlive) {
      return null;
    }
    Queue<LSP> res = new LinkedList<LSP>();
    updateNetworks();
    // System.out.println("Originating LSP of " + this.routerID);
    for (String key : this.cost.keySet()) {
      LSP p = new LSP(this.routerID, this.sequenceNum, initTTL,
                      clone(this.networks), this.routerID, key);
      this.sequenceNum = this.sequenceNum + 1;
      res.add(p);
    }
    return res;
  }

  /**
   * receive a packet from sender, produce a queue of packets that need to be
   * forwarded. Does nothing if the router is shutdown.
   * @param  LSP packet        received packet
   * @return     a queue of forwarding packet
   */
  public Queue<LSP> receivePacket(LSP packet) {
    if (!this.isAlive) {
      return null;
    }
    this.neighbor.put(packet.sender, this.tickCounter);
    packet.ttl = packet.ttl - 1;
    // ttl reaches 0 or a greater sequence number has been seen.
    if (packet.ttl == 0 || lowerSequence(packet.master, packet.sequenceNum)) {
      return null;
    }

    // prepare to send the packet
    compareNetworks(packet);
    Queue<LSP> res = new LinkedList<LSP>();
    this.visiter.put(packet.master, packet.sequenceNum);
    for (String key : this.cost.keySet()) {
      if (!key.equals(packet.sender)) {
        LSP p = new LSP(packet.master, packet.sequenceNum, packet.ttl,
                        clone(packet.networks), this.routerID, key);
        p.target = key;
        this.sequenceNum = this.sequenceNum + 1;
        res.add(p);
      }
    }
    return res;
  }

  /**
   * print the current routing table of this router
   */
  public void printRoutingTable() {
    System.out.printf("Routing Table for Router %s is:\n", this.routerID);
    for (String n : this.networks.keySet()) {
      if (!n.equals(this.network) && this.networks.get(n) != Double.POSITIVE_INFINITY) {
        System.out.printf("\t%s, %d, %s\n", n, this.networks.get(n).intValue(),
                          this.networkRouter.get(n));
      }
    }
  }

  /**
   * make a clone of a map
   * @param  Map<String, Double>       the map we want to clone.
   * @return             cloned map
   */
  private Map<String, Double> clone(Map<String, Double> m) {
    Map<String, Double> c = new HashMap<String, Double>();
    for (String key : m.keySet()) {
      c.put(key, m.get(key));
    }
    return c;
  }

  /**
   * get a cost of a link, the cost is infinity if the router hasn't sent any
   * packet for past 2 tick.
   * @param  String routerID      the router this link connect to.
   * @return        the cost of the link.
   */
  private double linkCost(String routerID) {

    // initially nothing in neighbor
    if (!this.neighbor.containsKey(routerID)) {
      this.neighbor.put(routerID, this.tickCounter);
    }

    // not recv packet from this, cost is inf
    if (this.tickCounter - this.neighbor.get(routerID) > 1) {
      return Double.POSITIVE_INFINITY;
    }
    return this.cost.get(routerID);
  }

  /**
   * update the network graph after the tick
   */
  private void updateNetworks() {
    for (String network : this.networks.keySet()) {
      if (!network.equals(this.network) && linkCost(this.networkRouter.get(network)) == Double.POSITIVE_INFINITY) {
        this.networks.put(network, Double.POSITIVE_INFINITY);
      }
    }
  }

  /**
   * compare and update the network after receive a LSP
   * @param LSP p the LSP router received
   */
  private void compareNetworks(LSP p) {
    for (String network : p.networks.keySet()) {
      if (network.equals(this.network)) {
        continue;
      }
      System.out.println("host " + this.routerID + " sender " + p.sender);
      double newCost = p.networks.get(network) + linkCost(p.sender);
      p.networks.put(network, newCost);
      if (!this.networks.containsKey(network) || newCost < this.networks.get(network)) {
        this.networks.put(network, newCost);
        this.networkRouter.put(network, p.sender);
      }
    }
  }

  /**
   * check if the packet's origin router has already visited with a higher sequence
   * number.
   * @param  String router        LSP's originate router.
   * @param  int    sequenceNum   LSP's sequence number.
   * @return        boolean value of the comparison.
   */
  private boolean lowerSequence(String router, int sequenceNum) {
    if (!this.visiter.containsKey(router)) {
      return false;
    }
    return sequenceNum <= this.visiter.get(router);
  }
}
