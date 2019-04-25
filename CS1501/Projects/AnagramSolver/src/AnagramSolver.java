import java.util.ArrayList;
import java.util.List;

public class AnagramSolver {

	private List<String> dictionary;
	private CharCompare userInput;
	private List<String> sortedDict;
	private List<String> anagramList = new ArrayList<String>();

	public AnagramSolver(List<String> list) {
		dictionary = list;
	}

	/**
	 * This method decode anagram.
	 * Taking word to decode from user.
	 * Total number is flag that makes decode function to stop after printing out all anagrams
	 * @param wordToDecode
	 * @param totalNumber
	 */
	public void decode(String wordToDecode, int totalNumber) {

			userInput = new CharCompare(wordToDecode);

			sortDictionary();
			if (userInput.isEmpty()) {
				printResult();
			}

			if (totalNumber == 1) {
				for (String word : sortedDict) {
					CharCompare word1 = new CharCompare(word);
					if (word1.equals(userInput)) {
						anagramList.add(word);
						printResult();
						anagramList.remove(word);
					}
				}
			} else {
				String changedInput;
				CharCompare anagram;
				for (String word : sortedDict) {
					CharCompare word2 = new CharCompare(word);
					if (userInput.contains(word2) != null) {
						anagramList.add(word);
						anagram = userInput.contains(word2);
						changedInput = anagram.toString();

						if (totalNumber == 0) {
							totalNumber = sortedDict.size();
						}

						decode(changedInput, totalNumber - 1);
						anagramList.remove(word);
						userInput = new CharCompare(wordToDecode);
					}
				}
			}

	}

	/**
	 * Simple print statement function
	 */
	private void printResult() {
		if (anagramList.size() > 0) {
			String str = "";
			// Add the word associated
			for (String word : anagramList) {
				str = str + word + ", ";
			}

			str = str.substring(0, str.length() - 2);
			AnagramDecoder.out.println(str);
		}
	}

	/**
	 * This method is for sorting only words that are possible for anagram for user input.
	 * So that program does not have go through all words in dictionary
	 */
	private void sortDictionary() {
		sortedDict = new ArrayList<String>();
		for (String word : dictionary) {
			CharCompare sort = new CharCompare(word);

			if (userInput.contains(sort) != null) {
				sortedDict.add(word);
			}
		}
	}
}
