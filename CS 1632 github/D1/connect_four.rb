require_relative 'game.rb'

# Print the usage message to STDOUT and then exit the program
# Note that it exits with code 1, meaning there was an error
# (0 is generally used to indicate "no error")

def show_usage_and_exit
  puts 'Usage:'
  puts 'ruby connect_four.rb *x*'
  puts '*x* should be a nonnegative integer'
  exit 1
end

# Returns true if and only if:
# 1. There is one and only one argument
# 2. That argument, when converted to an integer, is nonnegative
# Returns false otherwise
# If any errors occur (e.g. args is nil), just return false - we are
# going to exit anyways, so no need for more detailed categorization
# of the error

def check_args(args)
  number = to_numeric(args)

  args.count == 1 && args[0].to_i > 0 && (number.is_a?(Integer)) #this check if number is Integer
rescue StandardError
  false
end

#This method changes integer string into integer and float string into float so that 
#program can check the number and if it is float program should display error message
def to_numeric(args)
  if args[0].index('.')
    return args[0].to_f
  else
    return args[0].to_i
  end
end
# EXECUTION STARTS HERE

# Verify that the arguments are valid

valid_args = check_args ARGV

# If arguments are valid, create a new game of size x (first and only argument)
# Then play it!
# Otherwise, show proper usage message and exit program

if valid_args
  x_size = ARGV[0].to_i
  g = Game.new x_size
  g.play
  g.show_results
else
  show_usage_and_exit
end
