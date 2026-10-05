package javaCollectionsAssignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RegistrationManager {

    private ArrayList<String> registrations;

    public RegistrationManager() {
        registrations = new ArrayList<>();
    }

    public void addRegistration(String participantName) {
        registrations.add(participantName);
    }

    public void displayRegistrations() {
        System.out.println("Registrations:");

        for (String participant : registrations) {
            System.out.println(participant);
        }
    }

    public int getTotalRegistrations() {
        return registrations.size();
    }

    public Set<String> getUniqueParticipants() {
        Set<String> uniqueParticipants = new HashSet<>();

        for (String participant : registrations) {
            uniqueParticipants.add(participant);
        }

        return uniqueParticipants;
    }
}