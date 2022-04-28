package massim.javaagents.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import massim.javaagents.MailService;

//kleiner Disclaimer: ich habe bisher nie als Programmierer gearbeitet, daher würde es mich nicht wundern, 
//wenn euch mein Code etwas unbeholfen vorkommt.
//Ich erkläre hier vorab wie der Agent arbeitet.
//Das ganze basiert auf dem Buch "Reinforcement Learning An Introduction second edition" von Richard S. Sutton und Andrew G. Barto"

// Weil es mehrere Arten von Reinforcementagenten gibt, ist die Klasse ReinforcementLearning und SARSA getrennt.
// Jeder input (bei Massim als "percept" bezeichnet, in RL meist als "state") ist eine Liste an Zahlen, genauer floats. 
// Der Input wird über die "input" an den Agenten übergeben. 
// Nach jeder Eingabe wird überprüft, ob der gegebene inpt bereits bekannt ist, dies geschieht über die Funktion "findPercept". 
// In "findPercept" wird die Funktion "getKnownPerceptIndex" ausgeführt, welche überprüft, ob bereits states bekannt sind, welche die gleiche Dimension haben wie 
// der aktuelle input. Wenn dies der Fall sein sollte, gibt getKnownPerceptIndex den Index für knownPercepts an, welche die Liste mit den states dieser 
// Dimension speichern. Ansonsten gibt die Funktion den Wert -1 aus. Wenn noch kein input dieser Länge bekannt war wird ein Modell für Inputs dieser Länge angelegt
// und die Länge der states der Elemente dieses Modells in stateDimKnownPercepts gepseichert (einfach für schnelle Überprüfung) dies wird über die Funktion 
// "newPerceptDimension" gesteuert.
// Wenn eine Liste (folgend auch als Modell bezeichnet) mit states der gesuchten Dimension gefunden wurde, wird mittels "findPercept" überprüft, ob der 
// vorliegende Input bereits als state gespeichert wurde. Die Funktion gibt entweder den Index des übereinstimmenden Elements im Modell an oder -1 falls der 
// Input bisher unbekannt war.
// Wenn der percept neu ist wird er gespeichert. Jedem bekannten State werden Wahrscheinlichkeiten für die Auswahl einer Aktionen bei Eintreten dieses 
// states zugewiesen. Die Wahrscheinlichkeiten zur Auswahl der Aktionen werden als State-Action-Value bezeichnet 
// State und stateActioneValues werden gemeinsam als statePair-Objekt in knownPercepts gespeichert. Die Deklaration eines neuen statePairs wird
// über die Funktion "addNewState" geregelt.
// Falls der Input einem bekannten state gleichen sollte, werden entsprechend der mit dem input beiliegenden Belohnung (Reward) die StateActionValues der gemerkten 
// letzten states und der in diesen states ausgeführten Aktionen akuelisiert. Wenn also ein hoher Reward erzielt wurde wird mindestens der stateActionValue (SAV) 
// der zuletzt ausgeführten Aktion des zuletzt erfahrenen states angepasst.
// Die Aktualisierung des SAV geschieht über die Funktion "stateActionValueUpdate". Da diese Funktion abhängig von der Implementierung (z.B. SARSA) ist ist sie 
// hier abstract.
// Die Funktion "stateActionValueUpdate" wird in SARSA definiert, weil dies von der genauen Implementierung des ReinforcementLearnings abhängt.

// Auswahl der Aktionen: Die Wahl der Aktion wird über den Index angebeben. Der Index wird über die Funktion "chooseAction" ausgewählt.
// Es wird nicht immer die bisher als vielversprechenste Aktion ausgewählt, weil diese ja alle zufällig initialisiert werden. 
// Die Variable epsilon gibt die Wahrscheinlichkeit an, dass die beste Aktion ausgewählt wird. Diese wird über die Funktion "greedyAction" ausgegeben.
// mehr dazu in der Klasse "SARSA".

public abstract class ReinforcementLearning extends Agent{
	Random randomGenerator = new Random();
	int outputDimension;				/*Anzahl der verfügbaren Aktionen (binären Handlungsmöglichkeiten)*/
	float epsilon;						/* 1-epsilon ist die Wahrscheinlichkeit, dass statt der bisher besten Aktion in gegebenem State ein anderer ausgeführt wird, also Risikofreudiges/Neugieriges Verahlten*/
	float discount;						/*schwächt rewards in entfernter Zukunft ab, wenn <1*/
	float Reward;						/*der zum aktuellen Input gegebene reward*/
	int Output;							/*Ergebnis des letzten chooseAction Aufrufs*/
	// Hier werden die bereits durchlaufenen percepts gemeinsam mit den Wahrscheinlichkeiten der Aktionen gespeichert.
	// Es handelt sich um eine 2-dimensionale Datenbank. Die zweite Dimension dienen dazu states gleicher Dimension in Listen zu sortieren.
	List<List<statePair>> knownPercepts;		// Jede Liste enthält statePairs (state und stateActionValues) mit gleich langen states (inputDimension)
	List<Integer> stateDimKnownPercepts; 		// gibt korrespondierend zum Index in knownPercepts die Dimensionen der states in den Modellen an (muss demnach aktuell gehalten werden) 
	// darüber lässt sich streiten. Entweder wird ein Aufwand betrieben die Liste aktuell zu halten oder es werden einfach für alle möglichen Längen
	// bis zur höchsten Bekannten ggf. leere Listen erstellt, sodass der Index von knownPercepts angibt, wie lang die Percepts darin sind.
	
	float[] letzterInput;			/*der aktuelle Input oder auch state*/
	
	class statePair{
		//Hier vereinen sich stateActionValue und state in einem Objekt
		// in anderen Worten: in state ist der percept und in sAV die Wahrscheinlichkeit aus dem sensorischen Input (percept) heraus eine bestimmte Aktion zu wählen
		float[] state, stateActionValues;	// die stateActionValues müssen nicht in Summe 1 ergeben, damit sparen wir uns das ständige Normalisieren.
		int prefferedActionIndex;
		public statePair(float[] percept, float[] stateActionValues){
			this.state = percept;
			this.stateActionValues = stateActionValues;
		}
		public float[] getSAVs() {
			return stateActionValues;
		}
		public float getSAV(int index) {
			return stateActionValues[index];
		}
		public float[] getState() {
			return state;
		}
		public boolean compareState(float[] percept) {
			if (percept.length != state.length) {
				return false;
			}
			else {
				for (int i = 0; i < state.length; i++) {
					if (percept[i] != state[i]) {
						return false;
					}
				}
				return true;
			}
		}
		public int getPreferedAction() {
			return prefferedActionIndex;
		}
		public int getStateLength() {
			return state.length;
		}
		public void detPreferedAction() {
			int preff = 0;
			for (int i = 0; i < stateActionValues.length; i++) {
				if (stateActionValues[i] > stateActionValues[preff]){
					preff = i;
				}
			}
			prefferedActionIndex = preff;
		}
		public void detPreferedAction(int indexChanged) {
			// damit nicht alle durchlaufen werden müssen wird der Index der Aktion angegeben, deren SAV sich geändert hat
			if (stateActionValues[indexChanged] > stateActionValues[prefferedActionIndex]){
				prefferedActionIndex = indexChanged;
			}
			if (Math.abs(stateActionValues[prefferedActionIndex]) >= 10000) {
				// einfach damit wir einem overflow entgehen. 
				normalizeSAVs();
			}
		}
		public void normalizeSAVs() {
			// Ändert die Werte aller Wahrscheinlichkeiten, so dass sie summiert 1 ergeben. 
			float sum = 0;
			for (int i = 0; i < stateActionValues.length; i++) {
				sum += stateActionValues[i];
			}
			for (int i = 0; i < stateActionValues.length; i++) {
				stateActionValues[i] /= sum;
			}
		}
		public void setSAV(int index, float value) {
			stateActionValues[index] = value;
		}
	}
	
	class stateAddress{
		int modellIndex;	// gibt den index der Liste an (Listen enthalten percepts gleicher Dimensionen)
		int elementInList;	// gibt den index des state in dieser Liste an
		public stateAddress(int a, int b){	
			modellIndex = a;
			elementInList = b;
		}
		int getModell() {
			return modellIndex;
		}
		int getElement() {
			return elementInList;
		}
	}
	
	public ReinforcementLearning(String name, MailService mailbox, int outDim, float Epsilon, float Discount,
			List<List<statePair>> database ) {
		super(name, mailbox);
		this.epsilon= Epsilon;
		this.discount = Discount;
		outputDimension= outDim;
		knownPercepts = database;
	}

	
	stateAddress findPercept(float[] percept) {
		// als erstes das Modell mit passender Dimension suchen
		int perceptLength = percept.length;
		int index = getKnownPerceptIndex(perceptLength);
		if (index == -1) {
			return newPerceptDimension(percept);
		}
		List<statePair> statePairs = knownPercepts.get(index);
		for (int i = 0; i < statePairs.size(); i++) {
			if (statePairs.get(i).compareState(percept)) {
				return new stateAddress(index, i);
			}
		}  
		return addNewState(percept, index);
	}
	int getKnownPerceptIndex(int perceptDimension) {
		// sucht die List<statePair> aus knownPercepts aus, deren states alle die gesuchte Dimension vorweisen.
		for (int i = 0; i <stateDimKnownPercepts.size(); i++){
			if (stateDimKnownPercepts.get(i) == perceptDimension) {
				return perceptDimension; 
			}
		}
		return -1;
	}


	stateAddress addNewState(float[] percept, int stateListIndex) {
		List<statePair> statePairs = knownPercepts.get(stateListIndex);
		float[] randomStateActionValues = new float[outputDimension];
		float sum = 0;		// Die Summe der Wahrscheinlichkeiten soll 1 ergeben.
		float randomFloat; 
		for (int i = 0; i < outputDimension; i++) {
			if (sum < 1) {
				randomFloat = randomGenerator.nextFloat(0, 1-sum);
				randomStateActionValues[i] = randomFloat;
				sum += randomFloat; 
			}
			else {
				randomStateActionValues[i] = 0.0F;
			}
		}
		statePair newState = new statePair(percept, randomStateActionValues);
		statePairs.add(newState);		// hier sollte bei Zeiten eine lexikographische Sortierung angewandt werden
		return new stateAddress(stateListIndex, statePairs.size()-1);
	}
	
	stateAddress newPerceptDimension(float[] percept) {
		knownPercepts.add(new ArrayList<statePair>());
		stateDimKnownPercepts.add(percept.length);
		return addNewState(percept, knownPercepts.size()-1);
	}	
		
	public abstract float input(float[] input);
	abstract int chooseAction();
	int greedyAction(stateAddress address) {
		// Modellindex gibt den Index der Liste an in welcher der State gelistet ist
		return knownPercepts.get(address.getModell()).get(address.getElement()).getPreferedAction();
	}	
	abstract void stateActionValueUpdate(stateAddress address);
}
