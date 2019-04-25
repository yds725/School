//Using Prim's Algorithm to find MST


import java.util.ArrayList;
import java.util.Stack;

public class EuclideanTSP {

    public ArrayList<Edge> edges;
    public boolean[] marked;
    public City[] arrayOfCities;
    public int[] closestCity;

    public double[] totalMinimumEdgeWeight;

    public EuclideanTSP(City[] city){
        this.arrayOfCities = city;
    }

    //try to find edge weight(distance) between all cities and source city(start)

    public double generateMST(){

        int height;
        int width;
        double totalEdgeDistance = 0; //total edge weight

        for(int i = 1; i < arrayOfCities.length; i++){
            //c^2 = a^2+b^2 pythagorean triangle rule
            height = Math.abs(arrayOfCities[i].x_coor - arrayOfCities[0].x_coor);
            width = Math.abs(arrayOfCities[i].y_coor - arrayOfCities[0].y_coor);

            arrayOfCities[i].distance = Math.sqrt((height*height) + (width*width));
            arrayOfCities[i].closetNeighbor = arrayOfCities[0];
        }

        //for all cities not marked yet
        for(int i = 1; i < arrayOfCities.length; i++){
            int shortestNeighbor = -1;
            double shortestDistance =Double.MAX_VALUE;

            //find shortest distance and city to that shortest distance
            for(int j = i; j < arrayOfCities.length; j++){
                if(arrayOfCities[j].distance < shortestDistance) {
                    shortestNeighbor = j;
                    shortestDistance = arrayOfCities[j].distance;
                }
            }

            totalEdgeDistance += shortestDistance;
            //add closest city from the parent(from city)
            arrayOfCities[shortestNeighbor].closetNeighbor.addCity(arrayOfCities[shortestNeighbor]);

            //check that city has been visited by swapping visited index into i
            if(i < arrayOfCities.length -1){
                City temp = arrayOfCities[i];
                arrayOfCities[i] = arrayOfCities[shortestNeighbor];
                arrayOfCities[shortestNeighbor] = temp;
            }

            // change distance between each city from minimum spanning tree
            double distance;
            for(int j=i+1; j < arrayOfCities.length; j++){
                height = Math.abs(arrayOfCities[i].x_coor - arrayOfCities[j].x_coor);
                width = Math.abs(arrayOfCities[i].y_coor - arrayOfCities[j].y_coor);

                distance = Math.sqrt((height*height) + (width*width));

                if(distance < arrayOfCities[j].distance){
                    arrayOfCities[j].closetNeighbor = arrayOfCities[i];
                    arrayOfCities[j].distance = distance;
                }
            }

        }
        return  totalEdgeDistance;
    }

    public ArrayList<Edge> getAllEdges(){
        edges = new ArrayList<>(arrayOfCities.length-1);
        getAllEdges(arrayOfCities[0]);
        return edges;
    }

    //get all edges recursively of MST (preorder)
    public void getAllEdges(City city){
        for(int i = 0; i <city.cities.size(); i++){
            edges.add(new Edge(city.cities.get(i), city));
            getAllEdges(city.cities.get(i));
        }
    }

    public ArrayList<City> getTourWalk(){
        ArrayList<City> tour = new ArrayList<>(arrayOfCities.length);

        Stack<City> track = new Stack<>();
        tour.add( track.push(arrayOfCities[0])); //push very first city to stack and add it to tour walk arraylist

        while( !track.empty()){
            City current = track.peek();
            if(current.cities.isEmpty()) {
                track.pop();
            }else{
                tour.add( track.push( current.cities.remove(0)));
            }
        }
        tour.add(arrayOfCities[0]);
        return tour;
    }

}
