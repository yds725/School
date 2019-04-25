/**
 * Author: Daesang Yoon
 * username: day42
 */
import java.util.*;

public class AptTracker {

    private HashMap<String, Integer> priceSymbolTable = new HashMap<>();
    private HashMap<String, Integer> footageSymbolTable = new HashMap<>();

    private ArrayList<AptData> lowestPriceArray = new ArrayList<>();
    private ArrayList<AptData> highestFootageArray = new ArrayList<>();

    private static Scanner sc = new Scanner(System.in);
    private static String menuOption;

    private void addNewAPt(){

        StringBuilder concatenatedString = new StringBuilder(); // to build ID(key value) for HashMap symbol table
        String address, aptNumber, city, zipcode, ID;
        double price, footage;

        boolean flag;

        do{
            flag = true;
            System.out.printf("Please Enter an address: ");
            address = sc.nextLine();

            if(address.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for address was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an apartment number: ");
            aptNumber = sc.nextLine();

            if(aptNumber.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for number was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an city: ");
            city = sc.nextLine();

            if(city.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for city was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an zipcode: ");
            zipcode = sc.nextLine();

            if(zipcode.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for zipcode was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do {
            flag = true;

            System.out.printf("Please Enter price(dolllars): $");
            String priceForParse = sc.nextLine();

            try {
                price = Double.parseDouble(priceForParse);
            } catch (NumberFormatException e) {
                price = 0;
                System.out.printf("Input was invalid! Enter only interger (e.g., 450 or 550.5)! Try again!");
                flag = false;
            }

        }while(!flag);

        do {
            flag = true;

            System.out.printf("Please Enter footage: ");
            String footageForParse = sc.nextLine();

            try {
                footage = Double.parseDouble(footageForParse);
            } catch (NumberFormatException e) {
                footage = 0;
                System.out.printf("Input was invalid! Enter only interger (e.g., 450 or 550.5)! Try again!");
                flag = false;
            }

        }while(!flag);

        concatenatedString.append(address);
        concatenatedString.append(aptNumber);
        concatenatedString.append(zipcode);
        ID = concatenatedString.toString();

        AptData newApt = new AptData(address, aptNumber, city, zipcode, price, footage, ID);

        lowestPriceArray.add(newApt);
        sortToLowestPrice(newApt);

        highestFootageArray.add(newApt);
        sortToHighestFootage(newApt);
    }

    private void sortToLowestPrice(AptData newApt){

        int child = lowestPriceArray.size() - 1;

        while( child / 2 > 0 && (lowestPriceArray.get(child / 2).price >lowestPriceArray.get(child).price )){ //we are simply swapping

            AptData swap = lowestPriceArray.get(child / 2);

            lowestPriceArray.set(child / 2, newApt);

            lowestPriceArray.set(child, swap);

            priceSymbolTable.replace(lowestPriceArray.get(child).ID, child ); // updating symbol table after swap

            child /= 2;
        }

        priceSymbolTable.put(lowestPriceArray.get(child).ID, child);

    }

    private void sortToHighestFootage(AptData newApt){

        int child = highestFootageArray.size() - 1;

        while( child / 2 > 0 && (highestFootageArray.get(child / 2).footage < highestFootageArray.get(child).footage)){ //we are simply swapping if highest one is found

            AptData swap = highestFootageArray.get(child / 2);

            highestFootageArray.set(child / 2, newApt);

            highestFootageArray.set(child, swap);

            footageSymbolTable.replace(highestFootageArray.get(child).ID, child ); // updating symbol table after swap

            child /= 2;
        }

        footageSymbolTable.put(highestFootageArray.get(child).ID, child);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateApt(){

        StringBuilder concatenatedString = new StringBuilder();
        String address, aptNumber, zipcode, ID;
        boolean flag;

        System.out.println("\nCAUTION! You must put information as exactly same as information you already put when adding new apartment\n");

        do{
            flag = true;
            System.out.printf("Please Enter an address of apartment you want to update: ");
            address = sc.nextLine();

            if(address.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for address was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an apartment number of apt you want to update: ");
            aptNumber = sc.nextLine();

            if(aptNumber.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for number was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an zipcode of apartment you want to update: ");
            zipcode = sc.nextLine();

            if(zipcode.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for zipcode was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        concatenatedString.append(address);
        concatenatedString.append(aptNumber);
        concatenatedString.append(zipcode);
        ID = concatenatedString.toString();


        do {
            flag = true;

            System.out.println("What do you want to update? Enter 1 or 2 to select\n" +
                    "1. Price of the apartment\n" +
                    "2. Nothing\n");

            String updateChoice = sc.nextLine();

            switch (updateChoice){
                case "1":
                    System.out.printf("Please enter new price to update (Put only numbers): $");

                    String enteredPrice = sc.nextLine();
                    double newPrice = Double.parseDouble(enteredPrice);

                    updateApt(ID, newPrice);

                    flag = false;
                    break;

                case "2":
                    System.out.println("Nothing has been updated!\n");
                    flag = false;
                    break;

                default:
                    System.out.println("Input cannot be recognized. Please enter 1 or 2r\n");
                    break;
            }

        }while(flag);


    }

    private void updateApt(String ID, double priceToUpdate){

        //updating price
        int outdatedIndex = priceSymbolTable.get(ID);
        lowestPriceArray.get(outdatedIndex).price = priceToUpdate;

        if(lowestPriceArray.size() == 0){
            System.out.println("No apartments added. Cannot update!");
        }

        double parent;
        double left;
        double right;

        while(outdatedIndex / 2 > 0 || outdatedIndex * 2 < lowestPriceArray.size() || outdatedIndex * 2 + 1 < lowestPriceArray.size()){

            //if it has parents
            if(outdatedIndex / 2 > 0){

                parent = lowestPriceArray.get(outdatedIndex / 2).price;

                if(priceToUpdate < parent){

                    priceSymbolTable.replace(ID, moveHeapUp(lowestPriceArray.get(outdatedIndex)));

                    outdatedIndex /= 2;

                    continue;
                }
            }
            //if it has left child
            if(outdatedIndex * 2 < lowestPriceArray.size()){

                left = lowestPriceArray.get(outdatedIndex * 2).price;

                //if it has right child
                if(outdatedIndex * 2 + 1 < lowestPriceArray.size()){
                    right = lowestPriceArray.get(outdatedIndex * 2 + 1).price;

                    //if it is bigger than left child
                    if(priceToUpdate > left){

                        //if it is bigger than right child
                        if(priceToUpdate > right){

                            //see which child is smaller
                            if(left <= right){

                                //swap with left child
                                priceSymbolTable.replace(ID, moveHeapDown(lowestPriceArray.get(outdatedIndex), "left"));

                                outdatedIndex *= 2;

                            }else{

                                //swap with right child
                                priceSymbolTable.replace(ID, moveHeapDown(lowestPriceArray.get(outdatedIndex), "right"));

                                outdatedIndex *= 2;
                                outdatedIndex++;
                            }
                        } else {
                            //if it is only bigger than left child
                            //swap with only left child
                            priceSymbolTable.replace(ID, moveHeapDown(lowestPriceArray.get(outdatedIndex), "left"));

                            outdatedIndex *= 2;

                        }
                    } else if(priceToUpdate > right){
                        //if it is only bigger than right child
                        //swap with only right child
                        priceSymbolTable.replace(ID, moveHeapDown(lowestPriceArray.get(outdatedIndex), "right"));

                        outdatedIndex *= 2;
                        outdatedIndex++;

                    } else {
                        // if it is not bigger than any of its child (both left and right)
                        break;
                    }
                } else {
                    //if its right child does not exist
                    // and it is bigger than its left child, swap with left child
                    if(priceToUpdate > left){
                        priceSymbolTable.replace(ID, moveHeapDown(lowestPriceArray.get(outdatedIndex), "left"));

                        outdatedIndex *= 2;
                    } else {
                        break;
                    }
                }
            } else {
                // if it does not have any children
                // do not do anything
                break;
            }
        }
    }


    private int moveHeapUp(AptData toUpdate){

        int rootIndex = priceSymbolTable.get(toUpdate.ID);

        int parentIndex = rootIndex / 2;

        AptData swap = lowestPriceArray.get(parentIndex);
        lowestPriceArray.set(parentIndex, lowestPriceArray.get(rootIndex));
        lowestPriceArray.set(rootIndex, swap);

        priceSymbolTable.replace(lowestPriceArray.get(rootIndex).ID, rootIndex);

        return parentIndex;
    }

    private int moveHeapDown(AptData toUpdate, String rightOrLeft){

        if(rightOrLeft.equals("left")){
            // swapping with leftChild
            int rootIndex = priceSymbolTable.get(toUpdate.ID);

            int leftChild = rootIndex * 2;

            AptData swap = lowestPriceArray.get(leftChild);
            lowestPriceArray.set(leftChild, lowestPriceArray.get(rootIndex));
            lowestPriceArray.set(rootIndex, swap);

            priceSymbolTable.replace(lowestPriceArray.get(rootIndex).ID, rootIndex);

            return leftChild;
        } else {
            //swapping with rightChild
            int rootIndex = priceSymbolTable.get(toUpdate.ID);
            int rightChild = rootIndex * 2 + 1;

            AptData swap = lowestPriceArray.get(rightChild);
            lowestPriceArray.set(rightChild, lowestPriceArray.get(rootIndex));
            lowestPriceArray.set(rootIndex, swap);

            priceSymbolTable.replace(lowestPriceArray.get(rootIndex).ID, rootIndex);

            return rightChild;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void removeApt(){

        StringBuilder concatenatedString = new StringBuilder();
        String address, aptNumber, zipcode, ID;
        boolean flag;

        System.out.println("\nCAUTION! You must put information as exactly same as information you already put when adding new apartment\n");

        do{
            flag = true;
            System.out.printf("Please Enter an address of apartment you want to remove: ");
            address = sc.nextLine();

            if(address.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for address was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an apartment number of apt you want to remove: ");
            aptNumber = sc.nextLine();

            if(aptNumber.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for number was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        do{
            flag = true;
            System.out.printf("Please Enter an zipcode of apartment you want to remove: ");
            zipcode = sc.nextLine();

            if(zipcode.length() == 0){//if input is not valid, keep asking user for correct input
                System.out.printf("Input for zipcode was blank. Please  try again!\n");
                flag = false;
            }

        }while(!flag);

        concatenatedString.append(address);
        concatenatedString.append(aptNumber);
        concatenatedString.append(zipcode);
        ID = concatenatedString.toString();

        int indexPriceSymbolTable = priceSymbolTable.get(ID);
        int indexFootageSymbolTable = footageSymbolTable.get(ID);

        //if user wants to remove leaf node (no child node) or size is only 2, no need to update
        if( indexPriceSymbolTable * 2 >= lowestPriceArray.size() || indexPriceSymbolTable * 2 + 1 >= lowestPriceArray.size() || lowestPriceArray.size() == 2){
            lowestPriceArray.remove(indexPriceSymbolTable);
        } else {

            //if apartment is not at leaf node, call update to change price to last leaf node and swap that leaf node into removed one
            AptData leafPriceArray = lowestPriceArray.remove(lowestPriceArray.size() - 1);
            updateApt(ID, leafPriceArray.price);

            String aptAddress = leafPriceArray.address;
            String aptNum = leafPriceArray.aptNumber;
            String city = leafPriceArray.city;
            String aptZip = leafPriceArray.zipcode;

            double price = leafPriceArray.price;
            double footage = leafPriceArray.footage;

            String aptID = leafPriceArray.ID;
            indexPriceSymbolTable = priceSymbolTable.get(ID);

            lowestPriceArray.get(indexPriceSymbolTable).address = aptAddress;
            lowestPriceArray.get(indexPriceSymbolTable).aptNumber = aptNum;
            lowestPriceArray.get(indexPriceSymbolTable).city = city;
            lowestPriceArray.get(indexPriceSymbolTable).zipcode = aptZip;

            lowestPriceArray.get(indexPriceSymbolTable).price = price;
            lowestPriceArray.get(indexPriceSymbolTable).footage = footage;

            lowestPriceArray.get(indexPriceSymbolTable).ID = aptID;

            priceSymbolTable.remove(ID);
            priceSymbolTable.put(aptID, indexPriceSymbolTable);
        }

        //Chaning footageSymbolTable
        //if user wants to remove leaf node (no child node) or size is only 2, no need to update
        if( indexFootageSymbolTable * 2 >= highestFootageArray.size() || indexPriceSymbolTable * 2 + 1 >= highestFootageArray.size() || highestFootageArray.size() == 2){
            highestFootageArray.remove(indexFootageSymbolTable);
        } else {
            //if apartment is not at leaf node, call update to change price to last leaf node and swap that leaf node into removed one
            AptData leafFootageArray = highestFootageArray.remove(highestFootageArray.size() - 1);
            //updateApt(ID, leafFootageArray.price);

            String aptAddress = leafFootageArray.address;
            String aptNum = leafFootageArray.aptNumber;
            String city = leafFootageArray.city;
            String aptZip = leafFootageArray.zipcode;

            double price = leafFootageArray.price;
            double footage = leafFootageArray.footage;

            String aptID = leafFootageArray.ID;
            indexFootageSymbolTable = footageSymbolTable.get(ID);

            highestFootageArray.get(indexFootageSymbolTable).address = aptAddress;
            highestFootageArray.get(indexFootageSymbolTable).aptNumber = aptNum;
            highestFootageArray.get(indexFootageSymbolTable).city = city;
            highestFootageArray.get(indexFootageSymbolTable).zipcode = aptZip;

            highestFootageArray.get(indexFootageSymbolTable).price = price;
            highestFootageArray.get(indexFootageSymbolTable).footage = footage;

            highestFootageArray.get(indexFootageSymbolTable).ID = aptID;

            footageSymbolTable.remove(ID);
            footageSymbolTable.put(aptID, indexFootageSymbolTable);

            sortToHighestFootage(highestFootageArray.get(indexFootageSymbolTable));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void getInfoByCity(String option){
        System.out.printf("Please enter the city you want to retrieve: ");

        String city = sc.nextLine();

        if(option.equals("price")){

            for(int i = 1; i < lowestPriceArray.size(); i++){

                if(lowestPriceArray.get(i).city.equals(city)){
                    printPriceByCity(i);
                    return;
                }

            }
            System.out.println("There is no such city in your data.");
        } else { // if looking for footage

            for(int i = 1; i < highestFootageArray.size(); i++){

                if(highestFootageArray.get(i).city.equals(city)){
                    printFootageByCity(i);
                    return;
                }

            }
            System.out.println("There is no such city in your data.");

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void printPriceByCity(int index){

        System.out.println("\nLowest price apartment information for " + lowestPriceArray.get(index).city + " is:");
        System.out.print("\nAddress: " + lowestPriceArray.get(index).address);
        System.out.print("\nAptNumber: " + lowestPriceArray.get(index).aptNumber);
        System.out.print("\nCity: " + lowestPriceArray.get(index).city);
        System.out.print("\nZipcode: " + lowestPriceArray.get(index).zipcode);
        System.out.print("\nPrice: " + lowestPriceArray.get(index).price);
        System.out.print("\nFootage: " + lowestPriceArray.get(index).footage);

    }

    private void printFootageByCity(int index){

        System.out.println("\nHighest footage apartment information for " + highestFootageArray.get(index).city + " is:");
        System.out.print("\nAddress: " + highestFootageArray.get(index).address);
        System.out.print("\nAptNumber: " + highestFootageArray.get(index).aptNumber);
        System.out.print("\nCity: " + highestFootageArray.get(index).city);
        System.out.print("\nZipcode: " + highestFootageArray.get(index).zipcode);
        System.out.print("\nPrice: " + highestFootageArray.get(index).price);
        System.out.print("\nFootage: " + highestFootageArray.get(index).footage);
    }

    private void printPrice(int index){

        System.out.println("\nLowest price apartment information is:");
        System.out.print("\nAddress: " + lowestPriceArray.get(index).address);
        System.out.print("\nAptNumber: " + lowestPriceArray.get(index).aptNumber);
        System.out.print("\nCity: " + lowestPriceArray.get(index).city);
        System.out.print("\nZipcode: " + lowestPriceArray.get(index).zipcode);
        System.out.print("\nPrice: " + lowestPriceArray.get(index).price);
        System.out.print("\nFootage: " + lowestPriceArray.get(index).footage);

    }

    private void printFootage(int index){

        System.out.println("\nHighest footage apartment information is:");
        System.out.print("\nAddress: " + highestFootageArray.get(index).address);
        System.out.print("\nAptNumber: " + highestFootageArray.get(index).aptNumber);
        System.out.print("\nCity: " + highestFootageArray.get(index).city);
        System.out.print("\nZipcode: " + highestFootageArray.get(index).zipcode);
        System.out.print("\nPrice: " + highestFootageArray.get(index).price);
        System.out.print("\nFootage: " + highestFootageArray.get(index).footage);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args){
        AptTracker aptTracker = new AptTracker();

        //Important: we are neglecting index 0 at array
        aptTracker.lowestPriceArray.add(null);
        aptTracker.highestFootageArray.add(null);

        aptTracker.showMenu();

        do {
            System.out.printf("\nEnter \"y\" to show menu option.\nSelect one option: ");
            menuOption = sc.nextLine();

            switch(menuOption){

                case "1":
                    aptTracker.addNewAPt();
                    System.out.println("Apartment has been added!\n");
                    break;

                case "2":
                    aptTracker.updateApt();
                    System.out.println("Price has been updated!\n");
                    break;

                case "3":
                    aptTracker.removeApt();
                    System.out.println("Apartment has been removed!\n");
                    break;

                case "4":

                    if(aptTracker.lowestPriceArray.size() != 1){
                        aptTracker.printPrice(1);
                    } else {
                        System.out.println("You need to add at least two apartments to retrieve lowest price apartment\n");
                    }
                    break;

                case "5":

                    if(aptTracker.highestFootageArray.size() != 1){
                        aptTracker.printFootage(1);
                    } else {
                        System.out.println("You need to add at least two apartments to retrieve highest footage apartment\n");
                    }
                    break;

                case "6":

                    aptTracker.getInfoByCity("price");

                    break;

                case "7":
                    aptTracker.getInfoByCity("footage");
                    break;

                case "8":
                    System.out.println("Exit the program!\n");
                    System.exit(0);
                    break;

                case "y":
                case "Y":
                    aptTracker.showMenu();
                    break;

                default:
                    System.out.println("Input cannot be recognized. Please enter valid number\n");
                    break;
            }


        }while(true);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class AptData{
        double price, footage;
        String address, aptNumber, city, zipcode, ID;

        public AptData(String address, String aptNumber, String city, String zipcode, double price, double footage, String ID){
            this.address = address;
            this.aptNumber = aptNumber;
            this.city = city;
            this.zipcode = zipcode;
            this.ID = ID;
            this.price = price;
            this.footage = footage;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showMenu() {

        System.out.println("Which option do you want to select?\n");
        System.out.println("1. Add a new apartment\n" +
                "2. Update a apartment\n" +
                "3. Remove a specific apartment\n" +
                "4. Retrieve lowest price apartment\n" +
                "5. Retrieve highest sq footage apartment\n" +
                "6. Retrieve lowest price apartment by city\n" +
                "7. Retrieve highest sq footage apartment by city\n" +
                "8. Exit a program\n");
    }

}
