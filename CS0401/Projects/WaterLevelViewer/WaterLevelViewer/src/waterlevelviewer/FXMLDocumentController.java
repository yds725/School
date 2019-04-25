
package waterlevelviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

/**
 *
 * Water Level Map viewer
 */
public class FXMLDocumentController implements Initializable {
    private File file;
    String[][] waterLevelMap;
    private List<String[]> arrayList;
    
    boolean fileChooserUsed = false;
    
    private int rowCounter;
    private int numberOfWaterCells;
    private int orderOfSlice = 0;
    
    @FXML
    private GridPane waterLevelGrid;

    @FXML
    private Label sliceNumber;

    @FXML
    private Label waterVolume;
    
    @FXML
    private Label label;

    @FXML
    private TextField filePath;
    
    @FXML
    private BorderPane pane;

    @FXML
    void onBrowseFile(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Opening Water Level Map File");
            
            file = fileChooser.showOpenDialog(null);
            filePath.setText(file.getAbsolutePath());
            
            fileChooserUsed =true;
    }

    @FXML
    void onReadFile(ActionEvent event) {
        rowCounter = 0;
        numberOfWaterCells = 0;
        
        if(fileChooserUsed){
            
            if(file != null){
                readData(file);               
            }
            
        } else {
            typeFilePath();
            readData(file);   
        }
         colorGridPane(waterLevelMap[orderOfSlice]);
         getNumberOfSlice(orderOfSlice);
            
         fillWater(waterLevelMap[orderOfSlice]);
         getWaterVolume(numberOfWaterCells);
    }
    
    @FXML
    void onFirstMap(ActionEvent event) {
         orderOfSlice = 0;
         numberOfWaterCells = 0;
         unColorGridPane();
         colorGridPane(waterLevelMap[orderOfSlice]);
         getNumberOfSlice(orderOfSlice);
         
         fillWater(waterLevelMap[orderOfSlice]);
         getWaterVolume(numberOfWaterCells);
    }

    @FXML
    void onLastMap(ActionEvent event) {
         orderOfSlice = rowCounter - 1;
         numberOfWaterCells = 0;
         unColorGridPane();
         colorGridPane(waterLevelMap[orderOfSlice]);
         getNumberOfSlice(orderOfSlice);
         
         fillWater(waterLevelMap[orderOfSlice]);
         getWaterVolume(numberOfWaterCells);
    }

    @FXML
    void onNextMap(ActionEvent event) {
        orderOfSlice++;
        numberOfWaterCells = 0;
        unColorGridPane();
        colorGridPane(waterLevelMap[orderOfSlice]);
        getNumberOfSlice(orderOfSlice);

        fillWater(waterLevelMap[orderOfSlice]);
        getWaterVolume(numberOfWaterCells);

    }

    @FXML
    void onPreviousMap(ActionEvent event) {
        orderOfSlice--;
        numberOfWaterCells = 0;
        unColorGridPane();
        colorGridPane(waterLevelMap[orderOfSlice]);
        getNumberOfSlice(orderOfSlice);
        
        fillWater(waterLevelMap[orderOfSlice]);
        getWaterVolume(numberOfWaterCells);
    }
    
    private void readData(File file){
        arrayList = new ArrayList();
       
        try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                
            String line;
      
            int rowIndex = 0;
            String[] singleLine;
        
            while((line = br.readLine()) != null){              
                rowCounter++;
                line = line.replace(" ", "");
                singleLine = line.split(",");   
                arrayList.add(singleLine);                          
            }
            
            waterLevelMap = new String[rowCounter][20];
            for(String[] array : arrayList){    
                if(rowIndex < rowCounter){
                waterLevelMap[rowIndex] = array;
                rowIndex++;
                }
            }
            
            br.close();
            
        }catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
                   /*          
                     Scanner fileIn = null;
                    try {
                    fileIn = new Scanner(file);
                    } catch (FileNotFoundException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    int row = 0;
                    waterLevelMap= new String[15][20];
                    
                    
                    while(fileIn.hasNext()){
                    String line = fileIn.nextLine();
                    line = line.trim();
                    String[] singleLine = line.split(", ");
                    
                    if(row < 15){
                    waterLevelMap[row] = singleLine;
                    row++;
                    }
                    }
                    fileIn.close();
                    */
                              
    }
      
    
    private void getNumberOfSlice(int numberOfSlice){
        sliceNumber.setText(String.valueOf(numberOfSlice));
    }
    
    private void getWaterVolume(int numberOfCells){
        waterVolume.setText(String.valueOf(numberOfCells));
    }
    
    public void colorGridPane(String[] slice){
       
        for(int col = 0; col < 20; col++){
                for(int row = 10 - Integer.parseInt(slice[col]); row < 10; row++){
                     Node cell = getCellFromGridPane(waterLevelGrid, col, row);
                     cell.setStyle("-fx-background-color: #008000"); 
                    
                }                           
            }
       
    }
    
    private void fillWater(String[] slice){
        int max;
        boolean leftScan = false, rightScan = false;
        
            for(int col = 0; col < 20; col++){
                int value = 10 - Integer.parseInt(slice[col]);
                for(int row = (value - 1); row >= 0; row--){
                    for(max = col; max >=0; max--){
                        if((max-1) >= 0 &&  10 - Integer.parseInt(slice[max-1]) <= row){ // that strating point got be same as other row 10 -
                            leftScan = true;
                            max = -1;
                        } else{
                            leftScan = false;
                        }
                    }
                    
                    for(max = col; max < 20; max++){
                        if((max+1) < 20 && 10 - Integer.parseInt(slice[max+1]) <= row ){
                            //10 - x <= Integer.parseInt(slice[max+1]
                            
                            rightScan = true;
                            max = 20;
                        } else{
                            rightScan = false;
                        }
                    }
                    
                    if(leftScan && rightScan){
                        Node cell = getCellFromGridPane(waterLevelGrid, col, row);
                        cell.setStyle("-fx-background-color: blue");
                        numberOfWaterCells ++;
                    }
                }
                 
            }  
         
     }
    
    private void unColorGridPane(){
         for(int col = 0; col < 20; col++){
                for(int row = 0; row < 10; row++){
                     Node cell = getCellFromGridPane(waterLevelGrid, col, row);
                     cell.setStyle("-fx-background-color: null"); 
                    
                }                           
            }
    }
    
    private Node getCellFromGridPane(GridPane gridPane, int col, int row) {
        // this method returns a cell ID for a given row and column in the gridpane
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
       
    private void typeFilePath(){
        String path = filePath.getText();
        file = new File(path);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        waterLevelGrid = new GridPane();
            for(int x = 0; x < 20; x++){
                for(int y = 0; y < 10; y++){
                    Label label = new Label("  ");
                    label.prefWidthProperty().bind(waterLevelGrid.widthProperty());
                    label.prefHeightProperty().bind(waterLevelGrid.heightProperty());
                    waterLevelGrid.getChildren().add(label); //
                    
                    GridPane.setColumnIndex(label, x);
                    GridPane.setRowIndex(label, y);
            }
    }
            waterLevelGrid.setGridLinesVisible(true);
            pane.setCenter(waterLevelGrid);
        
   }
}
