package javaagents;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import aima.core.search.framework.Node;

import massim.javaagents.aimamassimworld.AgentPosition;
import massim.javaagents.aimamassimworld.MassimAction;
import massim.javaagents.aimamassimworld.MassimCell;
import massim.javaagents.aimamassimworld.MassimGrid;
import massim.javaagents.aimamassimworld.MassimFunctions;

public class MassimFunctionsTest {

	@Test
	public void testCreateActionsFunction() {
		MassimGrid grid = new MassimGrid(3,3);
		
		Function<AgentPosition, List<MassimAction>> f = MassimFunctions.createActionsFunction(grid);
		AgentPosition pos = new AgentPosition(2,2);
		List<MassimAction> actions = f.apply(pos);
		
		
		assertTrue("Es sollten vier Richtungen moeglich sein, wenn keine Hindernisse vorhanden", actions.size() == 4);
		assertTrue("NORTH sollte moeglich sein, wenn keine Hindernisse vorhanden", actions.contains(MassimAction.NORTH));
		assertTrue("SOUTH sollte moeglich sein, wenn keine Hindernisse vorhanden", actions.contains(MassimAction.SOUTH));
		assertTrue("WEST sollte moeglich sein, wenn keine Hindernisse vorhanden", actions.contains(MassimAction.WEST));
		assertTrue("EAST sollte moeglich sein, wenn keine Hindernisse vorhanden", actions.contains(MassimAction.EAST));
		
		
		Set<MassimCell> allowed = new HashSet<>();
		allowed.add(new MassimCell(2,3));
		grid.setAllowed(allowed);
		
		f = MassimFunctions.createActionsFunction(grid);
		pos = new AgentPosition(2,2);
		actions = f.apply(pos);
		
		assertTrue("Es sollte nur eine Richtung moeglich sein, wenn nur der Weg nach Sueden frei ist", actions.size() == 1);
		assertTrue("SOUTH sollte moeglich sein, wenn nur der Weg nach Sueden frei ist", actions.contains(MassimAction.SOUTH));
		
		
		allowed.clear();
		grid.setAllowed(allowed);
		
		f = MassimFunctions.createActionsFunction(grid);
		pos = new AgentPosition(2,2);
		actions = f.apply(pos);
		
		assertTrue("Es sollte keine Richtung moeglich sein, wenn der Weg verblockt ist", actions.size() == 0);
	}

	@Test
	public void testCreateResultFunction() {
		MassimGrid grid = new MassimGrid(3,3);
		
		BiFunction<AgentPosition, MassimAction, AgentPosition> bf = MassimFunctions.createResultFunction(grid);
		AgentPosition pos = new AgentPosition(2,2);
		assertEquals("Die neue Agentenposition sollte (2,1) sein.", bf.apply(pos, MassimAction.NORTH), new AgentPosition(2,1));
		assertEquals("Die neue Agentenposition sollte (1,2) sein.", bf.apply(pos, MassimAction.WEST), new AgentPosition(1,2));
		assertEquals("Die neue Agentenposition sollte (3,2) sein.", bf.apply(pos, MassimAction.EAST), new AgentPosition(3,2));
		assertEquals("Die neue Agentenposition sollte (2,3) sein.", bf.apply(pos, MassimAction.SOUTH), new AgentPosition(2,3));
		
		Set<MassimCell> allowed = new HashSet<>();
		grid.setAllowed(allowed);
		assertEquals("Der Agent sollte sich nicht bewegen.", bf.apply(pos, MassimAction.SOUTH), pos);
	}

	@Test
	public void testCreateManhattanDistanceFunction() {
		Set<AgentPosition> goals = new HashSet<>();
		
		ToDoubleFunction<Node<AgentPosition, MassimAction>> tdf = MassimFunctions.createManhattanDistanceFunction(goals);
		
		Node<AgentPosition, MassimAction> node = new Node<>(new AgentPosition(5,5));
		
		double distance = tdf.applyAsDouble(node);
		
		assertTrue("Wenn kein goal existiert, sollte die Manhattan Distanz Integer.MAX_VALUE sein.",
				distance == (double) Integer.MAX_VALUE);
		
		goals.add(new AgentPosition(7,7));
		tdf = MassimFunctions.createManhattanDistanceFunction(goals);
		distance = tdf.applyAsDouble(node);
		assertTrue("Das einzige Ziel sollte Distanz 4 haben.", distance == 4.0);
		
		goals.add(new AgentPosition(3,6));
		tdf = MassimFunctions.createManhattanDistanceFunction(goals);
		distance = tdf.applyAsDouble(node);
		assertTrue("Das nächste Ziel sollte Distanz 3 haben.", distance == 3.0);
		
		goals.add(new AgentPosition(5,4));
		tdf = MassimFunctions.createManhattanDistanceFunction(goals);
		distance = tdf.applyAsDouble(node);
		assertTrue("Das nächste Ziel sollte Distanz 1 haben.", distance == 1.0);
		
		goals.add(new AgentPosition(5,5));
		tdf = MassimFunctions.createManhattanDistanceFunction(goals);
		distance = tdf.applyAsDouble(node);
		assertTrue("Das nächste Ziel sollte Distanz 0 haben.", distance == 0.0);
	}

}
