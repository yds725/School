import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Contains many methods for NetworkAnalysis java file
 * minimum spanning trees(Kruskal's algorithm)
 * representing graph into adjacency list
 */

public class GraphFunction {

    private int totalNumVertices;//total number of vertices in Graph
    private Vertice[] vertices; //adjacency list (linked list) represents edge b/t vertices
    private ArrayList<Edges> edges; // keeping track of MST(Kruskal's method)
    private ArrayList<Edges> lowestLatencyMST; //
    private double lowestAvgLatency; // variable for MST
    private boolean isConnectedWithOnlyCopper = true; //variable for determined copper-only connected graph

    private Scanner sc;

    public GraphFunction(String textFile){
        lowestLatencyMST = null;
        makeGraph(textFile);
    }

    //--------------Important methods-----------------------------//

    //First make the graph from textfile
    private void makeGraph(String textFile){
        if(textFile == null)
            return;

        File file = new File(textFile);
        try{
            sc = new Scanner(file);
        }catch (FileNotFoundException e){
            System.out.println("Invalid Text File!");
            return;
        }

        if(sc.hasNextLine()){
            totalNumVertices = Integer.parseInt(sc.nextLine()); //first line in textfile is number of vertices in the graph
        } else {
            return;
        }

        vertices = new Vertice[totalNumVertices]; // making adjacency list
        for(int i =0; i < vertices.length; i++){
            vertices[i] = new Vertice(i); // initialize vertex i with correct number of vertex
        }

        edges = new ArrayList<Edges>();
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] text = line.split(" ");

            if(text.length != 5) // each line in text file must contain 5 informations separated by " "
                continue;

            //declare each information in text file into proper variable for graph-make use
            Vertice A = vertices[Integer.parseInt(text[0])];
            Vertice B = vertices[Integer.parseInt(text[1])];
            String type = text[2];
            int bandwidth = Integer.parseInt(text[3]);
            int length = Integer.parseInt(text[4]);

            //Create two edges b/t two vetices and make them full duplex
            Edges edgeAtoB = new Edges(type, bandwidth, length, A, B);
            Edges edgeBtoA = new Edges(type, bandwidth, length, B, A);
            A.getConnection().addFirst(edgeAtoB);
            B.getConnection().addFirst(edgeBtoA);

            edges.add(edgeAtoB); // add one edge to edges array list for MST
            if(type.equals("optical"))
                isConnectedWithOnlyCopper = false; //if type is optical in textfile then that edge will be false as only-copper cable

        }
    }

    private Object[] computeShortestPath(Vertice current, Vertice end, String path, double length, int minBandwidthCurrentPath){
        if(current == null || end == null || path == null || length < 0.0) //any invalid argument or input will return nothing
            return null;

        if(current == end){ //if destination vertex reached return following data
            return new Object[] {path, length, minBandwidthCurrentPath};
        }

        LinkedList<Edges> currentEdges = current.getConnection(); // get all edges of current vertex or node

        double minLength = -1.0;
        String pathOfMinLength = "";
        for(Edges e : currentEdges){ //permutation of all possible paths (going through over all edges)
            Vertice edgeDestination = e.getEndPoint();
            if(path.contains("" + edgeDestination.getVertexNumber()))
                continue; //The vertex on the end of this edge has already been visited, so just go to the next vertex

            String newPath = path + edgeDestination.getVertexNumber(); //make new path
            double newLength = 0.0;

            if(e.getType().equals("copper")){
                newLength = length + e.getTravelTime();
            } else if(e.getType().equals("optical")){
                newLength = length + e.getTravelTime();
            } else{
                return null; //Wrong cable type!
            }

            int minBandwidthNewPath = minBandwidthCurrentPath; //If edge being travelled now has a bandwidth lower than the current path's bandwidth,
            if(minBandwidthCurrentPath == -1.0 || e.getBandwidth() < minBandwidthCurrentPath)
                minBandwidthNewPath = e.getBandwidth(); //Set the new minimum path bandwidth

            Object[] dataOfPath = computeShortestPath(edgeDestination, end, newPath, newLength, minBandwidthNewPath); //traverse next path recursively
            if(dataOfPath == null)
                continue; //If there is no path data for the edge, go to the next edge

            String edgePath = (String) dataOfPath[0];
            double pathLength = (double) dataOfPath[1];
            int pathBandwidth = (int) dataOfPath[2];

            if(minLength == -1 || pathLength < minLength){ //Set new minimum length path
                minLength = pathLength;
                pathOfMinLength = edgePath;
                minBandwidthCurrentPath = pathBandwidth;
            } else if(pathLength == minLength && pathBandwidth > minBandwidthCurrentPath){ //if path lengths are the same but this new path has  better bandwidth than current path
                minLength = pathLength;
                pathOfMinLength = edgePath;
                minBandwidthCurrentPath = pathBandwidth;
            }
        }

        if(minLength > -1.0){ //if a path to reach dest from current vertex exists, return shortest path data
            return new Object[] { pathOfMinLength, minLength, minBandwidthCurrentPath };
        }

        return null; //if not at the destination and no edges from the current vertex are valid, return nothing
    }

    //compute maximum amount of data can be transmitted from one to another
    private int maximumDataTransmitted(Vertice current, Vertice end, String path, int maxBandwidth){
        if(current == null || end == null || path == null) // invalid input return -1 no value
            return -1;

        if(current == end) {
            return maxBandwidth;
        }

        LinkedList<Edges> currentEdges = current.getConnection();

        int maximum = -1;
        for(Edges e : currentEdges){
            Vertice edgeDest = e.getEndPoint();
            if(path.contains("" + edgeDest.getVertexNumber()))
                continue;

            int newMaxBandwidth = maxBandwidth;
            if(newMaxBandwidth == -1 || e.getBandwidth() < newMaxBandwidth)
                newMaxBandwidth = e.getBandwidth(); // set new minimum bandwidth?

            String newPath = path + edgeDest.getVertexNumber();
            int pathBandwidth = maximumDataTransmitted(edgeDest, end, newPath, newMaxBandwidth); //traverse graph recursively
            if(pathBandwidth == -1)
                continue; // if there is no data(bandwidth) go to next edge

            if(pathBandwidth > maximum)
                maximum = pathBandwidth; // get new path with max bandwidth
        }

        return maximum;
    }


    private void determineConnectedTwoVerticesFail(Vertice current, Vertice A, Vertice B, boolean[] isVisited){
        if(current == null || A == null || B == null || isVisited == null)
            return; //Invalid input return nothing

        if(isVisited[current.getVertexNumber()] == true){ //if current vertex already visited just return
            return;
        }

        isVisited[current.getVertexNumber()] = true; //set current vertex as visited

        LinkedList<Edges> currentEdges = current.getConnection();

        for(Edges e : currentEdges){ //do DFS search to traverse all edge or vertex except failed vertices in graph
            Vertice edgeDest = e.getEndPoint(); // get endpoint of vertex of current edge
            if(isVisited[edgeDest.getVertexNumber()]==true)
                continue;

            determineConnectedTwoVerticesFail(edgeDest, A, B, isVisited); // traverse graph recursively

        }

        return;
    }

    //Use Kruskal's method to find MST of graph with lowest weight
    private  double MSTOfKruskal(){
        // make union-find components; from book
        int[] parent = new int[totalNumVertices];
        byte[] rank = new byte[totalNumVertices];

        for(int i = 0; i < totalNumVertices; i++){
            parent[i] = i;
            rank[i] = 0;
        }

        Collections.sort(edges, (e1, e2)->e1.compareTo(e2));

        lowestLatencyMST = new ArrayList<Edges>(); // edges in MST
        double weight = 0.0;

        //Kruskal's method
        int currentEdge = 0;
        while(currentEdge != edges.size() -1 && lowestLatencyMST.size() < totalNumVertices -1){ //if edges are still left and MST has not reached all vertices
            Edges e = edges.get(currentEdge);
            int v = e.getStartPoint().getVertexNumber();
            int w = e.getEndPoint().getVertexNumber();

            if(!connected(v,w, parent)){// if edge of (v,w) does not create cycle
                union(v,w,parent, rank);// add edge of (v,w) to MST 's union of all its edges
                lowestLatencyMST.add(e); // add edge to MST
                weight += e.getTravelTime();
            }

            currentEdge++; // look at next edge when next iteration
        }

        return weight;

    }

    //union the component containing site p with component containing site q
    private void union(int p, int q, int[] parent, byte[] rank){
        int pRoot = find(p, parent);
        int qRoot = find(q, parent);
        if(pRoot == qRoot)
            return;

        if (rank[pRoot] <rank[qRoot]) {
            parent[pRoot] = qRoot;
        } else if(rank[pRoot] > rank[qRoot]){
            parent[qRoot] = pRoot;
        } else {
            parent[qRoot] = pRoot;
            rank[pRoot]++;
        }
    }

    //return true if two sites are in same component
    private boolean connected(int p, int q, int[] parent){
        return find(p, parent) == find(q, parent);
    }

    //returns identifier for component containing site p or q
    private int find(int p, int[] parent){
        while(p != parent[p]){
            parent[p]=parent[parent[p]]; //path compresssion by halfing
            p = parent[p];
        }

        return p;
    }

    //----------Method for NetworkAnalysis main file-----------------//

    //find for lowest latency path b/t any two vertices
    public void getLowestLatencyPath(){
        sc =new Scanner(System.in);
        Vertice startVertex = null;
        Vertice endVertex = null;

        //prompt user for two vertices that they wish to find LLP
        do{
            System.out.print("\nEnter the number(0 to v-1) of first vertex: ");
            String userInput = sc.nextLine();
            int key = Integer.parseInt(userInput);

            if(key >= totalNumVertices || key < 0){
                System.out.println("\nInvalid Vertex! Either exceed total number of vertices or typed negative number!\n");
            } else {
                startVertex = vertices[key];
            }
        }while(startVertex == null);

        //prompt user for second vertex
        do{
            System.out.print("\nEnter the number(0 to v-1) of second vertex: ");
            String userInput = sc.nextLine();
            int key = Integer.parseInt(userInput);

            if(key >= totalNumVertices || key < 0 || key == startVertex.getVertexNumber()){
                System.out.println("\nInvalid Vertex! Either exceed total number of vertices or typed negative number!\n");
            } else {
                endVertex = vertices[key];
            }
        }while(endVertex == null);

        //compute shortest path between two vertices and print output for user
        Object[] dataOfPath = computeShortestPath(startVertex, endVertex, "" + startVertex.getVertexNumber(), 0L, -1);
        if(dataOfPath == null)
            return;

        String path = (String) dataOfPath[0];
        String pathDirected = "";

        for(int i =0; i<path.length(); i++){
            if(i< path.length() - 1){
                pathDirected += path.charAt(i) + " -> ";
            } else {
                pathDirected += path.charAt(i);
            }
        }

        path = pathDirected;
        double pathTravelTime = (double) dataOfPath[1]; // time to send data from start to end vertex in nanoseconds
        int minBandwidthOfPath = (int) dataOfPath[2]; // minimum bandwidth of path b/t vertices; max amount of data allowed in the path
        System.out.printf("\nLowest latency path: %s\nTime taken; %.2f nanosec\nMinimum Bandwidth: %d Mbps \n", path, pathTravelTime, minBandwidthOfPath);
    }

    // determine whether or not graph is copper-only connected
    public void isCopperOnlyConnected(){
        if(isConnectedWithOnlyCopper){ //if the graph only connected with copper
            System.out.println("\nThe graph is only copper-connected. It is consisted of only copper wires.");
        } else {
            boolean copperConnected = true; //assume graph is connected with copper

            //iterate thru all vertices and check if has copper connection
            for(int i = 0; i < totalNumVertices; i++){
                LinkedList<Edges> vertexEdges = vertices[i].getConnection();

                boolean hasCopperCable = false;
                for(Edges e : vertexEdges){
                    if(e.getType().equals("copper")){
                        hasCopperCable = true;
                        break;
                    }
                }

                if(!hasCopperCable){
                    copperConnected = false;
                    break;
                }
            }

            if(copperConnected) {
                System.out.println("\nThe graph may be connected with only copper cables. However, this graph currently has optical cables.");
            } else{
                System.out.println("\nThis graph is not copper-only and can't be connected with only copper cables.");
            }
        }
    }

    //compute MST with lowest avg latency ; or tree that allows for fastest data transfer across the graph
    public void getLowestAvgLatencySpanningTree(){
        //generate MST using Kruskal's method
        if(lowestLatencyMST == null){
            lowestAvgLatency = MSTOfKruskal() / lowestLatencyMST.size(); //dividing total latency by total # of edges to get average latency
        }

        System.out.println("\nLowest Avg Latency Spanning Tree Edges are:\n");
        for(Edges e : lowestLatencyMST){
            System.out.println("(" + e.getStartPoint().getVertexNumber() + " , " + e.getEndPoint().getVertexNumber() + ")");
        }

        //print out avg latency of this MST
        System.out.printf("\nAverage latency of this MST: %.2f nanosec.\n", lowestAvgLatency);
    }

    public void getMaximumData(){
        sc = new Scanner(System.in);
        Vertice startVertex = null;
        Vertice endVertex = null;

        //prompt user for two vertices that they wish to find LLP
        do{
            System.out.print("\nEnter the number(0 to v-1) of first vertex: ");
            String userInput = sc.nextLine();
            int key = Integer.parseInt(userInput);

            if(key >= totalNumVertices || key < 0){
                System.out.println("\nInvalid Vertex! Either exceed total number of vertices or typed negative number!\n");
            } else {
                startVertex = vertices[key];
            }
        }while(startVertex == null);

        //prompt user for second vertex
        do{
            System.out.print("\nEnter the number(0 to v-1) of second vertex: ");
            String userInput = sc.nextLine();
            int key = Integer.parseInt(userInput);

            if(key >= totalNumVertices || key < 0 || key == startVertex.getVertexNumber()){
                System.out.println("\nInvalid Vertex! Either exceed total number of vertices or typed negative number!\n");
            } else {
                endVertex = vertices[key];
            }
        }while(endVertex == null);

        //compute maximum amount of data can be transmitted b/t tow vertices and print out that maximum bandwidth
        int maximumBandwidth = maximumDataTransmitted(startVertex, endVertex, "" + startVertex.getVertexNumber(), -1);
        System.out.println("\nMax amount of data b/t vertices " + startVertex.getVertexNumber() + " and " + endVertex.getVertexNumber() + ": " + maximumBandwidth + " Megabytes per sec");
    }

    //determine whether or not graph remain connected if any two vertices in graph failure
    public void isConnectedOnVerticesFailure(){

        for(int i =0; i < totalNumVertices - 1; i++){
            for(int j = i+1; j < totalNumVertices; j++){
                // ignore vertice i and j (because any two vertices are failure)
                //if path's length reaches total number of vertices - 2, then it is connected
                Vertice startVertex = null;
                Vertice failedA = vertices[i];
                Vertice failedB = vertices[j];
                boolean[] isVisited = new boolean[totalNumVertices];

                //mark failed vertices as already visited so no need to visit them again
                isVisited[failedA.getVertexNumber()] = true;
                isVisited[failedB.getVertexNumber()] = true;

                //set startVertex as starting point (can be any vertex except failed ones)
                if(i != 0){ // if vertice 0 is not failed, then startVertext start from there
                    startVertex = vertices[0];
                } else {
                    if(j != totalNumVertices -1){ //vertex number is up to only v-1(total - 1)
                        startVertex = vertices[j+1];
                    } else if(j-i != 1){
                        startVertex = vertices[j-1];
                    } else{
                        System.out.println("\nThis graph would not remain connected when any two vertices fail."); //if there are only two vertices in graph, it will be not connected
                        return;
                    }
                }

                //pass visited vertices
                determineConnectedTwoVerticesFail(startVertex, failedA, failedB, isVisited);

                //check if all vertices are visited; if visited then graph is still connected with failure
                boolean isGraphConnected = true;
                for(int l = 0; l < isVisited.length; l++){
                    if(isVisited[l] == false){ //if vertex is not visited, then it means graph is disconnected
                        isGraphConnected = false;
                        break;
                    }
                }

                if(!isGraphConnected){
                    System.out.println("\nThis graph would not remain connected when any two vertices fail."); //if there are only two vertices in graph, it will be not connected
                    return;
                }
            }
        }
        //after all permutations of two vertices in graph, graph is connected though any two vertices are failed
        System.out.println("\nThis graph would remain connected when any two vertices fail.");
        return;

    }




}
