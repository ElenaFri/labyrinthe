# A31 - « Labyrinthe »

## Arborescence du projet

Le schéma ci-dessous représente la structure de ce projet de développement.

```angular2html
a-31-labyrinthe
├── doc/                     # Documentation du projet
├── res/                     # Ressources du projet
│   ├── img/                    # Images
│   └── sfx/                    # Effets sonores
├── src/                     # Dossier source
│   ├── main/                   # Code source principal, développé suivant le patron MVC
│   │   └── labyrinth/				# Package racine
│   │   	├── controllers/            # Classes controleurs
│   │   	├── models/                 # Classes modèles
│   │   	├── views/                  # Classes vue
│   │   	└── Labyrinth.java         # Classe « main »
│   └── test/                   # Tests unitaires
├── uml/                     # Diagrammes de classes
├── .gitignore               # Fichiers à ignorer par Git
├── PROJECT_TREE.md          # Ce fichier, présentant la structure du dépot
├── README.md                # Cahier des charges
└── REPORT.md                # Rapport sur la première phase de développement
```
