package massim.javaagents.agents;
import java.util.List;

import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.MailService;

public class SARSAAgent extends SARSA {

	SARSAAgent(String name, MailService mailbox, int outDim, float Epsilon, float Discount,
			float StepSize,	List<List<statePair>> database) {
		super(name, mailbox, outDim, Epsilon, Discount, StepSize, database);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePercept(Percept percept) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Action step() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleMessage(Percept message, String sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	int chooseAction() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float input(float[] input) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void stateActionValueUpdate(stateAddress address) {
		// TODO Auto-generated method stub
		
	}
}
