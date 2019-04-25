/* Cut Class
 * to instantiate Cut object
 */

public class Cut {

    int x, y; //x is for vertical; y for horizontal
    int endX, endY; //similar as x2,y2
    int curPosition; //position - x(vertical) ; y (horizontal)
    boolean isVertical;

    public Cut(int x, int y, int endX, int endY, int position, boolean value){
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        curPosition = position;
        isVertical = value;
    }

    public String toString(){
        if(isVertical)
            return "Vertical" + curPosition + " on " + x + ", " + y;
        else
            return "Horizontal" + curPosition + " on " + x + ", " + y;
    }
}
