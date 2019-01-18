/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menadzeri;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author MIlos
 */
public class MenadzerRC extends Thread{
   
 
    private static int idPro=0;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static JMSConsumer consumer;
    public MenadzerRC(int idP){
        emf=Persistence.createEntityManagerFactory("MenadzeriPU");
        em=emf.createEntityManager();
        idPro=idP;
        JMSContext context = Main.connectionFactory.createContext();
        String id="Id"+idPro;
        context.setClientID(id+"RC");
        consumer = context.createConsumer(Main.queueReceive);
        
    }
    public void run(){
        while(true){
            Message mssg = consumer.receive();
            if (mssg instanceof ObjectMessage){
                String zahtev;
                try {
                    ObjectMessage oMssg=(ObjectMessage)mssg;
                    zahtev = oMssg.getStringProperty("Zahtev");
                    if(zahtev.equals("promena")){
                        //TODO
                        int idA = oMssg.getIntProperty("idA");
                        double cena = oMssg.getDoubleProperty("Cena");
                        promeniCenu(idA,cena);
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(MenadzerRC.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
    private static void promeniCenu(int idA, double cena){
        em.getTransaction().begin();
        Query upit = em.createQuery("UPDATE Artikal a SET a.cena=:cena WHERE"
                + " a.idArtikal=:idA");
        upit.setParameter("cena", cena);
        upit.setParameter("idA", idA);
        upit.executeUpdate();
        em.getTransaction().commit();
    }
}
