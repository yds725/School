import java.util.ArrayList;

/**
 * Hash entry node object which is for separate chaining
 */
 class HashEntry{
     //int key;
     String value;

     HashEntry next;

     HashEntry(String value){
         //this.key = key;
         this.value = value;
         this.next = null;

     }
}

public class StringTable {

     private int stringTableSize;
     private int numberOfHashEntries; //number of all key - value pairs

     private HashEntry[] hashTable;

     public StringTable(int tableSize){
         numberOfHashEntries = 0;

         stringTableSize = tableSize;

         hashTable = new HashEntry[stringTableSize];
         for(int i = 0; i < stringTableSize; i++){
             hashTable[i] = null;
         }
     }

     public void clearHashTable(){
         for(int i = 0; i < stringTableSize; i++){
             hashTable[i] = null;
         }
     }
     public String getValue(String word){
         return null;
     }

     public boolean contains(String word){
         return true;
     }

     /**
      * Hash table insert method; this uses separate chaining
      */
     public void insert(String word){
        int hashIndex = (hashFunction(word) % stringTableSize);

        if(hashTable[hashIndex] == null){
            hashTable[hashIndex] = new HashEntry(word);
        } else {
            HashEntry entry = hashTable[hashIndex];
            while (entry.next != null && !entry.value.equalsIgnoreCase(word)){
                entry = entry.next;
            }

            if(entry.value.equalsIgnoreCase(word)){
                entry.value = word;
            } else {
                entry.next = new HashEntry(word);
            }
        }

        numberOfHashEntries++;
     }

    /**
     * Hash function for string based on textbook
     * it takes key which is each word in dictionary.txt
     * @param key
     * @return hash value
     */
     private int hashFunction(String key){
         int hash = 0;
         int prime = 31;
         for (int i = 0; i < key.length(); i++)
             hash = (prime * hash + key.charAt(i)) % stringTableSize; //hash = (R * hash + s.charAt(i)) % M;

         if(hash < 0){
             hash = hash + stringTableSize;
         }

         return hash;
     }

    /**
     * Get all values(words in dictionary.txt) stored in hash table
     * and store those values in ArrayList for decoding anagram
     * @return arraylist of values
     */
    public ArrayList<String> valuesToArrayList(){

         ArrayList<String> valueList = new ArrayList<String>();

         for(int i = 0; i < stringTableSize; i++){
             HashEntry entry = hashTable[i];
             while(entry != null){
                 valueList.add(entry.value);
                 entry = entry.next;
             }
         }

         return valueList;
     }
}