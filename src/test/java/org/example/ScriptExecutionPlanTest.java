package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScriptExecutionPlanTest {

    @Test
    void testDuplicateScripts() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(1, List.of(3))
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ScriptExecutionPlan.getExecutionPlan(scripts);
        });

        assertEquals("Duplicate script IDs detected: 1", exception.getMessage());
    }

    @Test
    void testNonExistentDependency() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(3, List.of())
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ScriptExecutionPlan.getExecutionPlan(scripts);
        });

        assertEquals("Dependency 2 does not exist for script 1", exception.getMessage());
    }

    @Test
    void testCyclicDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2)),
                new VulnerabilityScript(2, List.of(3)),
                new VulnerabilityScript(3, List.of(1))
        );

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            ScriptExecutionPlan.getExecutionPlan(scripts);
        });

        assertTrue(exception.getMessage().startsWith("Graph contains cycles! Starting node:"));
    }

    @Test
    void testValidPlanWithNoDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of()),
                new VulnerabilityScript(2, List.of()),
                new VulnerabilityScript(3, List.of())
        );

        List<Integer> expectedOrder = List.of(1, 2, 3);
        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);

        assertEquals(expectedOrder.size(), executionOrder.size());
        assertTrue(executionOrder.containsAll(expectedOrder));
    }

    @Test
    void testValidPlanWithDependencies() {
        List<VulnerabilityScript> scripts = List.of(
                new VulnerabilityScript(1, List.of(2, 3)),
                new VulnerabilityScript(2, List.of(4)),
                new VulnerabilityScript(3, List.of(4, 5)),
                new VulnerabilityScript(4, List.of()),
                new VulnerabilityScript(5, List.of())
        );

        List<Integer> expectedOrder = List.of(4, 5, 2, 3, 1);
        List<Integer> executionOrder = ScriptExecutionPlan.getExecutionPlan(scripts);

        assertEquals(expectedOrder, executionOrder);
    }
}
