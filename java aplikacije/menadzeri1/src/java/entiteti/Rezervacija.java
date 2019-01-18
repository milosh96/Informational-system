/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MIlos
 */
@Entity
@Table(name = "rezervacija")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rezervacija.findAll", query = "SELECT r FROM Rezervacija r"),
    @NamedQuery(name = "Rezervacija.findByIdRezervacija", query = "SELECT r FROM Rezervacija r WHERE r.idRezervacija = :idRezervacija"),
    @NamedQuery(name = "Rezervacija.findByKontakt", query = "SELECT r FROM Rezervacija r WHERE r.kontakt = :kontakt"),
    @NamedQuery(name = "Rezervacija.findByKolicina", query = "SELECT r FROM Rezervacija r WHERE r.kolicina = :kolicina"),
    @NamedQuery(name = "Rezervacija.findByDatum", query = "SELECT r FROM Rezervacija r WHERE r.datum = :datum")})
public class Rezervacija implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idRezervacija")
    private Integer idRezervacija;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Kontakt")
    private String kontakt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Kolicina")
    private int kolicina;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @JoinColumn(name = "idArtikal", referencedColumnName = "idArtikal")
    @ManyToOne(optional = false)
    private Artikal idArtikal;
    @JoinColumn(name = "idProdavnica", referencedColumnName = "idProdavnica")
    @ManyToOne(optional = false)
    private Prodavnica idProdavnica;

    public Rezervacija() {
    }

    public Rezervacija(Integer idRezervacija) {
        this.idRezervacija = idRezervacija;
    }

    public Rezervacija(Integer idRezervacija, String kontakt, int kolicina, Date datum) {
        this.idRezervacija = idRezervacija;
        this.kontakt = kontakt;
        this.kolicina = kolicina;
        this.datum = datum;
    }

    public Integer getIdRezervacija() {
        return idRezervacija;
    }

    public void setIdRezervacija(Integer idRezervacija) {
        this.idRezervacija = idRezervacija;
    }

    public String getKontakt() {
        return kontakt;
    }

    public void setKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Artikal getIdArtikal() {
        return idArtikal;
    }

    public void setIdArtikal(Artikal idArtikal) {
        this.idArtikal = idArtikal;
    }

    public Prodavnica getIdProdavnica() {
        return idProdavnica;
    }

    public void setIdProdavnica(Prodavnica idProdavnica) {
        this.idProdavnica = idProdavnica;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRezervacija != null ? idRezervacija.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rezervacija)) {
            return false;
        }
        Rezervacija other = (Rezervacija) object;
        if ((this.idRezervacija == null && other.idRezervacija != null) || (this.idRezervacija != null && !this.idRezervacija.equals(other.idRezervacija))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Rezervacija[ idRezervacija=" + idRezervacija + " ]";
    }
    
}
