/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author MIlos
 */
@Entity
@Table(name = "artikal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Artikal.findAll", query = "SELECT a FROM Artikal a"),
    @NamedQuery(name = "Artikal.findByIdArtikal", query = "SELECT a FROM Artikal a WHERE a.idArtikal = :idArtikal"),
    @NamedQuery(name = "Artikal.findByNaziv", query = "SELECT a FROM Artikal a WHERE a.naziv = :naziv"),
    @NamedQuery(name = "Artikal.findByTip", query = "SELECT a FROM Artikal a WHERE a.tip = :tip"),
    @NamedQuery(name = "Artikal.findByCena", query = "SELECT a FROM Artikal a WHERE a.cena = :cena")})
public class Artikal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idArtikal")
    private Integer idArtikal;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Naziv")
    private String naziv;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "Tip")
    private String tip;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cena")
    private double cena;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idArtikal")
    private List<Rezervacija> rezervacijaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idArtikal")
    private List<Stanje> stanjeList;

    public Artikal() {
    }

    public Artikal(Integer idArtikal) {
        this.idArtikal = idArtikal;
    }

    public Artikal(Integer idArtikal, String naziv, String tip, double cena) {
        this.idArtikal = idArtikal;
        this.naziv = naziv;
        this.tip = tip;
        this.cena = cena;
    }

    public Integer getIdArtikal() {
        return idArtikal;
    }

    public void setIdArtikal(Integer idArtikal) {
        this.idArtikal = idArtikal;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    @XmlTransient
    public List<Rezervacija> getRezervacijaList() {
        return rezervacijaList;
    }

    public void setRezervacijaList(List<Rezervacija> rezervacijaList) {
        this.rezervacijaList = rezervacijaList;
    }

    @XmlTransient
    public List<Stanje> getStanjeList() {
        return stanjeList;
    }

    public void setStanjeList(List<Stanje> stanjeList) {
        this.stanjeList = stanjeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idArtikal != null ? idArtikal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Artikal)) {
            return false;
        }
        Artikal other = (Artikal) object;
        if ((this.idArtikal == null && other.idArtikal != null) || (this.idArtikal != null && !this.idArtikal.equals(other.idArtikal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Artikal[ idArtikal=" + idArtikal + " ]";
    }
    
}
