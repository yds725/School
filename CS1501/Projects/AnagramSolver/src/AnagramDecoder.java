
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class AnagramDecoder {

	public static PrintStream out = System.out;

	/**
	 * This is main method for anagram decoder program
	 * It takes args[0] which is file name (dictionary.txt)
	 * then user can enter any string that user want to scramble
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		out.println("Anagram Decoder Program");
		out.println();

		StringTable hashTable = new StringTable(3001);

		readDictionary(args[0], hashTable);

		List<String> dictionary = new ArrayList<String>();
		dictionary = hashTable.valuesToArrayList();

		List<String> fixedListOfWords = Collections.unmodifiableList(dictionary);
		AnagramSolver solver = new AnagramSolver(fixedListOfWords);
		String userInputWord;
		do {
			out.println();
			out.print("Type a string to decode (Enter no input to quit): ");
			userInputWord = sc.nextLine();
			if (userInputWord.length() != 0) {
				int limit = 0;

				solver.decode(userInputWord, limit);
			}
		} while (userInputWord.length() > 0);

	}

	/**
	 * Function for reading dictionary file and storing words in dictionary into hash table
	 * @param dict
	 * @param table
	 */
	private static void readDictionary(String dict, StringTable table){
		Scanner file = null;
		String line;
		try{
			file = new Scanner(new FileInputStream(dict));

			while(file.hasNextLine()){
				line = file.nextLine().toLowerCase();
				table.insert(line);
			}
		} catch (FileNotFoundException e) {
			out.println("Error reading file" + e.getMessage());
		}

		file.close();
	}
}
