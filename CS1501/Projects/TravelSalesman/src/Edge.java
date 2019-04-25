//Edge between Vertices (Cities)
// We can think of graph as tree having nodes

public class Edge {

    public City parent;
    public City children;

        public Edge(City parent, City children){
            this.parent = parent;
            this.children = children;
        }
}
