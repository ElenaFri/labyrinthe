# A31 - « Labyrinthe »

## Description du jeu

Au démarrage du jeu, un plateau de Labyrinthe classique est lancé. Les quatre joueurs sont humains : il faudra donc soit vous mettre à quatre devant un ordinateur pour jouer, soit qu'une personne incarne les quatre joueurs à tour de rôle.

Chaque joueur est associé à une couleur, celle de son image et de son pion : rouge, vert, bleu ou jaune. La fiche du joueur dont c'est le tour est affichée à gauche, son pion aussi que son image sont agrandis par rapport aux autres, pour mieux les repérer.

Avant de déplacer la rangée de son choix en cliquant sur la petite flèche correspondante, le joueur peut tourner la tuile supplémentaire affichée à droite, en appuyant sur le bouton placé sous la tuile, autant de fois que nécessaire. À chaque clic sur le bouton, la tuile tournera de 90° dans le sens horaire.

Le déplacement du pion sur la grille se fait case par case. Les cases sur lesquelles le joueur peut aller affichent un fin cadre rouge ; celle pointée par la souris, le même cadre mais en vert. Pour terminer son tour, parce qu'on le souhaite ou parce qu'on a épuisé les possibilités, il suffit de cliquer sur la case courante. Le tour passera alors au joueur suivant.

Un message de félicitations est affiché à chaque nouveau trésor, ou objectif trouvé. De même à la fin du jeu, une fenêtre de félicitation s'ouvre. On peut alors soit quitter le jeu, soit lancer une nouvelle partie.

Pour améliorer votre expérience utilisateur, pensez à activer votre sortie son si vous aimez un fond de musique avec votre jeu :-)

### Compilation

Pour compiler « Labyrinthe » à la main, depuis la racine du projet (Java 17 ou plus) :

```sh
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src/main -name "*.java")
```

### Lancement

Toujours depuis la racine du projet (le jeu y cherche son dossier `res/`) :

```sh
java -cp bin labyrinth.Labyrinth
```

Vous pouvez également, bien entendu, utiliser votre IDE préféré pour exécuter la classe `labyrinth.Labyrinth`.

### Tests

Les tests unitaires (JUnit 5) portent sur les règles du jeu. Avec le [JAR « console » de JUnit](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/) :

```sh
javac -encoding UTF-8 -cp bin:junit-platform-console-standalone.jar -d bin-test $(find src/test -name "*.java")
java -jar junit-platform-console-standalone.jar execute -cp bin:bin-test --scan-classpath bin-test
```

### Javadoc

Elle n'est pas conservée dans le dépôt : elle est générée à chaque déploiement et publiée sur [https://elenafri.github.io/labyrinthe/doc/](https://elenafri.github.io/labyrinthe/doc/).
Pour la générer en local, depuis la racine du projet :

```sh
javadoc -d doc -sourcepath src/main -subpackages labyrinth -encoding UTF-8 -docencoding UTF-8 -charset UTF-8
```

### Version web

La version web (`web/index.html`) exécute le jeu avec [CheerpJ](https://cheerpj.com/). Elle est construite et déployée par `.github/workflows/pages.yml`.

`web/preload.json` liste les parties du runtime Java dont le jeu a besoin, pour qu'elles soient téléchargées en parallèle au démarrage. Après un changement important du code, on peut la régénérer : ouvrir la page, jouer un tour, puis exécuter `cjGetRuntimeResources()` dans la console du navigateur et enregistrer le résultat (en JSON, sans les guillemets englobants).
