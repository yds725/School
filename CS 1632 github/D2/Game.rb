# runs a gold rush simulation with num prospectors and seed of random number as args
class Game
  attr_reader :map, :num_prospectors, :city, :num_days, :num_iteration, :curr_gold, :curr_silver
  attr_accessor :num_visited
  def initialize(input)
    unless check_arguments(input)
      puts '*seed* should be an integer *num_prospectors* should be a non-negative integer'
      exit 1
    end
    seed = input[0].to_i
    num_players = input[1].to_i
    @rand = Random.new seed # intialize random with seed from parameter
    @num_prospectors = num_players # initialize num_prospectors from parameter
    @map = {
      'Sutter Creek' => ['Coloma', 'Angels Camp'],
      'Coloma' => ['Sutter Creek', 'Virginia City'],
      'Angels Camp' => ['Nevada City', 'Sutter Creek', 'Virginia City'],
      'Nevada City' => ['Angels Camp'],
      'Virginia City' => ['Angels Camp', 'Coloma', 'Midas', 'El Dorado Canyon'],
      'Midas' => ['Virginia City', 'El Dorado Canyon'],
      'El Dorado Canyon' => ['Virginia City', 'Midas']
    }
    @max_silver = {
      'Sutter Creek' => 0,
      'Coloma' => 0,
      'Angels Camp' => 0,
      'Nevada City' => 0,
      'Virginia City' => 3,
      'Midas' => 5,
      'El Dorado Canyon' => 10
    }
    @max_gold = {
      'Sutter Creek' => 2,
      'Coloma' => 3,
      'Angels Camp' => 4,
      'Nevada City' => 5,
      'Virginia City' => 3,
      'Midas' => 0,
      'El Dorado Canyon' => 0
    }
    @city = 'Sutter Creek'
    @num_visited = 0 # initialize
    @num_days = 0 # initialize
    @curr_silver = 0 # initialize
    @curr_gold = 0 # initialize
  end

  def run_simulation
    i = 1
    while i <= @num_prospectors
      iteration @city, i
      @num_visited = 0
      @num_days = 0
      @curr_silver = 0
      @curr_gold = 0
      i += 1
    end
  end

  # returns a random number between 0 and up to bound - exclusive
  def random_num(bound)
    @rand.rand(bound)
  end

  # returns the next locations
  def next_location(current)
    # get list of destinations defined in map
    destinations = @map[current]
    # get count
    size = destinations.count
    # get random number within destinations indices
    rand_index = random_num(size)
    # return new location
    destinations[rand_index]
  end

  def get_gold(current)
    # get max number of gold at current location
    max = @max_gold[current]
    # add one because of exclusive random
    max += 1
    # get random number of silver
    rand_gold = random_num(max)
    rand_gold
  end

  def get_silver(current)
    # get max number of silver at current location
    max = @max_silver[current]
    # increment because rand is exclusive
    max += 1
    # get random number of silver
    rand_silver = random_num(max)
    rand_silver
  end

  def iteration(curr, num_iteration)
    puts 'Prospector ' + num_iteration.to_s + ' starting in Sutter Creek.'
    city = curr
    while num_visited < 5
      amount_silver = get_silver(city)
      amount_gold = get_gold(city)
      # if statement
      if num_visited < 3
        @num_days += 1
        if amount_silver.zero? && amount_gold.zero?
          current_city = city
          city = next_location(city)
          print_gold_and_silver_amounts(amount_silver, amount_gold)
          res = move_status(@curr_silver, @curr_gold, current_city, city)
          puts res
          # increment num_visited
          @num_visited += 1
        else
          @curr_silver += amount_silver
          @curr_gold += amount_gold
          print_gold_and_silver_amounts(amount_silver, amount_gold)
        end
      elsif num_visited < 5
        @num_days += 1
        if amount_gold <= 1 && amount_silver <= 2
          current_city = city
          city = next_location(city)
          @num_visited += 1
          @curr_silver += amount_silver
          @curr_gold += amount_gold
          # print gold and silver amounts
          print_gold_and_silver_amounts(amount_silver, amount_gold)
          puts move_status(@curr_silver, @curr_gold, current_city, city) if num_visited != 5
        else
          @curr_silver += amount_silver
          @curr_gold += amount_gold
          print_gold_and_silver_amounts(amount_silver, amount_gold)
        end
      end
    end
    puts 'After ' + @num_days.to_s + ' days ' + 'Prospector #' + num_iteration.to_s + ' returned to San Franciso with: '
    print_monetary_sum @curr_silver, @curr_gold
  end

  # print method
  def print_gold_and_silver_amounts(silver, gold)
    # print out gold and silver values
    if silver.zero? && gold.zero?
      s = "\tNo Precious Metals Found"
    elsif silver == 1 && gold == 1
      s = "\t" + silver.to_s + ' ounce of Silver and ' + gold.to_s + ' ounce of Gold!'
    elsif silver > 1 && gold == 1
      s = "\t" + silver.to_s + ' ounces of Silver and ' + gold.to_s + ' ounce of Gold!'
    elsif silver == 1 && gold > 1
      s = "\t" + silver.to_s + ' ounce of Silver and ' + gold.to_s + ' ounces of Gold!'
    elsif silver > 1 && gold > 1
      s = "\t" + silver.to_s + ' ounces of Silver and ' + gold.to_s + ' ounces of Gold!'
    elsif silver.zero? && gold == 1
      s = "\t" + gold.to_s + ' ounce of Gold!'
    elsif silver == 1 && gold.zero?
      s = "\t" + silver.to_s + ' ounce of Silver!'
    elsif silver > 1 && gold.zero?
      s = "\t" + silver.to_s + ' ounces of Silver!'
    elsif silver.zero? && gold > 1
      s = "\t" + gold.to_s + ' ounces of Gold!'
    end
    puts s
    # return s
    s
  end

  def print_monetary_sum(silver, gold)
    if gold == 1
      puts "\t" + gold.to_s + ' ounce of Gold'
    else
      puts "\t" + gold.to_s + ' ounces of Gold'
    end
    if silver == 1
      puts "\t" + silver.to_s + ' ounce of Silver'
    else
      puts "\t" + silver.to_s + ' ounces of Silver'
    end
    # calculate monetary sum
    money_gold = gold * 20.67
    money_silver = silver * 1.31
    sum = money_gold + money_silver
    puts"\tHeading home with $%0.2f.\n" % [sum]
    # return sum
    sum
  end

  def check_arguments(input)
    # checks for correct number of arguments (2)
    return false if input.count != 2
    # checks if seed is an integer
    return false unless input[0].to_i.to_s == input[0]
    # checks is num prospectors is an integer
    return unless input[1].to_i.to_s == input[1]
    # checks is num prospectors is negative
    return false if input[1].to_i < 0

    true
  end

  def move_status(silver, gold, current_city, city)
    if silver == 1 && gold == 1
      'Heading from ' + current_city + ' to ' + city + ' carrying ' + silver.to_s + ' ounce of silver and ' +
        gold.to_s + ' ounce of Gold!'
    elsif silver == 1 && gold != 1
      'Heading from ' + current_city + ' to ' + city + ' carrying ' + silver.to_s + ' ounce of silver and ' +
        gold.to_s + ' ounces of Gold!'
    elsif silver != 1 && gold == 1
      'Heading from ' + current_city + ' to ' + city + ' carrying ' + silver.to_s + ' ounces of silver and ' +
        gold.to_s + ' ounce of Gold!'
    else
      'Heading from ' + current_city + ' to ' + city + ' carrying ' + silver.to_s +
        ' ounces of silver and ' + gold.to_s + ' ounces of Gold!'
    end
  end
end
