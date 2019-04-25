public class StringLinkedList implements StringList {

  private StringNode head;
  private StringNode tail;
  
  private int size;
  
  public int add(String s) {
    if (head == null) {
      head = tail = new StringNode(s);
    } else {
      tail.next = new StringNode(s);
      tail = tail.next;
    }
    return size++;
  }
  
  public int size() {
    return size;
  }
  
  public boolean isEmpty() {
    return size == 0;
  }
  
  public void clear() {
    head = tail = null;
    size = 0;
  }
  
  private StringNode getNodeAt(int index) {
    checkBounds(index);
    StringNode result = head;
    for (int i = 0; i < index; i++) {
      result = result.next;
    }
    return result;
  }
  
  public void set(int i, String s) {
    getNodeAt(i).value = s;
  }
  
  private void checkBounds(int i) {
    if (i >= size || i < 0) throw new IndexOutOfBoundsException();
  }
  
  public String remove(int i) {
    checkBounds(i);
    String result;
    if (i == 0) {
      result = head.value;
      head = head.next;
      if (head == null) {
        tail = null;
      }
    } else {
      StringNode n = getNodeAt(i - 1);
      if (tail == n.next) {
        tail = n;
      }
      result = n.next.value;
      n.next = n.next.next;
    }
    size--;
    return result;
  }
  
  public int add(int i, String s) {
    checkBounds(i);
    if (i == 0) {
      head = new StringNode(s, head);
    } else {
      StringNode before = getNodeAt(i - 1);
      StringNode inserted = new StringNode(s, before.next);
      before.next = inserted;
	}
	size++;
    return i;
  }
  
  public int indexOf(String s) {
    StringNode current = head;
    for (int i = 0; current != null; i++) {
      if ((s == null && current.value == null) || (current.value != null && current.value.equals(s))) {
        return i;
      }
      current = current.next;
    }
    return -1;
  }
  
  public String get(int i) {
    return getNodeAt(i).value;
  }
  
  public boolean contains(String s) {
    return indexOf(s) >= 0;
  }
  
  public String[] toArray() {
    String[] result = new String[size];
    StringNode current = head;
    for (int i = 0; current != null; i++, current = current.next) {
      result[i] = current.value;
    }
    return result;
  }
  
  public StringListIterator iterator() {
    return new NodeIterator(head);
  }
  
  private static void SOP(String s) {
    // Simple shortcut method; mostly because I like to abbreviate System.out.println to SOP when I write on the board.
    System.out.println(s);
  }
  
  private void printList() {
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
  
  public static void main(String[] args) {
    // note that a main method is not required, but it might be useful for a sanity check
    StringLinkedList list = new StringLinkedList();
    SOP("New list:");
    // NOTE: printList is private, so the **only** main method that could access it is this one (i.e., the one in this class).
    list.printList();
    list.add("A");
    list.add("B");
    list.add("C");
    SOP("List with 3 elements:");
    list.printList();
    list.clear();
    SOP("Cleared list:");
    list.printList();
    list.add("X");
    list.add("Y");
    list.add("Z");
    list.add(0, "A");
    SOP("List with inserted A at the beginning:");
    list.printList();
    SOP("List with position 1 removed:");
    list.remove(1);
    list.printList();
    SOP("List with position 0 set to W:");
    list.set(0, "W");
    list.printList();
    list.clear();
    for (int i = 0; i < 200; i++) {
      list.add(Integer.toString(i));
    }
    SOP("List with first 200 integers (from 0):");
    list.printList();
  }
  
  private class NodeIterator implements StringListIterator {
    private StringNode current;
    
    private NodeIterator(StringNode node) {
      current = node;
    }
    
    public String next() {
      String result = current.value;
      current = current.next;
      return result;
    }
    
    public boolean hasNext() {
      return current != null;
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