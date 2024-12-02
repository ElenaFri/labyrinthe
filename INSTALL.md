# A31 - « Labyrinthe »

## Mode d'emploi

_remarques générales_

###  Compilation

Pour compiler « Labyrinthe » à la main, placez-vous dans le répertoire `src/main/labyrinth` et compilez avec `javac` en remontant la hiérarchie des classes. N'hésitez pas à copier-coller la (bien longue) commande ci-dessous qui respecte déjà le bon ordre :

```
    <textarea id="copyContent" style="width: 600px; height: 50px;">
javac models/geometry/*.java models/tiles/*.java models/game/Card.java models/game/Player.java models/observers/GameFacadeObserver.java models/game/GameFacade.java views/helpers/*.java models/data/*.java
controllers/GameFacadeController.java views/ViewsForObservers/GameFacadeTextView.java Labyrinth.java
```

_Commande à compléter, bien sûr. À noter que rien concernant le plateau n'y est encore (dépendances cycliques, mais ce sera résolu.)_

### Installation

_explications_

### Lancement

- Si vous avez compilé à la main, lancez `java Labyrinthe.java`, toujours depuis le même répertoire.

### Divers

_conseils particuliers_
