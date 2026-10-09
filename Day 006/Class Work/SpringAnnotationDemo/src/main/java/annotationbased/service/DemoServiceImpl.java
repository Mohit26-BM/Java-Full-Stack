package annotationbased.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import annotationbased.dao.DemoDao;

@Component
public class DemoServiceImpl implements DemoService {

    @Autowired
    DemoDao dao;

    @Override
    public String getDriver() {
        return dao.getDriver();
    }

    @Override
    public String getDatabase() {
        return dao.getDatabase();
    }
}