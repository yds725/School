require 'sinatra'
require 'sinatra/reloader'

default_True_Symbol = "T"
default_False_Symbol = "F"
default_table_size = 3
	
    # this reeturns check_args_state as 1 or 0 
    # 1 - true(valid input) 0 - false(false input)
    # this checks whether true, false input is single character and they are not same
    # also whether size table is greater than or equal to 2
	def check_args(true_symbol, false_symbol, size_input)
		if(true_symbol.length == 1 && false_symbol.length == 1 && true_symbol != false_symbol && size_input.to_i >= 2)
			return 1
		else
			return 0
		end
	end

	# create array of binary representation from integer by 2^size (for loop)
	def convertIntegerToBinaryBit(i_count, size, true_value, false_value)	
        array = Array.new(size) # create array with size of input size
        int = i_count
		temp = size
		until temp < 1
            #temp = temp - 1
            temp -= 1
			if int / (2**temp) > 0 # check if divided number is 0
				array[size-temp-1] = true_value
				int = int % (2**temp)
			else
				array[size-temp-1] = false_value
			end
		end
		array
	end

    # Logical AND always return false when one of input is false
    # returns only true when all of inputs are true
	def logicalAND(array, true_value, false_value)
		for a in array
			if a == false_value # if value is false input value
				return false_value
			end
		end
		return true_value
	end

    # Logical OR always return true when one of input is true
    # returns only false when all of inputs are false
	def logicalOR(array, true_value, false_value)
		for a in array
			if a == true_value # if value is true input value
				return true_value
			end
		end
		return false_value
	end

    # XOR gives a true output when the number of true inputs is odd
    # when even it gives false output (Wikipedia)
	def logicalXOR(array, true_value, false_value)
		trueInputCounter = 0 
		for a in array
			if a == true_value
				trueInputCounter += 1
			end
        end
        
		if (trueInputCounter % 2) == 1 #check if number of true input is odd or not
			return true_value
		else
			return false_value
		end
    end
    
    # create array of binary representation from integer by 2^size (for loop)
	def convertIntegerToBinaryBit(i_count, size, true_value, false_value)	
        array = Array.new(size) # create array with size of input size
        int = i_count
		temp = size
		until temp < 1
            #temp = temp - 1
            temp -= 1
			if int / (2**temp) > 0 # check if divided number is 0
				array[size-temp-1] = true_value
				int = int % (2**temp)
			else
				array[size-temp-1] = false_value
			end
		end
		array
	end

# Main home page setup
get '/' do
    
	true_value = params['true_input']
	false_value = params['false_input']
	size_value = params['table_size']
	
    check_args_state = 2

	if (true_value == "" and false_value == "" and size_value == "") # if inputs are empty string
		default_True_Symbol = "T"
		default_False_Symbol = "F"
		default_table_size = 3
        check_args_state = 1
    elsif (true_value.nil? and false_value.nil? and size_value.nil?) # if input values are nil (if user does not enter and not run)
        default_True_Symbol = nil
        default_False_Symbol = nil
        default_table_size = nil 
	else # get inputs if they do not violate rules
		default_True_Symbol = true_value
		default_False_Symbol = false_value
		default_table_size = size_value
        check_args_state = check_args(default_True_Symbol, default_False_Symbol, default_table_size)
    
	end

	if check_args_state == 0
		redirect 'error2'
	elsif check_args_state == 1
		redirect 'index2'
	end
		
	erb :index # main page
end

# show index2 page
get '/index2' do
	erb :index2, :locals => { true_input: default_True_Symbol, false_input: default_False_Symbol, size: default_table_size.to_i }
end

# Page specifying invalid address
not_found do
	status 404
	erb :error
end

get '/error2' do
	erb :error2 # invalid inputs error 
end