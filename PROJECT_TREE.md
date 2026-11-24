# A31 - « Labyrinthe »

## Arborescence du projet

Le schéma ci-dessous représente la structure de ce projet de développement.

```
labyrinthe
├── .github/workflows/       # Déploiement sur GitHub Pages
├── res/                     # Ressources du projet
│   ├── img/                    # Images
│   └── snd/                    # Musique
├── src/                     # Dossier source
│   ├── main/                   # Code source principal
│   │   └── labyrinth/              # Package racine
│   │   	├── model/                  # Règles du jeu (sans aucune dépendance à l'interface)
│   │   	├── view/                   # Interface graphique (Swing)
│   │   	├── Labyrinth.java          # Classe « main »
│   │   	├── MusicPlayer.java        # Musique de fond
│   │   	└── Resources.java          # Accès aux ressources
│   └── test/                   # Tests unitaires
├── web/                     # Page de la version web
├── INSTALL.md               # Compilation, lancement, tests
├── PROJECT_TREE.md          # Ce fichier, présentant la structure du dépot
└── README.md                # Présentation et règles
```
