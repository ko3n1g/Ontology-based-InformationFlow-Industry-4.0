package EventDataProcessor;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Main {

    public static void main(String[] args) throws Exception {

        final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        // Instanciates the smd process 100-times
        SMDProcess smd = new SMDProcess(manager, 100);

        // prints the results to IRIHandler.INPUT
        smd.print(true);
    }
}
