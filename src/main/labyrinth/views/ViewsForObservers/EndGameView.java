package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.models.game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class EndGameView extends JPanel {
    private Image backgroundImage;
    private Image winnerImage;
    private Runnable onNewGame;

    /**
     * Creates an EndGameView that displays the winner, winner image, and options to start a new game
     * or quit the application.
     * @param winner : player who won the game. Must not be null.
     * @param backgroundImage : background image to be displayed in the view.
     * @param winnerImage : image to represent the winner. Can be null.
     * @param onNewGame : a callback to handle starting a new game. Can be null.
     * @throws IllegalArgumentException if the winner is null.
     */
    public EndGameView(Player winner, Image backgroundImage, Image winnerImage, Runnable onNewGame) {
        if (winner == null) {
            System.out.println("Erreur : Le gagnant est null lors de la création d'EndGameView.");
            throw new IllegalArgumentException("Le gagnant ne peut pas être null.");
        }

        this.backgroundImage = backgroundImage;
        this.winnerImage = winnerImage;
        this.onNewGame = onNewGame;

        System.out.println("Création de EndGameView pour le gagnant : " + winner.getName());

        setLayout(null); // Utilisation d'un layout nul pour le positionnement absolu
        setPreferredSize(new Dimension(1920, 1080));

        // Créer et configurer le label de félicitations
        JLabel label = new JLabel("Félicitations " + winner.getName() + " ! Tu as gagné !");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30)); // Police plus grande
        label.setForeground(Color.WHITE); // Texte en blanc
        label.setBounds(0, 700, 1920, 50); // Positionner le label en bas

        add(label);

        // Afficher l'image du gagnant
        if (winnerImage != null) {
            JLabel winnerImageLabel = new JLabel(new ImageIcon(winnerImage));
            winnerImageLabel.setBounds(860, 300, 200, 200); // Ajuster la position et la taille
            add(winnerImageLabel);
        }

        // Ajouter un bouton pour commencer une nouvelle partie
        JButton newGameButton = new JButton("Nouvelle Partie");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 20));
        newGameButton.setBounds(860, 550, 200, 50); // Positionner le bouton
        newGameButton.addActionListener(e -> {
            // Appeler le callback pour réinitialiser le jeu
            if (onNewGame != null) {
                onNewGame.run();
            }
        });
        add(newGameButton);

        // Ajouter un bouton pour quitter le jeu
        JButton quitButton = new JButton("Quitter");
        quitButton.setFont(new Font("Arial", Font.BOLD, 20));
        quitButton.setBounds(860, 620, 200, 50); // Positionner le bouton
        quitButton.addActionListener(e -> System.exit(0)); // Fermer l'application
        add(quitButton);
    }

    /**
     * Overrides the paintComponent method to render the background image of the EndGameView panel.
     * The background image is resized to fit the entire panel.
     * @param g : Graphics object used to perform the drawing operations. Must be not null.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dessiner l'image de fond
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
