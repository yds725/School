///
/// Contents: JPanel to display cities, tours, and trees.
/// Author:   John Aronis
/// Date:     July 2018
///

import java.awt.* ;
import javax.swing.* ;
import java.util.ArrayList ;

public class Map extends JPanel {

  public Color BACKGROUND_COLOR = Color.white ;
  public Color CITY_COLOR       = Color.red ;
  public int   CITY_SIZE        = 10 ;
  public Color EDGE_COLOR       = Color.blue ;
  public int   EDGE_WIDTH       = 3 ;
  public Color ROUTE_COLOR = Color.red;

  private double maxX, maxY ;
  private int width, height ;

  public Map() {

    setPreferredSize(new Dimension(1024, 768)) ;
  }

  public void city(City city) {
    Graphics g = this.getGraphics() ;
    g.setColor(CITY_COLOR) ;
    int intX = city.x_coor-5 ;
    int intY = city.y_coor-5 ;
    g.fillOval(intX, intY, CITY_SIZE,CITY_SIZE) ;
  }

  public void edge(Edge edge) {
    Graphics g = this.getGraphics() ;
    g.setColor(EDGE_COLOR) ; 
    int city1X, city1Y, city2X, city2Y ;     
    city1X = edge.parent.x_coor ;
    city1Y = edge.parent.y_coor ;
    city2X = edge.children.x_coor ;
    city2Y = edge.children.y_coor ;
    g.drawLine(city1X,city1Y,city2X,city2Y) ;
  }

  public void route(City source, City dest){
    Graphics g = super.getGraphics();
    g.setColor(ROUTE_COLOR);
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke( new BasicStroke(3));
    g2.drawLine(source.x_coor, source.y_coor, dest.x_coor, dest.y_coor);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g) ;
    g.setColor(BACKGROUND_COLOR) ;
    g.fillRect(0,0, 1024, 768);
  }

}

/// End-of-File
