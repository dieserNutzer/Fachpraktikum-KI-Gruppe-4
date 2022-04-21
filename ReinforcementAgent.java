package massim.javaagents.agents;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import massim.javaagents.MailService;

import java.util.List;

// kleiner Disclaimer: ich habe bisher nie als Programmierer gearbeitet, daher würde es mich nicht wundern, 
// wenn euch mein Code etwas unbeholfen vorkommt.
// Ich erkläre hier vorab wie der Agent arbeitet.
// Das ganze basiert auf dem Buch "Reinforcement Learning An Introduction second edition" von Richard S. Sutton und Andrew G. Barto"

// Weil es mehrere Arten von Reinforcementagenten gibt, ist die Klasse Reinforcement und SARSA getrennt.
// Jeder input (bei Massim als "percept" bezeichent) ist eine Liste an Zahlen. Diese werden der Funktion "input" übergeben. 
// Nach der Eingabe wird überprüft, ob der gegebene percept bereits bekannt ist, dies geschieht über die Funktion "checkForUnknownStates" (wobei ich gerade merke, 
// der Name "checkIfKnown" mehr Sinn machen würde). 
// Wenn der percept neu ist wird er gespeichert und neue Wahrscheinlichkeiten für die Auswahl der Aktionen für diesen percept/state initialisiert, dies geschieht 
// über die Funktion "addNewState".
// In beiden Fällen wird der mit dem input übergebene Reward ausgewertet und die Wahrscheinlichkeiten für die Aktionen angepasst, dies geschieht über 
// die Funktion "stateActionValueUpdate". Diese wird in SARSA definiert, weil dies von der genauen Implementierung des ReinforcementLearnings abhängt.

// übersetzt aus C++
// ALLE Variablen hatten dort den Typ float oder std::vector<float>!
// Das würde ich daher immer als erstes als Fehlerquelle in Betracht ziehen.

// bin mir nicht mehr sicher wie genau die Regeln für abstrakte Klassen sind, stellt daher
// eine potentielle Fehlerquelle dar. War nötig für die Funktionen chooseAction und input.
public abstract class ReinforcementAgent extends Agent{
	int inputDimension;					/*Anzahl der Floats zur Repräsentation eines States*/
	int outputDimension;					/*Anzahl der verfügbaren Aktionen (binären Handlungsmöglichkeiten)*/
	float epsilon;						/*[eps-greedy = 1 - eps + (eps / anzahlActions), eps-non-greed = (eps / anzahlActions)] */
	// Die Belohnungen werden auf alle vor dem Erhalt der Belohung ausgeführten Aktionen verteilt, je länger die Aktionen her sind desto schwächer wird 
	// Belohnung auf diese wirken. Im SARSA wird nur der letzte Schritt betrachtet, bei anderen Formen, wie dem Q-Learning wird die Belohnung mehrfach mit
	// dem disount multipliziert um die Belohnung abzuschwächen. Daher liegt der Discount zwischen 0 und 1.
	float discount;						/*schwächt rewards in entfernter Zukunft ab, wenn <1*/
	float Reward;						/*der zum aktuellen Input gegebene reward*/
	int Output;						/*Index der gewählten Aktion (Ergebnis des letzten chooseAction Aufrufs)*/
	// Alle states werden in einer Liste gespeichert und der zweite state beginnt somit bei inputDimension-1.
	// gleiches gilt für stateActionValues. Die Position der stateActionValues korrespondiert mit der von states.
	List<Float> states;					/*bekannte states (die bereits erlebten Inputs)*/
	List<Float> stateActionValues;      			/*die Wahrscheinlichkeiten der Aktionen zu jedem state*/
	// bei preferedPolicyIndex bin ich mir nicht ganz sicher, ob Int richtig ist.
	List<Integer> preferedPolicyIndex;	/*die aktuell höchste Wahrscheinlichkeit aller Aktionen eines bestimmten states, also argmax(stateActionValues[stateIndex bis stateIndex+inputDim])*/
	List<Float> letzterInput;			/*der aktuelle Input (ein state)*/
	
	public ReinforcementAgent(String name, MailService mailbox, 
			int InDim, int outDim, float Epsilon, float Discount,
			float LetzterReward, int LetzterActionIndex, List<Float> States, List<Float> sAV, 
			List<Integer> pi, List<Float> LetzterInput ) {
		super(name, mailbox);
		epsilon= Epsilon;
		discount = Discount;
		inputDimension= InDim;
		outputDimension=outDim;
		states=States;
		stateActionValues=sAV;
		preferedPolicyIndex = pi;
		Reward = LetzterReward;
		letzterInput = LetzterInput;
		Output = LetzterActionIndex;
	}

	abstract int chooseAction();

	float greedyAction(int neutralIndex) {
		return preferedPolicyIndex.get(neutralIndex);
	}

	void checkForUnknownStates (List<Float> episode) {
		List<Integer> newStateIndices = findNewStates (states, inputDimension + 1, episode);
		if (newStateIndices.size () == 0)
			return;
		else {
			for (int i = 0; i < newStateIndices.size () / 2; i++) {	//weil matches für jeden Match die
				List<Float> newState = episode.subList(newStateIndices.get(i), newStateIndices.get(i) + inputDimension + 1);
				addNewState (newState);
			}
		}
	}

	void addNewState(List<Float> newState) {
		//add_Vector_to_Vector(states, newState);
		// als ich states.addAll( geschrieben habe, hat eclipse hat von alleine erkannt, dass ich newState einfügen möchte.. aber wie? aus dem oben Kommentierten Teil?! das waere insane!
		states.addAll(newState);
		List<Float> actionValues = new ArrayList<Float>((int)outputDimension);
		//add_Vector_to_Vector(stateActionValues, actionValues);
		// hier hat eclpise das ebenfalls erkannt.
		stateActionValues.addAll(actionValues);
		// nextInt is normally exclusive of the top value
		// ThreadLocalRandom.current().nextInt(min, max + 1) hab' ich von
		// https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
		// könnte also eine Fehlerquelle sein.
		int randomNum = ThreadLocalRandom.current().nextInt(0, (int)outputDimension);
		preferedPolicyIndex.add((int)randomNum);	// bei neuen States wird geraten
	}
	
	public abstract float input(List<Float> input);
	
	abstract void stateActionValueUpdate();
	
	// Ich habe den Code vor Beginn meines Studiums begonnen und weiß mittlerweile, dass dies eine sehr naive Implementierung ist.
	List<Integer> findNewStates (List<Float> knownStates, int stateDimensions,
			List<Float> potentiallyNewStates /* muss die selbe Dimension pro Punkt vorweisen*/) {
			int dimensions = stateDimensions;
			if (knownStates.size () % dimensions == 0 && potentiallyNewStates.size () % dimensions == 0) {
				int anzahlKnownStates = knownStates.size () / dimensions;
				int anzahlStatesToFind = potentiallyNewStates.size () / dimensions;
				List<Integer> result = new ArrayList<Integer>(); // gibt an Stelle geraden Indices den state, an ungeraden dann den Indice für die erste Übereinstimmung in vectorToFindThemIn
				for (int i = 0; i < anzahlStatesToFind; i++) {
					boolean noMatch = true;
					List<Float> state = potentiallyNewStates.subList((i*dimensions), (i + 1) *dimensions);
					for (int j = 0; j < anzahlKnownStates; j++) {
						boolean sameState = false;
						for (int k = 0; k < dimensions; k++) {
							if (state.get(k) != knownStates.get(j *dimensions + k)) {
								sameState = false;
								break;
								} else
								sameState = true;
							}
						if (sameState) {
							noMatch = false;
							break;
							}
						}
					if (noMatch)
						result.add(i);
					}
				// aus result sind die Dimensionen rausgerechnet
				return result;
				}
			return new ArrayList<Integer>();
			}

}
