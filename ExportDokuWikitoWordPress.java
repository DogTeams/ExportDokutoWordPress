
package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.text.SimpleDateFormat;  
import java.util.Date;  

public class ExportDokuWikitoWordPress {
    
private String Host;
private String content;
private String BDD;
private String BDD_name;
private String BDD_login;
private String BDD_mdp;

private boolean list=false;
private boolean list2=false;
private boolean list6=false;
private boolean list3=false;
private boolean list4=false;
private boolean list5=false;

    public ExportDokuWikitoWordPress(String Host, String BDD, String BDD_name, String BDD_login, String BDD_mdp){
        this.Host = Host;
        this.BDD = BDD;
        this.BDD_name = BDD_name;
        this.BDD_login = BDD_login;
        this.BDD_mdp = BDD_mdp;
    }
    public String readFile(String pathToFile){
        ArrayList result = new ArrayList();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(pathToFile)));
            String ligne;
            while((ligne = reader.readLine()) != null){
                ligne = guillemet(ligne);
                ligne = ListeOrdonnee(ligne);
                ligne = ListeNumOrdonnee(ligne);
                ligne = Ligne(ligne);
                ligne = Tableau(ligne);
                ligne = Title(ligne);
                ligne = Citation(ligne);
                ligne = Gras(ligne);
                ligne = Italique(ligne);
                ligne = Souligne(ligne);
                ligne = Lien(ligne);
                ligne = Image(ligne);
                ligne = antiSlash(ligne);
               
               
                System.out.println(ligne);
                result.add(ligne);
            }
            content= "";
            for(var res : result){ //Traitement du resultat
                content += res + "\n";
            }
            return "Envoye Réussie";
        } catch (Exception ex){
            return "Error. "+ex.getMessage();
        }
    }
    public String ExportBDD(String post_title, String post_name){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");  
        String strDate = formatter.format(date); 
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection cn = (Connection) DriverManager.getConnection("jdbc:mysql://"+BDD+"/"+BDD_name,BDD_login,BDD_mdp);
            
            Statement st = (Statement) cn.createStatement();
            
            var post_author = st.executeQuery("SELECT id FROM wp_users where user_nicename='WikitoWordPress'");
            if(!post_author.next()){
                String meta_key[] = {"nickname", "first_name", "last_name", "description", "rich_editing", "syntax_highlighting", "comment_shortcuts", "admin_color", "use_ssl", "show_admin_bar_front", "locale", "wp_capabilities", "wp_user_level", "dismissed_wp_pointers", "show_welcome_panel"};
                String meta_value[] = {"wikitowordpress", "", "", "", "true", "true", "false", "fresh", "0", "true", "", "a:1:{s:13:\"administrator\";b:1;}", "10", "", "1"};
                st.execute("INSERT INTO `wp_users` (`user_login`, `user_pass`, `user_nicename`, `user_email`, `user_url`, `user_registered`, `user_activation_key`, `user_status`, `display_name`) VALUES ('wikitowordpress', '$P$BptAKi0mF38ktqMq7BoZ9xUtmo8y8A/', 'WikitoWordPress', 'WikitoWordPress@test.com', '', '"+strDate+"', '', '0', 'WikitoWordPress')");
               /* for(int i = 0;i<15;i++){ st3.execute("INSERT INTO `wp_usermeta` (`user_id`, `meta_key`, `meta_value`) VALUES ('"+post_author.getInt("id")+"', '"+meta_key[i]+"', '"+meta_value[i]+"')");}*/
            }
            
            st = (Statement) cn.createStatement();
            
            var post_id = st.executeQuery("SELECT id FROM wp_posts ORDER BY id DESC LIMIT 0, 1");
            post_id.next();
            String sql = "INSERT INTO `wp_posts`(`ID`, `post_author`, `post_date`, `post_date_gmt`, `post_content`, `post_title`, `post_excerpt`, `post_status`, `comment_status`, `ping_status`, `post_password`, `post_name`, `to_ping`, `pinged`, `post_modified`, `post_modified_gmt`, `post_content_filtered`, `post_parent`, `guid`, `menu_order`, `post_type`, `post_mime_type`, `comment_count`) VALUES ('"+(post_id.getInt("id")+1)+"', '"+post_author.getInt("id")+"', '"+strDate+"', '"+strDate+"', "+'"'+content+'"'+", '"+post_title+"', '', 'publish', 'closed', 'closed', '', '"+post_name+"', '', '', '"+strDate+"', '"+strDate+"', '', '0', '/?page_id="+(post_id.getInt("id")+1)+"', '0', 'page', '', '0')";
            st.execute(sql);
            
            cn.close();
            st.close();
            return "Success";
        }catch(SQLException e){
            e.printStackTrace();
            return e.getMessage();
        }catch(ClassNotFoundException e){
            
            e.printStackTrace();
            return e.getMessage();
        }
    }
    private String Title(String ligne){
        if(ligne.contains("﻿")){ //Traitement d'un caractère insolite invisible
            ligne = ligne.replaceFirst("﻿","");
        }
        if(ligne.contains("=")){ //Traitement des titres
            ligne = ligne.replaceFirst("======","<h1>");
            ligne = ligne.replace("======","</h1>");
            ligne = ligne.replaceFirst("=====","<h2>");
            ligne = ligne.replace("=====","</h2>");
            ligne = ligne.replaceFirst("====","<h3>");
            ligne = ligne.replace("====","</h3>"); 
            ligne = ligne.replaceFirst("===","<h4>");
            ligne = ligne.replace("===","</h4>"); 
            ligne = ligne.replaceFirst("==","<h5>");
            ligne = ligne.replace("==","</h5>"); 
        }
        return ligne;
    }
    private String Ligne(String ligne){
        if(ligne.contains("---")){
            ligne = ligne.replace("-", "");
            ligne = "<hr>";
        }
        return ligne;
    }
    private String Citation(String ligne){
        if(ligne.startsWith(">")){ // Traitement des balises pour les citations
            ligne = ligne.replace(">", "<blockquote> ");
            ligne += " </blockquote>";
        }
        return ligne;
    }
    private String Gras(String ligne){
        String ponctuation[] = {"",".",",",";",":","!","?","”","“","“.","“,","“:","“!","“;","“?"};
        boolean verif = false;
        if(ligne.contains("**")){ //Traitement des balise pour mettre en gras
            var tab = ligne.split(" ");
            var test = tab;
            for(int i = 0; i< tab.length; i++){
                for(String p : ponctuation){
                    if(tab[i].startsWith("**")&&tab[i].endsWith("**"+p)||tab[i].startsWith("“**")&&tab[i].endsWith("**"+p)){
                        test = tab[i].split("\\*");
                        if(tab[i].startsWith("“**")){
                            tab[i] = "“<b>"+test[2]+"</b>"+p;
                        }
                        else{
                            tab[i] = "<b>"+test[2]+"</b>"+p;
                        }
                        verif = true;
                    }
                }
                if(!verif){
                    for(String p : ponctuation){ 
                        if(tab[i].startsWith("**")||tab[i].startsWith("“**")){
                            test = tab[i].split("\\*");
                            if(tab[i].startsWith("“**")){
                                tab[i] = "“<b>"+test[2];
                            }
                            else{
                                tab[i] = "<b>"+test[2];
                            }    
                        }
                        if(tab[i].endsWith("**"+p)){
                            test = tab[i].split("\\*");
                            tab[i] = test[0]+"</b>"+p;
                        }
                    }
                }
                verif = false;
            }
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String Souligne(String ligne){
        String ponctuation[] = {"",".",",",";",":","!","?","”","“","“.","“,","“:","“!","“;","“?"};
        boolean verif = false;
        if(ligne.contains("__")){ //Traitement des balise pour souligner
            var tab = ligne.split(" ");
            var test = tab;
            for(int i = 0; i< tab.length; i++){
                for(String p : ponctuation){
                    if(tab[i].startsWith("__")&&tab[i].endsWith("__"+p)||tab[i].startsWith("“__")&&tab[i].endsWith("__"+p)){
                        test = tab[i].split("_");
                        if(tab[i].startsWith("“__")){
                            tab[i] = "“<u>"+test[2]+"</u>"+p;
                        }
                        else{
                            tab[i] = "<u>"+test[2]+"</u>"+p;
                        }
                        verif = true;
                        //System.out.println(tab[i]);
                    }
                }
                if(!verif){
                    for(String p : ponctuation){
                        if(tab[i].startsWith("__")||tab[i].startsWith("“__")){
                            test = tab[i].split("_");
                            if(tab[i].startsWith("“__")){
                                tab[i] = "“<u>"+test[2];
                            }
                            else{
                                tab[i] = "<u>"+test[2];
                            }   
                        }
                        if(tab[i].endsWith("__"+p)){
                            test = tab[i].split("_");
                            tab[i] = test[0]+"</u>"+p;
                        }
                    }
                }
                verif = false;
            }
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String Italique(String ligne){
        String ponctuation[] = {"",".",",",";",":","!","?","”","“","“.","“,","“:","“!","“;","“?"};
        boolean verif = false;
        if(ligne.contains("//")){ //Traitement des balise pour l'italique
            var tab = ligne.split(" ");
            var test = tab;
            for(int i = 0; i< tab.length; i++){
                for(String p : ponctuation){
                    if(tab[i].startsWith("//")&&tab[i].endsWith("//"+p)||tab[i].startsWith("“//")&&tab[i].endsWith("//"+p)){
                        test = tab[i].split("/");
                        if(tab[i].startsWith("“//")){
                            tab[i] = "“<i>"+test[2]+"</i>"+p;
                        }
                        else{
                            tab[i] = "<i>"+test[2]+"</i>"+p;
                        }
                        verif = true;
                    }
                }
                if(!verif){
                    for(String p : ponctuation){
                        if(tab[i].startsWith("//")||tab[i].startsWith("“//")){
                            test = tab[i].split("/");
                            if(tab[i].startsWith("“//")){
                                tab[i] = "“<i>"+test[2];
                            }
                            else{
                                tab[i] = "<i>"+test[2];
                            }   
                        }
                        if(tab[i].endsWith("//"+p)){
                            test = tab[i].split("/");
                            tab[i] = test[0]+"</i>"+p;
                        }
                    }
                }
                verif = false;
            }
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String Lien(String ligne){ // A finir
        if(ligne.contains("[[")&&ligne.contains("]]")){ //Traitement des liens
            var tab = ligne.split(" ");
            var test = tab;
            var stock = "";
            boolean verif= false;
            int nb = 0;
            for(int i = 0; i< tab.length; i++){
                if(tab[i].startsWith("[[")&&!tab[i].contains("[[http")||verif&&!tab[i].contains("[[http")){
                    stock += tab[i]+" ";
                    if(!verif){
                        nb = i;
                        verif = true;
                    }
                    
                    if(tab[i].contains("]]")){
                        test = stock.split("\\|");
                        ligne = test[0];
                        ligne = ligne.replace(":", "-");
                        ligne = ligne.replace("[[", "<a href='http://"+Host+"/");
                        tab[i] = ligne + "'>"+test[1].replace("]]", "")+"</a>";
                        for(int j = nb;j<i;j++){
                            tab[j]="";
                        }
                        verif = false;
                        stock = "";
                        
                    }
                    verif = true;
                }
                if(tab[i].startsWith("[[")&&tab[i].contains("[[http")||verif&&tab[i].contains("[[http")){
                    stock += tab[i]+" ";
                    if(!verif){
                        nb = i;
                        verif = true;
                    }
                    
                    if(tab[i].contains("]]")){
                        stock = stock.replace("]]","");
                        stock = stock.replace("[[", "");
                        tab[i] = "<a href='"+stock+"'>"+stock+"</a>";
                        for(int j = nb;j<i;j++){
                            tab[j]="";
                        }
                        verif = false;
                        stock = "";
                    }
                }
            }
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String Image(String ligne){
        if(ligne.contains("{{")){ //Traitement des balises pour les images
            var tab = ligne.split(" ");
            var test = tab;
            var stock = "";
            boolean verif= false;
            int nb = 0;
            for(int i = 0; i< tab.length; i++){
                if(tab[i].startsWith("{{")||verif){
                    stock += tab[i]+" ";
                    if(!verif){
                        nb = i;
                        verif = true;
                    }
                    
                    if(tab[i].contains("}}")){
                        test = stock.split(":");
                        ligne = test[test.length-1];
                        ligne = ligne.replace("}}", "' />");
                        tab[i] = "<img src='/app/uploads/wiki/"+ligne;
                        for(int j = nb;j<i;j++){
                            tab[j]="";
                        }
                        verif =false;
                        stock = "";  
                    }
                    
                }
                if(tab[i].startsWith("{{")&&tab[i].contains("http")||verif&&tab[i].contains("http")){
                    stock += tab[i]+" ";
                    if(!verif){
                        nb = i;
                        verif = true;
                    }
                    
                    if(tab[i].contains("}}")){
                        stock = stock.replace("}}","");
                        stock = stock.replace("{{", "");
                        tab[i] = "<img src='" + stock + "'/>";
                        for(int j = nb;j<i;j++){
                            tab[j]="";
                        }
                        verif = false;
                        stock = "";
                    }

                }
            }
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String ListeOrdonnee(String ligne){//Traitement Liste *
        if(!ligne.startsWith("  *") && list &&!ligne.startsWith("    *")&&!ligne.startsWith("      *") ){ //Fin de liste
            // System.out.println(ligne);
            ligne = "</ul>" + ligne;
            list = false;
        }
        if(!ligne.startsWith("    *") && list2 &&!ligne.startsWith("      *")){ // Fin liste 2
            // System.out.println(ligne);
            ligne = "</ul>" + ligne;
            list2 = false;
        }
        /*if(!ligne.startsWith("      *") && list6){ // Fin liste 2
            // System.out.println(ligne);
            ligne = "</ul>" + ligne;
            list6 = false;
        }*/
        if(ligne.startsWith("</ul>  *")){ // Suite liste before liste 2
            //System.out.println(ligne);
            if(!list){
                ligne = ligne.replaceFirst("\\*","");
                var stock = ligne;
                ligne = "";
                ligne = "</ul><ul><li>" + stock +" </li>";
                list = true;
                // System.out.println(ligne);
            }
            if(!list2&&list){
                ligne = ligne.replaceFirst("\\*", "");
                ligne = ligne.replace("</ul>","");
                var stock = ligne;
               ligne = "";
                ligne = "</ul><li>" + stock + " </li>";
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\*", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("  *")){//Liste
            //System.out.println(ligne);
            if(!list){
                ligne = ligne.replaceFirst("\\*","");
                var stock = ligne;
                ligne = "";
                ligne = "<ul><li>" + stock +" </li>";
                list = true;
                // System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\*", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("</ul>    *")){ // Suite liste before liste 3
            //System.out.println(ligne);
            if(!list2){
                ligne = ligne.replaceFirst("\\*","");
                var stock = ligne;
                ligne = "";
                ligne = "</ul><li>" + stock +" </li>";
                list2 = true;
                // System.out.println(ligne);
            }
            if(!list6&&list2&&list){
                ligne = ligne.replaceFirst("\\*", "");
                ligne = ligne.replace("</ul>","");
                var stock = ligne;
               ligne = "";
                ligne = "</ul><li>" + stock + " </li>";
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\*", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("    *")){ //Liste 2
            //System.out.println(ligne);
            if(!list2){
                ligne = ligne.replaceFirst("\\*","");
                var stock = ligne;
                ligne = "";
                ligne = "<ul><li>" + stock +" </li>";
                list2 = true;
                //System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\*", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }

        if(ligne.startsWith("      *")){ //Liste 3
            //System.out.println(ligne);
            if(!list6){
                ligne = ligne.replaceFirst("\\*","");
                var stock = ligne;
                ligne = "";
                ligne = "<ul><li>" + stock +" </li>";
                list6 = true;
                //System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\*", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }

//Fin de Traitement Liste *
        return ligne;
    }
    private String ListeNumOrdonnee(String ligne){
        if(!ligne.startsWith("  -") && list3 &&!ligne.startsWith("    -") ){ // Traitement List -
            // System.out.println(ligne);
            ligne = "</ol>" + ligne;
            list3 = false;
        }
        if(!ligne.startsWith("    -") && list4){
            // System.out.println(ligne);
            ligne = "</ol>" + ligne;
            list4 = false;
        }
        if(ligne.startsWith("</ol>  -")){
            //System.out.println(ligne);
            if(!list3){
                ligne = ligne.replaceFirst("\\-","");
                var stock = ligne;
                ligne = "";
                ligne = "</ol><ol><li>" + stock +" </li>";
                list3 = true;
                // System.out.println(ligne);
            }
            if(!list4&&list3){
                ligne = ligne.replaceFirst("\\-", "");
                ligne = ligne.replace("</ol>","");
                var stock = ligne;
                ligne = "";
                ligne = "</ol><li>" + stock + " </li>";
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\-", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("  -")){
            //System.out.println(ligne);
            if(!list3){
                ligne = ligne.replaceFirst("\\-","");
                var stock = ligne;
                ligne = "";
                ligne = "<ol><li>" + stock +" </li>";
                list3 = true;
                // System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\-", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("    -")){
            //System.out.println(ligne);
            if(!list4){
                ligne = ligne.replaceFirst("\\-","");
                var stock = ligne;
                ligne = "";
                ligne = "<ol><li>" + stock +" </li>";
                list4 = true;
                //System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\-", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        if(ligne.startsWith("</ol>    -")){
            //System.out.println(ligne);
            if(!list4){
                ligne = ligne.replaceFirst("\\-","");
                var stock = ligne;
                ligne = "";
                ligne = "</ol><ol><li>" + stock +" </li>";
                list4 = true;
                System.out.println(ligne);
            }
            else{
                //System.out.println(ligne);
                ligne = ligne.replaceFirst("\\-", "");
                var stock = ligne;
                ligne = "";
                ligne = "<li>" + stock + " </li>";
            }
            ligne = ligne.replace("\\"," \n ");
        }
        return ligne;
    }
    private String Tableau(String ligne){
        if(!ligne.startsWith("|")&&list5){
            ligne = "</table>" + ligne;
            list5 = false;
        }
        if(ligne.startsWith("|")){
            var tab = ligne.split("\\|");
            if(list5){ // Les ligne suivante de la première
                for(int i = 0; i<tab.length; i++){
                    if(i==1){
                        tab[i] = "<tr><td>"+tab[i]+"</td>";
                    }
                    if(i==tab.length-1){
                        tab[i] = "<td>"+tab[i]+"</td></tr>";
                    }
                    if(i!=0 && i!=1 && i!=tab.length-1){
                        tab[i] = "<td>"+tab[i]+"</td>";
                    }
                }
            }
            if(!list5){ // Première ligne du tableau
                for(int i = 0; i<tab.length; i++){
                    if(i==1){
                        tab[i] = "<table><tr><td>"+tab[i]+"</td>";
                    }
                    if(i==tab.length-1){
                        tab[i] = "<td>"+tab[i]+"</td></tr>";
                    }
                    if(i!=0 && i!=1 && i!=tab.length-1){
                        tab[i] = "<td>"+tab[i]+"</td>";
                    }
                }
                list5 = true;
            }        
            ligne = "";
            for(int i = 0; i<tab.length; i++){
                if(i==0){
                    ligne = tab[i];
                }
                else{
                    ligne += " " + tab[i]; 
                }
            }
        }
        return ligne;
    }
    private String antiSlash(String ligne){
        if(ligne.contains("\\")){
            ligne = ligne.replace("\\", "");
        }
        return ligne;
    }
    private String guillemet(String ligne){
        if(ligne.contains("\"")){
            ligne = ligne.replace("\"","“");
        }
        if(ligne.contains("%%")){
            ligne = ligne.replace("%%","");
        }
        return ligne;
    }
}