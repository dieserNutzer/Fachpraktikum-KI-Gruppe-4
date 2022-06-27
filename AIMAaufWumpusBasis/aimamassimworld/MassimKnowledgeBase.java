//package aima.core.environment.massimworld;
package massim.javaagents.aimamassimworld;

import aima.core.logic.propositional.inference.EntailmentChecker;
import aima.core.logic.propositional.inference.OptimizedDPLL;
import aima.core.logic.propositional.kb.KnowledgeBase;
import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.search.framework.Metrics;

import java.util.*;

/**
 * A Knowledge base tailored to the Wumpus World environment.
 *
 */
public class MassimKnowledgeBase extends KnowledgeBase {
    public static final String LOCATION = "L";
    public static final String LOCATION_KNOWN = "LK"; // tuning...
    public static final String OBSTACLE = "O";
    public static final String OK_TO_MOVE = "OK";
    public static final String ACTION_WEST = MassimAction.WEST.getSymbol();
    public static final String ACTION_NORTH = MassimAction.NORTH.getSymbol();
    public static final String ACTION_SOUTH = MassimAction.EAST.getSymbol();
    public static final String ACTION_EAST = MassimAction.SOUTH.getSymbol();
    public static final String PERCEPT_OBSTACLE = "Obstacle";
    public static final String PERCEPT_OKTOMOVE = "Okay";
    public static final String PERCEPT_LOC_KNOWN = "LocationKnown";
    /*public static final String BREEZE = "B";
    public static final String STENCH = "S";
    public static final String PIT = "P";
    public static final String WUMPUS = "W";
    public static final String WUMPUS_ALIVE = "WumpusAlive";
    public static final String HAVE_ARROW = "HaveArrow";
    public static final String FACING_NORTH = AgentPosition.Orientation.FACING_NORTH.getSymbol();
    public static final String FACING_SOUTH = AgentPosition.Orientation.FACING_SOUTH.getSymbol();
    public static final String FACING_EAST = AgentPosition.Orientation.FACING_EAST.getSymbol();
    public static final String FACING_WEST = AgentPosition.Orientation.FACING_WEST.getSymbol();
    public static final String PERCEPT_STENCH = "Stench";
    public static final String PERCEPT_BREEZE = "Breeze";
    public static final String PERCEPT_GLITTER = "Glitter";
    public static final String PERCEPT_BUMP = "Bump";
    public static final String PERCEPT_SCREAM = "Scream";
    public static final String ACTION_FORWARD = WumpusAction.FORWARD.getSymbol();
    public static final String ACTION_SHOOT = WumpusAction.SHOOT.getSymbol();
    public static final String ACTION_TURN_LEFT = WumpusAction.TURN_LEFT.getSymbol();
    public static final String ACTION_TURN_RIGHT = WumpusAction.TURN_RIGHT.getSymbol();*/
    //public static final String OK_TO_MOVE_INTO = "OK";

    private int fieldXDimension;
    private int fieldYDimension;
    private AgentPosition start;
    private EntailmentChecker checker;
    private boolean disableNavSentences;
    private long reasoningTime; // in milliseconds

    public MassimKnowledgeBase(int caveXDim, int caveYDim) {
        this(caveXDim, caveYDim, null);
        this.checker = new OptimizedDPLL();
    }

    public MassimKnowledgeBase(int caveXDim, int caveYDim, EntailmentChecker checker) {
        this(caveXDim, caveYDim, new AgentPosition(5, 5), checker);
        tell(newSymbol(LOCATION, 0, start.getX(), start.getY()));
    }

    /**
     * Create a Knowledge Base that contains the atemporal "wumpus physics".
     *
     * @param checker     the SAT solver implementation to use for answering 'ask' queries.
     * @param caveXDim x dimensions of the wumpus world's cave.
     * @param caveYDim y dimensions of the wumpus world's cave.
     */
    public MassimKnowledgeBase(int caveXDim, int caveYDim, AgentPosition start, EntailmentChecker checker) {
        this.start = start;
        this.checker = checker;
        fieldXDimension = caveXDim;
        fieldYDimension = caveYDim;
        tellAtemporalPhysicsSentences();
    }

    public int getFieldXDimension() {
        return fieldXDimension;
    }

    public int getFieldYDimension() {
        return fieldYDimension;
    }

    /**
     * Disables creation of computational expensive temporal navigation sentences.
     */
    public void disableNavSentences() {
        disableNavSentences = true;
    }

    public AgentPosition askCurrentPosition(int t) {
        int locX = -1, locY = -1;
        for (int x = 1; x <= getFieldXDimension() && locX == -1; x++) {
            for (int y = 1; y <= getFieldYDimension() && locY == -1; y++) {
                if (ask(newSymbol(LOCATION, t, x, y))) {
                    locX = x;
                    locY = y;
                }
            }
        }
        if (locX == -1 || locY == -1)
            throw new IllegalStateException("Inconsistent KB, unable to determine current room position.  t = " + t);

        AgentPosition current = new AgentPosition(locX, locY);

        return current;
    }
    
    public Set<MassimCell> askAllowedFields(int t) {
        Set<MassimCell> allowed = new LinkedHashSet<>();
        for (int x = 1; x <= getFieldXDimension(); x++) {
            for (int y = 1; y <= getFieldYDimension(); y++) {
                if (ask(newSymbol(OK_TO_MOVE, t, x, y))) {
                	allowed.add(new MassimCell(x, y));
                }
            }
        }
        return allowed;
    }
    
    // unvisited <- {[x, y] : ASK(KB, L<sup>t'</sup><sub>x,y</sub>) = false for all t' &le; t}
    public Set<MassimCell> askUnknownFields(int t) {
        Set<MassimCell> unknown = new LinkedHashSet<>();

        for (int x = 1; x <= getFieldXDimension(); x++) {
            for (int y = 1; y <= getFieldYDimension(); y++) {
                if (!ask(newSymbol(LOCATION_KNOWN, x, y)))
                	unknown.add(new MassimCell(x, y)); // i.e. is false for all t' <= t

//				way to slow: (try it out!)
//				for (int tPrime = 0; tPrime <= t; tPrime++) {
//					if (ask(newSymbol(LOCATION, tPrime, x, y)))
//						break; // i.e. is not false for all t' <= t
//					if (tPrime == t)
//						unvisited.add(new Room(x, y)); // i.e. is false for all t' <= t
//				}
            }
        }
        return unknown;
    }

    public boolean ask(Sentence query) {
        long tStart = System.currentTimeMillis();
        boolean result = checker.isEntailed(this, query);
        reasoningTime += System.currentTimeMillis() - tStart;
        return result;
    }

    
    public void makePerceptSentence(MassimPercept p, AgentPosition current) {
    	
    	tell(newSymbol(PERCEPT_LOC_KNOWN, p.getXValue() + current.getX(), p.getYValue() + current.getY()));
        if (p.isObstacle())
        {
            tell(newSymbol(PERCEPT_OBSTACLE, p.getXValue() + current.getX(), p.getYValue() + current.getY() + current.getY()));
            tell(new ComplexSentence(Connective.NOT, newSymbol(PERCEPT_OKTOMOVE, p.getXValue() + current.getX(), p.getYValue() + current.getY())));
        }
        else
        {
        	tell(newSymbol(PERCEPT_OKTOMOVE, p.getXValue() + current.getX(), p.getYValue() + current.getY()));
        	tell(new ComplexSentence(Connective.NOT, newSymbol(PERCEPT_OBSTACLE, p.getXValue() + current.getX(), p.getYValue() + current.getY())));
        }
    }

    /**
     * Add to KB sentences that describe the action a
     *
     * @param a    action that must be added to KB
     * @param time current time
     */
    public void makeActionSentence(MassimAction a, int time) {
        for (MassimAction action : MassimAction.values()) {
            if (action.equals(a))
                tell(newSymbol(action.getSymbol(), time));
            else
                tell(new ComplexSentence(Connective.NOT, newSymbol(action.getSymbol(), time)));
        }
    }

    /**
     * TELL the KB the atemporal "physics" sentences (used to initialize the KB).
     */
    protected void tellAtemporalPhysicsSentences() {

        tell(newSymbol(OK_TO_MOVE, start.getX(), start.getY()));

    }

    /**
     * TELL the KB the temporal "physics" sentences for time t.
     * As in this version, the agent does not communicate its current position
     * to the knowledge base, general navigation axioms are needed, which
     * entail the current position. Therefore, navigation sentences are always
     * added, independent of the value of {@link #disableNavSentences}.
     *
     * @param t current time step.
     */
    public void tellTemporalPhysicsSentences(int t) {


        for (int x = 1; x <= fieldXDimension; x++) {
            for (int y = 1; y <= fieldYDimension; y++) {
                tell(new ComplexSentence(
                        newSymbol(LOCATION, t, x, y),
                        Connective.IMPLICATION,
                        new ComplexSentence(newSymbol(PERCEPT_OBSTACLE, t), Connective.BICONDITIONAL, newSymbol(OBSTACLE, x, y))));
                
                tell(new ComplexSentence(
                        newSymbol(LOCATION, t, x, y),
                        Connective.IMPLICATION,
                        new ComplexSentence(newSymbol(PERCEPT_LOC_KNOWN, t), Connective.BICONDITIONAL, newSymbol(LOCATION_KNOWN, x, y))));
            }
        }

        tellCommonTemporalPhysicsSentences(t);
        for (int x = 1; x <= fieldXDimension; x++) {
            for (int y = 1; y <= fieldYDimension; y++) {
                tellSuccessorStateLocationAxiom(t, x, y);
                // Optimization to make questions about unvisited locations faster
            }
        }
    }

    /**
     * TELL the KB the temporal "physics" sentences for time t.
     * This version profits from the agent's knowledge about its current position.
     * Verbosity of the created sentences depends on the value of {@link #disableNavSentences}.
     *
     * @param t current time step.
     */
    public void tellTemporalPhysicsSentences(int t, AgentPosition agentPosition) {
        tell(newSymbol(LOCATION, t, agentPosition.getX(), agentPosition.getY()));
 
        tellCommonTemporalPhysicsSentences(t);
        if (!disableNavSentences) {
            tellSuccessorStateLocationAxiom(t, agentPosition.getX(), agentPosition.getY());
            //tellSuccessorStateOrientationAxioms(t);
        }
    }

    private void tellCommonTemporalPhysicsSentences(int t) {
        for (int x = 1; x <= fieldXDimension; x++) {
            for (int y = 1; y <= fieldYDimension; y++) {
                // The most important question for the agent is whether
                // a square is OK to move into, that is, the square contains
                // no pit nor live wumpus.
                tell(new ComplexSentence(
                        newSymbol(OK_TO_MOVE, t, x, y),
                        Connective.BICONDITIONAL,
                        new ComplexSentence(newSymbol(LOCATION_KNOWN, x, y), Connective.AND, new ComplexSentence(Connective.NOT, newSymbol(OBSTACLE, x, y)))));

            }
        }
    }

    private void tellSuccessorStateLocationAxiom(int t, int x, int y) {
        // Successor state axiom for square [x, y]
        // Rules about current location
        List<Sentence> locDisjuncts = new ArrayList<>();
        
        
        List<Sentence> negatedMovements = new ArrayList<>();
        
        negatedMovements.add(new ComplexSentence(Connective.NOT, newSymbol(ACTION_WEST, t)));
        negatedMovements.add(new ComplexSentence(Connective.NOT, newSymbol(ACTION_EAST, t)));
        negatedMovements.add(new ComplexSentence(Connective.NOT, newSymbol(ACTION_SOUTH, t)));
        negatedMovements.add(new ComplexSentence(Connective.NOT, newSymbol(ACTION_NORTH, t)));
        
        locDisjuncts.add(new ComplexSentence(
                newSymbol(LOCATION, t, x, y),
                Connective.AND,
                Sentence.newConjunction(negatedMovements)));
        
        
        if (x > 1) { // West room is possible
            locDisjuncts.add(new ComplexSentence(
                    newSymbol(LOCATION, t, x - 1, y),
                    Connective.AND,
                    newSymbol(ACTION_EAST, t)));
        }
        if (y < fieldYDimension) { // North room is possible
            locDisjuncts.add(new ComplexSentence(
                    newSymbol(LOCATION, t, x, y + 1),
                    Connective.AND,
                    newSymbol(ACTION_SOUTH, t)));
        }
        if (x < fieldXDimension) { // East room is possible
            locDisjuncts.add(new ComplexSentence(
                    newSymbol(LOCATION, t, x + 1, y),
                    Connective.AND,
                    newSymbol(ACTION_WEST, t)));
        }
        if (y > 1) { // South room is possible
            locDisjuncts.add(new ComplexSentence(
                    newSymbol(LOCATION, t, x, y - 1),
                    Connective.AND,
                    newSymbol(ACTION_NORTH, t)));
        }

        tell(new ComplexSentence(
                newSymbol(LOCATION, t + 1, x, y),
                Connective.BICONDITIONAL,
                Sentence.newDisjunction(locDisjuncts)));
    }
    
    @Override
    public String toString() {
        List<Sentence> sentences = getSentences();
        if (sentences.size() == 0) {
            return "";
        } else {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for (Sentence s : sentences) {
                if (!first) {
                    sb.append("\n");
                }
                sb.append(s.toString());
                first = false;
            }
            return sb.toString();
        }
    }

    public PropositionSymbol newSymbol(String prefix, int timeStep) {
        return new PropositionSymbol(prefix + "_" + timeStep);
    }

    public PropositionSymbol newSymbol(String prefix, int x, int y) {
        return new PropositionSymbol(prefix + "_" + x + "_" + y);
    }

    public PropositionSymbol newSymbol(String prefix, int timeStep, int x, int y) {
        return newSymbol(newSymbol(prefix, timeStep).toString(), x, y);
    }

    public Metrics getMetrics() {
        Metrics result = new Metrics();
        result.set("kb.size", size());
        result.set("kb.sym.size", getSymbols().size());
        result.set("kb.cnf.size", asCNF().size());
        result.set("reasoning.time[s]", reasoningTime / 1000);
        return result;
    }
}
