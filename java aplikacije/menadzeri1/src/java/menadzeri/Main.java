/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menadzeri;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
    public static Topic topicMenadzer;
    @Resource(lookup = "topicProdavac")
    public static Topic topicSend;
    @Resource(lookup = "queueMenadzer")
    public static Queue queueReceive;   
    public static void main(String[] args) {
        // TODO code application logic here
        int idPro=Integer.parseInt(args[0]);
        //public emf i em
        //4 niti za svaku operaciju posebno
        

        MenadzerP menadzerP = new MenadzerP(idPro);
        MenadzerRC menadzerRC = new MenadzerRC(idPro);
        MenadzerRR menadzerRR = new MenadzerRR(idPro);
        menadzerP.start();
        menadzerRC.start();
        menadzerRR.start();
        System.out.println("Menadzer za prodavnicu #"+idPro+" otpoceo rad!");
    }
    
}
