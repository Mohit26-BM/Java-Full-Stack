import java.util.function.Supplier;

public class DefaultConfigurationSupplie {

    /*
    Method returns the given value.
    If the value is empty, it returns the default value
    provided by the Supplier.
    */

    static String getValue(String value, Supplier<String> defaultValue) {

        if (value.isEmpty()) {
            return defaultValue.get();
        }

        return value;
    }


    public static void main(String[] args) {

        /*
        Supplier for default application name
        */

        Supplier<String> defaultApplicationName =
                () -> "Banking Application";


        /*
        Supplier for default environment
        */

        Supplier<String> defaultEnvironment =
                () -> "DEV";


        /*
        Supplier for default database URL
        */

        Supplier<String> defaultDatabaseUrl =
                () -> "jdbc:mysql://localhost:3306/bankdb";


        /*
        Values provided by the user/application
        */

        String applicationName = "";

        String environment = "PROD";

        String databaseUrl = "";


        /*
        getValue() uses the supplied default
        when the value is empty.
        */

        System.out.println("Application Name: "
                + getValue(applicationName, defaultApplicationName));

        System.out.println("Environment: "
                + getValue(environment, defaultEnvironment));

        System.out.println("Database URL: "
                + getValue(databaseUrl, defaultDatabaseUrl));
    }
}