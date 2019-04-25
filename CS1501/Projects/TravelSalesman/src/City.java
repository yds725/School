//City class
//Cities are like nodes in tree

import java.util.ArrayList;

public class City {

    public ArrayList<City> cities; // list to keep cities(vertices)
    public double distance; // edge weight of graph
    public String name; //name of the city
    public City closetNeighbor; //closet neighbor city

    public int x_coor; // x coordinate
    public int y_coor; // y coordinate

    public City(int x, int y, String name){
        this.cities = new ArrayList<>();
        this.distance = 0;
        this.name = name;
        this.x_coor = x;
        this.y_coor = y;
    }

    public void mostAdjacent(City source){
        this.closetNeighbor = source;
    }

    public void addCity(City child){
        this.cities.add(child);
    }
}
