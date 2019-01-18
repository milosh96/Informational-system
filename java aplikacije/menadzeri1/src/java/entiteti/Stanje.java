/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MIlos
 */
@Entity
@Table(name = "stanje")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Stanje.findAll", query = "SELECT s FROM Stanje s"),
    @NamedQuery(name = "Stanje.findByIdStanja", query = "SELECT s FROM Stanje s WHERE s.idStanja = :idStanja"),
    @NamedQuery(name = "Stanje.findByKolicina", query = "SELECT s FROM Stanje s WHERE s.kolicina = :kolicina")})
public class Stanje implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idStanja")
    private Integer idStanja;
    @Basic(optional = false)
    @NotNull
    @Column(name = "kolicina")
    private int kolicina;
    @JoinColumn(name = "idArtikal", referencedColumnName = "idArtikal")
    @ManyToOne(optional = false)
    private Artikal idArtikal;
    @JoinColumn(name = "idProdavnica", referencedColumnName = "idProdavnica")
    @ManyToOne(optional = false)
    private Prodavnica idProdavnica;

    public Stanje() {
    }

    public Stanje(Integer idStanja) {
        this.idStanja = idStanja;
    }

    public Stanje(Integer idStanja, int kolicina) {
        this.idStanja = idStanja;
        this.kolicina = kolicina;
    }

    public Integer getIdStanja() {
        return idStanja;
    }

    public void setIdStanja(Integer idStanja) {
        this.idStanja = idStanja;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
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
        hash += (idStanja != null ? idStanja.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stanje)) {
            return false;
        }
        Stanje other = (Stanje) object;
        if ((this.idStanja == null && other.idStanja != null) || (this.idStanja != null && !this.idStanja.equals(other.idStanja))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Stanje[ idStanja=" + idStanja + " ]";
    }
    
}
