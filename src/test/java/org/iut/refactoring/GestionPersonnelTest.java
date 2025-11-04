package org.iut.refactoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestionPersonnelTest {

    private GestionPersonnel gestion;

    @BeforeEach
    void setUp() {
        gestion = new GestionPersonnel();
    }

    private List<String> ajouterTousLesProfils() {
        List<String> ids = new ArrayList<>();
        ids.add(gestion.ajouteSalarie("DEVELOPPEUR", "DevSenior", 50000, 6, "IT"));     // dev > 5
        ids.add(gestion.ajouteSalarie("DEVELOPPEUR", "DevJunior", 50000, 2, "IT"));     // dev <= 5
        ids.add(gestion.ajouteSalarie("DEVELOPPEUR", "DevExpert", 55000, 12, "IT"));    // dev > 10
        ids.add(gestion.ajouteSalarie("CHEF DE PROJET", "ChefSenior", 60000, 4, "RH")); // chef > 3
        ids.add(gestion.ajouteSalarie("CHEF DE PROJET", "ChefJunior", 60000, 2, "RH")); // chef <= 3
        ids.add(gestion.ajouteSalarie("STAGIAIRE", "Stagiaire", 20000, 0, "IT"));
        ids.add(gestion.ajouteSalarie("AUTRE", "Autre", 30000, 1, "OPS"));              // type inconnu
        return ids;
    }

    @Test
    void testAjouteSalarieEtSalairesStockes() {
        List<String> ids = ajouterTousLesProfils();
        // on doit avoir 7 employés
        assertEquals(7, gestion.getEmployes().size());
        // les salaires calculés à l'ajout doivent exister dans la map
        for (String id : ids) {
            assertTrue(gestion.calculSalaire(id) > 0);
        }
        // un log par ajout
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        gestion.printLogs();
        System.setOut(original);
        String logsTxt = out.toString();
        int count = logsTxt.split("Ajout de l'employé").length - 1;
        assertEquals(7, count);
    }

    @Test
    void testCalculSalaireDeveloppeurAvecBonusExperience() {
        String id = gestion.ajouteSalarie("DEVELOPPEUR", "DevSenior", 50000, 6, "IT");

        double salaire = gestion.calculSalaire(id);
        // 50000 * 1.2 = 60000 ; exp > 5 => *1.15 = 69000
        assertEquals(50000 * 1.2 * 1.15, salaire, 0.001);
    }

    @Test
    void testCalculSalaireDeveloppeurTresExperimente() {
        String id = gestion.ajouteSalarie("DEVELOPPEUR", "DevExpert", 55000, 12, "IT");

        double salaire = gestion.calculSalaire(id);
        // 55000 * 1.2 = 66000
        // exp > 5 => *1.15 = 75900
        // exp > 10 => *1.05 = 79695
        assertEquals(79695.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireChefDeProjetSenior() {
        String id = gestion.ajouteSalarie("CHEF DE PROJET", "Boss", 60000, 4, "RH");

        double salaire = gestion.calculSalaire(id);
        // 60000 * 1.5 = 90000
        // exp > 3 => *1.1 = 99000
        // + 5000 = 104000
        assertEquals(104000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireChefDeProjetJunior() {
        String id = gestion.ajouteSalarie("CHEF DE PROJET", "BossJr", 60000, 2, "RH");

        double salaire = gestion.calculSalaire(id);
        // 60000 * 1.5 = 90000
        // pas de *1.1
        // + 5000 = 95000
        assertEquals(95000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireStagiaire() {
        String id = gestion.ajouteSalarie("STAGIAIRE", "Stagiaire", 20000, 0, "IT");

        double salaire = gestion.calculSalaire(id);
        assertEquals(20000 * 0.6, salaire, 0.001);
    }

    @Test
    void testCalculSalaireTypeInconnu() {
        String id = gestion.ajouteSalarie("AUTRE", "Temp", 30000, 1, "OPS");

        double salaire = gestion.calculSalaire(id);
        assertEquals(30000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireEmployeInexistant() {
        // on capture la sortie juste pour exécuter la ligne d'erreur
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        double salaire = gestion.calculSalaire("ID-QUI-N-EXISTE-PAS");

        System.setOut(original);

        assertEquals(0.0, salaire, 0.001);
        assertTrue(out.toString().contains("ERREUR"));
    }

    @Test
    void testGenerationRapportsToutesBranches() {
        ajouterTousLesProfils();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        // SALAIRE sans filtre
        gestion.generationRapport("SALAIRE", null);
        // SALAIRE avec filtre vide
        gestion.generationRapport("SALAIRE", "");
        // SALAIRE avec filtre existant
        gestion.generationRapport("SALAIRE", "IT");
        // SALAIRE avec filtre inexistant
        gestion.generationRapport("SALAIRE", "PAS-LA");

        // EXPERIENCE avec filtre null (true)
        gestion.generationRapport("EXPERIENCE", null);
        // EXPERIENCE avec filtre inexistant (false)
        gestion.generationRapport("EXPERIENCE", "PAS-LA");

        // DIVISION
        gestion.generationRapport("DIVISION", null);

        // type inconnu
        gestion.generationRapport("TRUC-AUTRE", null);

        System.setOut(original);

        // 8 rapports => 8 logs en plus des 7 ajouts
        assertTrue(out.toString().contains("=== RAPPORT: SALAIRE ==="));
        assertTrue(out.toString().contains("=== RAPPORT: DIVISION ==="));
    }

    @Test
    void testAvancementEmployeSuccesEtEchec() {
        String id = gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 50000, 6, "IT");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        // succès
        gestion.avancementEmploye(id, "CHEF DE PROJET");
        // échec
        gestion.avancementEmploye("INCONNU", "CHEF DE PROJET");

        System.setOut(original);

        // le type a bien changé
        double salaireMisAJour = gestion.calculSalaire(id);
        // salaire recalculé : base 50000, chef, exp 6
        // 50000 * 1.5 = 75000
        // exp > 3 => *1.1 = 82500
        // + 5000 = 87500
        assertEquals(87500.0, salaireMisAJour, 0.001);

        String console = out.toString();
        assertTrue(console.contains("Employé promu avec succès!"));
        assertTrue(console.contains("ERREUR: impossible de trouver l'employé"));
    }

    @Test
    void testGetEmployesParDivision() {
        ajouterTousLesProfils();

        ArrayList<Salarie> it = new ArrayList<>(gestion.getEmployesParDivision("IT"));
        ArrayList<Salarie> rh = new ArrayList<>(gestion.getEmployesParDivision("RH"));
        ArrayList<Salarie> vide = new ArrayList<>(gestion.getEmployesParDivision("FINANCE"));

        assertFalse(it.isEmpty());
        assertFalse(rh.isEmpty());
        assertTrue(vide.isEmpty());
    }

    @Test
    void testPrintLogs() {
        ajouterTousLesProfils();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        gestion.printLogs();

        System.setOut(original);

        String txt = out.toString();
        assertTrue(txt.contains("=== LOGS ==="));
        assertTrue(txt.contains("Ajout de l'employé"));
    }

    @Test
    void testCalculBonusAnnuelTousTypes() {
        List<String> ids = ajouterTousLesProfils();

        // On récupère les IDs dans l'ordre d'ajout
        String idDevSenior   = ids.get(0);
        String idDevJunior   = ids.get(1);
        String idDevExpert   = ids.get(2);
        String idChefSenior  = ids.get(3);
        String idChefJunior  = ids.get(4);
        String idStagiaire   = ids.get(5);
        String idAutre       = ids.get(6);

        // Dev senior: base 50000 -> 10% = 5000, exp>5 => *1.5 = 7500
        assertEquals(7500.0, gestion.calculBonusAnnuel(idDevSenior), 0.001);

        // Dev junior: base 50000 -> 10% = 5000, pas de *1.5
        assertEquals(5000.0, gestion.calculBonusAnnuel(idDevJunior), 0.001);

        // Dev expert: base 55000 -> 10% = 5500, exp>5 => *1.5 = 8250
        assertEquals(8250.0, gestion.calculBonusAnnuel(idDevExpert), 0.001);

        // Chef senior: base 60000 -> 20% = 12000, exp>3 => *1.3 = 15600
        assertEquals(15600.0, gestion.calculBonusAnnuel(idChefSenior), 0.001);

        // Chef junior: base 60000 -> 20% = 12000 (pas de *1.3)
        assertEquals(12000.0, gestion.calculBonusAnnuel(idChefJunior), 0.001);

        // Stagiaire: 0
        assertEquals(0.0, gestion.calculBonusAnnuel(idStagiaire), 0.001);

        // Autre: retombe dans le "return 0" final
        assertEquals(0.0, gestion.calculBonusAnnuel(idAutre), 0.001);
    }
}
