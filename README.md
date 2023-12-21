# SDN - projekt "Zastosowanie sterownika Floodlight w wykrywaniu i reakcji na tzw. 'elephant flows'"

### Topologia:
![Topologia](https://github.com/kubinskaaw/sdn-projekt/blob/main/SDN_project.jpg)

### Uruchamianie customowej topologii

zamiast ./topo.py można podać bezwzględną ścieżkę do pliku

sudo mn --custom ./topo.py --topo=topo

### Opis wykorzystanego generatora

##### W projekcie wykorzystano [mininet-flow-generator](https://github.com/stainleebakhla/mininet-flow-generator). Zaletą tego rozwiązania jest fakt zbierania kluczowych w projekcie statystyk (loss, bandwidth) w plikach txt, co pozwala na łatwą analizę zebranych wyników. Dodatkowo podczas uruchamiania projektu można zdefiniować ilości elephant i mice flowów oraz czas trwania badania.

### Algorytm rozwiązania problemu sieci z przepływami 'elephant flows'

![Algorytm](https://github.com/kubinskaaw/sdn-projekt/blob/main/Algorithm.png)
