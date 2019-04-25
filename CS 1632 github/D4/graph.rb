require_relative 'node.rb'
# Reference from exercise 2 from Dr.Laboon
# Just graph class that represents ascyclic graph
class Graph
  attr_accessor :valid_wordlist, :nodes
  # initialize method
  def initialize
    @nodes = {}
    @valid_wordlist = []
  end

  def add_node(each_line)
    # split each line with ';' "1;C;2,3" -> [1,C,(2,3)]
    split_line = each_line.split(';')
    node_id = split_line[0]
    alphabet = split_line[1]
    node = Node.new(node_id, alphabet)
    id = node.id
    @nodes[id] = node
    id
  end

  def get_actual_neighbors_node(each_line)
    # split each line with ';' "1;C;2,3" -> [1,C,(2,3)]
    split_line = each_line.split(';')
    node_id = split_line[0]
    adjacent_nodes = split_line[2]

    if adjacent_nodes != "\n"
      # chomp removes \n character
      neighbor_nodes = adjacent_nodes.chomp.split(',')
      neighbor_nodes.each do |nb|
        parent = get_node(node_id)
        parent.add_neighbor get_node(nb)
        id = parent.id
        id
      end
    end
  end

  def get_node(id)
    @nodes.each do |_, node|
      if node.id == id
        # return node
        return node
      end
    end
  end

  def go_through_nodes(word_list)
    @nodes.each do |_, node|
      paths(node) { |path|
        lower_case = to_lower_case path

        list = all_permutations lower_case
        same_words = compare_two_string_arrays(list, word_list)
        # puts "same words:"
        # puts same_words.inspect

        push_word_to_list same_words
      }
    end
    # puts @valid_wordlist.inspect
    longest_words = find_longest_words @valid_wordlist
    # puts longest_words.inspect

    longest_words = to_upper_case longest_words # uppercase strings in array
    # puts longest_words.inspect

    print_final_output longest_words
    # puts wordList
  end

  def paths(node, path = '', &proc)
    if node.neighbors.empty?
      yield(path + node.string)
    else
      node.neighbors.each { |n| paths(n, path + node.string, &proc) }
    end
  end

  def all_permutations(word)
    list_permuted = word.chars.to_a.permutation.map(&:join)
    list_permuted
  end

  def push_word_to_list(words)
    words.each do |w|
      @valid_wordlist << w
    end
  end

  def compare_two_string_arrays(perm_list, word_list)
    same_words = perm_list & word_list
    same_words
  end

  def to_lower_case(word)
    word.downcase
  end

  def find_longest_words(words)
    maximum_len = words.map(&:length).max
    longest = words.select { |w| w.length == maximum_len }
    longest
  end

  def to_upper_case(str_array)
    str_array.map(&:upcase)
  end

  def print_final_output(longest_words)
    puts 'Longest Valid Word(s):'
    puts longest_words
  end
end
