import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /**
     * Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc.
     */
    private HashMap<Long, LinkedList<Long>> adj = new HashMap<>();
    private HashMap<Long, Node> listOfNodes = new HashMap();
    private HashMap<String, LinkedList<Node>> locations = new HashMap<>();
    Trie trie = new Trie();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Object[] nodes = listOfNodes.keySet().toArray();
        for (Object node : nodes) {
            long id = (long) node;
            if (adj.get(id).isEmpty()) {
                removeNode(id);
                listOfNodes.remove(id);
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return adj.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     */
    Iterable<Long> adjacent(long v) {
        return adj.get(v);
    }

    /**
     * Returns the Euclidean distance between vertices v and w, where Euclidean distance
     * is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ).
     */
    double distance(long v, long w) {
        double lonV = listOfNodes.get(v).lon;
        double latV = listOfNodes.get(v).lat;
        double lonW = listOfNodes.get(w).lon;
        double latW = listOfNodes.get(w).lat;
        return Math.sqrt(((lonV - lonW) * (lonV - lonW)) + ((latV - latW) * (latV - latW)));
    }

    /**
     * Returns the vertex id closest to the given longitude and latitude.
     */
    long closest(double lon, double lat) {
        double shortest = 1.0 / 0;
        long id = 0;
        for (Node node : listOfNodes.values()) {
            double distance = distance(node.lon, node.lat, lon, lat);
            if (shortest > distance) {
                shortest = distance;
                id = node.id;
            }
        }
        return id;
    }

    double distance(double lonV, double latV, double lonW, double latW) {
        return Math.sqrt(((lonV - lonW) * (lonV - lonW)) + ((latV - latW) * (latV - latW)));
    }

    /**
     * Longitude of vertex v.
     */
    double lon(long v) {
        return listOfNodes.get(v).lon;
    }

    /**
     * Latitude of vertex v.
     */
    double lat(long v) {
        return listOfNodes.get(v).lat;
    }

    void addNode(Node node) {
        listOfNodes.put(node.id, node);
        LinkedList<Long> list = new LinkedList<>();
        adj.put(node.id, list);
    }

    void addEdge(String from, String to) {
        if (from == null || to == null) {
            return;
        } else {
            long newFrom = Long.parseLong(from);
            long newTo = Long.parseLong(to);
            adj.get(newFrom).add(newTo);
            adj.get(newTo).add(newFrom);
        }
    }
    public LinkedList<Node> getLocation(String name) {
        return locations.get(name);
    }

    void removeNode(long id) {
        listOfNodes.remove(id);
        adj.remove(id);
    }

    void setLocation(String name, Node node) {
        String cleanName = cleanString(name);
        if (locations.containsKey(cleanName)) {
            locations.get(cleanName).add(node);
        } else {
            LinkedList<Node> list = new LinkedList<>();
            list.add(node);
            locations.put(cleanName, list);
        }
        trie.put(name, cleanName);
    }
}
