

import java.util.*;
/**
 *CS 0401 Assignment One - DAE SANG YOON.
  This Program helps user to order meal in convenient way.
 */
public class MealOrderProgram {
      Scanner input = new Scanner(System.in); 
      int choice;                    // These variable represents userInput
      int select;                                      
      
    private final int STANDARD_MEAL = 1; // All these represent each choice or selection for different numbers 
    private final int SUPER_SIZED_MEAL = 2;
    private final int KIDS_MEAL = 3;
    private final int CUSTOMER_OTHER = 4;
    private final int CANCEL_ORDER = 5;
    private final int SANDWICH = 1;
    private final int BEVERAGE = 2;
    private final int FRIES = 3;
    private final int RETURN = 4;
    
    private final int SIMPLE = 1;        //These variables represent each different choice of sandwiches for different numbers
    private final int DOUBLE_STACKED = 2; 
    private final int TRIPLE_STACKED = 3;
    private final int NO_SANDWICH = 4;
    
    private final int COKE = 1;   //These variables represent each choice of beverage for different numbers
    private final int SPRITE = 2;
    private final int ICED_TEA = 3;
    private final int NO_BEVERAGE = 4;
    
    private final int SMALL = 1; //These variables represent each choice of size for different number
    private final int MEDIUM = 2;
    private final int LARGE = 3;
    
    private final int MEAL1 = 0; //These determine meal discount for each different meals,
    private final int MEAL2 = 1; //If user selects same choices for KIDS MEAL, it will be same MEAL1 and so on.
    private final int MEAL3 = 2;
    
    private final int YES = 1; //These variables are for questions asking YES or NO
    private final int NO = 2;
    
    String[] sandwich = {"Simple Hamburger", "Double-Stacked Hamburger", "Triple-Stacked Hamburger", "No sandwich"};
    String[] beverage = {"Coca-cola", "Sprite", "Iced Tea", "No Beverage"};
    String[] friesSizes = {"Small", "Medium", "Large", " "};  //These arrays store each selection of sandwich,
    String[] beverageSize = {"Small", "Medium", "Large", " "};// beverage, fry size,beverage size
    
    double[] sandwichPrice = {3.99, 5.99, 7.99}; //These int arrays store price for different sandwich,fry size, beverage size
    double[] beveragePrice = {1.49, 1.69, 1.89}; 
    double[] friesPrice = {1.99, 2.49, 2.99};
    private final double TAX_RATE = 0.06;  // this is tax rate
    private final double MEAL_DISCOUNT = 0.15; // this is meal discount rate
    private final double PROMO_DISCOUNT = 0.085; // promotion discount rate
    
    String promotionCode = "FATHERS_DAY"; //promotion code
    int sandwichChoice;  //These int variables track which selection user chooses and
    int frySize;         //help print out each items user orders using array (sandwich[sandwichChoice)
    int beverSize; 
    int beverageChoice;
    
    boolean promoDiscount; //These boolean variables are to determine there is discount or not,
    boolean mealDiscount;  // and also there is beverage or sandwich or fries or not
    boolean isBeverage;
    boolean isSandwich;
    boolean isFries;
    
    boolean orderChange;   //These boolean variables are for while loop (if true do loop, if false, break loop)
    boolean customerAnswer = false; //this is for mainmenu while loop (so if user put different input other than 1,2,3,4,5
                                    //it will notify user for worng input and do it again.But for only first main menu
    double subtotal=0;  
    double mdPrice =0;  //Thse double variables are for subtotal,mealdiscount price, promotion discount price,
    double pdPrice =0;  //tax, invoice total
    double tax;
    double invoiceTotal;
    
    public static void main (String args[]) {
        
        new MealOrderProgram(); 
    }
    
    public MealOrderProgram(){
          
        System.out.println("************************************\n" + "     Welcome to LOTTERIA!     " 
                + "\n" + "************************************\n");
        
        mainMenu();
               
    }   
        /**
         * mainMenu method is first order menu in the program. User chooses number 1 ~ 5.
         * It also makes user to put number again if they put wrong input. But for only this method; not other ones
         */
        
        public void mainMenu() {
            String a = "1 - Standard Meal:    Double-Stacked Hamburger, Medium Beverage, and Medium Fries";
            String b = "2 - Super Sized Meal: Triple-Stacked Hamburger, Large Beverage, and Large Fries";
            String c = "3 - Kids Meal:        Simple Hamburger, Small Beverage, and Small Fries";
            String d = "4 - Customer Other:   Select each item individually";
            String e = "5 - Cancel Order";
            System.out.println("Main Menu: ");        
            System.out.println(a+"\n"+b+"\n"+c+"\n"+d+"\n"+e);
            System.out.print("\nPlease make a selection from the above menu. Select a number: ");
         
        while(!customerAnswer) {
        int orderNumber;
        orderNumber = input.nextInt();
        if(orderNumber == STANDARD_MEAL || orderNumber == SUPER_SIZED_MEAL || orderNumber == KIDS_MEAL ){
            confirmSelection(orderNumber); //if user input is 1, 2, or 3; calls confirmSelection method
        }
        else if(orderNumber == CUSTOMER_OTHER){
            customizeOrder(orderNumber); //if user input is 4; calls customizeOrder method
        }
        else if(orderNumber == CANCEL_ORDER){ //cancel order
            System.out.println("You have canceled your order: Don't come back plz!");
            customerAnswer = true;
        }
        else{
            System.out.println("Invalid Input: Try Again!");
            System.out.println("\n*************************************\n");
            mainMenu();
        }
            } 
        }
        /**
         * confirmSelection method is to confirm selection for each meals:Standard,Super-sized, Kids meal.
         * @param number this is for number that user put in
         */
       
        
       public void confirmSelection(int number){       
                           
           orderChange = false; //makes loop go
           mealDiscount = true; //since user chooses meal, there must be meal discount and all items.
           isFries = true; 
           isSandwich = true;
           isBeverage = true;
           
        while(!orderChange){
           if(STANDARD_MEAL == number){
               System.out.println(
                "You have selected: Standard Meal that comes with:\n" +
                   " * Double-Stacked Hamburger\n" +
                   " * Medium Beverage\n" +
                   " * Medium Fries");
                   sandwichChoice = 1; //this will help assign each item in string array and price array
                   frySize = 1;
                   beverSize = 1;
                   
           }
            else if(SUPER_SIZED_MEAL == number){
                       System.out.println(
                         "You have selected: Super Sized Meal that comes with:\n" +
                           " * Triple-Stacked Hamburger\n" +
                           " * Large Beverage\n" +
                           " * Large Fries");
                    sandwichChoice = 2;
                    frySize = 2;
                    beverSize = 2;
                    
            }
            else if(KIDS_MEAL == number){
                       System.out.println( 
                         "You have selected: Kids Meal that comes with:\n" +
                           " * Simple Hamburger\n" +
                           " * Small Beverage\n" +
                           " * Small Fries");
                    sandwichChoice = 0;
                    frySize = 0;
                    beverSize = 0;
            }                    
                    beverageMenu(); //calls beverageMenu (ask to select beverage)
                    
                    promotionalDiscount(); //calls promotionalDiscount method(ask for promotion code)
                    
                    orderSummary(); //prints order summary
                    
                    changeOrder();  //ask user to change order or not. If yes, it will go back to mainMenu,               
    }                               //if not, it proceeds to checkout
  }
      /**
       * customizeOrder method is for user to customize selection
       * @param number this number is from user input in mainMenu
       */
       public void customizeOrder(int number){    
             
               isSandwich = false; //since user do not order anything yet, all of them are false
               isBeverage = false;
               isFries = false;
           
           orderChange = false;// starts loop
           if(number == CUSTOMER_OTHER){
               System.out.println("You have selected: Custom Order"
                       + "\nBecause you select Custom Order, you won't get the meal discount unless you select exact same order of those meals");
               System.out.println("Proceed with your order");
                          
               while(!orderChange){
                   System.out.println("\n*************************************\n");
               System.out.println("Customized Menu: " + "\n1 - Sandwich" + "\n2 - Beverage" + "\n3 - Fries" + "\n4 - Return" );
               System.out.print("\nWhat would you like to add? Select a number: ");
               select = input.nextInt();
                            
               if(select == SANDWICH){
                    isSandwich = true; //user select sandwich, there is sandwich and becomes true
                    sandwichMenu(); 
                    continue; //it makes go loop again so it goes back to customize selecting menu
               } else if(select == BEVERAGE) {
                   isBeverage = true; // same thig as sandwich above
                    beverageMenu();
                    beverageSize();
                    continue;           
               } else if(select == FRIES) {
                   isFries = true; //same thing as sandwich above
                   friesSize();
                   continue;
               } else if(select == RETURN) {
                     // this determines which meal user orders when selecting items
                   if(sandwichChoice == MEAL1 && beverSize == MEAL1 && frySize == MEAL1) { 
                       mealDiscount = true;
                       System.out.println("\n*******************************************");
                       System.out.println("Your order is the same as KIDS MEAL we provide." 
                               +"\nThe meal discount will be applied to your order");
                   }
                   else if(sandwichChoice == MEAL2 && beverSize == MEAL2 && frySize == MEAL2) { 
                       mealDiscount = true;
                       System.out.println("\n*******************************************");
                       System.out.println("Your order is the same as STANDARD MEAL we provide." 
                               +"\nThe meal discount will be applied to your order");
                   }
                   else if(sandwichChoice == MEAL3 && beverSize == MEAL3 && frySize == MEAL3){
                       mealDiscount = true;
                       System.out.println("\n*******************************************");
                       System.out.println("Your order is the same as SUPER SIZED MEAL we provide." 
                               +"\nThe meal discount will be applied to your order");
                   }
                   else{
                       mealDiscount = false; //no meal discount
                   }
                   
                   promotionalDiscount();
                   orderSummary();
                   changeOrder();                                   
               }
            
               
           }
       }
           
    }/**
     * sandwichMenu method is for printing sandwich menu and user can select.
     * Not returning value. Just print menu out
     */
       
     private void sandwichMenu() {
                System.out.println("\n*************************************\n");
                   System.out.println("Sandwich Menu: ");
                   System.out.println("1 - Simple Hamburger");
                   System.out.println("2 - Double-Stacked Hamburger");
                   System.out.println("3 - Triple-Stacked Hamburger");
                   System.out.println("4 - No Sandwich");
                   
                   System.out.print("\nWhat sandwich would you like? Select a number: ");
                   select = input.nextInt();
                   if(select == SIMPLE){
                       sandwichChoice = 0; //this is to track which sandwich user ordered, in this case it is simple hamburger
                   }
                   else if(select == DOUBLE_STACKED){
                       sandwichChoice = 1;
                   }
                   else if(select == TRIPLE_STACKED){
                       sandwichChoice = 2;
                   }   
                   else{                     
                       isSandwich = false; //if user select no sandwich, there is no sandwich. Thus it is false
                   }                         
    }
     
     /**
     * beverageMenu method is for printing beverage menu and user can select. 
     * Not returning value. Just print menu out
     */  
    private void beverageMenu(){
           System.out.println("\n*************************************\n");
           System.out.print("Beverage Menu:\n" +
                                    "1 - Coca-cola\n" +
                                    "2 - Sprite\n" +
                                    "3 - Iced Tea\n" +
                                    "4 - No Beverage\n" + "\nWhat kind of beverage would you like? Select a number: ");
           choice = input.nextInt();
           
            if(choice == COKE){
                        System.out.println("Your beverage selection: " + beverage[choice - 1]);
                            beverageChoice = 0; //this is to track which beverage user ordered, in this case it is coca cola
                    }
                    else if(choice == SPRITE){
                        System.out.println("Your beverage selection: " + beverage[choice - 1]);
                            beverageChoice = 1;
                    }
                    else if(choice == ICED_TEA){
                        System.out.println("Your beverage selection: " + beverage[choice - 1]);
                            beverageChoice = 2;
                    }
                    else{
                        System.out.println("Your beverage selection: " + beverage[choice - 1]);
                            beverageChoice =3; 
                            isBeverage = false; //if no beverage selected, there is no beverage
                                                // and it does not proceed to beverageSize menu
                    } 
    }
    
    /**
     * beverageSize method is for printing beverage size menu and user can select. 
     * Not returning value. Just print menu out
     */  
    private void beverageSize(){
        System.out.println("\n*************************************\n");
        if(isBeverage == true){    
                   System.out.println("Beverage Size Menu: ");
                   System.out.println("1 - Small");
                   System.out.println("2 - Medium");
                   System.out.println("3 - Large");
                   
                   System.out.print("\nWhat beverage size would you like? Select a number: ");
                   select = input.nextInt();
                   if(select == SMALL){
                       beverSize = 0;
                   }
                   else if(select == MEDIUM){
                       beverSize = 1;
                   }
                   else if(select == LARGE){
                       beverSize = 2;
                   }   
                   else{
                       beverSize = 3; // this does nothing actually. It is for empty string but not needed
                   }
                 }
    }
    
    /**
     * friesSize Menu method is for printing fry size menu and user can select. 
     * Not returning value. Just print menu out
     */  
    private void friesSize(){
        System.out.println("\n*************************************\n");
        
        System.out.println("French Fry Size Menu: ");
                   System.out.println("1 - Small");
                   System.out.println("2 - Medium");
                   System.out.println("3 - Large");
                   System.out.print("\nWhat fries size would you like? Select a number: ");
                   select = input.nextInt();
                   if(select == SMALL){
                       frySize = 0;
                   }
                   else if(select == MEDIUM){
                       frySize = 1;
                   }
                   else if(select == LARGE){
                       frySize = 2;
                   }   
                   else{
                       beverSize = 3; // this also does nothing. It represents empty string but not needed
                   }
    }
    
    /**
     * promotionalDiscount method is for asking for promotion code and user can type. 
     * Not returning value. Just print menu out
     */  
    private void promotionalDiscount() {
       System.out.println("\n*************************************\n");
        System.out.println("Do you have the current promotion code?" + "\n1 - Yes" + "\n2 - No" ); 
        System.out.print("\nPlease select one option. Select a number: ");
                choice = input.nextInt();
                String userInputCode;
                    
                    if(choice == YES){
                                           
                        for(int i = 0; i < 2; i++){ //this loop asks code for only twice.
                            System.out.print("Please enter promotion code: ");
                                 userInputCode= input.next();
                                      input.nextLine();
                                if(promotionCode.equalsIgnoreCase(userInputCode)){ //make userinupt case insensitive
                                    promoDiscount = true;
                                    i = 2;
                                }
                                else{
                                   promoDiscount = false;
                                }
                        }                       
                    }
                    else{
                        promoDiscount = false;
                    }
       
    }
    
    /**
     * orderSummary method is for printing order summary and it will print what user orders. 
     * Not returning value. Just print menu out
     */  
    
    private void orderSummary(){
        
        double priceSandwich = sandwichPrice[sandwichChoice]; //price for each item assigned from price array
        double priceBeverage = beveragePrice[beverSize];
        double priceFries = friesPrice[frySize];
                 
                     System.out.println("\n*************************************\n");
                     System.out.println("Order Summary: ");
                if(isSandwich == true){    
                    subtotal += priceSandwich; //add sandwichprice to subtotal and this printf nicely column formatting output
                    System.out.printf("%-10s %-30s $%.2f\n", "Sandwich:", sandwich[sandwichChoice], sandwichPrice[sandwichChoice]); 
                }
                if(isFries == true){
                     subtotal += priceFries; // same thing as sandwich one
                      System.out.printf("%-10s %-30s $%.2f\n", "Fries:", friesSizes[frySize]+" Fries", friesPrice[frySize]);
                } 
                
                if(isBeverage == true){
                     subtotal += priceBeverage; //same thing as sandwich one
                    System.out.printf("%-10s %-30s $%.2f\n", "Beverage:", beverageSize[beverSize]+" "+ beverage[beverageChoice], beveragePrice[beverSize]);

                }
                    System.out.println("-----------------------------------------------");
                    
                    System.out.printf("%-10s %-30s $%.2f\n", "Subtotal:", " ", subtotal); // print subtotal
                    System.out.println("-----------------------------------------------");
                    
             if(mealDiscount == true){
                    mdPrice = subtotal * MEAL_DISCOUNT; //calculate meal discount price if there is meal discount 
                    System.out.printf("%-20s %-20s $%.2f\n", "Meal Discount:"," ", mdPrice); // and print meal discount
                }
             
             if(promoDiscount == true){
                    pdPrice = subtotal * PROMO_DISCOUNT; //same thing as meal discount
                    System.out.printf("%-20s %-20s $%.2f\n", "Promotion Discount:"," ", pdPrice);

                }
             
                tax = (subtotal - mdPrice -pdPrice) * TAX_RATE;  //calculate tax rate (applied after all discounts applied)
                invoiceTotal = subtotal - (mdPrice + pdPrice - tax); //calculate invoice total
            
                    System.out.printf("%-10s %-30s $%.2f\n", "Tax:"," ", tax); //print tax
                    System.out.println("-----------------------------------------------");
                    System.out.printf("%-20s %-20s $%.2f\n", "Invoice Total:"," ", invoiceTotal); //print invoice total

                
                subtotal = 0; //after invoice total calculation is done, value of subtotal,tax,pdPrice,mdPrice
                pdPrice = 0;  //must be reset because when user changes order, program needs to do different calculation again
                mdPrice = 0;
                tax = 0;
    }
    /**
     * changeOrder method is to ask user whether change order or not. 
     * If yes, go back to mainMenu or if not, it goes to check out
     * Not returning value. Just print output
     */  
    private void changeOrder(){
        boolean cashValueProvided = false; //starts loo[
            System.out.println("\n*************************************");
        System.out.println("Do you want to change your order?" + "\n1 - Yes" + "\n2 - No, proceed to check out");
                    System.out.print("\nPlease select your choice. Select a number: ");
                    choice = input.nextInt();
                      
                    if(choice == YES){
                        System.out.println("\n*************************************");
                        mainMenu();
                    }
                    else if(choice == NO){
                     //this while loop is for determining whether user put corrent amount of cash
                     // if correct, loop ands and program finishes. If wrong, it asks for new cash value
                        while(!cashValueProvided){ 
                        System.out.println("\n*************************************\n");
                        System.out.print("Cash Value Provided by the Customer: ");
                        double payment = input.nextDouble();
                        if(payment >= invoiceTotal){
                        double change = payment - invoiceTotal;
                        System.out.printf("Total:\t$%.2f" + "\n", invoiceTotal);
                        System.out.printf("Paid:\t$%.2f" + "\n", payment);
                        System.out.printf("Change:\t$%.2f" + "\n", change);
                        System.out.println("\nTransaction Completed - THANK YOU FOR PURCHASING AT LOTTERIA!");
                        cashValueProvided = true;
                        orderChange = true;
                        customerAnswer = true; 
                        }
                        else{
                        System.out.println("Not enough money, please review your total price!");
                        continue;
                        }
                    }
    }
  }    
   
     
}