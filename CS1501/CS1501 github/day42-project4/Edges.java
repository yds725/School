/**
 * Edge class for GraphFunction class
 * it implements javaEdge library class
 */
public class Edges implements Comparable<Edges>{
    private final int COPPERDATA_SPEED = 230000000;
    private final int OPTICDATA_SPEED = 200000000;

    private String Type; //Optical or Copper edge
    private int Length;
    private int Bandwidth;
    private Vertice StartPoint; // Source or starting point vertext
    private Vertice EndPoint; //Destination Vertext Number
    private double TravelTime; //Time taken to travel on the edge (nano seconds)

    public Edges(String type, int bandwidth, int length, Vertice startPoint, Vertice endPoint){
        Type = type;
        Bandwidth = bandwidth;
        Length = length;
        StartPoint = startPoint;
        EndPoint = endPoint;


        if(Type.equals("copper")){
            TravelTime = Length * ((double) 1/COPPERDATA_SPEED) * Math.pow(10,9); //convert travel time to nanoseconds
        } else if(Type.equals("optical")){
            TravelTime = Length * ((double) 1/OPTICDATA_SPEED) * Math.pow(10,9); //convert travel time to nanoseconds
        }

    }

    public void setType(String newType) {
        Type = newType;
    }


    public void setLength(int newLength) {
        Length = newLength;
    }


    public void setBandwidth(int newBandwidth) {
        Bandwidth = newBandwidth;
    }


    public void setEndPoint(Vertice newEndPoint) {
        EndPoint = newEndPoint;
    }

    public String getType() {
        return Type;
    }

    public int getLength() {
        return Length;
    }

    public int getBandwidth() {
        return Bandwidth;
    }

    public Vertice getStartPoint() {
        return StartPoint;
    }

    public Vertice getEndPoint() {
        return EndPoint;
    }

    public double getTravelTime() {
        return TravelTime;
    }

    public int compareTo(Edges edge){ //compareTo function
        if(TravelTime > edge.getTravelTime()){
            return 1;
        } else if(TravelTime == edge.getTravelTime()){
            return 0;
        } else {
            return -1;
        }
    }

}
