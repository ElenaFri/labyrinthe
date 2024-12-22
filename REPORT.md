# A31 - « Labyrinthe »

## Phase 2 : Implémentation, tests et livraison

### Rapport au 22/12/24

Le bilan principal que nous pouvons faire au bout de ce projet : le jeu est fonctionnel, implémenté au niveau 3 du cahier des charges, avec génération aléatoire de tous les éléments qui peuvent l'être et réalisation des bonus suggérés.

L'interface utilisateur reproduit fidèlement l'apparence du jeu physique. Les images des joueurs ont été générées par une IA.

#### Brainstorming

- Maintenant que le plateau statique est en place, comment optimiser son fonctionnement dynamique ? Plusieurs éléments sont concernés :
  -  **_les rangéees de tuiles_**, qui seront décalées par la tuile libre (toutes se décalent à gauche, la tuile libre prend la dernière place et l'ex-première tuile devient celle libre) ;
  - **_la main du joueur_** (de tous les joueurs), où la carte du trésor trouvé est retournée (remplacée par une instance de carte spéciale, qui affiche le verso au lieu du recto - avec l'attribut __isFound_ à _on_, bien sûr) ;
  - **_les pions_** qui changent de coordonnées - mais c'est facile ! Ce qui est compliqué, c'est la logique du **_ chemin_** derrière. Jusqu'ici, on a prévu d'avancer case par case, tant que le trésor n'est pas trouvé.
    - Étant donné que c'est, de toute façon, le joueur qui choisit le chemin, cela ne devrait pas poser de problème : seule la confirmation du trésor trouvé serait automatisée par le `UIController`, et l'arrêt sur une case intermédiaire enregistré, s'il se produit.
    - Par contre, cela risque d'affecter l'expérience utilisateur, car ce dernier serait vite lassé de cliquer sur chaque tuile sur son passage ! Ce serait plus intuitif de cliquer directement sur la case où il veut aller. L'itinéraire serait alors calculé en fonction des __openSides_ des tuiles.
      - **algo du labyrinthe**, justement !
- L'écran de fin du jeu serait simple : le nom (et la tête ?) du vainqueur seront affichés, éventuellement accompagnés des trésors qu'il a trouvés (sans cartes).

### Choix conceptuels

- Les vues texte sont complétées par les vues graphiques, en Swing. Elles sont couplées avec `ImageHelper`, qui, à son tour, puise dans `ImageStore`, data au niveau des modèles. 
- Suggestion : utiliser directement un algo de recherche de chemin dans un labyrinthe, il en existe plusieurs (seule variation, on ne cherche pas la sortie mais un point précis sur la grille).

#### Interface graphique

- _i1_
- _i2_

#### Nommage

- La convention de nommage utilisée est le _Camel Case_.
- Les attibuts privés sont préfixés par un underscore.
- Nous nous sommes efforcées à nommer nos attributs et nos méthodes en anglais - bien que certains noms devraient être corrigés pour harmoniser le tout.

### Questionnements divers

Nous ne décrirons ici, il va sans dire, que les soucis majeurs auquels nous avons fait face en développant.

- Nous nous sommes heurtées au problème de la tuile manquante, toujours une seule.
  - Nous avons testé en essayant de la faire bouger jusqu'au bord du plateau : elle était effectivement _null_.
  - La tuile vide n'apparaissait qu'à l'initialisation d'une partie.
  - La tuile vide était toujours de propriété _movable_ (non fixe, distribuée au début du jeu).
  - En comptant les tuiles par type, on a constaté que la tuile manquante devait toujours être de type droite ; pire, on a vu qu'en fait, même quand aucune tuile ne manquait, on en fabriquait 11 et non pas 12 !!!

En fait, nous oubliions que la tuile libre était créée séparément, dans le constructeur de `Gameboard`. Ceci dit, essayer de générér simplement 15 tuiles angle au lieu de 16 n'a pas automatiquement corrigé le problème.

C'est augmenter le nombre de tentatives de placement (200, seuil conseillé que nous avons pu déduire des sources consultées) qui a résolu le problème.

En revanche, on peut se demande ce que cela coûte en termes d'optimisation.

- _q2_

### Améliorations possibles

- Côté **conception** :
  - Il n'y a pas de liason entre `GameFacadeController` et `GameboardController`, ce qui nous oblige, par exemple, à réaliser le déplacement du joueur évincé par un déplacement dans la vue : le principe de séparation des préoccupations s'en trouve violé.
  - dfq
  - dqf
- Côté **GUI** :
  - Afin de permettre un redimensionnement parfait de la fenêtre du jeu, il faudrait donner une position relative aux flèches (tous les autres éléments l'ont déjà).
  - dfqf
