# A31 - « Labyrinthe »

## Phase 2 : Implémentation, tests et livraison

### Rapport au 22/12/24

Le cahier des charges proposé a été réalisé dans son intégralité :

- Le jeu est fonctionnel ;
- Implémenté au niveau maximum ; 
- Génération aléatoire de tous les éléments qui le sont, possibilité de tourner la tuile libre ;
- Les bonus ont été implémentés.

L'interface utilisateur reproduit fidèlement l'apparence du jeu physique. 

Les images des joueurs ont été générées par une IA, chacune à la couleur associée au joueur concerné.

En ce qui concerne le partage du code et le versionnement, nous avons utilisé les fonctionnalités _Issues_ et _Milestones_ de Git, ainsi que les _Tags_ pour marquer des jalons importants. Afin de minimiser le risque de détérioration voire de pertes de données au cours du développement, nous avons forké le dépôt pour travailler dessus. 

#### Brainstorming

- Maintenant que le plateau statique est en place, comment optimiser son fonctionnement dynamique ? Plusieurs éléments sont concernés :
  -  **_les rangéees de tuiles_**, qui seront décalées par la tuile libre (toutes se décalent à gauche, la tuile libre prend la dernière place et l'ex-première tuile devient celle libre) ;
  - **_la main du joueur_** (de tous les joueurs), où la carte du trésor trouvé est retournée (remplacée par une instance de carte spéciale, qui affiche le verso au lieu du recto - avec l'attribut __isFound_ à _on_, bien sûr) ;
  - **_les pions_** qui changent de coordonnées - mais c'est facile ! Ce qui est compliqué, c'est la logique du **_ chemin_** derrière. Jusqu'ici, on a prévu d'avancer case par case, tant que le trésor n'est pas trouvé.
    - Étant donné que c'est, de toute façon, le joueur qui choisit le chemin, cela ne devrait pas poser de problème : seule la confirmation du trésor trouvé serait automatisée par le `UIController`, et l'arrêt sur une case intermédiaire enregistré, s'il se produit.
    - Par contre, cela risque d'affecter l'expérience utilisateur, car ce dernier serait vite lassé de cliquer sur chaque tuile sur son passage ! Ce serait plus intuitif de cliquer directement sur la case où il veut aller. L'itinéraire serait alors calculé en fonction des __openSides_ des tuiles.
      - **algo du labyrinthe**, justement !
- L'écran de fin du jeu serait simple : le nom (et la tête ?) du vainqueur seront affichés, éventuellement accompagnés des trésors qu'il a trouvés (sans cartes).

A posteriori, nous pouvons considérer que l'implémentation d'un déplacement direct (plutôt que case par case), ainsi que l'affichage détaillé de la fin sont des pistes d'amélioration. 

### Choix conceptuels

- Comme on peut le retracer à travers les _commits_ de cette seconde partie, nous étions partis de vues textes avant d'implémenter la vue graphique. 
- Les _design patterns_ principaux restent **Facade** (pour regrouper les fonctionnalités complexes de gestion du plateau, d'une part, et celle des joueurs avec leurs cartes et leur position sur le plateau, d'autre part) et **Factory** (pour générer les tuiles), sans parler de **MVC couplé à Observer**. Après avoir étudié plusieurs possibilités, nous avons décidé d'en rester à des solutions simples.
- Nous avons veillé à respecter les principes **SOLID**, avec un peu moins de succès pour la vue (voir ci-dessous) :
- **Injection de dépendances** dans les contrôleurs.

#### Interface graphique

- Nous avons utilisé des _layouts_ à l'intérieur d''éléments du jeu, mais un positionnement relatif / ancrage pour les disposer dans la fenêtre principale. Sauf pour les flèches, qui mériteraient d'être peaufinées, la fenêtre est parfaitement redimensionnable. Ceci dit, le jeu est lancé par défaut en mode plein écran.
- `ImageHelper` a été modifié pour faciliter la création des cartes avec le trésor au centre, et non pas sur le côté (méthode `merge_center()` ; en revanche, nous avons utilisé la `merge()` d'origine pour créer les tuiles avec trésors).
- Nous avons défini une palette de couleurs minimale pour l'interface, en variables globales de la classe.
- Nous avions hésité entre rendre la tuile libre cliquable, afin de pouvoir la tourner, et ajouter un bouton. Finalement, le mouvement et l'affichage sont dissociés.
- Un accomagnement musical a été rajouté (bibliothèque `javax.sound.sampled`). 

#### Nommage

- La convention de nommage utilisée est _Camel Case_.
- Les attibuts privés sont préfixés par un underscore.
- Nous nous sommes efforcées à nommer nos attributs et nos méthodes en anglais - bien que certains noms doivent être corrigés pour harmoniser le nommage.

### Questionnements

- Nous nous sommes heurtées au problème de la tuile manquante, toujours une seule.
  - Nous avons testé en essayant de la faire bouger jusqu'au bord du plateau : elle était effectivement _null_.
  - La tuile vide n'apparaissait qu'à l'initialisation d'une partie.
  - La tuile vide était toujours de propriété _movable_ (non fixe, distribuée au début du jeu).
  - En comptant les tuiles par type, on a constaté que la tuile manquante devait toujours être de type droite ; pire, on a vu qu'en fait, même quand aucune tuile ne manquait, on en fabriquait 11 et non pas 12 !!!

En fait, nous oubliions que la tuile libre était créée séparément, dans le constructeur de `Gameboard`. Ceci dit, essayer de générér simplement 15 tuiles angle au lieu de 16 n'a pas automatiquement corrigé le problème.

C'est augmenter le nombre de tentatives de placement (200, seuil conseillé que nous avons pu déduire des sources consultées) qui a résolu le problème.

En revanche, on peut se demande ce que cela coûte en termes d'optimisation.

- Au départ, nous avions prévu des vues séparées pour les éléments distincts du jeu, dans le respect du principe _SRP_. Malheureusement, nous n'avons pas su mettre cette idée en pratique tout en assurant l'interaction correctee de ces éléments. Dans la version actuelle, tout est donc regroupé dans une vue, `GameBordFacadeView`, devenu très lourde et peu lisible. Bien qu'elle fonctionne telle quelle, nous sommes conscients des inconvénients que cette solution présente : difficulté de maintenance (nous avons pu le constater nous-mêmes, en travaillant sur le projet), réutilisabilité réduite, peu de flexibilité.

- La gestion des déplacements présente une petite faille fonctionnelle : deux pions arrivent sans problème à se retrouver sur une même tuile. Or, les règles classiques ne le permettent pas : on doit soit se mettre sur la case suivante, derrière celle occupée, soit s'arrêter juste devant. 
	- Cela pose en plus un problème d'affichage, les deux images de pions se superposant l'une à l'autre : un des deux (ou deux des trois, ou trois des quatre !) joueurs n'est plus visible quand cela se produit.
	- Comme solution provisoire, on pourrait s'entendre de respecter la règle soi-même en attendant, sans y être forcé par l'application) : s'arrêter avant, ou continuer au-dela de la case problématique.

### Améliorations possibles

- Côté **conception** :
  - Il n'y a pas de liason entre `GameFacadeController` et `GameboardController`, ce qui nous oblige, par exemple, à réaliser le déplacement du joueur évincé par un déplacement dans la vue : le principe de séparation des préoccupations s'en trouve violé.
  - Trop de dépendances « haut niveau », impliquant les contrôleurs. À corriger absolument, si une version ultérieure devait voir le jour !
  - Implémenter un algorithme de parcours du labyrinthe : pour épargner au joueur le déplacement case par case, mais également dans un souci d'optimisation.
  - Préparer une batterie de tests ! C'est le seul ticket (sur 15) que nous n'avons pas fermé.
- Côté **GUI** :
  - Afin de permettre un redimensionnement parfait de la fenêtre du jeu, il faudrait donner une position relative aux flèches (tous les autres éléments l'ont déjà) - ou refaire le tout avec un _layout_ adapté.
  - Peaufiner la fenêtre de fin de jeu.
