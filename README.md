# Fachpraktikum-KI-Gruppe-4

Kurze Einführung:
Die Agenten müssen Erben der Klasse Agent sein und werden in der Liste "agents" in Scheduler.java ausgefürht bzw. werden ihnen die Nachrichten und sensorischen Daten zugeordnet. Diese finden wir in "massim_2022-main\javaagents\src\main\java\massim\javaagents"
Die Agenten können sich gegenseitig Nachrichten senden. Das scheint unabhängig vom Server zu funktionieren, dabei können die Agenten einzeln addressiert werden oder es wird gebroadcasted. 
Ich hab hier schonmal ReinforcementAgenten hochgeladen, einfach damit wir eine Diskussionsbasis haben. Reinforcement wird im Buch von gerhard weiss auf Seite 266 erklärt.
Der Vorteil beim ReinforcementLearning wäre, dass wir uns wenig Wissen über das Spiel selbst aneignen müssen.

Reinforcementagent:
Außer der Eingabe- und Ausgabedimension wird kein Wissen über das System benötigt.
Der Agent spielt das Spiel und wählt in jedem Schritt zufällig eine Aktion. Er speichert jeden sensorischen Input (aus Sich des ReinforcementLearnings "state" genannt, in Massim "percept" genannt) den er erhält einmalig ab, bspw. ganz naiv in einer Liste. Zu jedem state ist ggf. eine andere Aktion vorteilhafter. Die Aktionen, welche zu höheren Belohnungen führen, sollen mit höher Wahrscheinlichkeiten ausgewählen werden. Das bedeutet, dass für jeden state eine Liste an Wahrscheinlichkeiten (stateActionValue) für die Auswahl der Aktionen vorliegt. Diese werden als Zufallszahl initialisiert.
Sobald der Agent beim Spielen durch Zufall eine Belohnung erhält, wird die Wahrscheinlichkeit der in den letzten states ausgewählten Aktion erhöht.

Das ist ganz grob wie ReinforcementLearning funktioniert. Die Implementierung die ich hochgeladen habe ist ein sogenannter SARSA-Agent. Dieser speichert keine Liste der zuletzt verwendeten Aktionen und states, sondern erhöht nur den letzten. Dadurch ist das Lernen etwas langsamer, aber Schrittanzahl für die ein Zusammenhang mit der Belohnung erkannt werden kann ist nicht fest vorgegeben.

Ich hoffe das ist auch nur ansatzweise hilfreich. Ich habe den Code gerade noch mti ein paar Kommentaren versehen. 

Den Reinforcementagenten können wir, wie erwähnt, nicht direkt anwenden, weil die Eingabedimension sowie die Ausgabedimension sich ändern kann. Dafür gibt es aber Workarounds. 
