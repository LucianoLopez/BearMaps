import java.util.*;

/**
 * Created by Luciano1 on 4/10/17.
 */
public class QuadTree {
    private Tile root;

    /**
     * This Node class aids in the construction of the QuadTree
     */
    class Tile {
        private String name;
        private double ullat;
        private double ullon;
        private double lrlat;
        private double lrlon;
        private int depth;
        private Tile NWbranch;
        private Tile NEbranch;
        private Tile SEbranch;
        private Tile SWbranch;

        public Tile(double ROOT_ULLAT, double ROOT_ULLON, double ROOT_LRLAT, double ROOT_LRLON, String IMGname, int _depth) {
            name = "img/" + IMGname + ".png";
            ullat = ROOT_ULLAT;
            ullon = ROOT_ULLON;
            lrlat = ROOT_LRLAT;
            lrlon = ROOT_LRLON;
            depth = _depth;
            NWbranch = null;
            NEbranch = null;
            SEbranch = null;
            SWbranch = null;
        }
    }

    public QuadTree() {
        this.put(37.892195547244356, -122.2998046875, 37.82280243352756,
                -122.2119140625, "root"); // The master root
        InputAll(37.892195547244356, -122.2998046875, 37.82280243352756,
                -122.2119140625, "");

    }

    /**
     * This method puts all imgs from img directory into the quad tree, starting at the root tile
     */
    private void InputAll(double ullat, double ullon, double lrlat, double lrlon, String prevName) {
        if (prevName.length() == 7) {
            return;
        }
        double midLon = ((ullon - lrlon) / 2) + lrlon;
        double midLat = ((ullat - lrlat) / 2) + lrlat;
        put(ullat, ullon, midLat, midLon, prevName + "1");
        put(ullat, midLon, midLat, lrlon, prevName + "2");
        put(midLat, ullon, lrlat, midLon, prevName + "3");
        put(midLat, midLon, lrlat, lrlon, prevName + "4");
        InputAll(ullat, ullon, midLat, midLon, prevName + "1");
        InputAll(ullat, midLon, midLat, lrlon, prevName + "2");
        InputAll(midLat, ullon, lrlat, midLon, prevName + "3");
        InputAll(midLat, midLon, lrlat, lrlon, prevName + "4");
    }

    /**
     * Puts the given node into correct position in the QuadTree
     */
    void put(double ROOT_ULLAT, double ROOT_ULLON, double ROOT_LRLAT, double ROOT_LRLON, String imgName) {
        root = putHelper(ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON, imgName, root, 0);
    }

    private Tile putHelper(double ullat, double ullon, double lrlat, double lrlon, String imgName, Tile node, int depth) {
        if (node == null) {
            node = new Tile(ullat, ullon, lrlat, lrlon, imgName, depth);
            return node;
        }
        double midLon = ((node.ullon - node.lrlon) / 2) + node.lrlon;
        double midLat = ((node.ullat - node.lrlat) / 2) + node.lrlat;
        if (ullon < midLon && ullat > midLat) {
            node.NWbranch = putHelper(ullat, ullon, lrlat, lrlon, imgName, node.NWbranch, depth += 1);
        } else if (lrlon > midLon && ullat > midLat) {
            node.NEbranch = putHelper(ullat, ullon, lrlat, lrlon, imgName, node.NEbranch, depth += 1);
        } else if (lrlon > midLon && lrlat < midLat) {
            node.SEbranch = putHelper(ullat, ullon, lrlat, lrlon, imgName, node.SEbranch, depth += 1);
        } else if (ullon < midLon && lrlat < midLat) {
            node.SWbranch = putHelper(ullat, ullon, lrlat, lrlon, imgName, node.SWbranch, depth += 1);
        }
        return node;
    }

    /**
     * This method determines what should be rasterized in the quadtree.
     * Looks to see if the given tile is intersected && the tile has the correct resolution
     */
    HashMap<String, Object> rasterList(double ullat, double ullon, double lrlat, double lrlon, int desiredDepth) {
        LinkedHashMap<Double, LinkedList<Tile>> rasterList = new LinkedHashMap<>();
        LinkedHashMap<Double, LinkedList<Tile>> list = rasterListHelper(ullat, ullon, lrlat, lrlon, root, desiredDepth, rasterList);
        return sortedList(list);
    }

    LinkedHashMap<Double, LinkedList<Tile>> rasterListHelper(double ullat, double ullon, double lrlat, double lrlon, Tile tile, int desiredDepth, LinkedHashMap<Double, LinkedList<Tile>> rasterList) {
        if (intersectsTile(tile, ullat, ullon, lrlat, lrlon) && desiredDepth == tile.depth) {
            if (rasterList.containsKey(tile.ullat)) {
                LinkedList<Tile> list = rasterList.get(tile.ullat);
                list.addLast(tile);
            } else {
                LinkedList<Tile> tileList = new LinkedList<>();
                tileList.addLast(tile);
                rasterList.put(tile.ullat, tileList);
            }
        } else if (!intersectsTile(tile, ullat, ullon, lrlat, lrlon)) {
            return null;
        } else if (intersectsTile(tile, ullat, ullon, lrlat, lrlon) && desiredDepth != tile.depth) {
            rasterListHelper(ullat, ullon, lrlat, lrlon, tile.NWbranch, desiredDepth, rasterList); //top left
            rasterListHelper(ullat, ullon, lrlat, lrlon, tile.NEbranch, desiredDepth, rasterList); //top right
            rasterListHelper(ullat, ullon, lrlat, lrlon, tile.SWbranch, desiredDepth, rasterList); //bottom left
            rasterListHelper(ullat, ullon, lrlat, lrlon, tile.SEbranch, desiredDepth, rasterList); //bottom right
        }
        return rasterList;
    }

    private boolean intersectsTile(Tile tile, double ullat, double ullon, double lrlat, double lrlon) {
        return (tile.ullon < lrlon && tile.lrlon > ullon && tile.ullat > lrlat && tile.lrlat < ullat);
    }

    /**
     * This method sorts the given hashmap and returns a hashmap with all necessary params required for
     * Rasterer
     */
    private HashMap<String, Object> sortedList(LinkedHashMap<Double, LinkedList<Tile>> list) {
        HashMap<String, Object> finalMap = new HashMap<>();
        String[][] finalList;
        Object[] keys = list.keySet().toArray();
        Tile tile = list.get(keys[0]).get(0);
        int col = list.get(keys[0]).size();
        int row = list.size();
        finalList = new String[row][col];
        double largestUllat = tile.ullat;
        double largestUllon = tile.ullon;
        LinkedList<Tile> array = list.get(keys[keys.length - 1]);
        Tile bottomTile = array.get(array.size() - 1);
        double lowestlrlat = bottomTile.lrlat;
        double lowestlrlon = bottomTile.lrlon;
        LinkedList<Tile> values;
        for (int i = 0; i < row; i++) {
            values = list.get(keys[i]);
            for (int x = 0; x < col; x++) {
                finalList[i][x] = values.get(x).name;
            }
        }
        finalMap.put("raster_ul_lon", largestUllon);
        finalMap.put("raster_ul_lat", largestUllat);
        finalMap.put("depth", tile.depth);
        finalMap.put("raster_lr_lon", lowestlrlon);
        finalMap.put("raster_lr_lat", lowestlrlat);
        finalMap.put("query_success", true);
        finalMap.put("render_grid", finalList);
        return finalMap;
    }

    public static void main(String[] args) {
//        long startTime = System.currentTimeMillis();
        QuadTree tree = new QuadTree();
//        long endTime = System.currentTimeMillis();
//        long duration = (endTime - startTime);
//        System.out.print(duration);
    }
}
