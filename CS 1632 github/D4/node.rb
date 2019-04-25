# reference from exercise 2 node class from Dr.Laboon
# wordNode class that represents node
class Node
  attr_accessor :id, :string, :neighbors

  def add_neighbor(node)
    @neighbors << node
  end

  def connected?
    @neighbors.count.nonzero?
  end

  def alone?
    @neighbors.count.zero?
  end

  def initialize(id, string)
    @id = id # node id
    @neighbors = []
    @string = string # letter in that node
  end

  # def to_s
  #   neighbor_ids = self.connected? ? @neighbors.join(',') : '---'
  #   "Node #{@id}: [ #{neighbor_ids} ]"
  # end
end
