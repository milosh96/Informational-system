/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radnik;

import entiteti.Artikal;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author MIlos
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static javax.jms.ConnectionFactory connectionFactory;
    @Resource(lookup = "topicMenadzer")
    private static Topic topicM;
    @Resource(lookup = "queueMenadzer")
    private static Queue queueM; 
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static JMSProducer producer;
    private static JMSContext context;
    public static void main(String[] args) {
        // TODO code application logic here
        int idR=Integer.parseInt(args[0]);
        emf=Persistence.createEntityManagerFactory("RadnikPU");
        em=emf.createEntityManager();
        context = connectionFactory.createContext();
        String iidR="Idr"+idR;
        context.setClientID(iidR);
        producer=context.createProducer();
        
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("1. Promena cene.");
            System.out.println("2.Salji artikle");
            System.out.println("3.Izlaz.");
            int izbor = scanner.nextInt();
            if(izbor==1){
                //Cena na queue se salje
                System.out.println("Unesite idArtikla:");
                int idA=scanner.nextInt();
                //potencijalno obavestavanje o gresci
                System.out.println("Unesite novu cenu:");
                double cena=scanner.nextDouble();
                ObjectMessage poruka = context.createObjectMessage();
                Artikal artikal = em.find(Artikal.class,idA);
                if(artikal==null) break;
                em.getTransaction().begin();
                artikal.setCena(cena);
                em.getTransaction().commit();
                try {
                    poruka.setObject(artikal);
                    poruka.setStringProperty("Zahtev", "promena");
                    poruka.setDoubleProperty("Cena", cena);
                    poruka.setIntProperty("idA", idA);
                    producer.send(queueM, poruka);
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            else if(izbor==2){
                //nova roba na topic
                System.out.println("Unesite idArtikla, idProdavnice i novu kolicinu za proizvodnju");
                int idA=scanner.nextInt();
                int idP=scanner.nextInt();
                int zaProizvodnju=scanner.nextInt();
                Artikal artikal = em.find(Artikal.class, idA);
                if(artikal==null) break;
                int vreme=artikal.getVreme();
                try {
                    Thread.sleep(vreme*1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ObjectMessage poruka = context.createObjectMessage();
                String idpro="Id"+idP;
                try {
                    poruka.setStringProperty("idStore", idpro);
                    poruka.setIntProperty("idA", artikal.getIdArtikal());
                    poruka.setIntProperty("idP", idP);
                    poruka.setObject(artikal);
                    poruka.setIntProperty("Dodaj", zaProizvodnju);
                    poruka.setStringProperty("Zahtev", "novi");
                    producer.send(topicM, poruka);
                } catch (JMSException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                System.exit(1);
            }
        }
    }
    
}
