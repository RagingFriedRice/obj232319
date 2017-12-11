public class Vertex implements Comparable<Vertex> {
	
	public String routerID;
	public Double cost;
	public String ol;
	
	public Vertex(String rid, Double cost, String ol) {
		this.routerID = rid;
		this.cost = cost;
		this.ol = ol;
	}
	
	public int compareTo(Vertex b) {
		return this.cost.compareTo(b.cost);
	}
}