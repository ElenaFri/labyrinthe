package main.labyrinth;

import main.labyrinth.models.data.ImageStore;
import main.labyrinth.models.game.Card;
import main.labyrinth.models.game.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Créer une instance de ImageStore
        ImageStore imageStore = new ImageStore();

        // Créer un deck de 24 cartes
        List<Card> deck = createDeck();
        Collections.shuffle(deck); // Mélanger les cartes

        // Créer 4 joueurs et leur attribuer 6 cartes chacun
        Player[] players = new Player[4];
        for (int i = 0; i < 4; i++) {
            players[i] = new Player(i, "Player " + (i + 1));
            List<Card> playerCards = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                playerCards.add(deck.remove(0));  // Distribuer 6 cartes à chaque joueur
            }
            players[i].setCards(playerCards.toArray(new Card[0])); // Attribuer les cartes au joueur
        }

        // Créer un JPanel pour afficher les cartes
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int startX = 50;  // Position de départ pour l'affichage des cartes
                int startY = 50;  // Position de départ en Y
                int cardWidth = 80;  // Largeur d'une carte
                int cardHeight = 120;  // Hauteur d'une carte
                int cardOverlap = 20;  // Chevauchement des cartes

                int index = 0; // Initialiser l'index des cartes

                // Afficher les cartes de chaque joueur
                for (Player player : players) {
                    g.drawString(player.getName(), startX, startY - 10); // Afficher le nom du joueur
                    for (Card card : player.getCards()) {
                        int cardIndex = card.getTreasure(); // Index de la carte
                        boolean isOpen = true; // La carte est ouverte

                        // Obtenir l'image de la carte
                        BufferedImage cardImage = imageStore.getCardWithTreasure(cardIndex, isOpen);

                        if (cardImage != null) {
                            // Calculer les positions des cartes
                            int x = startX + index * (cardWidth - cardOverlap);
                            int y = startY;

                            // Dessiner la carte avec chevauchement
                            g.drawImage(cardImage, x, y, cardWidth, cardHeight, null);
                        } else {
                            // Afficher un message de débogage si l'image est null
                            System.out.println("Erreur: L'image de la carte " + cardIndex + " est introuvable.");
                        }
                        index++;
                    }
                    startY += cardHeight + 40; // Espacer les cartes entre les joueurs
                    index = 0;  // Réinitialiser l'index pour le prochain joueur
                }
            }
        };

        // Créer la fenêtre pour afficher les cartes
        JFrame frame = new JFrame("Affichage des cartes des joueurs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(panel);
        frame.setVisible(true);
    }

    // Créer un deck de 24 cartes
    public static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            deck.add(new Card(i)); // Créer des cartes avec des trésors numérotés de 0 à 23
        }
        return deck;
    }
}
