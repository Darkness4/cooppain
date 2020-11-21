import cooppain.entities.Subvention;
import cooppain.exceptions.LibelleConflictException;
import cooppain.exceptions.SubventionLimitReachedException;
import cooppain.sessions.GestionSubvention;
import cooppain.sessions.InfosSubvention;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class Client {
    final GestionSubvention gestionSubvention;
    final InfosSubvention infosSubvention;

    public Client() {
        try {
            final InitialContext initialContext = new InitialContext();
            gestionSubvention = (GestionSubvention) initialContext.lookup("cooppain.sessions.GestionSubvention");
            infosSubvention = (InfosSubvention) initialContext.lookup("cooppain.sessions.InfosSubvention");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        final Client client = new Client();
        client.beforeAllTests();
        client.runTests();
    }

    public void beforeAllTests() {
        System.out.println("Clear all subventions.");
        System.out.println("Subventions deleted: " + infosSubvention.clear());
    }

    public void runTests() {
        // NOTE : Les tests ne sont pas unitaires. L'ordre des tests est important.
        createMaxSubventions();
        createTooMuchSubvention();
        removeSomeSubventions();
        createDuplicateLibelleShouldThrows();
        editSubventionShouldWork();
        editSubventionChangeToDuplicateLibelleShouldThrows();
        editSubventionChangeCampusShouldThrowsLimitReached();
    }

    private void createMaxSubventions() {
        System.out.println("Init. Using : " + gestionSubvention.init(1));
        System.out.println("\nTEST: Create max subventions");
        try {
            for (int i = 0; i < gestionSubvention.getSubventionLimit(); i++) {
                final Subvention subvention = gestionSubvention.createSubvention(new Subvention(
                        "Subvention " + i,
                        new Timestamp(System.currentTimeMillis()),
                        false,
                        "Subvention " + i + " Description"
                ));
                System.out.println(subvention);
            }
            System.out.println("OK");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void createTooMuchSubvention() {
        System.out.println("\nTEST: Create more subvention that he can take. Should throw.");
        try {
            System.out.println(gestionSubvention.createSubvention(new Subvention(
                    "Out of range",
                    new Timestamp(System.currentTimeMillis()),
                    false,
                    "Out of range"
            )));
            System.out.println("FAIL : Unwanted behavior");
        } catch (SubventionLimitReachedException e) {
            System.out.println("OK : " + e);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void removeSomeSubventions() {
        System.out.println("\nTEST: Remove 2 subventions that campus has.");
        try {
            final List<Subvention> subventions = infosSubvention.find();
            System.out.println("DELETED : " + gestionSubvention.deleteSubvention(subventions.get(0),  subventions.get(1)));
            System.out.println("LEFT : " + infosSubvention.find());
            System.out.println("OK");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void createDuplicateLibelleShouldThrows() {
        System.out.println("\nTEST: Create a subvention that has the same name as another. Should throw. Creation should be rollbacked.");
        try {
            final Subvention subvention = gestionSubvention.createSubvention(new Subvention(
                    "A name",
                    new Timestamp(System.currentTimeMillis()),
                    false,
                    "A description"
            ));
            System.out.println(subvention);
            System.out.println(gestionSubvention.createSubvention(new Subvention(
                    "A name",
                    new Timestamp(System.currentTimeMillis()),
                    false,
                    "A subvention that has the same name."
            )));
            System.out.println("FAIL : Unwanted behavior");
        } catch (LibelleConflictException e) {
            System.out.println("OK : " + e);
            System.out.println("SUBTEST: 'A subvention that has the same name.' should not exists.");
            final List<Subvention> subventions = infosSubvention.find();

            final long count = subventions.stream().filter(subvention -> subvention.getDescription().equals("A subvention that has the same name.")).count();
            System.out.println(subventions);
            System.out.println(count == 0 ? "OK" : "FAIL");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void editSubventionShouldWork() {
        System.out.println("\nTEST: Edit a subvention should work.");
        try {
            final List<Subvention> subventions = infosSubvention.find();
            final Subvention subvention = subventions.get(0);
            subvention.setLibelle("A new Libelle");
            System.out.println(gestionSubvention.updateSubvention(subvention));
            System.out.println("OK");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void editSubventionChangeToDuplicateLibelleShouldThrows() {
        System.out.println("\nTEST: Edit a subvention and change Libelle as duplicate. Should throw. Edit should be rollbacked.");
        final List<Subvention> subventions = infosSubvention.find();
        final Subvention subvention = subventions.get(0);
        final Subvention subvention2 = subventions.get(1);
        try {
            subvention.setLibelle(subvention2.getLibelle());
            System.out.println(gestionSubvention.updateSubvention(subvention));
            System.out.println("FAIL : Unwanted behavior.");
        } catch (LibelleConflictException e) {
            System.out.println("OK : " + e);
            System.out.println("SUBTEST: Shouldn't have duplicated libelle. (Should have rollbacked)");
            final Subvention oldSubvention = infosSubvention.findById(subvention.getId());
            System.out.println(infosSubvention.find());
            System.out.println(!oldSubvention.getLibelle().equals(subvention2.getLibelle()) ? "OK" : "FAIL");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void editSubventionChangeCampusShouldThrowsLimitReached() {
        System.out.println("\nTEST: Create subvention to an another campus. Change to a new campus and reach the limit. Should throw. Edit should be rollbacked.");
        // Arrange
        Queue<Subvention> subventions = new LinkedList<>();

        try {
            System.out.println("Using : " + gestionSubvention.init(2));
            for (int i = 0; i < gestionSubvention.getSubventionLimit(); i++) {
                subventions.add(gestionSubvention.createSubvention(new Subvention(
                        "Subvention " + i + " Campus 2",
                        new Timestamp(System.currentTimeMillis()),
                        false,
                        "Subvention " + i + " Description"
                )));
            }
            System.out.println("Using : " + gestionSubvention.init(3));
            for (int i = 0; i < gestionSubvention.getSubventionLimit(); i++) {
                subventions.add(gestionSubvention.createSubvention(new Subvention(
                        "Subvention " + i + " Campus 3",
                        new Timestamp(System.currentTimeMillis()),
                        false,
                        "Subvention " + i + " Description"
                )));
            }
        } catch (SubventionLimitReachedException | LibelleConflictException e) {
            e.printStackTrace();
        }
        System.out.println("Subventions that will be moved to campus 1 : \n" + subventions);

        // Act
        Subvention last = null;
        System.out.println("Using : " + gestionSubvention.init(1));
        try {
            for (final Subvention subvention : subventions) {
                last = subvention;
                subvention.setCampusID(1);
                System.out.println(gestionSubvention.updateSubvention(subvention));
            }
            System.out.println("FAIL : Unwanted behavior");
        } catch (SubventionLimitReachedException e) {
            // Assert
            System.out.println("OK : " + e);
            System.out.println("SUBTEST: Subvention " + last.getId() + " should have rollbacked.");
            final Subvention subvention = infosSubvention.findById(last.getId());
            System.out.println(infosSubvention.find());
            System.out.println(subvention.getCampusID() != 1 ? "OK" : "FAIL");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

