/**
 * Created by Luciano1 on 4/18/17.
 */
public class SearchNode implements Comparable<SearchNode> {
    Long id;
    double distFromStart;
    double distToDest;
    SearchNode prev;

    public SearchNode(Long nodeID, double distToDest, double distanceFromStart, SearchNode prev) {
        this.id = nodeID;
        this.distToDest = distToDest;
        this.distFromStart = distanceFromStart;
        this.prev = prev;
    }
    public double getPriority() {
        return distFromStart + distToDest;
    }

    @Override
    public int compareTo(SearchNode other) {
        double compare = getPriority() - other.getPriority();
        if (compare > 0) {
            return 1;
        } else if (compare == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
