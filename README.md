<h1 align="left">Projet de billetterie pour les JO avec Studi</h1>

###

<p align="left">Ce projet a pour but de mettre en application les concepts de bases et intermédiaire de Spring boot.<br>
  Pour faire fonctionner l'application dans son ensemble vous devrez aussi avoir le frontend que vous trouverez ici https://github.com/CedricGuette/billetterie-front, il vous servira d'interface avec l'application.<br><br>
  Pour utiliser le back il faudra tout d'abord télécharger le script en cliquant sur l'onglet "<> Code" vous pouvez télécharger en ZIP ou <br>
  en copiant l'adresse de l'onglet HTTPS dans un terminal allez dans le dossier où vous désirez télécharger le script et rentrez "git clone" + l'adresse copiée. 
  <br><br>
  L'application étant développée en Java 21 il vous faudra installer le SDK correspondant, que vous trouverez ici : https://www.oracle.com/fr/java/technologies/downloads/#java21 <br>
  Puis il faut créer des variables d'environnement (.env) avec les paramètres qui suivent:<br><br>
  #URL du front de l'application<br>
  URL_FRONT=<br><br>
  #Données relative à la base de données MySQL que vous allez utiliser<br>
  DATABASE_URL=<br>
  DATABASE_USERNAME=<br>
  DATABASE_PASSWORD=<br><br>
  #Clé qui sert pour les token de session (vous pouvez y mettre n'importe quoi)<br>SECRET_KEY=<br><br>
  #Clés de l'API Stripe pour simuler les paiement en mode test<br>
  STRIPE_SECRET_KEY=<br><br>#Identifiant et mot de passe de l'administrateur <br>
  ADMIN_USERNAME=<br>ADMIN_PASSWORD=<br><br>
  Si votre IDE vous permet de lancer l'application directement vous n'aurez qu'à cliquer sur le bouton pour ce faire.<br>
  Sinon vous devez compiler l'application avec Maven en utilisant un clean package ("mvn clean package" dans un terminal à la racine), pour lancer l'application il faudra entrer dans un terminal à la racine du script "java -jar --enable-preview target/billetterie-0.0.1-SNAPSHOT.jar"<br><br>
  L'application tournera donc en toile de fond à l'adresse: http://localhost:8081/</p>

###

<h2 align="left">Documentation Swagger</h2>

###

<p align="left">Lorsque l'application est lancée : http://localhost:8081/swagger-ui.html</p>

###

<h2 align="left">Réalisé avec</h2>

###

<div align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" height="40" alt="java logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" height="40" alt="spring logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" height="40" alt="mysql logo"  />
</div>

###
