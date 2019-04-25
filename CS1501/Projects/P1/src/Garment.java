/*Garment class
 * to instantiate Garment object
 */

public class Garment {
    public int positionX;
    public int positionY;
    String name;

    public int width;
    public int height;

    public Garment(int x, int y, String n){
        positionX = x;
        positionY = y;
        name  = n;
    }

    public void setDimensions(int w, int h){
        width = w;
        height = h;
    }

    public String toString(){
        return "[" + name + ", " + positionX + ", " + positionY + "]";
    }

}
