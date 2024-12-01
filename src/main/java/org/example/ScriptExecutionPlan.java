package org.example;

import java.util.*;

public class ScriptExecutionPlan {
    public static void main(String[] args) {

        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of(4, 5)),
                new VulnerabilityScript(4, List.of()),
                new VulnerabilityScript(5, List.of())
        );

        List<Integer> executionOrder = getExecutionPlan(scripts);
        System.out.println("Execution Order: " + executionOrder);
    }

    public static List<Integer> getExecutionPlan(List<VulnerabilityScript> scripts) {

        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, Integer> inDegree = new HashMap<>();

        for (VulnerabilityScript script : scripts) {
            graph.putIfAbsent(script.getScriptId(), new ArrayList<>());
            inDegree.putIfAbsent(script.getScriptId(), 0);

            for (int dependency : script.getDependencies()) {
                graph.computeIfAbsent(dependency, k -> new ArrayList<>());
                graph.get(dependency).add(script.getScriptId());
                inDegree.put(script.getScriptId(), inDegree.getOrDefault(script.getScriptId(), 0) + 1);
            }
        }


        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Integer> executionOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            executionOrder.add(current);

            for (int neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (executionOrder.size() != graph.size()) {
            throw new IllegalStateException("Graph contains cycles!");
        }

        return executionOrder;
    }
}

class VulnerabilityScript {
    private final int scriptId;
    private final List<Integer> dependencies;

    public VulnerabilityScript(int scriptId, List<Integer> dependencies) {
        this.scriptId = scriptId;
        this.dependencies = dependencies;
    }

    public int getScriptId() {
        return scriptId;
    }

    public List<Integer> getDependencies() {
        return dependencies;
    }
}