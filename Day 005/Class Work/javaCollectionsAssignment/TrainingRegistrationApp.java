package javaCollectionsAssignment;

import java.util.Scanner;
import java.util.Set;

public class TrainingRegistrationApp {

    public static void main(String[] args) {

        RegistrationManager registrationManager =
                new RegistrationManager();

        TechnologyManager technologyManager =
                new TechnologyManager();

        ParticipantManager participantManager =
                new ParticipantManager();

        registrationManager.addRegistration("Praveen");
        registrationManager.addRegistration("Ravi");
        registrationManager.addRegistration("Kiran");
        registrationManager.addRegistration("Anil");
        registrationManager.addRegistration("Ravi");

        technologyManager.addTechnology("Java");
        technologyManager.addTechnology("Spring");
        technologyManager.addTechnology("Docker");
        technologyManager.addTechnology("Python");

        participantManager.addParticipant("Praveen", 2);
        participantManager.addParticipant("Ravi", 2);
        participantManager.addParticipant("Kiran", 3);
        participantManager.addParticipant("Anil", 2);


        registrationManager.displayRegistrations();

        System.out.println();


        System.out.println("Unique Participants:");

        Set<String> uniqueParticipants =
                registrationManager.getUniqueParticipants();

        for (String participant : uniqueParticipants) {
            System.out.println(participant);
        }

        System.out.println();

        technologyManager.displayTechnologies();

        System.out.println();

        participantManager.displayParticipantTopics();

        System.out.println();

        System.out.println("========== TRAINING REGISTRATION SUMMARY ==========");
        System.out.println();

        System.out.println(
                "Total Registrations : "
                + registrationManager.getTotalRegistrations()
        );

        System.out.println(
                "Unique Participants : "
                + uniqueParticipants.size()
        );

        System.out.println();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter participant name to search: ");
        String name = scanner.nextLine();

        if (participantManager.containsParticipant(name)) {

            int topicCount =
                    participantManager.getTopicCount(name);

            System.out.println(
                    name + " selected " + topicCount + " topics."
            );

        } else {

            System.out.println("Participant not found.");
        }

        scanner.close();
    }
}