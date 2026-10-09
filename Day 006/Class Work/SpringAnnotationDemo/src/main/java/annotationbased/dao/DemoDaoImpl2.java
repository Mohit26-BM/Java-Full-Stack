package annotationbased.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("oracle")
@Primary
public class DemoDaoImpl2 implements DemoDao {

    @Override
    public String getDriver() {
        return "Type2 driver";
    }

    @Override
    public String getDatabase() {
        return "Oracle Driver";
    }
}