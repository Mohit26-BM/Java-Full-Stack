package javaCollectionsAssignment;

import java.util.HashSet;

public class TechnologyManager {

    private HashSet<String> technologies;

    public TechnologyManager() {
        technologies = new HashSet<>();
    }

    public void addTechnology(String technology) {
        technologies.add(technology);
    }

    public void displayTechnologies() {
        System.out.println("Available Technologies:");

        for (String technology : technologies) {
            System.out.println(technology);
        }
    }

    public int getTechnologyCount() {
        return technologies.size();
    }
}