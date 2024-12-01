package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScriptExecutionPlanTest {

    @Test
    void testBasicScenario() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of(4, 5)),
                new VulnerabilityScript(4, List.of()),
                new VulnerabilityScript(5, List.of())
        );

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);


        assertTrue(isValidExecutionOrder(scripts, executionOrder));
    }

    @Test
    void testNoDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of()),
                new VulnerabilityScript(2, List.of()),
                new VulnerabilityScript(3, List.of())
        );

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);

        assertEquals(List.of(1, 2, 3).size(), executionOrder.size());
        assertTrue(executionOrder.containsAll(List.of(1, 2, 3)));
    }

    @Test
    void testLinearDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of(3)),
                new VulnerabilityScript(3, List.of())
        );

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);


        assertEquals(List.of(3, 2, 1), executionOrder);
    }

    @Test
    void testNestedDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of(4)),
                new VulnerabilityScript(4, List.of())
        );

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);


        assertTrue(isValidExecutionOrder(scripts, executionOrder));
    }

    @Test
    void testSingleScript() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of())
        );

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);

        assertEquals(List.of(1), executionOrder);
    }

    @Test
    void testEmptyScriptList() {
        List<VulnerabilityScript> scripts = List.of();

        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);

        assertTrue(executionOrder.isEmpty());
    }

    @Test
    void testCyclicDependency() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of(3)),
                new VulnerabilityScript(3, List.of(1))
        );

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            ScriptExecutionPlan.getExecutionPlan(scripts);
        });

        assertEquals("Graph contains cycles!", exception.getMessage());
    }


    private boolean isValidExecutionOrder(List<VulnerabilityScript> scripts, List<Integer> executionOrder) {
        Map<Integer, List<Integer>> dependencyMap = new HashMap<>();
        for (VulnerabilityScript script : scripts) {
            dependencyMap.put(script.getScriptId(), script.getDependencies());
        }

        Set<Integer> executed = new HashSet<>();
        for (int scriptId : executionOrder) {
            List<Integer> dependencies = dependencyMap.getOrDefault(scriptId, List.of());
            if (!executed.containsAll(dependencies)) {
                return false;
            }
            executed.add(scriptId);
        }

        return true;
    }
}