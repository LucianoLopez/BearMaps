import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Luciano1 on 4/19/17.
 */
public class Trie {
    TrieNode root;

    public Trie() {
        root = new TrieNode(null, false);
    }

    public void put(String oldName, String cleanName) {
        if (cleanName.length() == 0) {
            return;
        }
        char[] ch = cleanName.toCharArray();
        root = putHelper(ch, oldName, cleanName, root, 0);
    }

    private TrieNode putHelper(char[] ch, String oldName, String word, TrieNode node, int depth) {
        if (depth == ch.length) {
            node.isWord = true;
            node.text = oldName;
        } else {
            char character = ch[depth];
            if (node.children.containsKey(character)) {
                TrieNode newNode = node.children.get(character);
                newNode = putHelper(ch, oldName, word, node.children.get(character), depth + 1);
            } else {
                TrieNode newNode = new TrieNode(null, false);
                node.children.put(character, newNode);
                newNode = putHelper(ch, oldName, word, newNode, depth + 1);
            }
        }
        return node;

    }

    public ArrayList<String> get(String string) {
        ArrayList<String> words = new ArrayList<>();
        if (string.length() == 0) {
            return new ArrayList<>();
        }
        char[] ch = string.toCharArray();
        return getHelper(words, ch, root, 0);
    }

    private ArrayList<String> getHelper(ArrayList<String> words, char[] ch, TrieNode node, int depth) {
        if (node.isWord != null && node.isWord) {
            words.add(node.text);
        }
        if (node.children.isEmpty()) {
            return null;
        }
        if (depth < ch.length) {
            char c = ch[depth];
            if (node.children.containsKey(c)) {
                getHelper(words, ch, node.children.get(c), depth + 1);
            } else {
                return null; //because then there is no longer a prefix matching any words;
            }
        } else {
            PriorityQueue<Character> minPQ = new PriorityQueue<>();
            for (char c : node.children.keySet()) {
                minPQ.add(c);
            }
            while (!minPQ.isEmpty()) {
                char c = minPQ.poll();
                getHelper(words, ch, node.children.get(c), depth + 1);
            }
        }
        return words;
    }

//        if (node.isWord != null) {
//            words.add(node.text);
//        }
//        if (depth >= ch.length) {
//            if (node.children.isEmpty()) {
//                return null;
//            } else {
//                PriorityQueue<Character> listChar = new PriorityQueue<>();
//                for (Character c : node.children.keySet()) {
//                    listChar.add(c);
//                }
//                char character = listChar.poll();
//                getHelper(words, ch, node.children.get(character), depth + 1);
//            }
//        } else {
//            char character = ch[depth];
//            if (node.children.containsKey(character)) {
//                getHelper(words, ch, node.children.get(character), depth + 1);
//            } else {
//                return null;
//            }
//        }
//        return words;


    class TrieNode {
        Boolean isWord;
        String text;
        HashMap<Character, TrieNode> children;

        public TrieNode(String text, Boolean isWord) {
            text = text;
            isWord = isWord;
            children = new HashMap<>();

        }
        /** Represents the actual word
         * Only needs to be done if it represent's a word
         */
    }

    public static void main(String[] args) {
        Trie word = new Trie();
        word.put("yes", "yes");
        word.put("yesterday", "yesterday");
        word.put("yellow", "yellow");
        System.out.println(word.get("ye"));

    }
}
