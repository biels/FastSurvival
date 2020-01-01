package com.biel.FastSurvival.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimpleMazeGenerator {
    public class Node {
        int x;
        int y;
        Node parent;
        char c;
        char dirs;
    }

    private Node[] nodes;
    private Vector start;
    private Vector end;
    private int width;
    private int height;
    private Random rand;
    private boolean[][] walls;
    private boolean[][] spears;


    public void generateMaze(int sideLength) {
        this.width = sideLength;
        this.height = sideLength;

        init();
        generate();
        randomizeStartAndEnd();
        setWalls();
        randomizeSpears();
    }

    public boolean[][] getWalls() {
        return walls;
    }

    public boolean[][] getSpears() {
        return spears;
    }

    public Vector getStart() {
        return start;
    }

    public Vector getEnd() {
        return end;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void printMaze() {
        //Outputs maze to terminal - nothing special
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Node node = nodes[j + i * width];

                System.out.print(node.c + " ");
            }
            System.out.println();
        }
    }

    public void build(Location location, int wallScale, int spacingScale, Material wallMaterial,
                      Material airMaterial, Material beginningMaterial, Material endingMaterial) {
//        printMaze();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Node node = nodes[j + i * width];
                Location l = location.clone().add(i, 0, j);
                int scale = node.c == '#' ? wallScale : spacingScale;
//                Utils.getCuboidAround(l.clone().add(scale))
                Material m = Material.AIR;
                switch (node.c) {
                    case '#':
                        m = wallMaterial;
                        break;
                    case ' ':
                        m = airMaterial;
                        break;
                    case 'B':
                        m = beginningMaterial;
                        break;
                    case 'E':
                        m = endingMaterial;
                        break;
                    case 'S':
                        m = airMaterial;
                        break;
                    default:
                        break;
                }
                l.getBlock().setType(m);
            }
        }
        Stream.concat(
                Utils.getLine(location.toVector().add(new Vector(0, 0, height)), new Vector(1, 0, 0), width + 1).stream(),
                Utils.getLine(location.toVector().add(new Vector(width, 0, 0)), new Vector(0, 0, 1), height).stream())
                .map(v -> v.toLocation(Objects.requireNonNull(location.getWorld())).getBlock())
                .forEach(b -> b.setType(wallMaterial));
    }

    private void init() {
        int i, j;
        Node n;

        rand = new Random();
        nodes = new Node[width * height];

        // Initialize
        for (i = 0; i < width * height; i++) {
            nodes[i] = new Node();
        }

        //Setup crucial nodes
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                n = nodes[i + j * width];
                if (i * j % 2 != 0) {
                    n.x = i;
                    n.y = j;
                    n.dirs = 15; //Assume that all directions can be explored (4 youngest bits set)
                    n.c = ' ';
                } else {
                    n.c = '#'; //Add walls between nodes
                }
            }
        }
    }

    private void generate() {
        //Setup start node
        Node start = nodes[1 + width];
        start.parent = start;
        Node last = start;

        //Connect nodes until start node is reached and can't be left
        while ((last = link(last)) != start) ;
    }

    private Node link(Node n) {
        //Connects node to random neighbor (if possible) and returns
        //address of next node that should be visited

        int x = 0;
        int y = 0;
        char dir;
        Node dest;

        //Nothing can be done if null pointer is given - return
        if (n == null) return null;

        //While there are directions still unexplored
        while (n.dirs != 0) {
            //Randomly pick one direction
            dir = (char) (1 << (rand.nextInt(Integer.MAX_VALUE) % 4));

            //If it has already been explored - try again
            if ((~n.dirs & dir) != 0) continue;

            //Mark direction as explored
            n.dirs &= ~dir;

            //Depending on chosen direction
            switch (dir) {
                //Check if it's possible to go right
                case 1:
                    if (n.x + 2 < width) {
                        x = n.x + 2;
                        y = n.y;
                    } else continue;
                    break;

                //Check if it's possible to go down
                case 2:
                    if (n.y + 2 < height) {
                        x = n.x;
                        y = n.y + 2;
                    } else continue;
                    break;

                //Check if it's possible to go left
                case 4:
                    if (n.x - 2 >= 0) {
                        x = n.x - 2;
                        y = n.y;
                    } else continue;
                    break;

                //Check if it's possible to go up
                case 8:
                    if (n.y - 2 >= 0) {
                        x = n.x;
                        y = n.y - 2;
                    } else continue;
                    break;
            }

            //Get destination node into pointer (makes things a tiny bit faster)
            dest = nodes[x + y * width];

            //Make sure that destination node is not a wall
            if (dest.c == ' ') {
                //If destination is a linked node already - abort
                if (dest.parent != null) continue;

                //Otherwise, adopt node
                dest.parent = n;

                //Remove wall between nodes
                nodes[n.x + (x - n.x) / 2 + (n.y + (y - n.y) / 2) * width].c = ' ';

                //Return address of the child node
                return dest;
            }
        }

        //If nothing more can be done here - return parent's address
        return n.parent;
    }

    private void randomizeStartAndEnd() {
        Node startNode;
        Node endNode = nodes[0];

        int startIndex;
        int maxDistance = 0;

        // Randomize a start node
        do {
            startIndex = rand.nextInt(nodes.length - 1);
            startNode = nodes[startIndex];
        }
        while (startNode.c == '#' || (startNode.x == 0 && startNode.y == 0));

        // Set the endpoint to the node that is farthest away from the start node
        for (int i = 0; i < nodes.length - 1; i++) {
            Node node = nodes[i];

            if (node.c == '#' || (node.x == 0 && node.y == 0) || i == startIndex) {
                continue;
            }

            // Manhattan distance
            int distance = Math.abs(startNode.x - node.x) + Math.abs(startNode.y - node.y);

            if (maxDistance < distance) {
                maxDistance = distance;
                endNode = node;
            }
        }

        startNode.c = 'B';
        endNode.c = 'E';

        start = new Vector(startNode.x, 0, startNode.y);
        end = new Vector(endNode.x, 0, endNode.y);
    }

    private void setWalls() {
        walls = new boolean[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                walls[i][j] = nodes[j + i * width].c == '#';
            }
        }
    }

    private void randomizeSpears() {
        ArrayList<Node> freeNodes = new ArrayList<Node>();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].c == ' ' && nodes[i].x != 0 && nodes[i].y != 0) {
                freeNodes.add(nodes[i]);
            }
        }

        int numberOfSpears = (int) (freeNodes.size() * 0.4);

        for (int i = 0; i < numberOfSpears; i++) {
            int index = rand.nextInt(freeNodes.size() - 1);
            freeNodes.get(index).c = 'S';
            freeNodes.remove(index);
        }

        spears = new boolean[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                spears[i][j] = nodes[j + i * width].c == 'S';
            }
        }
    }

}
