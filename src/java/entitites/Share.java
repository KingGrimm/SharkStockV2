/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitites;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kingu
 */
@Entity
@Table(name = "share")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Share.findAll", query = "SELECT s FROM Share s")
    , @NamedQuery(name = "Share.findByShareId", query = "SELECT s FROM Share s WHERE s.shareId = :shareId")
    , @NamedQuery(name = "Share.findByValue", query = "SELECT s FROM Share s WHERE s.value = :value")
    , @NamedQuery(name = "Share.findByProbeDate", query = "SELECT s FROM Share s WHERE s.probeDate = :probeDate")
    , @NamedQuery(name = "Share.findByProbeNumber", query = "SELECT s FROM Share s WHERE s.probeNumber = :probeNumber")})
public class Share implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "share_id")
    private Integer shareId;
    @Basic(optional = false)
    @Column(name = "value")
    private double value;
    @Basic(optional = false)
    @Column(name = "probe_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date probeDate;
    @Basic(optional = false)
    @Column(name = "probe_number")
    private int probeNumber;
    @JoinColumn(name = "company", referencedColumnName = "company_id")
    @ManyToOne(optional = false)
    private Company company;

    public Share() {
    }

    public Share(Integer shareId) {
        this.shareId = shareId;
    }

    public Share(Integer shareId, double value, Date probeDate, int probeNumber) {
        this.shareId = shareId;
        this.value = value;
        this.probeDate = probeDate;
        this.probeNumber = probeNumber;
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getProbeDate() {
        return probeDate;
    }

    public void setProbeDate(Date probeDate) {
        this.probeDate = probeDate;
    }

    public int getProbeNumber() {
        return probeNumber;
    }

    public void setProbeNumber(int probeNumber) {
        this.probeNumber = probeNumber;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (shareId != null ? shareId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Share)) {
            return false;
        }
        Share other = (Share) object;
        if ((this.shareId == null && other.shareId != null) || (this.shareId != null && !this.shareId.equals(other.shareId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entitites.Share[ shareId=" + shareId + " ]";
    }
    
}
