import java.util.*;
import java.lang.Double;

public class Router {
	public String routerID;
	public String network;
	public int sequence;
	public int tick;
	public Map<String, Double> networks;						// all networks in this graph
	public Map<String, String> networkRouter;					// network, routerID outlink
	public Map<String, Double> costRef;							// reference for cost
	public Map<String, Double> neighborCost;					// cost of neighbor
	public Map<String, Integer> neighborStates;					// neighbor, tick
	public Map<String, Integer> routerStates;					// router, sequence
	public Map<String, Map<String, Double>> adjList;			// adjecency list
	public boolean isAlive;
	public Map<String, String> rtn;

	public Router(String routerID,
				  String network,
				  Map<String, Double> costRef) {
		this.routerID = routerID;
		this.network = network;
		this.sequence = 1;
		this.tick = 0;
		this.networks = new HashMap<String, Double>();
		this.networks.put(this.network, 0.0);
		this.networkRouter = new HashMap<String, String>();
		this.networkRouter.put(routerID, network);
		this.costRef = costRef;
		this.neighborCost = new HashMap<String, Double>();
		this.neighborStates = new HashMap<String, Integer>();
		this.routerStates = new HashMap<String, Integer>();
		this.adjList = new HashMap<String, Map<String, Double>>();
		this.isAlive = true;
		this.rtn = new HashMap<String, String>();
	}

	public void shutdown() {
		this.isAlive = false;
	}

	public void boot() {
		this.isAlive = true;
	}

	public LSP originatePacket() {
		if (!this.isAlive) {
			return null;
		}
		updateNetworks();
		for (String key : this.neighborCost.keySet()) {
			System.out.println("after update " + this.routerID + " " + key + " " + this.neighborCost.get(key));
		}
		LSP p = new LSP(this.routerID, this.network, 10, this.sequence, this.neighborCost);
		p.sender = this.routerID;
		this.sequence = this.sequence + 1;
		return p;
	}

	public LSP receivePacket(LSP p) {
		p.ttl = p.ttl - 1;
		if (!this.isAlive || p.ttl < 1 || lowerSequence(p.router, p.sequence)) {
			return null;
		}
		this.adjList.put(p.router, p.cost);
		this.routerStates.put(p.router, p.sequence);
		String source = p.sender;
		this.neighborStates.put(source, this.tick);
		this.rtn.put(p.router, p.network);
		p.sender = this.routerID;
		return p;
	}

	public void printRoutingTable() {
		System.out.println("The routing table of router " + this.routerID + ": ");
		apsp();
		for (String n : this.networks.keySet()) {
			if (n.equals(this.network)) {
				continue;
			}
			String nw = n;
			int cost = this.networks.get(n).intValue();
			String ol = this.networkRouter.get(n);
			System.out.printf("Network: %s, Cost: %d, Outlink: %s\n", nw, cost, ol);
		}
		System.out.println();
	}

	// update networks after each tick
	private void updateNetworks() {
		this.tick = this.tick + 1;
		for (String key : this.costRef.keySet()) {
			System.out.println("updateing " + this.routerID + " neighbor " + key + " my tick " + this.tick);
			System.out.println(key + " record tick " + this.neighborStates.get(key));
			if (!this.neighborStates.containsKey(key) || this.tick - this.neighborStates.get(key) > 1) {
				this.neighborCost.put(key, Double.POSITIVE_INFINITY);
			} else {
				this.neighborCost.put(key, this.costRef.get(key));
			}
		}
	}

	
	private boolean lowerSequence(String router, int sequenceNum) {
		if (!this.routerStates.containsKey(router)) {
			return false;
		}
		return sequenceNum <= this.routerStates.get(router);
	}

	// djikstra apsp algo
	private void apsp() {
		System.out.println("APSP ROUTER " + this.routerID);
		System.out.println("----------------------------------------------");
		Map<String, Double> nwc = new HashMap<String, Double>();
		Map<String, String> nwr = new HashMap<String, String>();
		PriorityQueue<Vertex> q = new PriorityQueue<Vertex>();
		Set<String> visited = new HashSet<String>();
		for (String r : routerStates.keySet()) {
			if (r.equals(this.routerID)) {
				q.add(new Vertex(r, 0.0, this.routerID));
			} else {
				q.add(new Vertex(r, Double.POSITIVE_INFINITY, this.routerID));
			}
		}
		while (!q.isEmpty()) {
			Vertex n = q.poll();

			// priority queue gives a inf cost vertex, the rest cost is inf, break the loop
			if (n.cost == Double.POSITIVE_INFINITY) {
				break;
			}

			// already visted
			if (visited.contains(n.routerID)) {
				continue;
			}

			visited.add(n.routerID);
			// TODO: NANA
			String tmpRt = n.routerID;
			if (!nwc.containsKey(this.rtn.get(tmpRt)) || nwc.get(this.rtn.get(tmpRt)) > n.cost) {
				nwc.put(this.rtn.get(tmpRt), n.cost);
				nwr.put(this.rtn.get(tmpRt), n.ol);
			}
			Map<String, Double> tmpneighbor =l this.adjList.get(tmpRt);
			for (String rt : tmpneighbor.keySet()) {
				if (this.costRef.containsKey(rt)) {
					q.add(new Vertex(rt, n.cost + tmpneighbor.get(rt), rt));
				} else {
					q.add(new Vertex(rt, n.cost + tmpneighbor.get(rt), n.routerID));
				}
			}
		}
		this.networks = nwc;
		this.networkRouter = nwr;
		System.out.println("----------------------------------------------");
	}

//	private Map<String, Double> clone(Map<String, Double> m) {
//	    Map<String, Double> c = new HashMap<String, Double>();
//	    for (String key : m.keySet()) {
//	      c.put(key, m.get(key));
//	    }
//	    return c;
//	}
}
