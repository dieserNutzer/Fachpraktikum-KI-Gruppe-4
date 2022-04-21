package massim.javaagents.agents;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.MailService;

public class SARSA extends ReinforcementAgent {
	float stepSize;		// gibt an wie stark die Wahrscheinlichkeit geändert werden soll 
	int letzterSAVIndex;    // Index der zuletzt gewählten Aktion (also der Aktion die uns in en aktuellen state versetzt hat)

	public SARSA(String name, MailService mailbox, int InDim, int outDim, float Epsilon, float Discount,
		float LetzterReward, int LetzterActionIndex, float StepSize, int LetzterSAVIndex,
		List<Float> States, List<Float> sAV, List<Integer> pi, List<Float> LetzterInput ) {
		super (name, mailbox, InDim, outDim, Epsilon, Discount, LetzterReward, LetzterActionIndex, States, sAV, pi, LetzterInput);
		this.stepSize = StepSize;
		this.letzterSAVIndex = LetzterSAVIndex;
		}

	public float input(List<Float> input, float reward) {
		letzterInput = input;
		Reward = reward;
		int neutralIndex = chooseAction();
		UpdateStateActionValues(neutralIndex);
		UpdatePolicy(neutralIndex);
		return Output;
		}
	
	// hier wird der reward als letztes Element in der Liste input interpretiert
	public float input(List<Float> input) {
		for (int i = 0; i < input.size() -1; i++)
		{
			letzterInput.set(i, input.get(i));
		}
		Reward = input.get(input.size() - 1);
		int neutralIndex = chooseAction();
		UpdateStateActionValues(neutralIndex);
		UpdatePolicy(neutralIndex);
		//UpdateAllPolicies();
		return Output;
	}

	int chooseAction() {
		// als erstes wird nach dem percept in state gesucht
		int match = findFirstMatch (letzterInput, inputDimension, states);
		int actionIndex;
		// wenn der percept bereits bekannt ist wird eine zufällige Zahl generiert und somit eine Aktion gewählt.
		// Das heißt, dass nicht immer die beste Gewählt wird, aber wenn korrekt gelernt wird immer häufiger.
		if(match != -1) {
			// ThreadLocalRandom.current().nextInt(0, 10000+1) hab' ich von
			// https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
			// könnte also eine Fehlerquelle sein.
			// hier stand vorher int random = rand() % 100 + 1;, was wenn ich mich richtig erinnere ein Int zwischen 0 und 1000 generiert 
			int random = ThreadLocalRandom.current().nextInt(0, 10000+1) % 100;
			// als erstes wird entschieden, ob einfach "gierig" die zur Zeit beste Aktion gewählt wird. 
			// Die Wahrscheinlichkeit für gieriges Verhalten wird mit epsilon, einem Wert zwischen 0 und 1 angegeben. 
			if (random <= epsilon * 100)
				actionIndex = preferedPolicyIndex.get(match);
			// wenn nicht gierig gehandelt werden soll, wird eine neue zufällige Zahl erstellt und eine Aktion gewählt.
			// (Die Zufallszahl muss neu bestimmt werden, weil die Wahl von epsilon sonst einen Einfluss auf die Wahl der
			// Aktionen im nicht gierigen Fall hat)
			else {
				int newrandom = ThreadLocalRandom.current().nextInt(0, 10000+1) % (outputDimension - 1) + 1;
				actionIndex = (preferedPolicyIndex.get(match) + newrandom) % outputDimension;
			}
		}
		// wenn der percept nicht bekannt ist, wird er zu states hinzugefügt.
		else {
			addNewState (letzterInput);
			match = states.size () / inputDimension - 1;
			actionIndex = ThreadLocalRandom.current().nextInt(0, outputDimension);
			}
		Output = actionIndex;
		return match;
		}

	void UpdateStateActionValues(int neutralIndex) {
		//dieser kleine chunk ist dann das Update:
		int SAVIndex = neutralIndex*outputDimension + Output; // der Index des letzten aktorischen Outputs
		stateActionValues.set(letzterSAVIndex, stateActionValues.get(letzterSAVIndex) + stepSize*(Reward + discount*stateActionValues.get(SAVIndex) - stateActionValues.get(letzterSAVIndex)));
		letzterSAVIndex = SAVIndex;
		}

	// um nicht für jeden state immer wieder alle Wahrscheinlichkeiten der Aktionen auslesen zu müssen wird der Index der Besten in preferedPolicyIndex
	// gespeichert. Dementsprechend muss ab und zu überprüft werden welche die Beste ist. Wir checken dies hier einfach bei jeder Eingabe, kann aber
	// für Geschwindigkeit sletener ausgeführt werden.
	void UpdatePolicy(int neutralIndex) {
		for(int j = 0; j < outputDimension; j++) {
			if(stateActionValues.get(neutralIndex*outputDimension + preferedPolicyIndex.get(neutralIndex))
			   < stateActionValues.get(neutralIndex*outputDimension + j)) {
				preferedPolicyIndex.set(neutralIndex, j);	}	}
		}
	
	// falls nicht nach jedem Input der beste StatActionValue für genau diesen state bestimmt wird, können diese auch für alle gleichzeitig
	// überprüft werden. 
	void UpdateAllPolicies() {
		for(int i = 0; i < states.size() / outputDimension; i++) {
			for(int j = 0; j < outputDimension; j++) {
				if(stateActionValues.get(i*outputDimension + preferedPolicyIndex.get(i))
				   < stateActionValues.get(i*outputDimension + j)) {
					preferedPolicyIndex.set(i, j);	}	}	}
		}
	
	int findFirstMatch (List<Float> statesToFind, int stateDimensions,
			List<Float> vectorToFindThemIn /* muss die selbe Dimension pro Punkt vorweisen*/) {
			int dimensions = (int)stateDimensions;
			if (statesToFind.size () % dimensions == 0 && vectorToFindThemIn.size () % dimensions == 0) {
				int anzahlStates = statesToFind.size () / dimensions;
				int anzahlStatesToFindThemIn = vectorToFindThemIn.size () / dimensions;
				// keine Ahnung was ich mit dem Kommentar sagen wollte
				// ..also potentielle Fehlerquelle
				int result = -1; // gibt an Stelle geraden Indices den state, an ungeraden dann den Indice für die erste Übereinstimmung in vectorToFindThemIn
				for (int i = 0; i < anzahlStates; i++) {
					List<Float> state = statesToFind.subList((i*dimensions), (i + 1) *dimensions);
					for (int j = 0; j < anzahlStatesToFindThemIn; j++) {
						boolean same = false;
						for (int k = 0; k < dimensions; k++) {
							if (state.get(k) != vectorToFindThemIn.get(j *dimensions + k)) {
								same = false;
								break;
								} else
								same = true;
							}
						if (same) {
							result = (j);
							break;
							}
						}
					}
				// aus result sind die Dimensionen rausgerechnet
				return result;
				}
			return -1;
			}

	@Override
	void stateActionValueUpdate() {
		// TODO Auto-generated method stub
		
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
	};
