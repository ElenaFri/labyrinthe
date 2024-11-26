package models.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Card {
    private Integer _treasure;  // Le trésor associé à cette carte
    private boolean _isFound;   // Si le trésor de cette carte a été trouvé

    // Constructor
    public Card() {
        _isFound = false;
        _treasure = null;  // La carte n'a pas de trésor par défaut
    }

    // Générer un trésor spécifique (sur 24 trésors)
    public void generateTreasure(List<Integer> treasurePool) {
        // Si le pool de trésors est vide, on ne génère pas de trésor
        if (!treasurePool.isEmpty()) {
            // Choisir un trésor aléatoire dans le pool
            Collections.shuffle(treasurePool);
            _treasure = treasurePool.remove(0);  // Enlever et récupérer un trésor du pool
        }
    }

    // Accéder au trésor associé à la carte
    public Integer getTreasure() {
        return _treasure;
    }

    // Vérifier si le trésor a été trouvé
    public boolean checkIfFound() {
        return _isFound;
    }

    // Marquer le trésor comme trouvé
    public void setFound() {
        _isFound = true;
    }

    // Définir si la carte est un dos (pas de trésor associé)
    public boolean isBackCard() {
        return _treasure == null;
    }

    // Statique : initialiser le pool de trésors (par exemple de 0 à 23)
    public static List<Integer> createTreasurePool() {
        List<Integer> pool = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            pool.add(i);  // Ajouter les trésors (ici les indices de 0 à 23)
        }
        return pool;
    }
}
