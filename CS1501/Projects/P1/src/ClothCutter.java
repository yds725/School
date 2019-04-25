/* ClothCutter class is important class of project
 * It contains solution using dynamic prgramming and memoization
 */

import java.util.ArrayList;
public class ClothCutter {
    //original width and height of largest cloth
    private int originalWidth;
    private int originalHeight;

    public ArrayList<Pattern> patterns;
    public ArrayList<Garment> garments; //stores garment object
    public ArrayList<Cut> cutSeries; // stores every cut
    public ArrayList<Cut> optimalCuts; //final optimal cut series

    public int memoization[][]; //store value of maximum price; helps recursive method run faster

    //initiate ClothCutter object
    public ClothCutter(int width, int height, ArrayList<Pattern> pattern){
        originalWidth = width;
        originalHeight = height;
        patterns = pattern;

        memoization = new int[originalWidth + 1][originalHeight + 1];

        for(int i = 0; i <originalWidth + 1; i++){
            for(int j = 0; j < originalHeight + 1; j++){
                memoization[i][j] = -1; //initialize memo to -1 indicating subproblems are not solved yet
            }
        }

        garments = new ArrayList<Garment>();
        cutSeries = new ArrayList<Cut>();
        optimalCuts = new ArrayList<Cut>();

    }

    //creates memo and actual recursive dynamic programming method
    public void optimize(){

        for(int i = 1; i < originalWidth + 1; i++){
            for(int j = 1; j < originalHeight + 1; j++){
                memoization[i][j] = solveSubCloths(i,j);
            }
        }

        if(cutSeries.size() > 0){
            calculateFinalCuts(cutSeries.get(cutSeries.size()-1), 0, 0);
        } else {
            System.out.println("There are no patterns that can fit on given inputs. Try with other inputs!");
        }
    }

    public int solveSubCloths(int subWidth, int subHeight){

        int optimalMemoization = memoization[subWidth][subHeight];
        if(optimalMemoization > -1)
            return optimalMemoization;

        int solution = 0;
        int bestPatternValue = 0;
        int optimalHorizontal = 0;
        int optimalVertical = 0;

        Cut optimalHorizontalCut = null;
        Cut optimalVerticalCut = null;

        for(Pattern p : patterns){
            if(p.width <= subWidth && p.height <= subHeight && p.value > bestPatternValue){
                bestPatternValue = p.value;
            }
        }

        //try all vertical cuts and get optimal
        for(int i = 1; i < subWidth; i++){
            int temp = solveSubCloths(i, subHeight) + solveSubCloths(subWidth - i, subHeight);
            if(temp > optimalVertical){
                optimalVerticalCut = new Cut(subWidth, subHeight, 0, 0, i, true);
                optimalVertical = temp;
            }
        }

        //try all horizontal cuts and get optimal
        for(int i = 1; i < subHeight; i++){
            int temp = solveSubCloths(subWidth, i) + solveSubCloths( subWidth, subHeight - i);
            if(temp > optimalHorizontal){
                optimalHorizontalCut = new Cut(subWidth, subHeight, 0, 0, i, false);
                optimalHorizontal = temp;
            }
        }
        //return best optimal value from among three
        solution = Math.max(bestPatternValue, Math.max(optimalHorizontal, optimalVertical));

        //store cuts into series of all cut
        if(optimalHorizontalCut != null && solution == optimalHorizontal){
            cutSeries.add(optimalHorizontalCut);
        } else if (optimalVerticalCut != null && solution == optimalVertical){
            cutSeries.add(optimalVerticalCut);
        }

        return solution;
    }

    ///// Helper Methods /////

    //calculate rest of cuts that needs to be made after filling the memoization
    public void calculateFinalCuts(Cut cut, int offsetX, int offsetY){
        if(cut.curPosition == -1){
            makeGarment(cut.x, cut.y, offsetX, offsetY);
            return;
        }

        //add sequence of optimal cuts
        optimalCuts.add(new Cut(cut.x, cut.y, offsetX, offsetY, cut.curPosition, cut.isVertical ));

        if(cut.isVertical){
            calculateFinalCuts(searchCut(cut.x - cut.curPosition, cut.y), offsetX + cut.curPosition, offsetY);
            calculateFinalCuts(searchCut(cut.curPosition, cut.y), offsetX, offsetY);
        } else if(!cut.isVertical){
            calculateFinalCuts(searchCut(cut.x,  cut.y - cut.curPosition), offsetX ,offsetY + cut.curPosition );
            calculateFinalCuts(searchCut(cut.x, cut.curPosition), offsetX, offsetY);
        }

    }

    //return garment from cloth
    public ArrayList<Garment> getGarments(){
        return  garments;
    }

    //return optimal cuts or sequence has been cut
    public ArrayList<Cut> getOptimalCuts(){
        return optimalCuts;
    }

    //optimal value can be made from cloth
    public int getOptimalValue(){
        return memoization[originalWidth][originalHeight];
    }

    //take size x,y and offset for pattern and turns cloth into garment
    public void makeGarment(int x, int y, int offsetX, int offsetY){

        int val = -1;
        Garment series = null;

        for(Pattern p: patterns){
            if(p.width == x && p.height == y && p.value > val ){
                val = p.value;
                series = new Garment(offsetX, offsetY, p.name);
                series.setDimensions(x,y);
            }
        }

        garments.add(series);
    }

    public Cut searchCut(int x, int y){
        for (Cut c: cutSeries){
            if(c.x == x && c.y == y){
                return c;
            }
        }

        return new Cut(x, y, -1, -1, -1, true); //if there is no cut matching with given dimension
    }

}
