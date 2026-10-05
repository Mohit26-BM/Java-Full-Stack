package javaCollectionsAssignment;

import java.util.HashMap;

public class ParticipantManager {

    private HashMap<String, Integer> participantTopics;

    public ParticipantManager() {
        participantTopics = new HashMap<>();
    }

    public void addParticipant(String participantName, int topicCount) {
        participantTopics.put(participantName, topicCount);
    }

    public void displayParticipantTopics() {
        System.out.println("Participant Topic Details:");

        for (String participant : participantTopics.keySet()) {
            System.out.println(
                participant + " -> " + participantTopics.get(participant)
            );
        }
    }

    public boolean containsParticipant(String participantName) {
        return participantTopics.containsKey(participantName);
    }

    public int getTopicCount(String participantName) {
        return participantTopics.get(participantName);
    }

    public int getParticipantCount() {
        return participantTopics.size();
    }
}
