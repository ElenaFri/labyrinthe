# A31 - « Labyrinthe »

## Phase 1 : Conception

### Rapport au 01/12/24

Le texte qui suit explique nos [choix de conception](#Choix-retenus-à-la-phase-1-du-développement), mais reflète également le raisonnement qui les a précédés [
(_« Brainstorming »_)](#Brainstorming) et accompagnés [(_« Questionnements divers »_)](#Questionnements-divers)

#### Brainstorming

- Comment modéliser le chemin parcouru par un joueur ?
- Quelles données mémorise-t-on et dans quel(s) conteneur(s) ?
- Comment gérer les différents états du jeu ?
- Comment assurer la communication entre les joueurs et le plateau ?

#### Principes de conception à respecter

Les principes étant généraux, ils devraient être respectés dans n'importe quel projet de développement. Cependant, certains nous semblent particulièrement importants pour celui-ci :

- **_KISS_**, notamment par rapport au plateau : il comprend des éléments à la fois semblables à la base (cela reste des tuiles !), mais bien différents dans leur comportement. Avant même de réfléchir sur des patrons de conception adaptés, ce serait important de toujours garder à l'esprit que la modélisation doit rester simple.
- **_DRY_**, toujours en modélisant le plateau : identifier les propriétés (=> attributs) et les comportements (=> méthodes) identiques pour tous les types de tuiles pour pouvoir factoriser au maximum en codant.
- **_La loi de Demeter_** : viser le maximum d'indépendance pour les classes qui "s'emboîtent", tel le plateau qui est composés de tuiles, mais qui a aussi des couloirs => autant dire qu'il se compose plutôt de rangées de tuiles de taille fixe, dont la moitié sont immobiles et l'autre moitié, dynamiques. L'interface graphique (la vue, d'un point de vue de la conception) ne connaîtra que le plateau - et le joueur, que l'interface graphique.

#### Patrons de conceptions pertinents

Les patrons de conception sont là pour fournir une solution éprouvé à un problème spécifique récurrent, concernant l'instanciation d'objets (=> patrons de création), leurs agencements (=> patrons de structure) ou leurs échanges (=> patrons de comportements). C'est une question à se poser en adéquation avec, d'une part, ce qui est demandé (un jeu qui fonctionne de telle manière ; cela a été un avantage d'y avoir beaucoup joué) et, d'autre part, ce qu'on peut imaginer comme modélisation simple, concise en termes de code et respectant le principe de l'incapsulation.

- Patrons de création :
	- **_Factory_** pour la création des tuiles de différents types ?
	- ... et **_Builder_** pour créer le plateau ?
	- Éviter ou préférer le **_Singleton_**, le plateau étant, par définition, unique ?
	- **_Flyweight_** pour générer les cartes ? (À creuser, car nous ne l'avons pas vu en cours.)
- Patrons de structure :
	- **_Composite_** pour créer le chemin et/ou la rangée de tuiles (qui peuvent tous les deux être représentés comme des arborescences) ?
- Patrons de comportement :
	- **_State_** pour gérer les états du jeu ? (À creuser, car nous ne l'avons pas vu en cours, non plus.)

### Choix retenus à la phase 1 du développement

- Le _design pattern_ incontournable reste le **_MVC_** (+ **_Observer_** pour garantir une mise à jour dynamique), d'autant plus qu'il s'agit d'un jeu doté d'une GUI. Il est, d'ailleurs, explicitement nommé dans la consigne ; il va de soi. On disposera de plusieurs observateurs, afin de pouvoir retracer la position et l'objectif poursuivit par le joueur, ainsi que les changements du plateau. Pour nous simplifier la tâche dans la première version, nous rechargerons le plateau en entier à chaque tour, plutôt que de détecter le changement et de le transcrire ; ce serait une amélioration à apporter.
- Une _ **_Factory_** sera utilisé pour créér des tuiles, possédant déjà une position (_i.e._ tuiles sur le plateau, dont certaines fixes) ou pas (tuile libre). La seule tuile sans position est celle libre, qui permet de décaler le labyrinthe ; par convention, sa position vaut (-1, -1).
- Une **_Facade_** renfermera tout ce que possède le joueur (position sur le plateau, cartes, objectif(s) visé(s) - les cartes et les objectifs étant, à leur tour, associés à des « trésors ».). Cela permettra de créer facilement les quatre joueurs, de leur distribuer les cartes sans introduire de système de traçage pour assurer qu'elles sont uniques, d'occulter, pour les vues et les contrôleurs, la façon dont sont gérés les cartes et les joueurs au bas niveau. En plus, cette Façade encapsule les joueurs, qui ne connaissent pas le plateau - et inversement, le tableau, représenté par la classe `Gameboard`, ne connaît pas les joueurs, réduisant ainsi les dépendances.
- De manière plus générale, nous nous sommes efforcés de créer une **_architecture verticale_** pour notre application, où les classes d'un même package n'ont pas nécessairement de relations directes entre elles, mais interagissent plutôt avec des classes d'autres packages, d'un niveau logique supérieur. À notre sens, cela doit permettre d'assurer :
	- l'encapsulation ;
	- des responsabilités claires (principe SRP) ;
	- une meilleure lisibilité et possibilité de réutilisation du code ;
	- une conception évolutive.

#### Préparation de l'interface graphique

Bien que nous n'ayons pas entamé l'implémentation de la GUI, nous l'avons bien imaginée et commencé à rassembler des éléments graphiques qui la constitueront :

- Nous avons préparé les images trésors numérotées de 0 à 23, qui reproduisent fidèlement les graphismes du jeu physique. Elles ont été normalisées en taille 128x128px (par précaution, pour assurer la bonne qualité du rendu même sur un grand écran, même si, au moment de la sélection de l'objectif par exemple, on choisit de zoomer sur l'image ; à ajuster au besoin), format PNG pour avoir un fond transparent qui permettrait de les superposer aux cartes et aux tuiles. Stockées dans `res/img/treasures`.
- Les cartes sont représentées par juste deux images, le recto et le verso ; les trésors seront « collés » dessus. Stockées dans `res/img/cards`.
- Les tuiles sont au nombre de trois, en fonction de leur forme, toutes les configurations possibles étant obtenues par rotation (assurée par les fonctions helpers fournies). De taille 160x160px, pour bien positionner le trésor au milieu en laissant un bord ; stockées dans `res/img/tiles`. Dans un premier temps, nous nous contenterons de disposer les tuiles correctement sur le plateau, en suivant les contraintes de la forme (angles tournés dans le bon sens aux quatre coins du plateau, 8 tuiles en T suivant le bord, les quatre tuiles restantes positionnées sans contraintes au milieu) mais en leur attribuant des trésors aléatoirement - alors que dans le jeu d'origine, les trésors sont bien entendu fixés d'avance. On pourrait juste interdire l'application de trésors aux tuiles droites, car cela a un sens pour la stratégie du jeu. 
- Bien que les pions ne seront modélisés que par les coordonnées du joueur sur le plateau, il faudra bien les afficher aux bons endroits ! Nous avons donc préparé les quatre images PNG identiques sauf pour la couleur, 32x32px, stockées dans `res/img/pieces`.
- Il nous faudra également un fond du plateau et un fond d'écran du jeu. Comme suggéré plus loin, tous les fichiers images utilisés seront en format PNG, pour uniformiser la qualité du rendu. Par contre, il faudra vérifier, à la fin du projet, que tous les éléments superposés sont bien regroupés et redimensionnés ensemble au besoin.

### Questionnements divers

- La différence entre les tuiles n'est visible qu'au niveau de la vue et du contrôleur, mais il faut savoir en garder trace dans `Tile`.
	- Solution : modéliser les voies de passage d'une tuile comme un ensemble de booléens constituant son périmètre (dans la classe `Sides`).
- Comment modéliser les couloirs, devrait-on faire une classe dédiée ?
	- Solution : abandon de la classe `Hallway` initialement envisagée, car le couloir n'existe que pendant le jeu (<= au niveau du contrôleur), il ne résulte pas d'un rassemblement mécanique de tuiles (<= au niveau du modèle)
- Que doivent observer les Observers ? Les éléments du jeu directement (lesquels, avec quelle granularité ?) ou bien `GameFacade` et `Gameboard`, qui sont au sommet des modèles ?
	- Solution : c'est bien `GameFacade` et `Gameboard` qui seront observés, à savoir la position du joueur et l'objectif qu'il poursuit, d'une part ;  
- Au niveau des graphismes, il est évident qu'on devra utiliser le format png pour les éléments de l'interface, mais quid des deux écrans - qui, par définition, seront toujours en bas de la pile des couches ?
	- Solution : tout faire en png, pour faciliter la gestion et assurer une qualité d'affichage uniforme.
- Où stocker les entiers correspondants aux trésors ? Au niveau des modèles, c'est juste un tableau (de chemins vers le fichier png ?) indexé de 0 à 23.
    - Solution intermédiaire : nulle part pour simplifier, mais il faut trouver moyen de déduire les numéros attribués.
      - Solution pratique : on va les attribuer de manière aléatoire en boucle dans la méthode de génération. Le chemin, lui :
        - Devrait se trouver dans les vues et non pas dans les modèles ;
        - Peut être factorisé et calculé de manière automatique si les fichiers d'images suivent un nommage cohérent.
- Nous sommes en plein cycle conception -> développement -> résultat intermédiaire / retour -> adaptation -> conception, en ce qui concerne le package `game`. Plus tard, nous reverrons probablement l'organisation de la classe `Gameboard`, par exemple, qui est, pour l'instant, assez encombrée (mais c'est une première approche, et tous les éléments existants nous semblent pertinents, c'est juste une question d'agencement).
	- Solution : la meilleure solution serait de trouver un algo plus élégant, mais nous préférons, surtout à ce stade où nous ne pouvons pas encore proposer de prototype du jeu, la fonctionnalité au raffinement.
