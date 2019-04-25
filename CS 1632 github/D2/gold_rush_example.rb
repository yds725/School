require_relative "Game.rb"

	def show_direction_and_exit
		puts "Usage: *seed* should be an integer *num_prospectors* should be a non-negative integer"
		exit 1
	end

	def check_arguments(args)
		args.count == 2 
		args[1].to_i > 0
	rescue StandardError
		false
	end
		
	valid_args = check_arguments ARGV

	if valid_args
		seed = ARGV[0].to_i
		players = ARGV[1].to_i
		sim = Game.rb(seed, players)
	else
		show_direction_and_exit
	end


	









