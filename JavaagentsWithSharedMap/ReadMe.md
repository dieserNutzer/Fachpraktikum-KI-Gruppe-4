Fachpraktikum Multiagentensystem - SS 2022 - Gruppe 4

enthält die zweite Version des javaagents package der Gruppe 4 


Überblick 

Der hier programmierte MassimTeam4Agent teilt seine Karte mit anderen Agenten seines Teams, denen er begegnet,
und nutzt diese Informationen bei der Planung und Ausführung seiner Aktionen.
- Exploration der Umgebung
- Übernahme der worker-Rolle 
- Erledigung von EinBlockAufgaben.

Es wurden Vorbereitungen getroffen, auch ZweiBlockAufgaben zu lösen, 
diese konnten jedoch nicht mehr zufriedenstellend umgesetzt werden


Bauen und Starten des javaagents:

- Im Unterordner javaagents maven aufrufen erzeugt das jar-file mit dependencies:<br /> 
  mvn clean install

- Ausführen des Agenten mit Konfiguration für einen lokalen Server erfolgt mittels:<br />
  java -jar javaagents-2022-1.1-jar-with-dependencies.jar conf/Team4Agents







