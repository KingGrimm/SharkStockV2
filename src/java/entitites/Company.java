/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitites;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Kingu
 */
@Entity
@Table(name = "company")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")
    , @NamedQuery(name = "Company.findByCompanyId", query = "SELECT c FROM Company c WHERE c.companyId = :companyId")
    , @NamedQuery(name = "Company.findByName", query = "SELECT c FROM Company c WHERE c.name = :name")
    , @NamedQuery(name = "Company.findBySymbol", query = "SELECT c FROM Company c WHERE c.symbol = :symbol")})
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "company_id")
    private Short companyId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "symbol")
    private String symbol;
    @JoinColumn(name = "stock", referencedColumnName = "stock_id")
    @ManyToOne(optional = false)
    private Stock stock;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private List<Share> shareList;

    public Company() {
    }

    public Company(Short companyId) {
        this.companyId = companyId;
    }

    public Company(Short companyId, String name, String symbol) {
        this.companyId = companyId;
        this.name = name;
        this.symbol = symbol;
    }

    public Short getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Short companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @XmlTransient
    public List<Share> getShareList() {
        return shareList;
    }

    public void setShareList(List<Share> shareList) {
        this.shareList = shareList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (companyId != null ? companyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Company)) {
            return false;
        }
        Company other = (Company) object;
        if ((this.companyId == null && other.companyId != null) || (this.companyId != null && !this.companyId.equals(other.companyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entitites.Company[ companyId=" + companyId + " ]";
    }
    
}
