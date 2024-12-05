# A31 - « Labyrinthe »

## Phase 2 : Implémentation, tests et livraison

### Rapport au 22/12/24

_smth_

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

### Questionnements divers

- _q1_
- _q2_

### Améliorations possibles

- _a1_
- _a2_
