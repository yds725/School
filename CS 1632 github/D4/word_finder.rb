# main file to run
# take one argument which is txt file and produce longest list of words
require_relative 'graph.rb'
require_relative 'node.rb'

def show_usage_no_args
  puts 'Usage:'
  puts 'ruby word_finder.rb *graph_txt_file*'
  puts 'Require one argument which is the name of txt file'
  exit 1
end

def show_usage_file_error
  puts 'Could not open file'
  puts 'File may not exist. Please check the path or file name.'
end

# tries to check if argument is not only one
# and file existence

def check_arguments(args)
  if args.count != 1
    show_usage_no_args
  elsif args.count == 1
    begin
      txt_file = File.new(args[0], 'r')
      File.open(txt_file, 'r')
    rescue StandardError
      show_usage_file_error
    end
  end
  txt_file
end
# if argument is valid
# execute the program
valid_args = check_arguments(ARGV)
if valid_args
  graph = Graph.new
  # read each line with I/O foreach
  File.foreach(valid_args) do |line|
    graph.add_node line
  end

  File.foreach(valid_args) do |line|
    graph.get_actual_neighbors_node line
  end

  # read file each line by line and use chomp to remove \n
  word_list = File.readlines('wordlist.txt').map(&:chomp)

  # graph.print_node_as_string
  graph.go_through_nodes word_list
end
