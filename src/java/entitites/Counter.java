/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitites;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kingu
 */
@Entity
@Table(name = "counter")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Counter.findAll", query = "SELECT c FROM Counter c")
    , @NamedQuery(name = "Counter.findByCounterId", query = "SELECT c FROM Counter c WHERE c.counterId = :counterId")
    , @NamedQuery(name = "Counter.findByValue", query = "SELECT c FROM Counter c WHERE c.value = :value")})
public class Counter implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "counter_id")
    private Integer counterId;
    @Column(name = "value")
    private Integer value;

    public Counter() {
    }

    public Counter(Integer counterId) {
        this.counterId = counterId;
    }

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (counterId != null ? counterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Counter)) {
            return false;
        }
        Counter other = (Counter) object;
        if ((this.counterId == null && other.counterId != null) || (this.counterId != null && !this.counterId.equals(other.counterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entitites.Counter[ counterId=" + counterId + " ]";
    }
    
}
