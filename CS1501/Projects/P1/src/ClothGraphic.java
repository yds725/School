/* Graphic display class for cloth cutter project
 * Very similar to GraphicExample.java given by instructor
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClothGraphic extends JPanel {

    int width, height;
    int offset; // value to multiply dimension to scale appropriately in screen

    public Color BACKGROUND_COLOR = Color.blue;
    public Color LINE_COLOR = Color.black;
    public Color RECT_COLOR = Color.yellow;

    ArrayList<Garment> garments = new ArrayList<Garment>();
    ArrayList<Cut> cuts =new ArrayList<Cut>();

    public ClothGraphic(int w, int h, int off){
        width = w;
        height = h;
        offset = off;
        setPreferredSize(new Dimension(width*off, height*off));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(BACKGROUND_COLOR); //coloring background
        g.fillRect(0,0, width * offset, height * offset);
        g.setColor(LINE_COLOR); //coloring lines

        for(Cut c: cuts){
            if(c.isVertical) {
                g.drawLine(c.endX*offset + c.curPosition * offset, offset*c.endY, offset *(c.endX + c.curPosition), offset * (c.endY + c.y));
            } else {
                g.drawLine(offset*c.endX,  offset*(c.endY + c.curPosition), offset *(c.endX + c.x), offset * (c.endY + c.curPosition));
            }
        }

        for(Garment gar: garments){
            if( gar != null){
                g.setColor(RECT_COLOR);
                g.fillRect(offset*gar.positionX+1, offset*gar.positionY+1, offset*gar.width-1, offset*gar.height-1);
                g.setColor(Color.black);
                g.drawString(gar.name, offset*gar.positionX + offset/2, offset*gar.positionY + offset/2);
            }
        }

    }

    //this repaints the scene
    public void drawCuts(Cut c){
        cuts.add(c);
        this.repaint();
    }

    public void drawGarment(Garment g){
        garments.add(g);
        this.repaint();
    }

}
