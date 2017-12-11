import java.util.*;

public class testR {

	public static void main(String[] args) {
		Map<String, Double> cr1 = new HashMap<String, Double>();
		cr1.put("2", 1.0);
		Router r1 = new Router("1", "1.1.1.1", cr1);

		Map<String, Double> cr2 = new HashMap<String, Double>();
		cr2.put("1", 1.0);
		Router r2 = new Router("2", "2.2.2.2", cr2);

		Map<String, Router> rM = new HashMap<String, Router>();
		rM.put("1", r1);
		rM.put("2", r2);

		proto_process(rM);

	// 	for (String r: rM.keySet()) {
	// 		if (r.equals("1")) {
	// 			continue;
	// 		}
	// 		rM.get(r).printRoutingTable();
	// 	}
  //
	// 	proto_process(rM);
  //
	// 	for (String r: rM.keySet()) {
	// 		if (r.equals("1")) {
	// 			continue;
	// 		}
	// 		rM.get(r).printRoutingTable();
	// 	}
	}

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
