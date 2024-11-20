A31 - Projet "Labyrinthe"
=========================

![](res/img/labyrinthe.jpg)![](res/img/labyrinthe2.jpg)

À vous de développer une application multi-joueurs du jeu de société Labyrinthe !

Consignes générales
-------------------

### Pour démarrer avec le dépôt Git

1. **Créez un groupe `nom1-nom2`** où nom1 et nom2 sont les noms de famille des deux membres du binôme.
1. **Forkez** le dépôt <https://git.unistra.fr/a313/a31-labyrinthe> dans le groupe créé ci-dessus.
1. Ajoutez le responsable du module ET votre ou vos enseignant de TD/TP comme **Reporter** de votre dépôt

### Pour toute la durée du projet

- Ce projet est à réaliser **en binôme**.
- Vous devez concevoir avec UML et implémenter en Java le jeu de société Labyrinthe, en respectant une **architecture MVC** et en proposant une **interface graphique en Swing**.
- Vous devez respecter les **principes de conception** et mettre en œuvre les **patrons de conception** vus en cours, **lorsque cela est pertinent**. Attention, il ne s'agit pas d'essayer d'utiliser tous les patrons de conception vus en cours !
- Il est possible que vous soyez amenés à **renoncer à une partie du développement par manque de temps**. Dans ce cas, le rapport final est l’endroit où le signaler ("il aurait fallu faire *ceci* mais nous n’avions pas assez de temps et avons préféré nous concentrer sur *cela* parce que ...").
- Votre dépôt devra être **mis à jour au minimum une fois par semaine** de façon à ce que nous puissions évaluer votre progression sur toute la durée du projet.

Les règles du jeu
-----------------

Elles sont disponibles :

- en version textuelle : [https://www.regledujeu.fr/labyrinthe/](https://www.regledujeu.fr/labyrinthe/).
- en vidéo avec Ludochrono : [https://www.youtube.com/watch?v=pGzenrQnJ9g](https://www.youtube.com/watch?v=pGzenrQnJ9g).

Côté matériel, le jeu de société comporte **50 tuiles** de 3 sortes :

- 20 angles dont 4 sont fixes et 16 sont déplaçables,
- 12 sections droites toutes déplaçables,
- 18 en forme de "T" dont 12 sont fixes et 6 sont déplaçables.

Il y a également **24 objectifs**.

Nous nous limiterons à un nombre fixe de **4 joueurs**.

Les fonctionnalités
-------------------

Vous devez modéliser et implémenter les fonctionalités ci-dessous.

1 - La **génératon des tuiles**

- Niveau 1 : avec une orientation initiale fixe
- Niveau 2 : avec une orientation initiale aléatoire

2 - Le **génération du plateau** avec les tuiles :

- Niveau 1 : placées de façon fixe (toujours le même plateau de départ)
- Niveau 2 : placées toutes de façon aléatoire
- Niveau 3 : placées de façon fixe ou aléatoire comme dans le jeu de société

3 - L'**ajout des objectifs** sur les tuiles :

- Niveau 1 : placés de façon fixe
- Niveau 2 : placés de façon totalement aléatoire, 1 seul maximum par tuile
- Niveau 3 : placés de façon aléatoire mais pas sur les cases du départ des joueurs

4 - La **distribution des "cartes objectifs"** aux joueurs :

- Niveau 1 : de façon fixe
- Niveau 2 : de façon aléatoire

5 - Le **déplacement des pions case par case**

6 - Le **déplacement d'une ligne ou colonne du plateau** par la tuile supplémentaire :

- Niveau 1 : Toutes les lignes et colonnes
- Niveau 2 : Uniquement les lignes et colonnes impaires
- Niveau 3 : En gérant le passage d'un joueur éjecté au côté opposé de la ligne/colonne actuelle
- Niveau 4 : En interdisant le déplacement inverse du précédent

7 - La **détection d'un objectif atteint** par un joueur et le passage à l'objectif suivant

8 - La **détection de la fin du tour d'un joueur**

9 - La **détection de la fin de partie** lorsqu'un joueur a trouvé tous ses objectifs et est revenu à sa position de départ

> ***Conseil** : Faites la conception UML qui tient en compte tous les niveaux mais implémentez d'abord le jeu en entier avec tous les niveaux 1 avant d'implémenter les autres niveaux.*

L'interface graphique
---------------------

Votre application doit proposer 2 écrans :

- un **écran principal** qui permet de jouer une partie
- un **écran de fin de partie**

### Écran principal

L'écran principal doit afficher :

- le plateau de 7 par 7 avec les 49 tuiles, les 24 objectifs et les pions de chaque joueur
- La tuile supplémentaire actuelle
- Pour chaque joueur :
  - son objectif actuel
  - le nombre d'objectifs déjà récupérés
  - le nombre d'objectifs restants

À son tour, **un joueur doit pouvoir réaliser les actions suivantes** :

- Pousser une ligne ou colonne avec la tuile supplémentaire
- Se déplacer de case en case sur les tuiles voisines où le déplacement est possible.

Vous avez à votre disposition le fichier [`ImageHelper.java`](src/main/views/helpers/ImageHelper.java) pour manipuler les images, notamment :

- combiner plusieurs images en une seule
- appliquer une rotation de 90 degrés à une image

***Bonus 1***

À son tour, le joueur peut faire **tourner la tuile supplémentaire** par pas de 90 degrés dans le sens horaire ou anti-horaire avant de l'utiliser pour pousser une ligne ou colonne.

### Écran de fin de partie

L'écran de fin de partie doit annoncer le vainqueur.

***Bonus 2***

L'écran de fin de partie propose de commencer une nouvelle partie : tout doit petre réinitialisé pour recommencer.

Les rendus
----------

### 1er rendu

Date limite : le dimanche **1/12 à 23h59**

Documents :

- le **diagramme de classes** UML
  - le package `model` doit être décris en anticipant tout le développement
  - les packages `controller` et `view` doivent proposer une version de base mais peuvent être incomplets
- le **code source**
- un **rapport** pour expliquer vos choix de conception

Le rendu est à faire sur votre dépôt Git **sur une branche nommée `rendu1`**.

### 2ème rendu

Date limite : le dimanche **20/12 à 23h59**

Documents :

- le **diagramme de classes** UML qui doit :
  - représenter l'intégralité de votre application
  - être entièrement cohérent avec le code
- le **code source**
- un **mode d'emploi** nommé `INSTALL.md` à la racine de votre projet, qui explique comment compiler, installer et lancer votre application
- un **rapport** pour présenter vos nouveaux choix de conception depuis le 1er rendu et expliquer les raisons des évolutions des choix de conception qui avaient été annoncés au 1er rendu.

Le rendu est à faire sur votre dépôt Git **sur une branche nommé `rendu2`**.

### Précisions

- Vos diagrammes UML doivent être au format PlantUML,
- À la fin du projet, votre dépôt devra contenir au minimum une branche `rendu1` et une branche `rendu2`. Ce seront les seules branches évaluées. Vous êtes libre de gérer le reste de votre dépôt comme vous le souhaitez.