package org.iut.refactoring;

import java.time.LocalDateTime;
import java.util.*;

public class GestionPersonnel {

    private final Map<String, Salarie> employes = new LinkedHashMap<>();
    private final List<String> logs = new ArrayList<>();
    private final RemunerationService remunerationService = new RemunerationService();

    // nouvelle version : on passe un objet
    public String ajouterSalarie(Salarie salarie) {
        employes.put(salarie.getId(), salarie);
        log("Ajout de l'employé: " + salarie.getNom());
        return salarie.getId();
    }

    // version de compatibilité pour votre ancien main
    public String ajouteSalarie(String type, String nom, double salaireDeBase, int experience, String equipe) {
        Salarie s = new Salarie(TypeSalarie.fromLabel(type), nom, salaireDeBase, experience, equipe);
        return ajouterSalarie(s);
    }

    public double calculSalaire(String employeId) {
        Salarie s = employes.get(employeId);
        if (s == null) {
            System.out.println("ERREUR: impossible de trouver l'employé");
            return 0;
        }
        return remunerationService.calculSalaire(s);
    }

    public double calculBonusAnnuel(String employeId) {
        Salarie s = employes.get(employeId);
        if (s == null) {
            return 0;
        }
        return remunerationService.calculBonus(s);
    }

    public void generationRapport(String typeRapport, String filtre) {
        System.out.println("=== RAPPORT: " + typeRapport + " ===");

        switch (typeRapport) {
            case "SALAIRE" -> afficherRapportSalaire(filtre);
            case "EXPERIENCE" -> afficherRapportExperience(filtre);
            case "DIVISION" -> afficherRapportDivision();
            default -> System.out.println("Type de rapport inconnu.");
        }

        log("Rapport généré: " + typeRapport);
    }

    private void afficherRapportSalaire(String filtre) {
        for (Salarie s : employes.values()) {
            if (filtre == null || filtre.isEmpty() || s.getEquipe().equals(filtre)) {
                double salaire = remunerationService.calculSalaire(s);
                System.out.println(s.getNom() + ": " + salaire + " €");
            }
        }
    }

    private void afficherRapportExperience(String filtre) {
        for (Salarie s : employes.values()) {
            if (filtre == null || filtre.isEmpty() || s.getEquipe().equals(filtre)) {
                System.out.println(s.getNom() + ": " + s.getExperience() + " années");
            }
        }
    }

    private void afficherRapportDivision() {
        Map<String, Integer> compteur = new HashMap<>();
        for (Salarie s : employes.values()) {
            compteur.merge(s.getEquipe(), 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : compteur.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " employés");
        }
    }

    public void avancementEmploye(String employeId, String newType) {
        Salarie s = employes.get(employeId);
        if (s == null) {
            System.out.println("ERREUR: impossible de trouver l'employé");
            return;
        }
        s.setType(TypeSalarie.fromLabel(newType));
        log("Employé promu: " + s.getNom());
        System.out.println("Employé promu avec succès!");
    }

    public List<Salarie> getEmployesParDivision(String division) {
        List<Salarie> resultat = new ArrayList<>();
        for (Salarie s : employes.values()) {
            if (s.getEquipe().equals(division)) {
                resultat.add(s);
            }
        }
        return resultat;
    }

    public void printLogs() {
        System.out.println("=== LOGS ===");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    private void log(String message) {
        logs.add(LocalDateTime.now() + " - " + message);
    }

    // utile pour les tests
    public Collection<Salarie> getEmployes() {
        return employes.values();
    }
}
