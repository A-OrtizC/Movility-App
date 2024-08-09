package org.MovilityApp.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MovilityAppController {
    // Devuelve el nombre de una ubicacion dado su indice.
    private Map<Integer, String> locationsNames = new HashMap<Integer, String>();
    
    // Devuelve el indice de una ubicacion dado su nombre.
    private Map<String, Integer> locationsIndex = new HashMap<String, Integer>();
    private List<Map<Integer, Node>> adj = new ArrayList<Map<Integer, Node>>();
    
    // Lista de lineas de texto en el archivo para no tener que leerlo mas de una vez.
    private List<String> lines;
    private Node[] predecessors;

    // Indice de la salida.
    private Integer source;

    // Indice del destino.
    private Integer destination;

    // Es el arreglo que se tiene para obtener el camino mas corto.
    private int[] dist;

    // Método recursivo para generar combinaciones
    private static void generateCombinations(List<List<Integer>> arrays, int depth, List<Integer> current, List<List<Integer>> result) {
        if (depth == arrays.size()) {
            // Se alcanzó la profundidad de los arreglos, añadir la combinación actual a los resultados
            result.add(new ArrayList<>(current));
            return;
        }

        List<Integer> currentArray = arrays.get(depth);
        for (int num : currentArray) {
            current.add(num);
            generateCombinations(arrays, depth + 1, current, result);
            current.remove(current.size() - 1); // Backtracking
        }
    }

    /**
     * Retorna el camino mas corto, la trayectoria que se hace y los segundos que demora.
     */
    private String getSolution() {
        List<Node> path = getShortestPathNode(destination);
        List<List<Integer>> arrays = new ArrayList<List<Integer>>();
        for (Node node : path) {
            arrays.add(node.routes);
        }
        // Lista para almacenar las combinaciones
        List<List<Integer>> result = new ArrayList<>();
        // Generar combinaciones
        generateCombinations(arrays, 0, new ArrayList<>(), result);
        Map<Integer, List<Integer>> bestRoutes = new HashMap<>();
        int least = Integer.MAX_VALUE;
        for (List<Integer> list : result) {
            int changes = 0;
            for (int i = 0; i < list.size()-1; i++) {
                if (list.get(i) != list.get(i+1)) {
                    changes++;
                }
            }
            if (changes < least) {
                least = changes;
            }
            bestRoutes.put(changes, list);
        }
        for (int i = 0; i < bestRoutes.get(least).size(); i++){
            path.get(i).finalRoute = bestRoutes.get(least).get(i);
        }
        String res = "";
        res += "Desde " + locationsNames.get(source) + " a " + locationsNames.get(destination) + " son " + dist[destination] + " segs\n";
        if (predecessors != null) {
            res += "Trayectoria:\n";
            for (Node node : path) {
                res += locationsNames.get(node.node) + "(Ruta: " + node.finalRoute + ") ";
            }
        }
        res += locationsNames.get(destination);
        return res;
    }

    /**
     * Obtiene y recorre los vecinos de un nodo usando PQ.
     *
     * @param u Indice del nodo.
     * @param visited Set que guarda si un nodo ya se visitó o no.
     * @param pq La cola de prioridad.
     */
    private void e_Neighbours(int u, Set<Integer> visited, PriorityQueue<Node> pq) {
        int edgeDistance;
        int newDistance;

        for (Map.Entry<Integer, Node> entry : adj.get(u).entrySet()) {
            Node v = entry.getValue();

            if (!visited.contains(v.node)) {
                edgeDistance = v.cost;
                newDistance = dist[u] + edgeDistance;

                if (newDistance < dist[v.node]) {
                    dist[v.node] = newDistance;
                    predecessors[v.node] = new Node(u, dist[u], v.routes);
                }

                pq.add(new Node(v.node, dist[v.node], v.routes));
            }
        }
    }

    /**
     * Implementacion de Dijkstra usando PQ.
     *
     * @param adj La forma de representar el grafo usando una lista de mapas.
     */
    private void dijkstra(List<Map<Integer, Node>> adj) {
        int V = adj.size();
        dist = new int[V];
        predecessors = new Node[V];
        Set<Integer> visited = new HashSet<Integer>();
        PriorityQueue<Node> pq = new PriorityQueue<Node>(V, new Node());

        Arrays.fill(dist,Integer.MAX_VALUE);
        dist[source] = 0;

        pq.add(new Node(source, 0, new ArrayList<Integer>()));

        while (visited.size() != V) {
            if (pq.isEmpty())
                return;

            int u = pq.remove().node;

            if (visited.contains(u))
                continue;

            visited.add(u);
            e_Neighbours(u, visited, pq);
        }
    }

    /**
     * Guarda en una lista los pasos que se han seguido desde el nodo inicial hasta el final.
     *
     * @param target El indice del nodo de destino.
     */
    public List<Node> getShortestPathNode(int target) {
        List<Node> path = new ArrayList<Node>();
        for (Node at = predecessors[target]; at != null; at = predecessors[at.node]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Se lee el archivo de texto y se guardan sus valores en sus respectivas variables.
     *
     * @param filePath La ruta del archivo a leer.
     */
    private void readFile(String filePath) {
        try {
            lines = Files.readAllLines(Paths.get(filePath));
            for (int i = 1; i < lines.size(); i++) {
                String[] lineArr = lines.get(i).split(" ");
                int route = Integer.parseInt(lineArr[0]);
                for (int j = 2; j < lineArr.length; j+=2) {
                    int cost = Integer.parseInt(lineArr[j]);
                    if (!locationsIndex.containsKey(lineArr[j-1])) {
                        locationsNames.put(locationsNames.size(), lineArr[j-1]);
                        locationsIndex.put(lineArr[j-1], locationsNames.size()-1);
                        adj.add(new HashMap<Integer, Node>());
                    }
                    if (!locationsIndex.containsKey(lineArr[j+1])) {
                        locationsNames.put(locationsNames.size(), lineArr[j+1]);
                        locationsIndex.put(lineArr[j+1], locationsNames.size()-1);
                        adj.add(new HashMap<Integer, Node>());
                    }
                    if (adj.get(locationsIndex.get(lineArr[j-1])).containsKey(locationsIndex.get(lineArr[j+1]))){
                        Node node = adj.get(locationsIndex.get(lineArr[j-1])).get(locationsIndex.get(lineArr[j+1]));
                        if (node.cost > cost){
                            node.cost = cost;
                            node.routes = new ArrayList<Integer>();
                            node.routes.add(route);
                        } else if (node.cost == cost) {
                            node.routes.add(route);
                        }
                        continue;
                    }
                    List<Integer> routes = new ArrayList<Integer>();
                    routes.add(route);
                    adj.get(locationsIndex.get(lineArr[j-1])).put(locationsIndex.get(lineArr[j+1]),new Node(locationsIndex.get(lineArr[j+1]),cost, routes));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la mejor ruta al GUI.
     * 
     * @param in Nombre de la estacion de salida.
     * @param out Nombre de la estacion destino.
     * @return Retorna un string con la solución.
     */
    public String obtainRoute(String in, String out) {
        destination = locationsIndex.get(out);
        source = locationsIndex.get(in);
        dijkstra(adj);
        return getSolution();
    }

    public MovilityAppController(String path) {
        readFile(path);
    }
}

class Node implements Comparator<Node> {
    public int node;
    public int cost;
    public List<Integer> routes;
    public int finalRoute;

    public Node() {}

    public Node(int node, int cost, List<Integer> routes) {
        this.node = node;
        this.cost = cost;
        this.routes = routes;
    }

    @Override
    public int compare(Node node1, Node node2) {
        return Integer.compare(node1.cost, node2.cost);
    }
}