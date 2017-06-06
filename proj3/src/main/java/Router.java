
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(
            GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        MinPQ<SearchNode> fringe = new MinPQ<>();
        HashSet<Long> visited = new HashSet<>();
        LinkedList<Long> path = new LinkedList<>();
        long startID = g.closest(stlon, stlat);
        long goalID = g.closest(destlon, destlat);
        double distanceTo = g.distance(startID, goalID);
        SearchNode curNode = new SearchNode(startID, distanceTo, 0, null);
        fringe.insert(curNode, curNode.getPriority());
        while (fringe.size() != 0) {
            curNode = fringe.removeMin();
            visited.add(curNode.id);
            if (isGoal(curNode, goalID)) {
                return pathCreator(curNode, goalID);
            }
            for (Long id : g.adjacent(curNode.id)) {
                if (!visited.contains(id)) {
                    double distanceFrom = curNode.distFromStart + g.distance(curNode.id, id);
                    distanceTo = g.distance(id, goalID);
                    SearchNode newNode = new SearchNode(id, distanceTo, distanceFrom, curNode);
                    newNode.distToDest = distanceTo;
                    newNode.distFromStart = distanceFrom;
                    newNode.prev = curNode;
                    fringe.insert(newNode, newNode.getPriority());
                }
            }
        }
        return path;

    }

    public static boolean isGoal(SearchNode node, long goalID) {
        return node.id == goalID;
    }

    public static LinkedList<Long> pathCreator(SearchNode curNode, long goalID) {
        LinkedList<Long> path = new LinkedList<>();
        path.addFirst(goalID);
        while (curNode.prev != null) {
            path.addFirst(curNode.prev.id);
            curNode = curNode.prev;
        }
        return path;
    }
}
