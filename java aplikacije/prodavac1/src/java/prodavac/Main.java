/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prodavac;

import entiteti.Artikal;
import entiteti.Prodavnica;
import entiteti.Promet;
import entiteti.Rezervacija;
import entiteti.Stanje;
import static java.lang.System.exit;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
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
import javax.persistence.Persistence;
import javax.persistence.Query;
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
    public static javax.jms.ConnectionFactory connectionFactory;
    
    @Resource(lookup = "topicMenadzer")
    public static Topic topicSend;
    
    @Resource(lookup = "topicProdavac")
    public static Topic topicReceive;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static int idPro=0;
    private static int kol=0;
    private static int index=0;
    private static int option=0;
    private static String tipA;
    private static String nazivA;
    private static String kontaktR;
    private static JMSContext context;
    private static JMSProducer producer;
    private static JMSConsumer consumer;
    public static void main(String[] args) {
        // TODO code application logic here
        idPro=Integer.parseInt(args[0]);
        if (idPro<0) exit(1);
        context = connectionFactory.createContext();
        String id="Id"+idPro;
        context.setClientID(id);
        producer = context.createProducer();
        consumer = context.createDurableConsumer(topicReceive, "idStore", "idStore = '"+id +"'", false);
        
        //za bazu
        emf=Persistence.createEntityManagerFactory("ProdavacPU");
        em=emf.createEntityManager();
        Scanner scanner = new Scanner(System.in);
        while(true){
            updateRez(idPro);
            System.out.println("1.Kupovina artikla.");
            System.out.println("2.Kupovina rezervisanog artikla");
            System.out.println("3.Izlaz.");
            int opt=scanner.nextInt();
            //lista artikala=return value
            List<Stanje> artikli=new ArrayList<>();
            switch(opt){
                case 1:
                    artikli=kupovina();
                    break;
                    
                case 2:
                    artikli=kupovinaRez();
                    break;
                
                default:
                    System.exit(1);
            }
            
            if((opt==1)&&(artikli.size()!=0)){
                ispisArtikala(artikli);
                System.out.println("Unesite index artikla(1..n)");
                index=scanner.nextInt();
                if ((index<=0)||(index>artikli.size())){
                        System.out.println("Los index odabran!");
                }
                else{
                    System.out.println("Provera stanja artikla?[Y|N]");
                    char zahtev=scanner.next().charAt(0);
                    if(zahtev=='Y'){
                        System.out.println("Unesite zeljeno stanje, zapakovan|otpakovan?");
                        String stanje=scanner.next();
                        //slanje zahteva menadzeru ove prodavnice
                        //kako kapsulirati zahtev?
                        TextMessage porukaStanje = context.createTextMessage();
                        try {
                            porukaStanje.setStringProperty("idStore", id);
                            porukaStanje.setStringProperty("Zahtev", "stanje");
                            porukaStanje.setStringProperty("Stanje", stanje);
                            porukaStanje.setIntProperty("Zeli", kol);
                            porukaStanje.setIntProperty("Ukupno", artikli.get(index-1).getKolicina());
                            producer.send(topicSend, porukaStanje);
                            Message odgovor = consumer.receive(10000);
                            if(odgovor==null){
                                System.out.println("Menadzer nije trenutno aktiviran. Kupovina nije registrovana.");
                            }
                            else if(odgovor instanceof TextMessage){
                                TextMessage txtOdgovor=(TextMessage) odgovor;
                                System.out.println("Odgovor menadzera je: "+txtOdgovor.getText());
                                System.out.println("Da li zelite kupovinu?[Y|N]");
                                char finKup=scanner.next().charAt(0);
                                if(finKup=='Y'){
                                    zabeleziKupovinu(artikli, opt);
                                }
                            }
                        } catch (JMSException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else{
                         zabeleziKupovinu(artikli,opt);
                         //ako je sa rezervacijom obrisi taj red, 2 funkcije trebaju
                    }
                }
            }
            else if(opt==1){
                System.out.println("Nema rezultata za datu pretragu! Da li zelite da proverimo u drugim prodavnicama?[Y|N]");
               /*
                ponovo pretraga(proverim atribut option)
                unesi index
                odatle dohvati idProdavnice
                stavi na topic
                */
               char zeli=scanner.next().charAt(0);
               if(zeli=='Y'){
                   if(option==1){
                       //po tipu u drugim prod
                       artikli=pretragaSveT();
                   }
                   else if(option==2){
                       //po nazivu
                       artikli=pretragaSveN();
                   }
                   
                   if(artikli.size()!=0){
                       ispisArtikala(artikli);
                       System.out.println("Unesite index artikla(1..n)");
                       index=scanner.nextInt();
                       if ((index<=0)||(index>artikli.size())){
                       System.out.println("Los index odabran!");
                       }
                       else{
                       int zahtevZa=artikli.get(index-1).getIdProdavnica().getIdProdavnica();
                       System.out.println("Provera stanja artikla?[Y|N]");
                       char zahtev=scanner.next().charAt(0);
                        if(zahtev=='Y'){
                            System.out.println("Unesite zeljeno stanje, zapakovan|otpakovan?");
                            String stanje=scanner.next();
                            //slanje zahteva menadzeru ove prodavnice
                            //kako kapsulirati zahtev?
                            TextMessage porukaStanje = context.createTextMessage();
                            try {
                                String idZa="Id"+zahtevZa;
                                porukaStanje.setStringProperty("idStore", idZa);
                                porukaStanje.setStringProperty("Zahtev", "stanje");
                                porukaStanje.setStringProperty("Stanje", stanje);
                                porukaStanje.setIntProperty("Zeli", kol);
                                porukaStanje.setIntProperty("Ukupno", artikli.get(index-1).getKolicina());
                                producer.send(topicSend, porukaStanje);
                                Message odgovor = consumer.receive(10000);
                                if(odgovor==null){
                                    System.out.println("Menadzer nije trenutno aktiviran. Kupovina nije registrovana.");
                                }
                                else if(odgovor instanceof TextMessage){
                                    TextMessage txtOdgovor=(TextMessage) odgovor;
                                    System.out.println("Odgovor menadzera je: "+txtOdgovor.getText());
                                    System.out.println("Da li zelite kupovinu?[Y|N]");
                                    char finKup=scanner.next().charAt(0);
                                    if(finKup=='Y'){
                                        rezervisi(artikli,index);
                                    }
                                }
                            } catch (JMSException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        else{
                             //napravi rezevaciju 2 opcije
                             rezervisi(artikli,index);
                        }   
                       }
                   }
                   
               }
               
            }
        }
    }
    private static List<Stanje> pretragaSveT(){
        TypedQuery<Stanje> upit = em.createQuery("SELECT s FROM Stanje s "
                + "WHERE s.idArtikal.tip=:tip"
                +" AND s.kolicina>=:kolicina", Stanje.class);
        upit.setParameter("tip", tipA);
        upit.setParameter("kolicina", kol);
        //exception?--returns null if empty
        return upit.getResultList();
    }
    private static List<Stanje> pretragaSveN(){
        TypedQuery<Stanje> upit = em.createQuery("SELECT s FROM Stanje s "
                + "WHERE s.idArtikal.naziv=:naziv"
               + " AND s.kolicina>=:kolicina", Stanje.class);
        upit.setParameter("naziv", nazivA);
        upit.setParameter("kolicina", kol);
        return upit.getResultList();
    }
    private static void rezervisi(List<Stanje> artikli, int index){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Da li zelite rezervaciju u toj prodavnici ili ovde?[1|2]");
        int gde=scanner.nextInt();
        //smanji stanje i rezervisi
        int idProdavnice=0;
        if (gde==1){
            idProdavnice=artikli.get(index-1).getIdProdavnica().getIdProdavnica();
        }
        else {
            idProdavnice=idPro;
        }
        System.out.println("Unesite kontakt:");
        String kontakt = scanner.next();
        //salji menadzeru
        ObjectMessage objectMessage = context.createObjectMessage();
        try {
            objectMessage.setObject(artikli.get(index-1));
            String id="Id"+idProdavnice;
            objectMessage.setStringProperty("idStore", id);
            objectMessage.setStringProperty("Zahtev", "rezervacija");
            objectMessage.setStringProperty("Kontakt", kontakt);
            objectMessage.setIntProperty("Kolicina", kol);
            objectMessage.setIntProperty("UProd", idProdavnice);
            producer.send(topicSend, objectMessage);
            System.out.println("Vas zahtev je poslat!");
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
    }
    private static void zabeleziKupovinu(List<Stanje> artikli, int rez){
        /*
        dodamo u promet danasnji-2 opcije prva stvar danas ili += uradjeno
        smanjimo u stanju kolicinu
         */
        //ako je rez 2 onda ne menjamo stanje, ali zato brisemo taj red!!!
        java.util.Date today = new java.util.Date();
        em.getTransaction().begin();
        double iznos=kol*artikli.get(index-1).getIdArtikal().getCena();
        Query upitPromet = em.createQuery("UPDATE Promet as p SET p.promet=p.promet+:iznos"
                + " WHERE p.idProdavnica.idProdavnica=:idPro AND p.datum=:today");
        upitPromet.setParameter("today", today);
        upitPromet.setParameter("idPro", idPro);
        upitPromet.setParameter("iznos", iznos);
        int redova = upitPromet.executeUpdate();
        if (redova==0){
             Query query = em.createQuery("SELECT MAX(p.idPromet) FROM Promet p");
                    int promId;
                    
                    try {
                        promId = (int)query.getSingleResult();
                        promId++;
                    } catch (Exception e) {
                        promId = 1;
                    }
                Promet promet=new Promet();
                promet.setIdPromet(promId);
            promet.setDatum(today);
            promet.setIdProdavnica(em.find(Prodavnica.class,idPro));
            promet.setPromet(iznos);
            em.persist(promet);      
        } 
        if (rez==1){
            //smanjimo stanje
            Query upitStanje = em.createQuery("UPDATE Stanje as s SET s.kolicina=s.kolicina-:kolicina "
                    + "WHERE s.idArtikal.idArtikal=:idArt AND s.idProdavnica.idProdavnica=:idPro");
            upitStanje.setParameter("idPro", idPro);
            upitStanje.setParameter("idArt", artikli.get(index-1).getIdArtikal().getIdArtikal());
            upitStanje.setParameter("kolicina", kol);
            upitStanje.executeUpdate();
 
        }
        else if(rez==2){
            TypedQuery<Rezervacija> upitRez = em.createQuery("SELECT r FROM Rezervacija r"
                    + " WHERE r.kontakt=:kontakt AND r.idProdavnica.idProdavnica=:idPro"
                    + " AND r.idArtikal.idArtikal=:idArt", Rezervacija.class);
            upitRez.setParameter("idArt", artikli.get(index-1).getIdArtikal().getIdArtikal());
            upitRez.setParameter("idPro", idPro);
            upitRez.setParameter("kontakt", kontaktR);
            Rezervacija rezervacija = upitRez.getSingleResult();
            if(rezervacija!=null) em.remove(rezervacija);
        }
        em.getTransaction().commit();
        em.clear();
    }
    private static List<Stanje> kupovina(){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Unos kolicine: ");
             int kolicina=scanner.nextInt();
             kol=kolicina;
             System.out.println("1.Pretraga po tipu.");
             System.out.println("2.Pretraga po nazivu.");       
             int opt=scanner.nextInt();
             String tip;
             String naziv;
             if(opt==1){
                 System.out.println("Unos tipa: ");
                 tip=scanner.next();
                 option=1;
                 tipA=tip;
                 return pretragaTip(tip,kolicina);
             }
             else{
                 System.out.println("Unos naziva: ");
                 naziv=scanner.next();
                 option=2;
                 nazivA=naziv;
                 return pretragaNaziv(naziv,kolicina);
             }
             
    }
    private static List<Stanje> pretragaTip(String tip, int kolicina){
        TypedQuery<Stanje> upit = em.createQuery("SELECT s FROM Stanje s "
                + "WHERE s.idProdavnica.idProdavnica=:idPro AND s.idArtikal.tip=:tip"
                +" AND s.kolicina>=:kolicina", Stanje.class);
        upit.setParameter("tip", tip);
        upit.setParameter("idPro",idPro);
        upit.setParameter("kolicina", kolicina);
        //exception?--returns null if empty
        return upit.getResultList();
    }
    private static List<Stanje> pretragaNaziv(String naziv, int kolicina){
       TypedQuery<Stanje> upit = em.createQuery("SELECT s FROM Stanje s "
                + "WHERE s.idProdavnica.idProdavnica=:idPro AND s.idArtikal.naziv=:naziv"
               + " AND s.kolicina>=:kolicina", Stanje.class);
        upit.setParameter("naziv", naziv);
        upit.setParameter("idPro",idPro);
        upit.setParameter("kolicina", kolicina);
        return upit.getResultList();
    }
    private static List<Stanje> kupovinaRez(){
        System.out.print("Unos rezervacije(kontakt): ");
        Scanner scanner = new Scanner(System.in);
        String kontakt = scanner.next();
        kontaktR=kontakt;
        //pretraga po tabeli Rez(znamo da je validan-uvek radimo update pre), trazimo za kontakt i ova prod!!!
        TypedQuery<Rezervacija> upitRez = em.createQuery("SELECT r FROM Rezervacija r"
                + " WHERE r.kontakt=:kontakt AND r.idProdavnica.idProdavnica=:idPro", Rezervacija.class);
        upitRez.setParameter("idPro", idPro);
        upitRez.setParameter("kontakt", kontakt);
        //POPRAVI
        /*
        Rezervacija rezervacija = upitRez.getSingleResult();
        if(rezervacija==null)   return null;
        TypedQuery<Stanje> upitStanje = em.createQuery("SELECT s FROM Stanje s WHERE s.artikal.idArtikal=:idArt"
        + " AND s.prodavnica.idProdavnica=:idPro", Stanje.class);
        upitStanje.setParameter("idArt", rezervacija.getIdArtikal().getIdArtikal());
        upitStanje.setParameter("idPro", rezervacija.getIdProdavnica().getIdProdavnica());
        return upitStanje.getResultList();*/
        List<Rezervacija> rezervacije = upitRez.getResultList();
        java.util.Date today = new java.util.Date();
        for(Rezervacija r:rezervacije){
            //izbrisi i promeni promet
            em.getTransaction().begin();
            double iznos=r.getKolicina()*r.getIdArtikal().getCena();
                Query upitPromet = em.createQuery("UPDATE Promet as p SET p.promet=p.promet+:iznos"
                    + " WHERE p.idProdavnica.idProdavnica=:idPro AND p.datum=:today");
            upitPromet.setParameter("today", today);
            upitPromet.setParameter("idPro", idPro);
            upitPromet.setParameter("iznos", iznos);
            int redova = upitPromet.executeUpdate();
            if (redova==0){
                Query query = em.createQuery("SELECT MAX(p.idPromet) FROM Promet p");
                    int promId;
                    
                    try {
                        promId = (int)query.getSingleResult();
                        promId++;
                    } catch (Exception e) {
                        promId = 1;
                    }
                Promet promet=new Promet();
                promet.setIdPromet(promId);
                promet.setDatum(today);
                promet.setIdProdavnica(em.find(Prodavnica.class,idPro));
                promet.setPromet(iznos);
                em.persist(promet);      
            } 
            System.out.println("Obavljena kupovina za '"+r.getKontakt()+"' i artikal: "+r.getIdArtikal().getNaziv());
            r=em.merge(r);
            em.remove(r);
            em.getTransaction().commit();
            em.clear();
        }
        return null;
    }
    private static void ispisArtikala(List<Stanje> artikli){
        for(Stanje a:artikli){
        System.out.println(a.getIdArtikal().getIdArtikal()+". "+a.getIdArtikal().getNaziv()+"   "
                +a.getIdArtikal().getTip()+"  "+a.getIdArtikal().getCena());
    }
    }
    private static void updateRez(int idProdavnica){
        //currDate
        TypedQuery<Rezervacija> upit = em.createQuery("SELECT r FROM Rezervacija r WHERE "
                + "r.idProdavnica.idProdavnica=:idProdavnica", Rezervacija.class);
       upit.setParameter("idProdavnica", idProdavnica);
        List<Rezervacija> resultList = upit.getResultList();
        java.util.Date currDate = new java.util.Date();
        for(Rezervacija r:resultList){
           long dani=porediDatume(currDate,r.getDatum());
           if(dani>2){
            //update kolicinu i izbrisi iz tabele REzervacije
            int dodajKol=r.getKolicina();
             em.getTransaction().begin();
               Query upitUpdate = em.createQuery("UPDATE Stanje as s SET s.kolicina=s.kolicina+:dodajKol"
                       +" WHERE s.idArtikal.idArtikal=:idArtikal AND s.idProdavnica.idProdavnica=:idProdavnica");
               upitUpdate.setParameter("idArtikal", r.getIdArtikal().getIdArtikal());
               upitUpdate.setParameter("idProdavnica", r.getIdProdavnica().getIdProdavnica());
               int executeUpdate = upitUpdate.executeUpdate();
               //PROMENI!!!!     
               em.remove(r);
                em.getTransaction().commit();
                /*
               Query upitDelete = em.createQuery("DELETE FROM Rezervacija as z WHERE z.idRezervacija=:idRez");
               upitDelete.setParameter("idRez", r.getIdRezervacija());
               int executeUpdate1 = upitDelete.executeUpdate();*/
           }
        }
      
    }
    public static long porediDatume(java.util.Date today, java.util.Date res){
        long razlika=today.getTime()-res.getTime();
        return TimeUnit.DAYS.convert(razlika, TimeUnit.MILLISECONDS);
    }
}
