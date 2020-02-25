## Export DokuWiki to Wordpress ##
Explication du fonctionnement l'application <br>
Explaining how work the application
===
Voici la Doc pour utiliser l'application: **Export Dokuwiki to Wordpress**. <br>
Here the Doc for use application: **Export Dokuwiki to Wordpress**.

L'application convertit une page Dokuwiki qui est en format .txt. Pour cela les balises utilisées sur doku sont converties en balises html. <br>
The application convert a page Dokuwiki who is in .txt format. The tags used on Dokuwiki are converted to html tags.

L'application vous demande plusieurs informations. <br>
The application ask for many inforamtion.

Le "**Host site**" qui correspond au domaine de votre site. <br>
"**Host site**" which corresponds to your site domain.

Le "**DB HOST**" qui correspond au domaine de votre base de données. <br>
"**DB HOST**" which corresponds to your database domain.

Le "**DB Name**" qui correspond au nom de la base de données de votre WordPress. <br>
"**DB Name**" which corresponds to the database name used for your WordPress.

le "**DB Login**" qui correspond à votre login de votre base de données. <br>
"**DB Login**" which corresponds to your database login.

le "**DB mdp**" qui correspond à votre mot de passe de votre base de données. <br>
"**DB mdp**" which correspond to your database password.

le "**Titre de la page**" correspond au nom de la page que vous voulez créer pour wordpress. <br>
"**Titre de la page**" which correspond to the name of the page you want to create.

le "**url page**" qui correspond au lien qui mène à votre page sur votre site **Il est conseillé de reprendre le lien de dokuwiki lors de la création de la page (ex: fr:manager:accueil) et de remplacer les ":" par des "-" pour faciliter l'intégration des liens internes** <br>
"**url page**" is the link to your page for your site **It's recommended to take up the link of Dokuwiki (ex fr:manager:accueil) and replace ":" by "-" to avoid issues with the integration of internal links.**

IMPORTANT  Pour les images de votre Dokuwiki il faudra déplacer toute les images dans le répétoire `/uploads` de wordpress et créer un dossier `/wiki` ainsi mettre tout les images dedans.

Explication du code <br>
Explaining how work the code
===

Il y a deux class:
* "**ExportDokuWikitoWordPress**" contient les méthode pour convertir et exporté le contenu de la page Dokuwiki
* "**ExportDokuWikitoWordPressView**" contient le code pour l'interface en JFrame

La class "**ExportDokuWikitoWordPress**" :
===

Paramétre privé:
===
* **String Host** : Le domaine du site
* **String content** : Le contenu de la page
* **String BDD** : Le domaine de la base de donnée
* **String BDD_name** : le nom de la base de donnée
* **String BDD_login** : L'identifiant de la base de données
* **String BDD_mdp** : Le mot de passe de la base de données
* **boolean list = false** : Elle permet le bon fonctionnement de la class "**ListOrdonnee(String ligne)**"
* **boolean list2 = false** : Elle permet le bon fonctionnement de la class "**ListOrdonnee(String ligne)**"
* **boolean list3 = false** : Elle permet le bon fonctionnement de la class "**ListNumOrdonnee(String ligne)**"
* **boolean list4 = false** : Elle permet le bon fonctionnement de la class "**ListNumOrdonnee(String ligne)**"
* **boolean list4 = false** : Elle permet le bon fonctionnement de la class "**Tableau(String ligne)**"

Méthode public:
===
* **ExportDokuWikitoWordPress(String Host, String BDD, String BDD_name, String BDD_login, String BDD_mdp)** : C'est le constructeur de la class
* **readFile(String pathToFile)** : Cette méthode permet récupérer le contenu du .txt et de lui appliquer les changement grace au différente méthode de l'application
* **ExportBDD(String post_title, String post_name)** : Cette méthode envoie le contenu de "**content**" à la base de données ainsi que le "**post_title**" et le "**post_name**"

Méthode privée: 
===
* **Title(String ligne)** : Cette méthode remplace les balises "`=`" par des balises html "`<h1>` , `<h2>`, etc..."<br> 
ex: `======Titre======` sera changer en `<h1>Titre</h1>`
* **Citation(String ligne)** : Cette méthode remplace les balises ">" par celle en html "`<blockquote></blockquote>`" <br>
ex: `> citation` sera charger en `<blockquote>citation</blockquote>`
* **Gras(String ligne)** : Cette méthode remplace les balises "`**`" par celle en html "`<b></b>`" <br>
ex: on remplace `**un mot**` par `<b>un mot</b>`
* **Souligne(String ligne)** :Cette méthode ramplace les balises "`__`" par celle en html "`<u></u>`" <br>
ex: on remplace `__un mot__` par `<u>un mot</u>`
* **Italique(String ligne)** : Cette méthode remplace les balises "`//`" par celle en html "`<i></i>`"
* **Lien(String ligne)** : Cette méthode remplace les liens de dokuwiki en lien html <br>
ex: un lien dokuwiki resemble a ça "`[[fr:manager:accueil|Manager]]`" et la méthode va en sortir ça `<a href='http://Host/fr-manager-accueil>Manager</a>`
* **Image(String ligne)** : Cette méthode remplace les lien des images par un lien d'image en html <br>
ex: une image sur dokuwiki `{{fr:stagiaire.png}}` et la méthode va en sortir `<img src="/app/uploads/wiki/stagiaire.png"/>`
* **ListeOrdonnee(String ligne)** : Cette méthode remplace les balises "`*`" par des balises html `<ul><li></li></ul>`
* **ListeNumOrdonnee(String ligne)** : Cette méthode remplace les balises "`*`" par des balises html `<ol><li></li></ol>`
* **antiSlash(String ligne)** : 
* **guillemet** : 

Title
===
le traitement des titres est géré avec des simple `replace()` et `replaceFirst()`. Donc si la ligne contient un `=` alors il les remplacent mais une succetion définit<br>
Par exemple: `======Titre======` Il y a 6 `=` donc la méthode remplace par `<h1>` pour 5 `=` on obtient `<h2>` pour 4 `=` on obtient `<h3>`

Gras / Souligner / Italique
===
Le traitement des la mise en gras, de l'italique et le soulignement fonctionne de la même façon. La méthode comporte deux variable `ponctuation[]` un tableau rassemblant différente ponctuation afin de tester si elle est collé a la balise `**` ou `__` ou `//` a la fin car sinon `.endsWith("**")` ne fonctionnerai pas. Et `boolean verif = false` qui permettra de savoir si la balise prenez un seul mot ex: `**mot**` alors dans le programme la valeur de celui-ci deviendra vrai
Le tableau contient `["", ".", ",", ";", ":", "!", "?", "”"]`, la première valeur du tableau contient rien car si pas de ponctuation.
La méthode récupére une ligne donc on va la split à chaque espace et contenir la table dans une variable tab `var tab = ligne.split(" ")`.
puis on peut faire une boucle pour `for(int i=0;i<tab.lenght;i++)` ainsi on peut analyser bien la ligne.
apres une autre boucle pour `for(String p : ponctuation` afin de vérifier cette emplacement avec les différentes ponctuations.

Lien
===


Image
===


ListeOrdonnee ListNumOrdonnee
===

Tableau
===