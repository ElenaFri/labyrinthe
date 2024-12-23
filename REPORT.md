# A31 - « Labyrinthe »

## Phase 3 : Post-livraison - Corrections - Améliorations

### Améliorations proposées dans le Rendu 2

- Côté **conception** :
  - Il n'y a pas de liason entre `GameFacadeController` et `GameboardController`, ce qui nous oblige, par exemple, à réaliser le déplacement du joueur évincé par un déplacement dans la vue : le principe de séparation des préoccupations s'en trouve violé.
  - Trop de dépendances « haut niveau », impliquant les contrôleurs. À corriger absolument, si une version ultérieure devait voir le jour !
  - Implémenter un algorithme de parcours du labyrinthe : pour épargner au joueur le déplacement case par case, mais également dans un souci d'optimisation.
  - Préparer une batterie de tests ! C'est le seul ticket (sur 15) que nous n'avons pas fermé.
- Côté **GUI** :
  - Afin de permettre un redimensionnement parfait de la fenêtre du jeu, il faudrait donner une position relative aux flèches (tous les autres éléments l'ont déjà) - ou refaire le tout avec un _layout_ adapté.
  - Peaufiner la fenêtre de fin de jeu.

### Corrections réalisées

### Améliorations réalisées
