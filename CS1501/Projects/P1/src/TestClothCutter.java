/* Daesang Yoon - day42@pitt.edu
 * Actual clothe cutter test and main run file
 */

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

public class TestClothCutter {

    static int SYNC = 500;
    static int SLEEP = 30;

    public static void main(String[] args) {

        ArrayList<Pattern> patterns = new ArrayList<Pattern>();
        patterns.add(new Pattern(2,2,1,"A")) ;
        patterns.add(new Pattern(2,6,4,"B")) ;
        patterns.add(new Pattern(4,2,3,"C")) ;
        patterns.add(new Pattern(5,3,5,"D")) ;

        int width = 30;
        int height = 15;
        int pixels = 30;

        ClothCutter cutter = new ClothCutter(width,height,patterns) ;
        cutter.optimize() ;
        System.out.println( cutter.getOptimalValue() ) ;
        System.out.println( cutter.getGarments()) ;

        ClothGraphic cloth = new ClothGraphic(width,height,pixels) ;
        JFrame frame = new JFrame("Nice Cloth Cutter") ;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        frame.getContentPane().add(cloth) ;
        frame.pack() ;
        //frame.setLocation(50, 50);
        frame.setVisible(true) ;
        sleep(SYNC) ;
        for (Cut c : cutter.getOptimalCuts()) { sleep(SLEEP) ; cloth.drawCuts(c); }
        sleep(SYNC) ;
        for (Garment g : cutter.getGarments()) { sleep(SLEEP) ; cloth.drawGarment(g) ; }
    }

    public static void sleep(long milliseconds) {
        Date d ;
        long start, now ;
        d = new Date() ;
        start = d.getTime() ;
        do { d = new Date() ; now = d.getTime() ; } while ( (now - start) < milliseconds ) ;
    }


}
