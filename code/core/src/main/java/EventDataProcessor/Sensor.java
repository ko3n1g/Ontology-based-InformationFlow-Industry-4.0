package EventDataProcessor;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Sensor extends Material {

    public Sensor(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "sensor");
    }


}
