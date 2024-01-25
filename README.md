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


### Uruchamianie całego rozwiązania
1. Pobrać [mininet-flow-generator](https://github.com/stainleebakhla/mininet-flow-generator).
2. Pobrać katalog Floodlight z tego repozytorium
3. W plikach [mininet-flow-generator](https://github.com/stainleebakhla/mininet-flow-generator) podmienić plik mesh.py na nasz plik topo.py ze zmienioną nazwą na mesh.py
4. Uruchomić generator przy pomocy komendy: sudo ./topo_launcher.py --topo=mesh --mac --controller=remote,ip=127.0.0.1,port=6653
5. Uruchomić sterownik floodlight przy pomocy Ant Runa ustawionego na run
6. Wysłać zapytanie postman z hostami do endpointa /lab/hosts
7. Rozpocząć generowanie ruchu
