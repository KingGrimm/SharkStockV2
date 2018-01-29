package entitites;

import entitites.Company;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-01-29T00:57:40")
@StaticMetamodel(Share.class)
public class Share_ { 

    public static volatile SingularAttribute<Share, Integer> shareId;
    public static volatile SingularAttribute<Share, Date> probeDate;
    public static volatile SingularAttribute<Share, Company> company;
    public static volatile SingularAttribute<Share, Integer> probeNumber;
    public static volatile SingularAttribute<Share, Double> value;

}