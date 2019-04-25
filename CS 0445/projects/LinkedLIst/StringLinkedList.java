
public class StringLinkedList implements StringList {

  private StringNode head;
  private StringNode tail;
  private int size;

  public StringLinkedList(){
    head = null;
    tail = null;
    size = 0;
  }
  
  public int add(String s) {
    StringNode newNode = new StringNode(s);
    if(head==null){
      head = newNode;
    } else {
      tail.next = newNode;
    }
      tail = newNode;
    size++;

      return size;
  }
  
  public int size() {
    //TODO: implement this method
    return size;
  }
  
  public boolean isEmpty() {
    return size == 0;
  }
  
  public void clear() {
    head = null;
    size = 0;
    //TODO: implement this method
  }
  
  public void set(int i, String s) {
    outOfBounds(i);
    getNode(i).value = s;
  }
  
  public String remove(int i) {
    //TODO: implement this method
    outOfBounds(i);
    String removedItem;
    size--;
    if(i==0){
       removedItem = head.value;
      head = head.next;
      if(head == null){
        tail = null;
      }
      return removedItem;
    } else {
      StringNode previousNode = getNode(i-1);
      removedItem = previousNode.next.value;
      if(previousNode.next == tail){
        tail = previousNode;
      }
      previousNode.next = previousNode.next.next;
      return removedItem;
    }
  }
  
  public int add(int i, String s) {
    //TODO: implement this method
    
      outOfBounds(i);
      if (i == 0) {
        head = new StringNode(s, head);
      } else {
        StringNode previousNode = getNode(i - 1);
        StringNode insertedNode = new StringNode(s, previousNode.next);
        previousNode.next = insertedNode;
      }
      size++;

    return i;
  }
  
  public int indexOf(String s) {
    //TODO: implement this method
    StringNode current = head;
    int index = -1;
    for(int i = 0; current != null; i++){
      if(current.value != null && current.value.equals(s)){
        index = i;
      }
      current = current.next;
    }
    return index;
  }
  
  public String get(int i) {
    //TODO: implement this method
   /* StringNode current = head;
    for (int j = 0; j < i; j++){
      current = current.next;
    }
      return current.value;
      */
     outOfBounds(i);
     return getNode(i).value;
  }

  private StringNode getNode(int i){
    outOfBounds(i);
    StringNode current = head;
    for (int j = 0; j < i; j++){
      current = current.next;
    }
      return current;
  }
  
  public boolean contains(String s) {
    //TODO: implement this method
    return indexOf(s) >= 0;

  }
  
  public String[] toArray() {
    String[] result = new String[size];
    StringNode current = head;
    for (int i = 0; current != null; i++) {
      result[i] = current.value;
      current = current.next;
    }
    return result;
  }

  private void outOfBounds(int i) {
    if( i >= size) throw new IndexOutOfBoundsException();
  }

  public StringListIterator iterator() {
    //TODO: implement this method

    return new NodeIterator();
  }

  private static void SOP(String s) {
    // Simple shortcut method; mostly because I like to abbreviate 
    // System.out.println to SOP when I write on the board.
    System.out.println(s);
  }
  
  public void printList() {
    // you could use a method like this for debugging purposes.
    System.out.print("[");
    String separator = "";
    for (StringNode current = head; current != null; current = current.next) {
      System.out.print(separator);
      System.out.print("\"" + current.value + "\"");
      separator = " => ";
    }
    SOP("]\n");
  }

  private class NodeIterator implements StringListIterator {

    private StringNode currentNode = head;

    public String next() {
      //TODO: implement this method
      String currentValue = null;
      if(hasNext()) {
       currentValue = currentNode.value;
        currentNode = currentNode.next;
      }
      return currentValue;
    }
    
    public boolean hasNext() {
      //TODO: implement this method
      return currentNode != null;
    }
  }



  private class StringNode {
    private String value;
    private StringNode next;
    
    public StringNode(String s, StringNode n) {
      this.value = s;
      this.next = n;
    }
    
    public StringNode(String s) {
      this(s, null);
    }
  }
}