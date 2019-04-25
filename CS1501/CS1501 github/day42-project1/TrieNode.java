import java.util.HashMap;
import java.util.Set;
/**
 * Created by 5years on 9/19/2017.
 */

public class TrieNode {

    private HashMap<Character, TrieNode> nextNode;
    private String text;
    private boolean hasWord;


    public TrieNode()
    {
        nextNode = new HashMap<Character, TrieNode>();
        text = "";
        hasWord = false;
    }

    public TrieNode(String text)
    {
        this();
        this.text = text;
    }


    public TrieNode getNextNode(Character c)
    {
        return nextNode.get(c);
    }


    public TrieNode addChar(Character c)
    {
        if (nextNode.containsKey(c)) {
            return null;
        }

        TrieNode next = new TrieNode(text + c.toString());
        nextNode.put(c, next);
        return next;
    }

    public String getText()
    {
        return text;
    }

    public void setLastNode(boolean val)
    {
        hasWord = val;
    }

    public boolean lastWord()
    {
        return hasWord;
    }

    public Set<Character> getNextChar()
    {
        return nextNode.keySet();
    }

}