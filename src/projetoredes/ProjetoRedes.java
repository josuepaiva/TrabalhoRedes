/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetoredes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lavid
 */
public class ProjetoRedes {

    /**
     * @param args the command line arguments
     */
    private static final Pattern PATTERN = Pattern.compile(".*código de usuário é ([a-z0-9\\-]+).*");
    
    public static void main(String[] args) {
        
   
        new MailClient();
    }
    
}
