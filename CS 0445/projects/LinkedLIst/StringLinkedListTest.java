import static org.junit.Assert.*;
import org.junit.Test;

public class StringLinkedListTest {

  @Test
  public void testAdd() {
    // Test basic creation.
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    list.add(s);
    assertEquals(list.size(), 1);
    assertEquals(list.get(0), s);
    
    // Test growth / capacity.
    list = new StringLinkedList();
    int size = 100000;
    for (int i = 0; i < size; i++) {
      list.add(Integer.toString(i));
    }
    assertEquals(size, list.size());
  }

  @Test
  public void testGet() {
    // test basic get
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    list.add(s);
    assertEquals(list.get(0), s);
    
    // test exception if index is out of bounds
    try {
      list.get(10);
      fail();
    } catch (Exception exc) {
      // OK - this should have thrown an exception.
    }
  }

  @Test
  public void testContains() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    list.add(s);
    assertTrue(list.contains(s));
    assertFalse(list.contains("Nonexistent"));
  }

  @Test
  public void testIndexOf() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    String t = "Test 2";
    list.add(s);
    list.add(t);
    assertEquals(list.indexOf(s), 0);
    assertEquals(list.indexOf(t), 1);
    
    // test negative case
    assertEquals(list.indexOf("Not here"), -1);
  }

  @Test
  public void testSize() {
    StringLinkedList list = new StringLinkedList();
    assertEquals(list.size(), 0);
    String s = "Test 1";
    list.add(s);
    assertEquals(list.size(), 1);
    
    list.clear();
    assertEquals(list.size(), 0);
  }

  @Test
  public void testInsert() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    list.add(s);
    String t = "Test 2";
    list.add(0, t);
    assertEquals(list.get(0), t);
    assertEquals(list.get(1), s);
    
    // test out of bounds
    try {
      list.add(100, s);
      fail();
    } catch (Exception exc) {
      // OK - this should have thrown an exception.
    }
    
    // Test proper growth for insert
    list = new StringLinkedList();
    int size = 10000;
    list.add("0");
    for (int i = 1; i < size; i++) {
      list.add(0, Integer.toString(i));
    }
    assertEquals(list.get(size - 1), "0");
    
    list = new StringLinkedList();
    list.add(null);
    list.add(null);
    assertEquals(list.indexOf("A"), -1);
  }

  @Test
  public void testClear() {
    StringLinkedList list = new StringLinkedList();
    list.add("x");
    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(list.size(), 0);
  }

  @Test
  public void testIsEmpty() {
    StringLinkedList list = new StringLinkedList();
    assertTrue(list.isEmpty());
    list.add("x");
    assertFalse(list.isEmpty());
    list.clear();
    assertTrue(list.isEmpty());
  }

  @Test
  public void testRemove() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    String t = "Test 2";
    list.add(s);
    list.add(t);
    list.remove(0);
    assertEquals(list.size(),1);
    assertEquals(list.get(0), t);
    list.remove(0);
    assertTrue(list.isEmpty());
    
    // test bounds
    try { 
      list.remove(0);
      fail();
    } catch (Exception exc) {
      // OK - this should have thrown an exception.
    }
    
    list = new StringLinkedList();
    list.add("A");
    list.add("B");
    list.add("C");
    list.add("D");
    list.remove(0);
    assertEquals(list.get(0), "B");
    assertEquals(list.get(1), "C");
    assertEquals(list.get(2), "D");
    assertEquals(list.size(), 3);
    
    list.remove(2); // list is now B, C
    list.remove(0); // list is now C
    list.add("E");  // list is now C, E
    assertEquals("E", list.get(1));
    assertEquals("C", list.get(0));
    assertEquals(2, list.size());
  }

  @Test
  public void testSet() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    String t = "Test 2";
    String u = "New";
    list.add(s);
    list.add(t);
    list.set(0, u);
    assertEquals(list.get(0), u);

    // test bounds
    try { 
      list.set(10000, u);
      fail();
    } catch (Exception exc) {
      // OK - this should have thrown an exception.
    }
  }

  @Test
  public void testToArray() {
    StringLinkedList list = new StringLinkedList();
    String s = "Test 1";
    String t = "Test 2";
    list.add(s);
    list.add(t);
    String[] strings = list.toArray();
    assertEquals(strings[0], s);
    assertEquals(strings[1], t);
    
    assertEquals(strings.length, 2);
    
    // Test to ensure that changing strings[] does not change the list.
    strings[0] = "Something else";
    assertEquals(list.get(0), s);
  }
  
  @Test
  public void testIterator() {
    StringLinkedList list = new StringLinkedList();
    for (int i = 10; i < 20; i++) {
      list.add(Integer.toString(i));
    }
    list.remove(0);    // list is 11, 12, 13, 14, 15, 16, 17, 18, 19
    list.remove(8);    // list is 11, 12, 13, 14, 15, 16, 17, 18
    list.add(0, "A");  // list is A, 11, 12, 13, 14, 15, 16, 17, 18
    list.remove(1);    // list is A, 12, 13, 14, 15, 16, 17, 18
    list.remove(7);    // list is A, 12, 13, 14, 15, 16, 17
    list.add(6, "Z");  // list is A, 12, 13, 14, 15, 16, Z, 17
    
    StringListIterator i = list.iterator();
    assertEquals("A", i.next());
    assertEquals("12", i.next());
    assertEquals("13", i.next());
    assertEquals("14", i.next());
    assertEquals("15", i.next());
    assertEquals("16", i.next());
    assertEquals("Z", i.next());
    assertTrue(i.hasNext());
    assertEquals("17", i.next());
    assertFalse(i.hasNext());
  }
}
