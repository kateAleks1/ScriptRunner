package org.example;

import java.util.*;

import java.util.*;

public class ScriptExecutionPlan {

    public static List<Integer> getExecutionPlan(List<VulnerabilityScript> scripts) {

        Set<Integer> scriptIds = new HashSet<>();
        for (VulnerabilityScript script : scripts) {
            if (!scriptIds.add(script.getScriptId())) {
                throw new IllegalArgumentException("Duplicate script IDs detected: " + script.getScriptId());
            }
        }

        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, Integer> inDegree = new HashMap<>();

        for (VulnerabilityScript script : scripts) {
            graph.putIfAbsent(script.getScriptId(), new ArrayList<>());
            inDegree.putIfAbsent(script.getScriptId(), 0);

            for (int dependency : script.getDependencies()) {
                if (!scriptIds.contains(dependency)) {
                    throw new IllegalArgumentException("Dependency " + dependency + " does not exist for script " + script.getScriptId());
                }
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


        if (executionOrder.size() != scripts.size()) {
            throw new IllegalStateException("Graph contains cycles! Starting node: " + findCycleStart(graph, inDegree));
        }

        return executionOrder;
    }

    private static int findCycleStart(Map<Integer, List<Integer>> graph, Map<Integer, Integer> inDegree) {
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() > 0) {
                return entry.getKey();
            }
        }
        return -1;
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