import java.util.*;

public class LSP {
	public String router;
	public String network;
	public int ttl;
	public int sequence;
	public Map<String, Double> cost;
	public String sender;
	public String target;

	public LSP(String router,
			   String network,
			   int ttl,
			   int sequence,
			   Map<String, Double> cost) {
		this.router = router;
		this.network = network;
		this.ttl = ttl;
		this.sequence = sequence;
		this.cost = cost;
	}
}
