public class StringArrayList implements StringList {

  private String[] array;
  private int size = 0;
  
  public StringArrayList() {
    this(100);
  }
  
  public StringArrayList(int initialCapacity) {
    array = new String[initialCapacity];
  }
  
  private static void SOP(String s) {
    // Simple shortcut method; mostly because I like to abbreviate System.out.println to SOP when I write on the board.
    System.out.println(s);
  }
  
  private void printArray() {
    // you could use a method like this for debugging purposes.
    System.out.print("[");
    String separator = "";
    for (int i = 0; i < size; i++) {
      System.out.print(separator);
      System.out.print("\"" + array[i] + "\"");
      separator = ", ";
    }
    SOP("]\n");
  }

  public static void main(String[] args) {
    // note that a main method is not required, but it might be useful for a sanity check
    StringArrayList list = new StringArrayList();
    SOP("New list:");
    // NOTE: printArray is private, so the **only** main method that could access it is this one (i.e., the one in this class).
    list.printArray();
    list.add("A");
    list.add("B");
    list.add("C");
    SOP("List with 3 elements:");
    list.printArray();
    list.clear();
    SOP("Cleared list:");
    list.printArray();
    list.add("X");
    list.add("Y");
    list.add("Z");
    list.add(0, "A");
    SOP("List with inserted A at the beginning:");
    list.printArray();
    SOP("List with position 1 removed:");
    list.remove(1);
    list.printArray();
    SOP("List with position 0 set to W:");
    list.set(0, "W");
    list.printArray();
    list.clear();
    for (int i = 0; i < 200; i++) {
      list.add(Integer.toString(i));
    }
    SOP("List with first 200 integers (from 0):");
    list.printArray();
  }

  private void ensureCapacity() {
    if (size == array.length) {
      // we use length * 2 + 1 in case somebody passes 0 as the initial list size.
      String[] temp = new String[array.length * 2 + 1];
      System.arraycopy(array, 0, temp, 0, size);
      array = temp;
    }
  }
  
  public int add(String s) {
    ensureCapacity();
    array[size] = s;
    return size++;
  }
  
  public String get(int i) {
    checkBounds(i);
    return array[i];
  }
  
  private void checkBounds(int i) {
    if (i >= size) throw new IndexOutOfBoundsException("Index is larger than list size.");
  }
  
  public boolean contains(String s) {
    return indexOf(s) >= 0;
  }
  
  public int indexOf(String s) {
    for (int i = 0; i < size; i++) {
      // want to make sure that we don't get a NullPointerException, in case there's a null value in the list.
      if ((array[i] == null && s == null) || (array[i] != null && array[i].equals(s))) {
        return i;
      }
    }
    return -1;
  }

  public int size() {
    return size;
  }

  public void clear() {
    size = 0;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public String remove(int i) {
    checkBounds(i);
    String result = array[i];
    System.arraycopy(array, i + 1, array, i, size - i - 1);
    size--;
    return result;
  }

  public void set(int index, String s) {
    checkBounds(index);
    array[index] = s;
  }

  public int add(int index, String s) {
    checkBounds(index);
    ensureCapacity();
    // It might be counter-intuitive, but System.arraycopy doesn't overwrite elements here.
    System.arraycopy(array, index, array, index + 1, size - index);
    array[index] = s;
    size++;
    return index;
  }

  public String[] toArray() {
    String[] temp = new String[size];
    System.arraycopy(array, 0, temp, 0, size);
    return temp;
  }
}
