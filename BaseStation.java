/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects.wsn1.nodes.nodeImplementations;

import java.awt.Color;
import sinalgo.runtime.Global;

import projects.wsn1.nodes.messages.WsnMsg;
import projects.wsn1.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

/**
 *
 * @author pozza
 */
public class BaseStation extends Node {

    private int control=1; 
    private Node noAnterior;
    private boolean BaseStation = false;
    //Armazena o número de sequencia da Ãºltima mensagem recebida
    private Integer sequenceNumber = 0;

    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof WsnMsg) {
               
                WsnMsg wsnMessage = (WsnMsg) message;
               
                if (wsnMessage.forwardingHop.equals(this)) 
                { 
                	continue;
                } else if (wsnMessage.tipoMsg == 0) {
                	if(this.BaseStation) {
                		continue;
                	}
                	if(noAnterior == null) {
                		noAnterior = inbox.getSender();
                		sequenceNumber = wsnMessage.sequenceID;
                		//System.out.println(wsnMessage.sequenceID);
                		
                		
                	}else if (sequenceNumber < wsnMessage.sequenceID) {
                    //Recurso simples para evitar loop.
                        //Exemplo: Noh A transmite em brodcast. Noh B recebe a
                        //msg e retransmite em broadcast.
                        //Consequentemente, noh A irÃ¡ receber a msg. Sem esse
                        //condicional, noh A iria retransmitir novamente, gerando um loop
                        sequenceNumber = wsnMessage.sequenceID;
                    } else {
                    	continue;
                        
                    }
                }
                else if (wsnMessage.tipoMsg == 1) {
                    //Devemos alterar o campo forwardingHop(da mensagem) para armazenar o
                	//noh que vai encaminhar a mensagem.
                	if(this.BaseStation) {
                		//System.out.println(wsnMessage.origem.ID + " retornou a mensagem " + wsnMessage.sequenceID + " para o nó sink");
                		continue;
                	}else if(!wsnMessage.destino.equals(this)) {
                		continue;
                	}
                    wsnMessage.destino = noAnterior;
                    
                }
                else {
                	continue;
                }
                wsnMessage.forwardingHop=this;
                broadcast(wsnMessage);
            }
        }
    }

    @Override
    public void preStep() {
    
    	// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    	if(!this.BaseStation) {
    		if(Global.currentTime % 100 == 0) {
    			WsnMsg wsnMessage = new WsnMsg(control, this, noAnterior, this, 1);
	       		WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
	       		timer.startRelative(1, this);	       		
	       		control++;
    			
    		}
    		 
    		
    	}
    }
    

    @NodePopupMethod(menuText = "Traçar rotas")
    public void construirRoteamento() {
        this.setColor(Color.GREEN);
        this.BaseStation = true;
        WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0);
        WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
        timer.startRelative(1, this);
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void neighborhoodChange() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void postStep() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    	   	 
    	 
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
