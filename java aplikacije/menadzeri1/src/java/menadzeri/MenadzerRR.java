/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menadzeri;

import entiteti.Artikal;
import entiteti.Prodavnica;
import entiteti.Stanje;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author MIlos
 */
public class MenadzerRR extends Thread{
    private static int idPro=0;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static JMSConsumer consumer;
    public MenadzerRR(int idP){
        emf=Persistence.createEntityManagerFactory("MenadzeriPU");
        em=emf.createEntityManager();
        idPro=idP;
        JMSContext context = Main.connectionFactory.createContext();
        String id="Id"+idPro;
        context.setClientID(id+"RR");
        consumer = context.createDurableConsumer(Main.topicMenadzer, "idStore", "idStore = '"+id +"'", false);
        
    }
    public void run(){
        while(true){
            Message mssg = consumer.receive();
            if (mssg instanceof ObjectMessage){
                String zahtev;
                try {
                    ObjectMessage oMssg=(ObjectMessage)mssg;
                    zahtev = oMssg.getStringProperty("Zahtev");
                    if(zahtev.equals("novi")){
                        //TODO
                        int idA = oMssg.getIntProperty("idA");
                        int idP = oMssg.getIntProperty("idP");
                        int dodaj = oMssg.getIntProperty("Dodaj");
                        promeniStanje(idA,idP,dodaj);
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(MenadzerRC.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
    private static void promeniStanje(int idA,int idP,int dodaj){
        em.getTransaction().begin();
        
        Query upit = em.createQuery("UPDATE Stanje s SET s.kolicina=s.kolicina+:dodaj"
                + " WHERE s.idArtikal.idArtikal=:idA AND s.idProdavnica.idProdavnica=:idP");
        upit.setParameter("idA", idA);
        upit.setParameter("idP", idP);
        upit.setParameter("dodaj", dodaj);
        int redovi=upit.executeUpdate();
        if(redovi==0){
            Stanje stanje = new Stanje(1, dodaj);
            stanje.setIdArtikal(em.find(Artikal.class, idA));
            stanje.setIdProdavnica(em.find(Prodavnica.class,idP));
            em.persist(stanje);
        }
        em.getTransaction().commit();
        em.clear();
    }
}
