package entitites;

import entitites.Share;
import entitites.Stock;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-01-29T00:57:40")
@StaticMetamodel(Company.class)
public class Company_ { 

    public static volatile SingularAttribute<Company, String> symbol;
    public static volatile SingularAttribute<Company, Short> companyId;
    public static volatile SingularAttribute<Company, String> name;
    public static volatile SingularAttribute<Company, Stock> stock;
    public static volatile ListAttribute<Company, Share> shareList;

}