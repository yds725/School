/**
 * This class is important for decoding anagarm.
 * It basically compares each character (alphabet letter) in dictionary word to user input
 */
public class CharCompare {

	private int[] alphabetCounter = new int[26];
	private int size;
	private String word;
	private int[] alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };

	/**
	 * Constructor that produces counts of characters (alphabet letter) in word as int array
	 * @param word
	 */
	public CharCompare(String word) {

		this.word = word.trim().toLowerCase();

		for(int i = 0; i < this.word.length(); i++){
			char letter = this.word.charAt(i);
			for(int j = 0; j < alphabetCounter.length; j++){
				if(letter == alphabet[j]){
					alphabetCounter[j]++;
					size++;
				}
			}
		}
	}

	public int size() {
		return size;
	}

	/**
	 * check if user input is just empty string which does not have any count of characters
 	 * @return boolean value
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * this overrides toString method of String object
	 * to change alphabet count array into string representation
	 */
	public String toString() {

		String str = "";
		for (char character = 'a'; character <= 'z'; character++) {
			for (int i = 0; i < alphabetCounter[character - 'a']; i++) {
				str += character;
			}
		}
		return str;
	}


	/**
	 * This is actually compares dictionary word and user input word. Main decoding function.
	 * It subtracts each alphabet character count in user input word with each count in dictionary word
	 * If result is negative, it means that the character in dictionary word is not in user input word
	 * so it cannot be anagram
	 * @param word
	 * @return result
	 */
	public CharCompare contains(CharCompare word) {
		CharCompare result = new CharCompare("");
		int[] inputString = this.alphabetCounter;
		int[] dictWord = word.alphabetCounter;
		for (int i = 0; i < result.alphabetCounter.length; i++) {
			result.alphabetCounter[i] = inputString[i] - dictWord[i];

			if (result.alphabetCounter[i] < 0) {
				return null;
			}
		}
		result.size = this.size - word.size;
		return result;
	}

	/**
	 * This overrides equals method of Object class.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof CharCompare)) {
			return false;
		} else {
			CharCompare compared = (CharCompare) obj;
			return this.toString().equals(compared.toString());
		}
	}
}
