package massim.javaagents.agents;

import java.util.List;
import massim.javaagents.MailService;

// Die Implementierung der Klasse SARSA ist in sofern besonders, dass hier nur der letzte state und die in diesem ausgeführte Aktion speichert.
// Andere Agenten speichern eine Liste der letzt k states und gewählten Aktionen. Um das umzusetzen wird auch der stateActionValue des aktuellen states 
// der aktuell gewählten Aktion auf den SAV der zuletzt ausgeführten Aktion verteilt (siehe Formel in stateActionValueUpdate).
// Vorteil: theoretisch sind beliebig lange zeitliche Zusammenhänge indentifierbar.
// Nachteil: Lernzeit (Konvergenz) leidet.

public abstract class SARSA extends ReinforcementLearning {
	float stepSize;				// gibt an in wie weit die Aktualisierung tatsächlich angewandt wird (in etwa wie eine Trägheit)
	statePair vorherigerState;    //Index der zuletzt gewählten Aktion (also der Aktion die uns in en aktuellen state versetzt hat)
	int vorherigeAktion;			// die speicherung vereinfacht das Update der präferierten Aktion, weil dadurch nach update des SAV für diese Aktion nicht alle SAVs sondern nur der der bisher präferierten Aktion verglichen werden müssen.

	public SARSA(String name, MailService mailbox, int outDim, float Epsilon, float Discount,
		float StepSize,	List<List<statePair>> database ) {
		super (name, mailbox, outDim, Epsilon, Discount, database);
		this.stepSize = StepSize;
		this.vorherigerState = null;
		}

	public float input(float[] percept, float reward) {
		// als erstes werden die Inputs abgespeichert, diese werden für die nächste Runde wieder benötigt.
		letzterInput = percept;
		Reward = reward;
		// dann wird die Adresse des states inklusive SAVs ermittelt, der dem sensorischen Input (percept) gleicht.
		stateAddress match = super.findPercept(letzterInput);
		// dieser wird in state vorgehalten
		statePair state = knownPercepts.get(match.getModell()).get(match.getElement());
		// nun wird eine Aktion ausgewählt
		chooseAction(state);		
		// nun wird die Belohnung für die Aktion des letzten states angewandt 
		// für diese wird auf den SAV für die aktuell gewählte Aktion zugegriffen, daher diese Reihenfolge zwischen chooseAction und stateActionValueUpdate.
		// (die Belohnung einer Aktion wird immer mit dem nächsten percept geliefert.)
		stateActionValueUpdate(state);
		// dann wird der aktuelle percept gespeichert, um ihn mit der Belohnung des nächsten Inputs aktualisieren zu können.
		vorherigerState = state; 
		// Hier wird überprüft, ob durch die Belohnung die zuletzt gewählte Aktion die neue präferierte Aktion dieses states ist
		state.detPreferedAction(vorherigeAktion);
		// und dann ausgegeben.
		return Output;
		}
	
	void chooseAction(statePair state) {
		//die Rückgabe ist nicht die ausgewählte Aktion sonder der Index des aktuellen zustands/inputs in states 
		//der Index der gewählten Aktion wird in letzterOutput gespeichert
		//von hier...
		
		int actionIndex;
		// nun werden zufallszahlen erstellt, die erste entscheidet, ob einfach gierig die bisher als beste bewertete Aktion ausgewählt wird
		// oder stattdessen eine andere ausprobiert, in der Hoffnung, dass diese besser ist.
		int random = randomGenerator.nextInt(0, 100+1);
		if (random <= epsilon * 100)
			actionIndex = state.getPreferedAction();
		// hier wird eine neue zufallszahl erstellt, die ohne Berücksichtigung der SAVs eine andere als die beste Aktion auswählt.
		else {
			// der erste Parameter des randomGenerator ist inklusiv der zweite exklusiv (für mehr Infos über die Funktion "nextInt" hoovern).
			int newrandom = random + randomGenerator.nextInt(0, (outputDimension));
			actionIndex = (state.getPreferedAction() + newrandom) % outputDimension;
			// Alternativ: Zum einbeziehen der SAVs müsste ein zufallszahl als float generiert werden, die mindestens so groß wie der SAV der präferierten 
			// Aktion ist und beginnend mit dem Wert des SAV dieser Aktion die Summe mit den SAVs der anderen Aktionen gebildet werden, bis die 
			// zufallszahl kleiner ist als diese Summe. 
		}
		Output = actionIndex;
		}

	void stateActionValueUpdate(statePair state) {
		// Hier wird die erhaltene Belohnung auf die SAVs des letzten besuchten states verteilt.
		if (vorherigerState == null) {
			return;
		}
		float bisherigerSAV = vorherigerState.getSAV(vorherigeAktion);
		float newSAV = bisherigerSAV + stepSize*(Reward + discount*state.getSAV(Output) - bisherigerSAV);
		vorherigerState.setSAV(vorherigeAktion, newSAV);
		}
}