# A31 - « Labyrinthe »

## Description du jeu

Au démarrage du jeu, un plateau de Labyrinthe classique est lancé. Les quatre joueurs sont humains : il faudra donc soit vous mettre à quatre devant un ordinateur pour jouer, soit qu'une personne incarne les quatre joueurs à tour de rôle.

Chaque joueur est associé à une couleur, celle de son image et de son pion : rouge, vert, bleu ou jaune. La fiche du joueur dont c'est le tour est affichée à gauche, son pion aussi que son image sont agrandis par rapport aux autres, pour mieux les repérer.

Avant de déplacer la rangée de son choix en cliquant sur la petite flèche correspondante, le joueur peut tourner la tuile supplémentaire affichée à droite, en appuyant sur le bouton placé sous la tuile, autant de fois que nécessaire. À chaque clic sur le bouton, la tuile tournera de 90° dans le sens horaire.

Le déplacement du pion sur la grille se fait case par case. Les cases sur lesquelles le joueur peut aller affichent un fin cadre rouge ; celle pointée par la souris, le même cadre mais en vert. Pour terminer son tour, parce qu'on le souhaite ou parce qu'on a épuisé les possibilités, il suffit de cliquer sur la case courante. Le tour passera alors au joueur suivant.

Un message de félicitations est affiché à chaque nouveau trésor, ou objectif trouvé. De même à la fin du jeu, une fenêtre de félicitation s'ouvre. On peut alors soit quitter le jeu, soit lancer une nouvelle partie.

Pour améliorer votre expérience utilisateur, pensez à activer votre sortie son si vous aimez un fond de musique avec votre jeu :-)

###  Compilation

Pour compiler « Labyrinthe » à la main, placez-vous dans le répertoire source `src` et compilez avec `javac` :

```
javac main/labyrinth/Labyrinth.java
```

### Lancement

Si vous avez compilé à la main, il suffit d'exécuter depuis le répertoire source `src` (l'ensemble des classes sera compilé grâce aux _import_) :

```aiignore
java main/labyrinth/Labyrinth
```

Vous pouvez également, bien entendu, utiliser votre IDE préférée pour exécuter le fichier `main/labyrinth/Labyrinth`.

### Java Doc

La documentation est disponible dans le répertoire `doc/`.

Pour modifier / re-générer la Java Doc en ligne de commande, lancez depuis la racine du projet :

```aiignore
javadoc -d ./doc -sourcepath ./src -subpackages main.labyrinth
```

Ceci dit, rien n'empêche d'utiliser les fonctionnalités de votre IDE pour ce faire.
