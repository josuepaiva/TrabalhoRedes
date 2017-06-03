package projetoredes;


import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Josue
 */
public abstract class  ParamsEmail {
    
    protected Properties props;
    
    ParamsEmail(){
    }
    
    public Properties getProperties(){
        return this.props;
    }
    
    public void setProperties(Properties props){
        this.props = props;
    }
}
