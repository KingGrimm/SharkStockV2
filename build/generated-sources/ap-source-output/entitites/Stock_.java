package entitites;

import entitites.Company;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-01-29T00:57:40")
@StaticMetamodel(Stock.class)
public class Stock_ { 

    public static volatile ListAttribute<Stock, Company> companyList;
    public static volatile SingularAttribute<Stock, Short> stockId;
    public static volatile SingularAttribute<Stock, String> name;

}