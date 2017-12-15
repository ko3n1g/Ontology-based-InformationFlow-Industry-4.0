package EventDataProcessor;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class PreparedBoard extends Material {

    public PreparedBoard(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "preParedBoard");
    }


}
