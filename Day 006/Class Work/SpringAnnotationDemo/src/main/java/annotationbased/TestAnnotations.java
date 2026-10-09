package annotationbased;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import annotationbased.config.AnnnotConfigs;
import annotationbased.service.DemoService;

public class TestAnnotations {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AnnnotConfigs.class);

        DemoService service = context.getBean(DemoService.class);

        System.out.println(service.getDriver());
        System.out.println(service.getDatabase());

        context.close();
    }
}