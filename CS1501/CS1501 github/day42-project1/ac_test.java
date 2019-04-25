import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Created by 5years on 9/19/2017.
 */
public class ac_test {

    private static String text;
    private static Scanner sc;
    private static ArrayList<String> list;
    private static String userInput;
    private static String concan;
    private static int choice;
    private static boolean nextWord;
    static BufferedWriter output;
    static File history;

    public static void main(String[] args) throws IOException {
        DLBTrie trie = new DLBTrie();

        fillDLB(trie);

        createFile();

        firstPhase(trie);

    }


    public static void firstPhase(DLBTrie trie) {
        boolean flag = true;

        sc = new Scanner(System.in);

        System.out.print("Please enter your first character: ");
        userInput = sc.nextLine();
        userInput = userInput.toLowerCase();

        System.out.println();

        concan = userInput;

        list = trie.printPredictions(userInput, 5);

        while (flag) {
            if (nextWord) {
                System.out.print("\nPlease enter first character of next word: ");
                nextWord = false;

                concan = "";
            } else {
                System.out.print("\n\nPlease enter next character: ");
            }

            if (sc.hasNextInt()) {
                userInput = sc.nextLine();
                choice = Integer.parseInt(userInput);
                String chosen = list.get(choice - 1);
                writeFile(chosen);

                System.out.println("Word Completed: " + chosen);
                nextWord = true;
                continue;
            }

            userInput = sc.nextLine();
            userInput = userInput.toLowerCase();

            if (userInput.equalsIgnoreCase("$")) {

            } else if (userInput.equalsIgnoreCase("!")) {
                System.exit(0);
            }

            concan = concatenate(concan, userInput);
            System.out.println();

            list = trie.printPredictions(concan, 5);

        }
    }

    public static String concatenate(String previous, String userInput) {
        StringBuilder str = new StringBuilder();

        str.append(previous).append(userInput);

        return str.toString();

    }

    public static void fillDLB(DLBTrie trie) {

        try {

            File dictionary = new File("./dictionary.txt");
            sc = new Scanner(dictionary);

            while (sc.hasNextLine()) {
                text = sc.nextLine().toLowerCase();
                trie.addWord(text);
            }

        } catch (IOException e) {

        }

        sc.close();

    }

    public static void createFile() {
        try {

            history = new File("./user_history.txt");
            if (!history.exists()) {
                history.createNewFile();
            }
        } catch (IOException e) {

        }

    }

    public static void writeFile(String userWord) {

        try {
            FileWriter fw = new FileWriter(history.getAbsoluteFile());
            output = new BufferedWriter(fw);

            output.write(userWord);

            output.close();
        } catch (IOException e) {

        }

    }
}