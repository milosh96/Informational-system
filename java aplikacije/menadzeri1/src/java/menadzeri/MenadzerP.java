/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menadzeri;

import entiteti.Prodavnica;
import entiteti.Rezervacija;
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
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author MIlos
 */
public class MenadzerP extends Thread{

    private  int idPro=0;
    private EntityManagerFactory emf;
    private EntityManager em;
    private JMSConsumer consumer;
    private JMSProducer producer;
    private JMSContext context;
    public MenadzerP(int idP){
        idPro=idP;
        context = Main.connectionFactory.createContext();
        String id="Id"+idPro;
        context.setClientID(id+"MP");
        producer = context.createProducer();
        consumer = context.createDurableConsumer(Main.topicMenadzer, "idStore", "idStore = '"+id +"'", false);
        emf=Persistence.createEntityManagerFactory("MenadzeriPU");
        em=emf.createEntityManager();
        //em.setFlushMode(FlushModeType.COMMIT);
    }
    public void run(){
        while(true){
            Message mssg = consumer.receive();
            if (mssg instanceof TextMessage){
                TextMessage txtMessage=(TextMessage) mssg;
                try {
                    String zahtev = txtMessage.getStringProperty("Zahtev");
                    if(zahtev.equals("stanje")){
                        String stanje = txtMessage.getStringProperty("Stanje");
                        int result=(int)Math.random()*10;
                        TextMessage odgovor = context.createTextMessage();
                        String id="Id"+idPro;
                        odgovor.setStringProperty("idStore", id);
                        
                        if(result>7){
                            odgovor.setText("Proizvod je zapakovan");
                        }
                        else{
                            odgovor.setText("Proizvod je otpakovan"); 
                        }
                        producer.send(Main.topicSend, odgovor);
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(MenadzerP.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            else if(mssg instanceof ObjectMessage){
                try {
                    String zahtev = mssg.getStringProperty("Zahtev");
                    if(zahtev.equals("rezervacija")){
                        ObjectMessage oMessage=(ObjectMessage) mssg;
                        //
                        em.getTransaction().begin();
                        int kol=oMessage.getIntProperty("Kolicina");
                        int idProdavnice=oMessage.getIntProperty("UProd");
                        String kontakt=oMessage.getStringProperty("Kontakt");
                        Stanje s=(Stanje)oMessage.getObject();
                        Query upit = em.createQuery("UPDATE Stanje s SET s.kolicina=s.kolicina-:kolicina "
                                +"WHERE s.idProdavnica.idProdavnica=:idPro AND s.idArtikal.idArtikal=:idArt");
                        //PROVERA
                        upit.setParameter("idArt", s.getIdArtikal().getIdArtikal());
                        upit.setParameter("idPro", s.getIdProdavnica().getIdProdavnica());
                        upit.setParameter("kolicina", kol);
                                             
                        int redova=upit.executeUpdate();
                        Query query = em.createQuery("SELECT MAX(r.idRezervacija) FROM Rezervacija r");
                        int rezId;
                    
                            try {
                            rezId = (int)query.getSingleResult();
                            rezId++;
                        } catch (Exception e) {
                            rezId = 1;
                        }
                        Prodavnica idProdavnica = em.find(Prodavnica.class, idProdavnice);  
                        
                        java.util.Date trenutni=new java.util.Date();
                        Rezervacija novaRez=new Rezervacija(rezId, kontakt, kol, trenutni);
                        novaRez.setIdArtikal(s.getIdArtikal());
                        novaRez.setIdProdavnica(idProdavnica);
                        em.persist(novaRez);
                        em.getTransaction().commit();
                        em.clear();
                    }
                } catch (JMSException ex) {
                    Logger.getLogger(MenadzerP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
