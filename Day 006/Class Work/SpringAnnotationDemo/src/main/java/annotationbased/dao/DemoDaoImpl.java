package annotationbased.dao;

import org.springframework.stereotype.Component;

@Component("mysql")
public class DemoDaoImpl implements DemoDao {

    @Override
    public String getDriver() {
        return "jdbc type4 driver";
    }

    @Override
    public String getDatabase() {
        return "mysql test database";
    }
}