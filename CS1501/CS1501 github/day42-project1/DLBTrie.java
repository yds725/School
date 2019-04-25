import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by 5years on 9/19/2017.
 */

public class DLBTrie {
    private TrieNode rootNode;

    public DLBTrie() {
        rootNode = new TrieNode();
    }


    public boolean addWord(String word) {
        TrieNode node = rootNode;

        for (Character c : word.toLowerCase().toCharArray()) {
            TrieNode child = node.getNextNode(c);

            if (child != null) {
                node = child;
            } else {
                node = node.addChar(c);
            }
        }

        if (node.lastWord()) {
            return false;
        }

        node.setLastNode(true);

        return true;
    }


    public ArrayList<String> printPredictions(String prefix, int textToPrint) {
        long startTime = System.nanoTime();
        TrieNode node = rootNode;


        for (Character c : prefix.toLowerCase().toCharArray()) {
            TrieNode child = node.getNextNode(c);
            if (child != null) {
                node = child;
            } else {

            }

        }

        ArrayList<String> predictions = new ArrayList<String>();
        int i = 1;

        Queue<TrieNode> queue = new LinkedList<>();
        queue.offer(node);

        while (!queue.isEmpty() && textToPrint > 0) {
            TrieNode n = queue.poll();

            if (n.lastWord()) {
                predictions.add(n.getText());
                textToPrint--;
            }

            for (Character c : n.getNextChar()) {
                queue.offer(n.getNextNode(c));
            }
        }

        double workTime = (System.nanoTime() - startTime)/1000000000.0;
        System.out.println(workTime + "s");
        System.out.println("Predictions: ");

        for (String s : predictions) {
            System.out.printf("(%d) %s ", i++, s);
        }

        return predictions;
    }




}
