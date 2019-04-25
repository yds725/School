require 'minitest/autorun'
require_relative 'Game.rb'


class GoldRushTest < Minitest::Test

	#All tests need test_sim which is instance of Gold_rush class
	#So by using setup method in Minitest library, it makes things easier
	#setup method runs before every test runs
	def setup 
	  input = ["50","2"]
	  @test_sim = Game.new(input)
	
	end

	#This test tests whether Gold_rush class returns actual instance 
	#and newly initialzed object is actual Gold_rush object
	def test_gold_rush_instance
		refute_nil @test_sim
		assert_kind_of Game, @test_sim
	end

	#These next four tests test the valid argument checks for num_prospectors and the seed for random number
	# floating seed value
	# invalid number of args
	# negative prospector value
	# floating prospector value

	def test_argument_check_float_seed
		input = ["1.1","1"]
		valid = @test_sim.check_arguments(input)
		refute valid
	end

	#EDGE CASE
	def test_argument_check_num_arguments
		input = ["1","1","2"]
		valid = @test_sim.check_arguments(input)
		refute valid
	end

	def test_argument_check_negative_prospector
		input = ["1","-1"]
		valid = @test_sim.check_arguments(input)
		refute valid
	end

	def test_argument_check_float_prospector
		input = ["1","1.1"]
		valid = @test_sim.check_arguments(input)
		refute valid
	end

	#These 4 tests print_monetary_sum method. The method returns sum of monetary value of silver and gold
	#Tests whether the correct monetary value is computed if gold == 1 and silver ==1
	#Total value should be the price of one ounce gold and one ounce of silver
	def test_monetary_sum_both_one
		silver = 1;
		gold = 1;
		res  = @test_sim.print_monetary_sum(silver,gold)

		expected = silver * 1.31 + gold * 20.67

		assert_equal expected, res

	end

	#Tests whether the correct monetary value is computed if gold > 1 and silver ==1
	#the sum of gold should be multplied by 20.67 * # of oz of gold and silver should 
	#be just the cost of one oz of silver
	def test_monetary_sum_gold_greater_one
		silver = 1;
		gold = 5;
		res  = @test_sim.print_monetary_sum(silver,gold)

		expected = silver * 1.31 + gold * 20.67

		assert_equal expected, res

	end

	#Tests whether the correct monetary value is computed if gold == 1 and silver > 1
	#Tests if the quanitity of silver is multiplied by the appropriate oz of silver 
	#sum of silver should be 1.31 * oz of silver and gold should just be cost of one oz of ghold
	def test_monetary_sum_silver_greater_1
		silver = 5;
		gold = 1;
		res  = @test_sim.print_monetary_sum(silver,gold)

		expected = silver * 1.31 + gold * 20.67

		assert_equal expected, res

	end

	
	#Tests whether the correct monetary value is computed if gold > 1 and silver > 1
	#Tests if the quantity of gold and silver is multiplied by the appropriate costs
	#Both the quantity of silver and gold (both>1) should be multiplied by costs and added together
	def test_monetary_sum_both_greater_1
		silver = 5;
		gold = 5;
		res  = @test_sim.print_monetary_sum(silver,gold)

		expected = silver * 1.31 + gold * 20.67

		assert_equal expected, res

	end




	#These 4 unit test test random_num method which takes bound value as parameter 
	# and returns a random number between 0 and bound value (exclusive)
	#Pass interger as parameter it should return
	#integer value x s.t. 0 <= x < max
	def test_random_num
		output = @test_sim.random_num(1)
 		assert_equal 0, output
	end
 	#If integer value max is passed, it should not return max
	#This is very important because simulator program heavily depends on
	#random value i.e move to random city, get random amount of gold and silver
	# EDGE CASE
	def test_random_num_max
		max = 1
		output = @test_sim.random_num(max)
 		refute_equal max, output
	end
 	#If non-integer value like string is passed, it should raise 
	#TypeError Exception
	def test_random_num_string
		assert_raises TypeError do
			@test_sim.random_num("Error")
		end
	end
 	#If float value is passed, it should also return float-type numeric value
	#not an integer
	def test_random_num_float
		output = @test_sim.random_num(2.0)
 		assert output.is_a? Float
	end

	#These tests test whether instance variables of Game object is initialized properly
	#Tests number of prospectors are set to same number as user-input and start location is set to sutter creek
	def test_game_initialize_num_prospectors
		assert_equal 2, @test_sim.num_prospectors
		assert_equal 'Sutter Creek', @test_sim.city
	end

	#tests whether list of mine location and array of destinations (next locations) are pointed to 
	#are set properly
	def test_game_initialize_map_array_sutter_creek
		current = 'Sutter Creek'
		assert_includes @test_sim.map[current], 'Angels Camp'
	end

	def test_game_initialize_map_array_coloma
		current = 'Coloma'
		assert_includes @test_sim.map[current], 'Virginia City'
	end

	def test_game_initialize_map_array_angels_camp
		current = 'Angels Camp'
		assert_includes @test_sim.map[current], 'Nevada City'
	end

	def test_game_initialize_map_array_nevada_city
		current = 'Nevada City'
		refute_includes @test_sim.map[current], 'Sutter Creek'
	end

	def test_game_initialize_map_array_nevada_city
		current = 'Nevada City'
		refute_includes @test_sim.map[current], 'Sutter Creek'
	end

	def test_game_initialize_map_array_midas
		current = 'Midas'
		assert_includes @test_sim.map[current], 'El Dorado Canyon'
		refute_includes @test_sim.map[current], 'Coloma'
	end

	#These tests test next_location method which takes current location as parameter
	#and then moves prospector to next city randomly
	#Because of radomness we need to use stubbing for random_num method 
	#This tests Sutter Creek -> Angels Camp 
	def test_sutter_creek_to_angels_camp
		expected_location = "Angels Camp"
		@test_sim.stub(:random_num, 1) do
			assert_equal expected_location, @test_sim.next_location("Sutter Creek")
		end
	end

	#This tests Sutter Creek -> Coloma
	def test_sutter_creek_to_coloma
		expected_location = "Coloma"
		@test_sim.stub(:random_num, 0) do
			assert_equal expected_location, @test_sim.next_location("Sutter Creek")
		end
	end

	#This tests Coloma -> Virginia City
	def test_coloma_to_virginia_city
		expected_location = "Virginia City"
		@test_sim.stub(:random_num, 1) do
			assert_equal expected_location, @test_sim.next_location("Coloma")
		end
	end

	#This tests Angels Camp -> Nevada City
	def test_angels_camp_to_nevada_city
		expected_location = "Nevada City"
		@test_sim.stub(:random_num, 0) do
			assert_equal expected_location, @test_sim.next_location("Angels Camp")
		end
	end

	#This tests Nevada City -> Angels Camp
	def test_nevada_city_to_angels_camp
		expected_location = "Angels Camp"
		@test_sim.stub(:random_num, 0) do
			assert_equal expected_location, @test_sim.next_location("Nevada City")
		end
	end

	#This tests Virginia City -> Midas
	def test_virginia_city_to_midas
		expected_location = "Midas"
		@test_sim.stub(:random_num, 2) do
			assert_equal expected_location, @test_sim.next_location("Virginia City")
		end
	end

	#This tests Midas -> El Dorado Canyon
	def test_midas_to_El_Dorado_Canyon
		expected_location = "El Dorado Canyon"
		@test_sim.stub(:random_num, 1) do
			assert_equal expected_location, @test_sim.next_location("Midas")
		end
	end

	#This tests El Dorado Canyon -> Virginia City
	def test_El_Dorado_Canyon_to_virginia_city
		expected_location = "Virginia City" 
		@test_sim.stub(:random_num, 0) do
			assert_equal expected_location, @test_sim.next_location("El Dorado Canyon")
		end
	end

	#These tests test two methods get_gold and get_silver.
	#Those two methods behaves very similarly so we decided to group them together.
	#Each test tests and confirms that returned values (random gold amount and silver amount less than maximum)
	#are in boundary of 0 ... max(for each different location)
	#These two methods have more than 10 equivalence classes.

	#Max gold for Sutter Creek is 2. Return value shall be in range of 0 ... 2
	def test_get_gold_sutter_creek
		current_city = "Sutter Creek"
		assert_operator 2, :>=, @test_sim.get_gold(current_city)
	end

	#Max gold for Coloma is 3. Return value shall be in range of 0 ... 3
	def test_get_gold_coloma
		current_city = "Coloma"
		assert_operator 3, :>=, @test_sim.get_gold(current_city)
	end

	#Max gold for Angels Camp is 4. Return value shall be in range of 0 ... 4
	def test_get_gold_angels_camp
		current_city = "Angels Camp"
		assert_operator 4, :>=, @test_sim.get_gold(current_city)
	end

	#Max gold for Nevada City is 5. Return value shall be in range of 0 ... 5
	def test_get_gold_nevada_city
		current_city = "Nevada City"
		assert_operator 5, :>=, @test_sim.get_gold(current_city)
	end

	#Max gold for Virginia City is 3. Return value shall be in range of 0 ... 3
	def test_get_gold_virginia_city
		current_city = "Virginia City"
		assert_operator 3, :>=, @test_sim.get_gold(current_city)
	end

	#Max gold for Midas is 0. Return value shall be in range of 0
	def test_get_gold_midas
		current_city = "Midas"
		assert_equal 0, @test_sim.get_gold(current_city)
	end

	#Max gold for El Dorado Canyon is 0. Return value shall be in range of 0
	def test_get_gold_El_Dorado_Canyon
		current_city = "El Dorado Canyon"
		assert_equal 0, @test_sim.get_gold(current_city)
	end

	#Max silver for Sutter Creek is 0. Return value shall be in range of 0
	def test_get_silver_sutter_creek
		current_city = "Sutter Creek"
		assert_equal 0, @test_sim.get_silver(current_city)
	end

	#Max silver for Coloma is 0. Return value shall be in range of 0
	def test_get_silver_coloma
		current_city = "Coloma"
		assert_equal 0, @test_sim.get_silver(current_city)
	end

	#Max silver for Angels Camp is 0. Return value shall be in range of 0
	def test_get_silver_angels_camp
		current_city = "Angels Camp"
		assert_equal 0, @test_sim.get_silver(current_city)
	end

	#Max silver for Nevada City is 0. Return value shall be in range of 0
	def test_get_silver_nevada_city
		current_city = "Nevada City"
		assert_equal 0, @test_sim.get_silver(current_city)
	end

	#Max silver for Virginia City is 3. Return value shall be in range of 0 ... 3
	def test_get_silver_virginia_city
		current_city = "Virginia City"
		assert_operator 3, :>=, @test_sim.get_silver(current_city)
	end

	#Max silver for Midas is 5. Return value shall be in range of 0 ... 5
	def test_get_silver_midas
		current_city = "Midas"
		assert_operator 5, :>=, @test_sim.get_silver(current_city)
	end

	#Max silver for El Dorado Canyon  is 10. Return value shall be in range of 0 ... 10
	def test_get_silver_El_Dorado_Canyon
		current_city = "El Dorado Canyon"
		assert_operator 10, :>=, @test_sim.get_silver(current_city)
	end

	#These many tests below test print_gold_and_silver_amounts. It returns print statement
	#We tested print statement with another string we created in each test
	#Equivalence classes for this method is > 6

	#tests if the print method works with correct grammar if silver == 1 and gold ==1
	#both silver and gold should be printed with "ounce"
	def test_gold_silver_amount_both_one

		
		o =@test_sim.print_gold_and_silver_amounts(1,1)
		silver = 1
		gold = 1
		e = "\t" + silver.to_s + ' ounce of Silver and ' + gold.to_s + ' ounce of Gold!'

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver == 0 and gold ==0
	#should return "No Previous Metals Found"
	def test_gold_silver_amount_both_zero

		
		o =@test_sim.print_gold_and_silver_amounts(0,0)
		silver = 0
		gold = 0
		e = "\tNo Precious Metals Found"

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver > 1 and gold == 1
	#silver should be printed with "ounces" and gold with "ounce"
	def test_gold_silver_greater_one
		o =@test_sim.print_gold_and_silver_amounts(5,1)
		silver = 5
		gold = 1
		e = "\t" + silver.to_s + ' ounces of Silver and ' + gold.to_s + ' ounce of Gold!'

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver == 1 and gold > 1
	#silver should be printed with "ounce" and gold with "ounces"
	def test_gold_silver_greater_one

		
		o =@test_sim.print_gold_and_silver_amounts(1,5)
		silver = 1
		gold = 5
		e = "\t" + silver.to_s + ' ounce of Silver and ' + gold.to_s + ' ounces of Gold!'

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver > 1 and gold > 1
	#both gold and silver should be printed with "ounces"
	def test_gold_silver_and_gold_greater_one
		o =@test_sim.print_gold_and_silver_amounts(2,2)
		silver = 2
		gold = 2
		e = "\t" + silver.to_s + ' ounces of Silver and ' + gold.to_s + ' ounces of Gold!'

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver == 1 and gold == 0 
	#just silver should be printed with "ounce"
	def test_gold_silver_one

		
		o =@test_sim.print_gold_and_silver_amounts(1,0)
		silver = 1
		gold = 0
		e = "\t" + silver.to_s + ' ounce of Silver!'

		assert_equal e, o
	end

	#tests if the print method works with correct grammar if silver == 0 and gold == 1
	#just gold should be printed with "ounce"
	def test_gold_gold_one

		o =@test_sim.print_gold_and_silver_amounts(0,1)
		silver = 0
		gold = 1
		e = "\t" + gold.to_s + ' ounce of Gold!'

		assert_equal e, o
	end

	#tests if the print method works when gold>1 and silver == 0 
	#just gold should be printed with "ounces"
	def test_gold_greater_one_silver_zero

		gold = 3
		silver = 0

		o = @test_sim.print_gold_and_silver_amounts(silver,gold)
		e = "\t" + gold.to_s + ' ounces of Gold!'

		assert_equal e, o

	end


	#tests if the print method works when gold==0 and silver > 1 
	#just silver should be printed with "ounces"
	def test_silver_greater_one_gold_zero

		gold = 0
		silver = 3

		o = @test_sim.print_gold_and_silver_amounts(silver,gold)
		e = "\t" + silver.to_s + ' ounces of Silver!'

		assert_equal e, o

	end

	#test if the print method works when silver > 1 and gold == 1
	#silver should be printed with "ounces" and gold with "ounce"
	def test_silver_greater_one_gold_equal_one

		gold = 1
		silver = 3

		o = @test_sim.print_gold_and_silver_amounts(silver,gold)
		e = "\t" + silver.to_s + ' ounces of Silver and ' + gold.to_s + ' ounce of Gold!'

		assert_equal e, o
	end


	#These 4 tests test move_status. The method returns print statement which shows propspector moving 
	#to another location with carrying certain amount of silver and gold

	#tests the print method's grammar when moving locations when gold==1 and silver ==1
	def test_move_location_statement_silver_gold_ones

		currCity = "Nevada City"
		city = "Angels Camp"
		silver = 1
		gold = 1
		e = 'Heading from ' + currCity + ' to ' + city + ' carrying ' + silver.to_s + ' ounce of silver and ' +gold.to_s + ' ounce of Gold!'
		o  = @test_sim.move_status(silver,gold,currCity,city)

		assert_equal e, o

	end

	#tests print method when moving locations when silver == 1 and gold!=1
	def test_move_location_statement_silver_one

		currCity = "Nevada City"
		city = "Angels Camp"
		silver = 1
		gold = 0
		e = 'Heading from ' + currCity + ' to ' + city + ' carrying ' + silver.to_s + ' ounce of silver and ' +gold.to_s + ' ounces of Gold!'
		o  = @test_sim.move_status(silver,gold,currCity,city)

		assert_equal e, o

	end

	#tests print method when moving locations when silver != 1 and gold==1
	def test_move_location_statement_gold_one

		currCity = "Nevada City"
		city = "Angels Camp"
		silver = 0
		gold = 1
		e = 'Heading from ' + currCity + ' to ' + city + ' carrying ' + silver.to_s + ' ounces of silver and ' +gold.to_s + ' ounce of Gold!'
		o  = @test_sim.move_status(silver,gold,currCity,city)

		assert_equal e, o

	end

	#tests print method when moving locations when silver != 1 and gold!=1
	def test_move_location_statement_gold_silver_not_one

		currCity = "Nevada City"
		city = "Angels Camp"
		silver = 2
		gold = 2
		e = 'Heading from ' + currCity + ' to ' + city + ' carrying ' + silver.to_s + ' ounces of silver and ' +gold.to_s + ' ounces of Gold!'
		o  = @test_sim.move_status(silver,gold,currCity,city)

		assert_equal e, o

	end

	#These 3 tests test iteration method that does operation on current silver, gold amount
	#and number of location visited and number of days has passed

	#tests whether number of location visited is incremented properly
	#it should be incremented until 5 because prospector can only move 5 times
	def test_gold_rush_num_visited

		g = Game.new(['1','1'])
		g.iteration("Sutter Creek",1)
		assert_equal 5, g.num_visited

	end

	#tests whether number of days has passed is incremented properly
	#day counts when prospector is done with mining and move
	#so it should be counted only up to 5 when prospector cannot find 
	# any silver and gold by stubbing random_num to always produce zero
	def test_gold_rush_num_days

		g = Game.new(['1','1'])
		g.stub(:random_num,0)do
			
			g.iteration("Sutter Creek",1)
			assert_equal 5, g.num_days
		end
	end

	#tests current silver, gold amount shall not change when 
	#number of location visited is more than 5 or equal to 5
	#Edge Case
	def test_curr_gold_and_silver_set_to_zero
		g = Game.new(['1','1'])
		g.num_visited = 5
		g.iteration("Sutter Creek",1)

		assert_equal 0, g.curr_gold
		assert_equal 0, g.curr_silver
	end




end


	







		





