# A31 - « Labyrinthe »

## Mode d'emploi

_remarques générales_

###  Compilation

Pour compiler « Labyrinthe » à la main, placez-vous dans le répertoire `src/main/labyrinth` et compilez avec `javac` en remontant la hiérarchie des classes. N'hésitez pas à copier-coller la (bien longue) commande ci-dessous qui respecte déjà le bon ordre :

<div>
    <textarea id="copyContent" style="width: 600px; height: 50px;">
javac models/geometry/*.java models/tiles/*.java models/game/Card.java models/game/Player.java models/observers/GameFacadeObserver.java models/game/GameFacade.java views/helpers/*.java models/data/*.java
controllers/GameFacadeController.java views/ViewsForObservers/GameFacadeTextView.java Labyrinth.java
    </textarea>
    <button onclick="copyToClipboard()">Copier</button>
</div>

_Commande à compléter, bien sûr. À noter que rien concernant le plateau n'y est encore (dépendances cycliques, mais ce sera résolu.)_

### Installation

_explications_

### Lancement

- Si vous avez compilé à la main, lancez `java Labyrinthe.java`, toujours depuis le même répertoire.

### Divers

_conseils particuliers_

<script>
function copyToClipboard() {
    var copyText = document.getElementById("copyContent");
    
    // Sélectionner le texte dans le champ
    copyText.select();
    copyText.setSelectionRange(0, 99999); // Pour mobile

    // Exécute la commande de copie
    document.execCommand("copy");

    // (Facultatif) Affiche une alerte pour confirmer que le texte a été copié
    alert("Texte copié : " + copyText.value);
}
</script>
