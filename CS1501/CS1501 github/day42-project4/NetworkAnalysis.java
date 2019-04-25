/* Daesang Yoon
    day42
 */

import java.util.*;

public class NetworkAnalysis {

    public static void main(String args[]){

        if(args.length == 1){
            GraphFunction graph = new GraphFunction(args[0]);
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n\t1. Lowest latency path");
                System.out.println("\t2. Determine whether graph is copper-only connected");
                System.out.println("\t3. Maximum amount of data that can be transferred from one vertex to another");
                System.out.println("\t4. Lowest average latency spanning tree");
                System.out.println("\t5. Determine whether the graph connects if any two vertices were to fail");
                System.out.println("\t6. Exit the program");
                System.out.print("Please select one of options above you want to search (enter number): ");

                String input = sc.nextLine();

                switch (input) {
                    case "1":
                        graph.getLowestLatencyPath();
                        break;

                    case "2":
                       graph.isCopperOnlyConnected();
                        break;

                    case "3":
                       graph.getMaximumData();
                        break;

                    case "4":
                       graph.getLowestAvgLatencySpanningTree();
                        break;

                    case "5":
                        graph.isConnectedOnVerticesFailure();
                        break;

                    case "6":
                        System.out.println("\nExit the program!");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("\nInvalid input. Please type a number 1 to 6");
                        break;
                }
            }
        }else {
             System.out.println("\nFatal Error! You can put only one text file! Try again!");
        }
    }
}
