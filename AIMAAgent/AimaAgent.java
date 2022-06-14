package massim.javaagents.agents;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import aima.core.logic.fol.domain.FOLDomain;
import aima.core.logic.fol.kb.FOLKnowledgeBase;
import aima.core.logic.planning.ActionSchema;
import aima.core.logic.planning.HeuristicForwardStateSpaceSearchAlgorithm;
import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.MailService;

public class AimaAgent extends Agent{
	FOLDomain logic;
	FOLKnowledgeBase kb;
	HeuristicForwardStateSpaceSearchAlgorithm planer; 
	Set<ActionSchema> actionSchemas;
	Optional<List<ActionSchema>> plan;
	
	AimaAgent(String name, MailService mailbox) {
		super(name, mailbox);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void handlePercept(Percept percept) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Action step() {
		// entweder in jedem Schritt überprüfen, ob vom Plan 
		// abgewichen wird und neu berechnen 
		// if ()
		// oder direkt den LRTA-Algorithmus einbeziehen
		// (siehe LRTAStarAgent)
		// so wie ich das aus dem Buch entnommen hatte verfolgt
		// der LRTA einen Plan.
		// Optional<List<ActionSchema>> plan = planer.search()
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void handleMessage(Percept message, String sender) {
		// TODO Auto-generated method stub
		
	} 
}
