/* Pattern class
 * to instantiate pattern object
 */

public class Pattern {

    public int width;
    public int height;
    public int value;
    String name;

    public Pattern(int width, int height, int value, String name){
        this.width = width;
        this.height = height;
        this.value = value;
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
