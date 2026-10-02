public class StringMethodsDemo {

    public static void main(String[] args) {

        String str = "Hello Java World";
        String str2 = "hello java world";

        // 1. length()
        System.out.println("1. length(): " + str.length());

        // 2. charAt()
        System.out.println("2. charAt(): " + str.charAt(1));

        // 3. substring()
        System.out.println("3. substring(): " + str.substring(6));

        // 4. substring(start, end)
        System.out.println("4. substring(start, end): " + str.substring(0, 5));

        // 5. equals()
        System.out.println("5. equals(): " + str.equals(str2));

        // 6. equalsIgnoreCase()
        System.out.println("6. equalsIgnoreCase(): " + str.equalsIgnoreCase(str2));

        // 7. compareTo()
        System.out.println("7. compareTo(): " + str.compareTo(str2));

        // 8. compareToIgnoreCase()
        System.out.println("8. compareToIgnoreCase(): "
                + str.compareToIgnoreCase(str2));

        // 9. concat()
        System.out.println("9. concat(): " + str.concat(" Programming"));

        // 10. contains()
        System.out.println("10. contains(): " + str.contains("Java"));

        // 11. startsWith()
        System.out.println("11. startsWith(): " + str.startsWith("Hello"));

        // 12. endsWith()
        System.out.println("12. endsWith(): " + str.endsWith("World"));

        // 13. indexOf()
        System.out.println("13. indexOf(): " + str.indexOf("Java"));

        // 14. lastIndexOf()
        System.out.println("14. lastIndexOf(): " + str.lastIndexOf("o"));

        // 15. toUpperCase()
        System.out.println("15. toUpperCase(): " + str.toUpperCase());

        // 16. toLowerCase()
        System.out.println("16. toLowerCase(): " + str.toLowerCase());

        // 17. trim()
        String str3 = "   Hello Java   ";
        System.out.println("17. trim(): " + str3.trim());

        // 18. replace()
        System.out.println("18. replace(): " + str.replace("Java", "Python"));

        // 19. replaceAll()
        System.out.println("19. replaceAll(): "
                + str.replaceAll("Java", "Python"));

        // 20. isEmpty()
        String emptyString = "";
        System.out.println("20. isEmpty(): " + emptyString.isEmpty());

        // 21. isBlank()
       /* String blankString = "   ";
        System.out.println("21. isBlank(): " + blankString.isBlank()); */

        // 22. split()
        String names = "Rahul,Amit,Priya";
        String[] nameArray = names.split(",");

        System.out.println("22. split():");
        for (String name : nameArray) {
            System.out.println(name);
        }

        // 23. join()
        String joinedNames = String.join("-", "Rahul", "Amit", "Priya");
        System.out.println("23. join(): " + joinedNames);

        // 24. repeat()
        /*System.out.println("24. repeat(): " + "Java ".repeat(3));*/

        // 25. strip()
        /*String str4 = "   Hello Java   ";
        System.out.println("25. strip(): " + str4.strip());*/
    }
}