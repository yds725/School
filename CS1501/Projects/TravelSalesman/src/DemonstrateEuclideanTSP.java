///
/// Contents: Demonstrate Euclidean TSP Programs
/// Author:   John Aronis
/// Date:     July 2018
///

import java.awt.* ;
import javax.swing.* ;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.Random ;

public class DemonstrateEuclideanTSP {

  public static int WIDTH=800, HEIGHT=800, WAIT=3000 ;
  City[] cities = new City[5];
  public static void main(String[] args) {

    //
    // Create cities:
    //

    if(args.length != 1){
      System.out.println("Please enter number of cities you want to simulate!");
      System.exit(0);
    }

    int cityNumbers = Integer.parseInt(args[0]);

    City[] cities = new City[cityNumbers]; // get array of city
    Random rand = new Random(); // random seed
    char[] name = new char[3];

    for(int i = 0; i < cityNumbers; i++){
      name[0] = (char)(rand.nextInt(26) + 'a');
      name[1] = (char)(rand.nextInt(26) + 'a');
      name[2] = (char)(rand.nextInt(26) + 'a');

      cities[i] = new City(rand.nextInt(1024), rand.nextInt(768), new String(name));
    }

//    ArrayList<City> cities = smallCities() ;     // Small test.
    //ArrayList<City> cities = randomCities(Integer.parseInt(args[0]),WIDTH,HEIGHT) ;

    //
    // MST-walk algorithm:
    //

     ;/*ArrayList<Edge> MST = EuclideanTSP.MST(cities) ;
    ArrayList<City> MSTTour = EuclideanTSP.MSTTour(MST) ;
    System.out.println("MST weight: " + EuclideanTSP.weight(MST)) ;
    System.out.println("MST tour length: " + EuclideanTSP.length(MSTTour))*/

    //
    // Create JFrame and map:
    //
    Map map = new Map();

    JFrame mstwalk_tour_frame = new JFrame("MST-Walk Tour") ;
    mstwalk_tour_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //Map mstwalk_tour_map = new Map(WIDTH,HEIGHT,WIDTH,HEIGHT) ;
    mstwalk_tour_frame.getContentPane().add(map) ;
    mstwalk_tour_frame.pack() ;
    mstwalk_tour_frame.setVisible(true) ;
    //mstwalk_tour_frame.repaint() ;
    sleep(WAIT) ;

    //
    // Display cities:
    //

    map.CITY_COLOR = Color.blue ;
    for(int i = 0; i < cityNumbers; i++){
      map.city(cities[i]); //draw cities
    }
    //sleep(WAIT) ;

    //
    // Display MST:
    //
    EuclideanTSP mst = new EuclideanTSP(cities);
    System.out.println("MST total edge weight(distance): " + mst.generateMST());
    ArrayList<Edge> edges= mst.getAllEdges();

    sleep(WAIT) ;
    map.EDGE_COLOR = Color.gray;
    for (int i=0 ; i<edges.size() ; i++) {
      map.edge(edges.get(i)) ;
    }
    sleep(1000);

    ArrayList<City> tour = mst.getTourWalk(); // get walk of tree
    double tourLength = 0;
    //
    // Display MST-Walk:
    //

    map.EDGE_COLOR = Color.red ;
    for (int i=0 ; i<tour.size()-1 ; i++) {
      sleep(10);
      map.route(tour.get(i), tour.get(i+1));
      int height = Math.abs(tour.get(i).y_coor - tour.get(i+1).y_coor);
      int width = Math.abs(tour.get(i).x_coor - tour.get(i+1).x_coor);
      double length = Math.sqrt(height*height + width*width);
      tourLength += length;
    }
    System.out.println("MST tour whole length: " + tourLength);
    //sleep(WAIT) ;

  }

  /*public static ArrayList<City> smallCities() {
    ArrayList<City> result = new ArrayList<City>() ;
    result = new ArrayList<City>() ;
    result.add(new City("a",100.0,100.0)) ;
    result.add(new City("b",300.0,300.0)) ;
    result.add(new City("c",300.0,500.0)) ;
    result.add(new City("d",500.0,100.0)) ;
    result.add(new City("e",600.0,100.0)) ;
    result.add(new City("f",200.0,600.0)) ;
    result.add(new City("g",300.0,600.0)) ;
    return result ;
  }

  public static ArrayList<City> randomCities(int n, double maxX, double maxY) {
    Random r = new Random() ;
    ArrayList<City> result = new ArrayList<City>() ;
    for (int i=0 ; i<n ; i++) {
      result.add( new City("city"+i,r.nextDouble()*maxX,r.nextDouble()*maxY) ) ;
    }
    return result ;
  }*/

  public static void sleep(long milliseconds) {
    Thread thread = new Thread() ;
    try { thread.sleep(milliseconds) ; }
    catch (Exception e) {}
  }

}

/// End-of-File
