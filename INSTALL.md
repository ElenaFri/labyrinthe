# A31 - « Labyrinthe »

## Mode d'emploi

_remarques générales_

###  Compilation

Pour compiler « Labyrinthe » à la main, placez-vous dans le répertoire `src` et compilez avec `javac` :

```
javac main/labyrinth/views/GameBoard/FacadeView 
```

_Commande à compléter, bien sûr. À noter que rien concernant le plateau n'y est encore (dépendances cycliques, mais ce sera résolu.)_

### Installation

_explications_

### Lancement

- Si vous avez compilé à la main, lancez `java `, toujours depuis le même répertoire.

### Java Doc

La documentation est disponible dans le répertoire `doc/`.

Pour modifier / re-générer la Java Doc en ligne de commande, lancez depuis la racine du projet :

```aiignore
javadoc -d ./doc -sourcepath ./src -subpackages main.labyrinth
```
(depuis le répertoire racine du projet)
