# A31 - « Labyrinthe »

## Phase 1 : Conception

### Premières remarques

#### Grosses questions à brainstormer

- Comment modéliser le chemin parcouru par un joueur ?
- Quelles données mémorise-t-on et dans quel(s) conteneur(s) ?
- Comment gérer les différents états du jeu ?

#### Principes de conception à respecter

Les principes étant généraux, ils devraient être respectés dans n'importe quel projet de développement. Cependant, certains nous semblent particulièrement importants pour celui-ci :

- **_KISS_**, notamment par rapport au plateau : il comprend des éléments à la fois semblables à la base (cela reste des tuiles !), mais bien différents dans leur comportement. Avant même de réfléchir sur des patrons de conception adaptés, ce serait important de toujours garder à l'esprit que la modélisation doit rester simple.
- **_DRY_**, toujours en modélisant le plateau : identifier les propriétés (=> attributs) et les comportements (=> méthodes) identiques pour tous les types de tuiles pour pouvoir factoriser au maximum en codant.
- **_La loi de Demeter_** : viser le maximum d'indépendance pour les classes qui "s'emboîtent", tel le plateau qui est composés de tuiles, mais qui a aussi des couloirs => autant dire qu'il se compose plutôt de rangées de tuiles de taille fixe, dont la moitié sont immobiles et l'autre moitié, dynamiques. L'interface graphique (la vue, d'un point de vue de la conception) ne connaîtra que le plateau - et le joueur, que l'interface graphique.

#### Patrons de conceptions pertinents

Les patrons de conception sont là pour fournir une solution éprouvé à un problème spécifique récurrent, concernant l'instanciation d'objets (=> patrons de création), leurs agencements (=> patrons de structure) ou leurs échanges (=> patrons de comportements). C'est une question à se poser en adéquation avec, d'une part, ce qui est demandé (un jeu qui fonctionne de telle manière ; cela a été un avantage d'y avoir beaucoup joué) et, d'autre part, ce qu'on peut imaginer comme modélisation simple, concise en termes de code et respectant le principe de l'incapsulation.

- Le _design pattern_ incontournable reste le **_MVC_** (+ **_Observer_** pour garantir une mise à jour dynamique), d'autant plus qu'il s'agit d'un jeu doté d'une GUI. Il est, d'ailleurs, explicitement nommé dans la consigne ; il va de soi.
	- Du côté application, on va commencer par identifier et étoffer les classes modèles, ce qui nous amènera à leur associer des contrôleurs ;
	- Du côté utilisateur, on va imaginer les vues nécessaires pour jouer, regardant quelles données des modèles ces dernières devront consulter et quelles instructions transmettre au contrôleur, pour ce faire.
- Patrons de création :
	- **_Factory_** pour la création des tuiles de différents types ?
	- ... et **_Builder_** pour créer le plateau.
	- Éviter ou préférer le **_Singleton_**, le plateau étant, par définition, unique ?
- Patrons de structure :
	- **_Composite_** pour créer le chemin et/ou la rangée de tuiles (qui sont tous les deux des objets complexes pouvant être composés de certains types d'éléments, pas toujours les mêmes) ?
	- **_Flyweight_** pour générer les cartes ? (À creuser, car nous ne l'avons pas vu en cours.)
- Patrons de comportement :
	- **_State_** pour gérer les états du jeu ? (À creuser, car nous ne l'avons pas vu en cours, non plus.)

### Choix retenus à la phase 1 du développement

1. dfq
2. dfqdf
3. dfq
