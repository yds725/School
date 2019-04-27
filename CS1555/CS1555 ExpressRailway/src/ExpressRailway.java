import java.io.*;
import java.sql.*;
import java.util.*;

public class ExpressRailway {
    private static String username;
    private static String password;
    private static Connection connection;

    public ExpressRailway(){
        System.out.println("\n\t*** Welcome to Express Railway ***");
    }

    public void deleteDatabase(){
        try{
            Scanner keyboard = new Scanner(System.in);
            System.out.println("\nAre you sure you want to delete the database data?");
            System.out.println("(1) Yes");
            System.out.println("(2) No");
            System.out.println("\nPlease choose an option from the menu:");
            int response = keyboard.nextInt();
            if(response == 1){
                CallableStatement cstmt = connection.prepareCall("{call delete_database()}");
                cstmt.execute();
                cstmt.close();
                System.out.println("\n*** The database data has been deleted ***");
                System.out.println("*** Press \"m\" to return to the main menu ***");
                String str = keyboard.nextLine();
                while (!str.equals("m"))
                    str = keyboard.nextLine();
            }
            else if(response == 2)
                return;
        }
        catch(SQLException sqlEx){
            System.out.println("Error running the query. Machine Error: " + sqlEx.toString());
        }
    }

    public void displayMenu() {
        System.out.println("\n\t\t*** MAIN MENU ***");
        System.out.println("\n1. Passenger Service Operations");
        System.out.println("\t1.1. Update Customer List");
        System.out.println("\t\t(a) 1.1.1. Add Customer");
        System.out.println("\t\t(b) 1.1.1. Edit Customer");
        System.out.println("\t\t(c) 1.1.1. View Customer");
        System.out.println("\t1.2. Find a Trip");
        System.out.println("\t\t(d) 1.2.1. Single Route Trip Search");
        System.out.println("\t\t(e) 1.2.2. Combination Route Trip Search");
        System.out.println("\t\t(f) 1.2.5. Add Reservation");
        System.out.println("\t1.3. Advanced Searches");
        System.out.println("\t\t(g) 1.3.1. Find all trains that pass through a specific station at a specific " +
                           "day/time combination");
        System.out.println("\t\t(h) 1.3.2. Find the routes that travel more than one rail line");
        System.out.println("\t\t(i) 1.3.3. Find the routes that pass through the same stations but don't have the " +
                           "same stops");
        System.out.println("\t\t(j) 1.3.4. Find any stations through which all trains pass through");
        System.out.println("\t\t(k) 1.3.5. Find all the trains that do not stop at a specific station");
        System.out.println("\t\t(l) 1.3.6. Find routes that stop at least at XX% of the stations they visit");
        System.out.println("\t\t(m) 1.3.7. Display the schedule of a route");
        System.out.println("\t\t(n) 1.3.8. Find the availability of a route at every stop on a specific day and time");
        System.out.println("\t1.4. Other Operations");
        System.out.println("\t\t(x) 1.4.1. Exit");
        System.out.println("2. Database Administrator");
        System.out.println("\t2.1. Import Database");
        System.out.println("\t\t(o) Import data to the database");
        System.out.println("\t2.2. Export Database");
        System.out.println("\t\t(p) Export data from the database");
        System.out.println("\t2.3. Delete Database");
        System.out.println("\t\t(q) Delete the database data");
    }

    public void executeQuery(String queryChoice){
        switch(queryChoice){
            case "a":
                addCustomer();
                break;
            case "b":
                editCustomer();
                break;
            case "c":
                viewCustomer();
                break;
            case "d":
                singleRouteSearch();
                break;
            case "e":
                combinationRouteSearch();
                break;
            case "f":
                addReservation();
                break;
            case "g":
                search131();
                break;
            case "h":
                search132();
                break;
            case "i":
                search133();
                break;
            case "j":
                search134();
                break;
            case "k":
                search135();
                break;
            case "l":
                search136();
                break;
            case "m":
                search137();
                break;
            case "n":
                search138();
                break;
            case "x":
                login();
                break;
            case "o":
                importData();
                break;
            case "p":
                exportData();
                break;
            case "q":
                deleteDatabase();
                break;
            default:
                System.out.println("\nQuery choice " + "\"" + queryChoice + "\""  + " cannot be found");
                try{
                    connection.close();
                }
                catch(Exception Ex){
                    System.out.println("Error connecting to database. Machine Error: " + Ex.toString());
                }
                break;
        }
    }

    public void exportData(){
        Scanner keyboard = new Scanner(System.in);
        String filename, tableName;
        File file;
        int response = 0;
        while(response != 2){
            System.out.println("Enter the name of the table to export:");
            tableName = keyboard.nextLine();
            System.out.println("Enter the name of the file to export to:");
            filename = keyboard.nextLine();
            try{
                file = new File(filename);
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                try{
                    Statement st = connection.createStatement();
                    st.execute("SELECT * FROM " + tableName + ";");
                    ResultSet rs = st.getResultSet();
                    while(rs.next()){
                        switch (tableName){
                            case("Stations"):
                                bw.write(String.format("%d;%s;%tT;%tT;%d;%s;%s;%s\n",
                                        rs.getInt(1), rs.getString(2), rs.getTime(3), rs.getTime(4),
                                        rs.getInt(5), rs.getString(6), rs.getString(7),
                                        rs.getString(8)));
                                bw.flush();
                                break;
                            case("RailLines"):
                                bw.write(String.format("%d;%d;%d;%d;%s\n",
                                        rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5)));
                                bw.flush();
                                break;
                            case("Routes"):
                                bw.write(String.format("%d;%d;%d;%d;%s\n",
                                        rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getString(5)));
                                bw.flush();
                                break;
                            case("Trains"):
                                bw.write(String.format("%d;%s;%s;%d;%d;%d\n",
                                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                                        rs.getInt(5), rs.getInt(6)));
                                bw.flush();
                                break;
                            case("Schedules"):
                                bw.write(String.format("%d;%s;%tT;%d\n",
                                        rs.getInt(1), rs.getString(2), rs.getTime(3), rs.getInt(4)));
                                bw.flush();
                                break;
                            case("Customers"):
                                bw.write(String.format("%d;%s;%s;%s;%s;%s\n",
                                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                        rs.getString(5), rs.getString(6)));
                                bw.flush();
                                break;
                            case("Reservations"):
                                bw.write(String.format("%d;%d;%s;%tT;%d\n",
                                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getTime(4),
                                        rs.getInt(5)));
                                bw.flush();
                                break;
                            default:
                                break;
                        }
                    }
                    st.close();
                }
                catch(SQLException sqlEx){
                    System.out.println("Error running the query. Machine Error: " + sqlEx.toString());
                }
                bw.close();
                System.out.println("\n*** The data from table \"" + tableName + "\" was exported into \"" + filename +
                                   "\" ***");
            }
            catch(IOException ioEx){
                System.out.println("IOException: " + ioEx.toString());
            }
            System.out.println("\nWould you like to export more data?");
            System.out.println("(1) Yes");
            System.out.println("(2) No");
            System.out.println("\nPlease enter an option from the menu:");
            response = keyboard.nextInt();
            keyboard.nextLine();
            if(response == 1)
                continue;
            else if(response == 2)
                break;
            else{
                while(response != 1 && response != 2){
                    System.out.println("\nPlease enter either \"1\" for \" Yes \" or \"2\" for \"No\":");
                    response = keyboard.nextInt();
                    keyboard.nextLine();
                }
            }
        }
    }

    public String getQuery(){
        Scanner keyboard = new Scanner(System.in);
        displayMenu();
        System.out.println("\nPlease choose an option from the menu: ");
        String query = keyboard.nextLine();
        return query;
    }

    public int getSortChoice(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("*** Sorting Options ***");
        System.out.println("\t(1) By fewest stops");
        System.out.println("\t(2) By running through the most stations");
        System.out.println("\t(3) By lowest price");
        System.out.println("\t(4) By highest price");
        System.out.println("\t(5) By least total time");
        System.out.println("\t(6) By most total time");
        System.out.println("\t(7) By least total distance");
        System.out.println("\t(8) By most total distance");
        System.out.println("\nPlease choose a sorting option from the menu:");
        int sortChoice = keyboard.nextInt();
        return sortChoice;
    }

    public void importData(){
        Scanner keyboard = new Scanner(System.in);
        String filename, tableName;
        File file;
        int response = 0;
        while(response != 2){
            System.out.println("Enter the file name:");
            filename = keyboard.nextLine();
            System.out.println("Enter the name of the table to import the data to:");
            tableName = keyboard.nextLine();
            try{
                file = new File(filename);
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line = new String();
                String insertQuery = new String();
                String[] lineArr;
                while((line = br.readLine()) != null){
                    try{
                        lineArr = line.split(";");
                        switch(tableName) {
                            case ("Stations"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + ",'" + lineArr[1]
                                               + "','" + lineArr[2] + "','" + lineArr[3] + "'," + lineArr[4] + ",'"
                                               + lineArr[5] + "','" + lineArr[6] + "','" + lineArr[7] + "');";
                                break;
                            case ("RailLines"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + "," + lineArr[1]
                                               + "," + lineArr[2] + "," + lineArr[3] + ",'" + lineArr[4] + "');";
                                break;
                            case ("Routes"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + "," + lineArr[1]
                                + "," + lineArr[2] + "," + lineArr[3] + ",'" + lineArr[4] + "');";
                                break;
                            case ("Trains"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + ",'" + lineArr[1]
                                               + "','" + lineArr[2] + "'," + lineArr[3] + "," + lineArr[4] + ","
                                               + lineArr[5] + ");";
                                break;
                            case ("Schedules"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + ",'" + lineArr[1]
                                               + "','" + lineArr[2] + "'," + lineArr[3] + ");";
                                break;
                            case ("Customers"):
                                insertQuery = "INSERT INTO " + tableName + " VALUES(" + lineArr[0] + ",'" + lineArr[1]
                                               + "','" + lineArr[2] + "','" + lineArr[3] + "','" + lineArr[4] + "','"
                                               + lineArr[5] + "');";
                                break;
                            default:
                                break;
                        }
                        Statement st = connection.createStatement();
                        st.execute(insertQuery);
                        st.close();
                    }
                    catch(SQLException sqlEx) {
                        System.out.println("Error running the query. Machine Error: " + sqlEx.toString());
                    }
                }
                System.out.println("\n*** The data from \"" + filename + "\" was imported into table \"" + tableName +
                                   "\" ***");
            }
            catch(IOException ioEx){
                System.out.println("IOException: " + ioEx.toString());
            }
            System.out.println("\nWould you like to import data from another file?");
            System.out.println("(1) Yes");
            System.out.println("(2) No");
            System.out.println("\nPlease enter an option from the menu:");
            response = keyboard.nextInt();
            keyboard.nextLine();
            if (response == 1)
                continue;
            else if (response == 2)
                break;
            else{
                while(response != 1 && response != 2) {
                    System.out.println("\nPlease enter either \"1\" for \" Yes \" or \"2\" for \"No\":");
                    response = keyboard.nextInt();
                    keyboard.nextLine();
                }
            }
        }
    }

    public static void login(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("\n*** Login ***");
        System.out.println("Please enter your username:");
        username = keyboard.nextLine();
        System.out.println("Please enter your password:");
        password = keyboard.nextLine();
    }

    public void addCustomer(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the customer's first name:");
        String firstName = keyboard.nextLine();
        System.out.println("Enter the customer's last name:");
        String lastName = keyboard.nextLine();
        System.out.println("Enter the customer's street:");
        String street = keyboard.nextLine();
        System.out.println("Enter the customer's town:");
        String town = keyboard.nextLine();
        System.out.println("Enter the customer's zip code:");
        String zip = keyboard.nextLine();
        try{
            CallableStatement cstmt = connection.prepareCall("{call add_customer(?, ?, ?, ?, ?)}");
            cstmt.setString(1, firstName);
            cstmt.setString(2, lastName);
            cstmt.setString(3, street);
            cstmt.setString(4, town);
            cstmt.setString(5, zip);
            cstmt.execute();
            cstmt.close();
            Statement st = connection.createStatement();
            st.execute("SELECT customerID FROM Customers WHERE firstName = '" + firstName + "' AND lastName = '"
                       + lastName + "';");
            ResultSet rs = st.getResultSet();
            while(rs.next()){
                System.out.println("\n*** \"" + firstName + " " + lastName + "\" was assigned customer ID " +
                                   String.format("%d", rs.getInt(1)) + " and added to the database ***");
            }
            st.close();
            System.out.println("*** Press \"m\" to return to the main menu ***");
            String str = keyboard.nextLine();
            while(!str.equals("m"))
                str = keyboard.nextLine();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
    }

    public void editCustomer(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the customer ID of the customer you wish to edit: ");
        int cid = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the field of the customer record you wish to edit (firstName, lastName, street, " +
                           "town, or zip): ");
        String toEdit = keyboard.nextLine();
        System.out.println("Enter the value you wish to substitute: ");
        String substituteValue = keyboard.nextLine();
        try{
            CallableStatement cstmt = connection.prepareCall("{call edit_customer(?, ?, ?)}");
            cstmt.setInt(1, cid);
            cstmt.setString(2, toEdit);
            cstmt.setString(3, substituteValue);
            cstmt.execute();
            cstmt.close();
            System.out.println("\n*** The field \"" + toEdit + "\" of customer " + cid + " was changed to \"" +
                               substituteValue + "\" ***");
            System.out.println("*** Press \"m\" to return to the main menu ***");
            String str = keyboard.nextLine();
            while (!str.equals("m"))
                str = keyboard.nextLine();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
    }

    public void viewCustomer(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the Customer ID of the customer you would like to view: ");
        int cid = keyboard.nextInt();
        try{
            CallableStatement cstmt = connection.prepareCall("{call view_customer(?)}");
            cstmt.setInt(1, cid);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            while(rs.next()){
                System.out.println(String.format("\n%d, %s %s, %s, %s, %s",
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)));
            }
            cstmt.close();
            System.out.println("*** Press \"m\" to return to the main menu ***");
            String str = keyboard.nextLine();
            while(!str.equals("m"))
                str = keyboard.nextLine();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
    }

    public void singleRouteSearch(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the arrival station ID:");
        int arrival = keyboard.nextInt();
        System.out.println("Enter the destination station ID:");
        int dest = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the day:");
        String day = keyboard.nextLine();
        int sortChoice = getSortChoice();
        String sortQuery = new String();
        switch(sortChoice){
            case(1):
                sortQuery = "{call sort_fewest_stops(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tNum Stops");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(2):
                sortQuery = "{call sort_most_stations(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tNum Stations");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(3):
                sortQuery = "{call sort_lowest_price(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tPrice");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(4):
                sortQuery = "{call sort_highest_price(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tPrice");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(5):
                sortQuery = "{call sort_least_time(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tTime");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(6):
                sortQuery = "{call sort_most_time(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tTime");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(7):
                sortQuery = "{call sort_least_distance(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tDistance");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(8):
                sortQuery = "{call sort_most_distance(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    while(rs.next()){
                        System.out.println("Route ID\tDistance");
                        System.out.println(String.format("%d\t\t%d", rs.getInt(1), rs.getInt(2)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            default:
                System.out.println("\nPlease enter a choice between 1 and 8");
                break;
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void combinationRouteSearch(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the arrival station ID:");
        int arrival = keyboard.nextInt();
        System.out.println("Enter the destination station ID:");
        int dest = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the day:");
        String day = keyboard.nextLine();
        int sortChoice = getSortChoice();
        String sortQuery = new String();
        switch(sortChoice){
            case(1):
                sortQuery = "{call sort_fewest_stops_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tNum Stops");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                                         rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(2):
                sortQuery = "{call sort_most_stations_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tNum Stations");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                           rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(3):
                sortQuery = "{call sort_lowest_price_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tPrice");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                           rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(4):
                sortQuery = "{call sort_highest_price_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tPrice");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(5):
                sortQuery = "{call sort_least_time_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tTime");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(6):
                sortQuery = "{call sort_most_time_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tTime");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(7):
                sortQuery = "{call sort_least_distance_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tDistance");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            case(8):
                sortQuery = "{call sort_most_distance_combination(?, ?, ?)}";
                try{
                    CallableStatement cstmt = connection.prepareCall(sortQuery);
                    cstmt.setInt(1, arrival);
                    cstmt.setInt(2, dest);
                    cstmt.setString(3, day);
                    cstmt.execute();
                    ResultSet rs = cstmt.getResultSet();
                    System.out.println("\nQuery Results:");
                    System.out.println("ID\tRoute 1 ID\tRoute 2 ID\tDistance");
                    while(rs.next()){
                        System.out.println(String.format("%d\t\t%d\t\t%d\t\t%d",
                                rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)));
                    }
                    cstmt.close();
                }
                catch(SQLException Ex){
                    System.out.println("Error running the query. Machine Error: " + Ex.toString());
                }
                break;
            default:
                System.out.println("\nPlease enter a choice between 1 and 8");
                break;
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while(!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void addReservation(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the customer ID of the customer making the reservation:");
        int cid = keyboard.nextInt();
        System.out.println("Enter the route ID:");
        int rid = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the day:");
        String day = keyboard.nextLine();
        System.out.println("Enter the time:");
        String timeStr = keyboard.nextLine();
        java.sql.Time time = Time.valueOf(timeStr);
        try{
            CallableStatement cstmt = connection.prepareCall("{call add_reservation(?, ?, ?, ?)}");
            cstmt.setInt(1, cid);
            cstmt.setInt(2, rid);
            cstmt.setString(3, day);
            cstmt.setTime(4, time);
            cstmt.execute();
            cstmt.close();
            System.out.println("\n*** Reservation added ***");
            System.out.println("*** Press \"m\" to return to the main menu ***");
            String str = keyboard.nextLine();
            while(!str.equals("m"))
                str = keyboard.nextLine();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
    }

    public void search131(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the station ID:");
        int sid = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the day:");
        String day = keyboard.nextLine();
        System.out.println("Enter the time:");
        String timeStr = keyboard.nextLine();
        java.sql.Time time = Time.valueOf(timeStr);
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_131(?, ?, ?)}");
            cstmt.setInt(1, sid);
            cstmt.setString(2, day);
            cstmt.setTime(3, time);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            while(rs.next()){
                System.out.println(String.format("%d", rs.getInt(1)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while(!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search132(){
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_132()}");
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            System.out.println("Route ID");
            while(rs.next()){
                System.out.println(String.format("%d", rs.getInt(1)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query.  Machine Error: " + Ex.toString());
        }
        Scanner keyboard = new Scanner(System.in);
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search133(){
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_133()}");
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            System.out.println("Route ID 1\tRoute ID 2");
            while(rs.next()){
                System.out.println(String.format("%d            %d",
                        rs.getInt(1),
                        rs.getInt(2)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query.  Machine Error: " + Ex.toString());
        }
        Scanner keyboard = new Scanner(System.in);
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search134(){
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_134()}");
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            System.out.println("Station ID");
            while(rs.next()){
                System.out.println(String.format("%d", rs.getInt(1)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query.  Machine Error: " + Ex.toString());
        }
        Scanner keyboard = new Scanner(System.in);
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search135(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the specific station ID:");
        int sid = keyboard.nextInt();
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_135(?)}");
            cstmt.setInt(1, sid);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            System.out.println("Train ID");
            while(rs.next()){
                System.out.println(String.format("%d", rs.getInt(1)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query.  Machine Error: " + Ex.toString());
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search136(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the percentage(e.g. 50, 70, etc):");
        float percentage = keyboard.nextFloat();
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_136(?)}");
            cstmt.setFloat(1, percentage);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("\nQuery Results:");
            System.out.println("Route ID");
            while(rs.next()){
                System.out.println(String.format("%d", rs.getInt(1)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query.  Machine Error: " + Ex.toString());
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search137(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the route ID:");
        int rid = keyboard.nextInt();
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_137(?)}");
            cstmt.setInt(1, rid);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("Query Results:");
            System.out.println("Route ID\tDay\t\tTime\t\tTrain Id");
            while(rs.next()){
                System.out.println(String.format("%d            %s            %tT            %d",
                                   rs.getInt(1),
                                   rs.getString(2),
                                   rs.getTime(3),
                                   rs.getInt(4)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public void search138(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter the route ID:");
        int rid = keyboard.nextInt();
        keyboard.nextLine();
        System.out.println("Enter the day:");
        String day = keyboard.nextLine();
        System.out.println("Enter the time:");
        String timeStr = keyboard.nextLine();
        java.sql.Time time = Time.valueOf(timeStr);
        try{
            CallableStatement cstmt = connection.prepareCall("{call search_138(?, ?, ?)}");
            cstmt.setInt(1, rid);
            cstmt.setString(2, day);
            cstmt.setTime(3, time);
            cstmt.execute();
            ResultSet rs = cstmt.getResultSet();
            System.out.println("Query Results:");
            System.out.println("Stop ID\t\tDay\t\tTime\t\tSeats");
            while(rs.next()){
                System.out.println(String.format("%d            %s            %tT            %d",
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getTime(3),
                        rs.getInt(4)));
            }
            cstmt.close();
        }
        catch(SQLException Ex){
            System.out.println("Error running the query. Machine Error: " + Ex.toString());
        }
        System.out.println("\n*** Press \"m\" to return to the main menu ***");
        String str = keyboard.nextLine();
        while (!str.equals("m"))
            str = keyboard.nextLine();
    }

    public static void main(String args[]) throws SQLException{
        login();
        try{
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/";
            connection = DriverManager.getConnection(url, username, password);
            ExpressRailway er = new ExpressRailway();
            String queryChoice = er.getQuery();
            while(true){
                er.executeQuery(queryChoice);
                queryChoice = er.getQuery();
            }
        }
        catch(Exception Ex){
            System.out.println("Error connecting to database. Machine Error: " + Ex.toString());
        }
        finally{
            connection.close();
        }
    }
}