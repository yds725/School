import java.util.LinkedList;

/**
 * Vertex class for GraphFunction class
 */
public class Vertice{
    private LinkedList<Edges> connection;
    private int vertextNumber;

    public Vertice(int vNumber){
        vertextNumber = vNumber;
        connection = new LinkedList<Edges>();

    }

    public LinkedList<Edges> getConnection(){
        return connection;
    }

    public int getVertexNumber(){
        return vertextNumber;
    }
}
