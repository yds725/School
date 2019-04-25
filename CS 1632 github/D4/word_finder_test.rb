require 'minitest/autorun'
require_relative 'graph.rb'
require_relative 'node.rb'

class WordFinderTest < Minitest::Test
    
  # Create a new nodes and wordlist before each test
  def setup
  	@w = Graph.new
  	@n = Node.new(0, "a")
  end

  # UNIT TESTS FOR METHOD initialize()
  # Equivalence classes:
  # Tests that the object is made correctly.
  def test_initialize_objects
	# assert_equal @w.nodes, 0
	# assert_equal @w.validWordList, 0
	assert_kind_of Graph, @w 
	assert_kind_of Node, @n
  end

  # Tests that attributes of a Node object are initialized properly. 
  def test_initialize_node
  	assert_equal @n.id, 0
  	assert_equal @n.neighbors, []
  	assert_equal @n.string, "a"
  end

  # Tests the ability to successfully add a neighbor to a Node object.
  def test_add_neighbor
  	test_neighbor = Node.new(1, "b")
  	@n.add_neighbor(test_neighbor)
  	assert_includes @n.neighbors, test_neighbor
  end

  # Tests if adding a new neighbor recognizes a node object as connected. 
  def test_connected_true
  	test_neighbor = Node.new(2, "c")
  	@n.add_neighbor(test_neighbor)
  	assert_equal @n.connected?, 1
  end

  # Tests if not adding a new neighbor recognizes a node object as not connected. 
  def test_connected_false 
  	test_neighbor = Node.new(4, "d")

  	assert_nil @n.connected?
  end

  def test_alone?
  	test_neighbor = Node.new(3, "d")
  	@n.add_neighbor(test_neighbor)
  	assert_equal @n.alone?, false
  end

def test_add_node
      test_line = "3;D;4,5"
      id = @w.add_node(test_line)
      refute_empty @w.nodes
      assert_equal "3", id
end

  def test_get_actual_neighbors_node
  	test_parent = "1;C;2;3"
	@w.add_node(test_parent)
  	id = @w.get_actual_neighbors_node(test_parent)
  	assert_equal ["2"], id
  end

 def test_get_node
   test_node = Node.new(2, "B")
   id = test_node.id
   @w.nodes[id] = test_node

   returned_node = @w.get_node(2)
   assert_equal test_node, returned_node
 end

  def test_all_permutations
  	test_word = "cat"
  	assert_includes @w.all_permutations(test_word), "tca"
  end

  def test_push_word_to_list
  	test_words = ["dog", "cat"]
  	@w.push_word_to_list(test_words)
  	assert_includes @w.valid_wordlist, "dog"
  end

  # If there is a string found in both arrays, that string will be returned. 
  def test_compare_two_string_arrays_one
  	test_list1 = ["cat", "dog", "cow"]
  	test_list2 = ["pig", "rat", "cow"]
  	assert_equal @w.compare_two_string_arrays(test_list1, test_list2), ["cow"]
  end

  # If there is more than one string found in both arrays, those strings will be returned. 
  def test_compare_two_string_arrays_more_than_one
  	test_list1 = ["cat", "dog", "cow"]
  	test_list2 = ["dog", "rat", "cow"]
  	assert_equal @w.compare_two_string_arrays(test_list1, test_list2), ["dog", "cow"]
  end

  # If there is no string found in both arrays, an empty array will be returned. 
  def test_compare_two_string_arrays_none
  	test_list1 = ["cat", "dog", "cow"]
  	test_list2 = ["pig", "rat", "rabbit"]
  	assert_empty @w.compare_two_string_arrays(test_list1, test_list2)
  end

  def test_to_lower_case
  	test_word = "A"
  	assert_equal @w.to_lower_case(test_word), "a" 
  end 

  def test_find_longest_words
  	test_wordList = ["cat", "at", "that"]
  	assert_equal @w.find_longest_words(test_wordList), ["that"]
  end

  def test_to_upper_case
  	test_wordList = ["a", "b", "c"]
  	assert_equal @w.to_upper_case(test_wordList), ["A", "B", "C"]
  end 

  def test_print_final_output
  	test_output = ["cake"]
  	assert_output("Longest Valid Word(s):\ncake\n") {@w.print_final_output(test_output)}
  end

end