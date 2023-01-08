Projet Dactylo-Game CPOO5

Binome 17 : 
ZEYNALI Kyan
ZHOU Sebastien

Pour lancer l'application, il faut taper la commande :

./gradlew run

Pour compiler et exécuter les tests, il faut taper la commande :

./gradlew test

Si votre machine exécute le serveur pour le mode multijoueur, tapez la commande :

./gradlew server

avant de lancer l'application (coté client)


MODE ENTRAINEMENT ET MODE SOLO :

Dans la section "Solo" du menu principal, vous pouvez choisir entre le mode Entrainement et le mode Solo 
et vous pouvez sauvegarder votre progression en appuyant sur le bouton "Save".


MODE MULTIJOUEUR :

Pour jouer en multijoueur, une machine doit exécuter le serveur (fichier Server.java du package network).
Cette dernière peut être l'un des postes des joueurs. Dans ce cas, il faut lancer le serveur avant de lancer l'application.
L'adresse IP de la machine qui exécute le serveur doit être renseignée dans les paramètres (cf. settings)
Seulement après avoir renseigné l'IP, les joueurs peuvent cliquer sur le bouton "Multiplayer" du menu principal pour se connecter au serveur.
Ainsi, les joueurs arrivent dans une sorte de salle attente où le nom de chaque nouveau joueur connecté apparaît.
Une fois qu'un joueur se sent prêt à commencer la partie, il clique sur le bouton "Ready".
La partie commence une fois que tous les joueurs dans la salle d'attente ont appuyé sur le bouton "Ready".
Par conséquent, n'appuyez pas trop vite sur le bouton "Ready" si vous devez attendre d'autres joueurs.
Enfin, une fois la partie terminée, les joueurs peuvent rejouer la partie, avec donc les même adversaires.
Si des joueurs doivent rejoindre ou quitter la partie, chaque joueur devra redémarrer l'application.


SECTION PARAMETRES :

La section "Settings" permet de mofifier certains paramètres du mode solo, comme la difficulté.
Il est aussi possible de modifier l'adresse IP du serveur pour le mode multijoueur.
La section "Leaderboard" permet aux joueur de consulter ses performances,
en accèdant à un historique classé des parties jouées en mode solo.


BOUTON QUIT :

Ferme l'application.

Fonctionnalités implémentées :

- Application Dactylo avec JavaFX pour l'interface graphique
- Serveur avec java.net.Socket pour le mode multijoueur
- Communcation Client-Serveur via packets avec com.google.gson.Gson
- Mode Entrainment avec les statistiques
- Mode Solo avec sauvegarde de la progression
- Mode Multijoueur avec modèle Client-Serveur
- Quelques tests concernant les mots avec Gradle pour le build

