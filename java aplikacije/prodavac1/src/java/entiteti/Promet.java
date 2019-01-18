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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MIlos
 */
@Entity
@Table(name = "promet")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promet.findAll", query = "SELECT p FROM Promet p"),
    @NamedQuery(name = "Promet.findByIdPromet", query = "SELECT p FROM Promet p WHERE p.idPromet = :idPromet"),
    @NamedQuery(name = "Promet.findByDatum", query = "SELECT p FROM Promet p WHERE p.datum = :datum"),
    @NamedQuery(name = "Promet.findByPromet", query = "SELECT p FROM Promet p WHERE p.promet = :promet")})
public class Promet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPromet")
    private Integer idPromet;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Datum")
    @Temporal(TemporalType.DATE)
    private Date datum;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Promet")
    private double promet;
    @JoinColumn(name = "idProdavnica", referencedColumnName = "idProdavnica")
    @ManyToOne(optional = false)
    private Prodavnica idProdavnica;

    public Promet() {
    }

    public Promet(Integer idPromet) {
        this.idPromet = idPromet;
    }

    public Promet(Integer idPromet, Date datum, double promet) {
        this.idPromet = idPromet;
        this.datum = datum;
        this.promet = promet;
    }

    public Integer getIdPromet() {
        return idPromet;
    }

    public void setIdPromet(Integer idPromet) {
        this.idPromet = idPromet;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public double getPromet() {
        return promet;
    }

    public void setPromet(double promet) {
        this.promet = promet;
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
        hash += (idPromet != null ? idPromet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Promet)) {
            return false;
        }
        Promet other = (Promet) object;
        if ((this.idPromet == null && other.idPromet != null) || (this.idPromet != null && !this.idPromet.equals(other.idPromet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Promet[ idPromet=" + idPromet + " ]";
    }
    
}
