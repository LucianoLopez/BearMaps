import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luciano1 on 4/18/17.
 */
public class Node implements Comparable<Node> {
    String name;
    long id;
    double lon;
    double lat;
    double distToDest;
    double distFromStart;
    long prev;

    public Node(long id, double lon, double lat, double distToDest, double distanceFromStart, long prev) {
        this.name = null;
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.distToDest = distToDest;
        this.distFromStart = distanceFromStart;
        this.prev = prev;
    }

    public double getPriority() {
        return distFromStart + distToDest;
    }

    @Override
    public int compareTo(Node other) {
//        System.out.println(other.id);
        double compare = getPriority() - other.getPriority();
//        System.out.println(compare);
        if (compare > 0) {
            return 1;
        } else if (compare == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
