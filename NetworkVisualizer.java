import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Node {
    String name;
    int x, y; // Position for visualization

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    // Override toString to display the node name in JComboBox
    @Override
    public String toString() {
        return this.name;
    }
}

// Edge class represents a network connection between two nodes
class Edge {
    Node node1, node2;
    int cost;
    int bandwidth;

    public Edge(Node node1, Node node2, int cost, int bandwidth) {
        this.node1 = node1;
        this.node2 = node2;
        this.cost = cost;
        this.bandwidth = bandwidth;
    }

    public int getCost() {
        return this.cost;
    }

    public int getBandwidth() {
        return this.bandwidth;
    }

    public Node getNode1() {
        return this.node1;
    }

    public Node getNode2() {
        return this.node2;
    }
}

// NetworkGraph class to manage nodes and edges
class NetworkGraph {
    List<Node> nodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node node1, Node node2, int cost, int bandwidth) {
        edges.add(new Edge(node1, node2, cost, bandwidth));
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    // Kruskal's Algorithm to find the Minimum Spanning Tree (MST)
    public List<Edge> getMST() {
        edges.sort(Comparator.comparingInt(Edge::getCost)); // Sort edges by cost

        // Union-Find data structure to manage connected components
        DisjointSet ds = new DisjointSet(nodes.size());
        List<Edge> mstEdges = new ArrayList<>();

        for (Edge edge : edges) {
            int root1 = ds.find(nodes.indexOf(edge.getNode1()));
            int root2 = ds.find(nodes.indexOf(edge.getNode2()));

            // If adding this edge doesn't form a cycle
            if (root1 != root2) {
                mstEdges.add(edge);
                ds.union(root1, root2);
            }
        }
        return mstEdges;
    }

    // Dijkstra's algorithm to calculate the shortest path based on bandwidth
    public List<Node> dijkstra(Node source, Node destination) {
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialize distances and predecessors
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
            predecessors.put(node, null);
        }
        distances.put(source, 0);

        queue.add(source);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(destination)) {
                break; // Stop when the destination is reached
            }

            // Process each neighboring node
            for (Edge edge : getEdges()) {
                Node neighbor = null;
                if (edge.getNode1().equals(current)) {
                    neighbor = edge.getNode2();
                } else if (edge.getNode2().equals(current)) {
                    neighbor = edge.getNode1();
                }

                if (neighbor != null) {
                    int newDist = distances.get(current) + edge.getBandwidth();
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        predecessors.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // Reconstruct the shortest path
        List<Node> path = new ArrayList<>();
        Node current = destination;
        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    // Calculate total cost and latency of the network
    public int calculateTotalCost() {
        return edges.stream().mapToInt(Edge::getCost).sum();
    }

    public int calculateTotalLatency() {
        return edges.stream().mapToInt(Edge::getBandwidth).sum();
    }

    // Disjoint-set (Union-Find) class to support Kruskal's Algorithm
    static class DisjointSet {
        int[] parent;

        public DisjointSet(int size) {
            parent = new int[size];
            Arrays.fill(parent, -1);
        }

        public int find(int i) {
            if (parent[i] == -1) {
                return i;
            }
            return find(parent[i]);
        }

        public void union(int i, int j) {
            int root1 = find(i);
            int root2 = find(j);

            if (root1 != root2) {
                parent[root1] = root2;
            }
        }
    }
}

// Main GUI class
public class NetworkVisualizer extends JFrame {
    private NetworkGraph networkGraph = new NetworkGraph();
    private JTextField nodeNameField, costField, bandwidthField;
    private JComboBox<Node> sourceNodeComboBox, destinationNodeComboBox;
    private JButton addNodeButton, addEdgeButton, optimizeButton, calculatePathButton;
    private JPanel drawingPanel;
    private JTextArea pathResultArea;
    private JLabel totalCostLabel, totalLatencyLabel;

    public NetworkVisualizer() {
        setTitle("Network Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create text fields and buttons for adding nodes and edges
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(7, 2));

        nodeNameField = new JTextField();
        controlPanel.add(new JLabel("Node Name:"));
        controlPanel.add(nodeNameField);

        costField = new JTextField();
        controlPanel.add(new JLabel("Cost:"));
        controlPanel.add(costField);

        bandwidthField = new JTextField();
        controlPanel.add(new JLabel("Bandwidth:"));
        controlPanel.add(bandwidthField);

        addNodeButton = new JButton("Add Node");
        addEdgeButton = new JButton("Add Edge");
        optimizeButton = new JButton("Optimize Network");

        controlPanel.add(addNodeButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(optimizeButton);

        // Add path calculation components
        controlPanel.add(new JLabel("Source Node:"));
        sourceNodeComboBox = new JComboBox<>();
        controlPanel.add(sourceNodeComboBox);

        controlPanel.add(new JLabel("Destination Node:"));
        destinationNodeComboBox = new JComboBox<>();
        controlPanel.add(destinationNodeComboBox);

        calculatePathButton = new JButton("Calculate Shortest Path");
        controlPanel.add(calculatePathButton);

        // Real-time evaluation of total cost and latency
        totalCostLabel = new JLabel("Total Cost: $0");
        totalLatencyLabel = new JLabel("Total Latency: 0 ms");

        controlPanel.add(totalCostLabel);
        controlPanel.add(totalLatencyLabel);

        add(controlPanel, BorderLayout.NORTH);

        // Path result display
        pathResultArea = new JTextArea(5, 40);
        pathResultArea.setEditable(false);
        add(new JScrollPane(pathResultArea), BorderLayout.SOUTH);

        // Drawing panel to display the network graph
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        add(drawingPanel, BorderLayout.CENTER);

        // Action listeners for buttons
        addNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nodeName = nodeNameField.getText();
                if (!nodeName.isEmpty()) {
                    // Create a new node and add it to the graph
                    Node newNode = new Node(nodeName, 50 + (networkGraph.getNodes().size() * 100), 50);
                    networkGraph.addNode(newNode);
                    sourceNodeComboBox.addItem(newNode);  // Add to JComboBox for source selection
                    destinationNodeComboBox.addItem(newNode);  // Add to JComboBox for destination selection
                    updateRealTimeEvaluation();
                    repaint();
                }
            }
        });
        

        addEdgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cost = Integer.parseInt(costField.getText());
                    int bandwidth = Integer.parseInt(bandwidthField.getText());
        
                    if (networkGraph.getNodes().size() > 1) {
                        // Get selected nodes from the JComboBox
                        Node node1 = (Node) sourceNodeComboBox.getSelectedItem();
                        Node node2 = (Node) destinationNodeComboBox.getSelectedItem();
                        
                        if (node1 != null && node2 != null && !node1.equals(node2)) {
                            networkGraph.addEdge(node1, node2, cost, bandwidth);  // Add the edge between selected nodes
                            updateRealTimeEvaluation();
                            repaint();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(NetworkVisualizer.this, "Please enter valid cost and bandwidth values.");
                }
            }
        });
        

        optimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Edge> mstEdges = networkGraph.getMST();
                networkGraph.edges = mstEdges;
                updateRealTimeEvaluation();
                repaint();
            }
        });

        calculatePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Node sourceNode = (Node) sourceNodeComboBox.getSelectedItem();
                Node destinationNode = (Node) destinationNodeComboBox.getSelectedItem();
                if (sourceNode != null && destinationNode != null) {
                    List<Node> shortestPath = networkGraph.dijkstra(sourceNode, destinationNode);
                    pathResultArea.setText("Shortest path from " + sourceNode.name + " to " + destinationNode.name + ":");
                    for (Node node : shortestPath) {
                        pathResultArea.append("\n" + node.name);
                    }
                }
            }
        });
    }

    // Method to draw the network graph
    private void drawGraph(Graphics g) {
        for (Node node : networkGraph.getNodes()) {
            g.fillOval(node.x, node.y, 30, 30);
            g.drawString(node.name, node.x, node.y - 10);
        }

        for (Edge edge : networkGraph.getEdges()) {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            g.drawLine(node1.x + 15, node1.y + 15, node2.x + 15, node2.y + 15);
        }
    }

    // Method to update total cost and latency
    private void updateRealTimeEvaluation() {
        totalCostLabel.setText("Total Cost: $" + networkGraph.calculateTotalCost());
        totalLatencyLabel.setText("Total Latency: " + networkGraph.calculateTotalLatency() + " ms");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NetworkVisualizer().setVisible(true);
            }
        });
    }
}
