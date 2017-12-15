package EventDataProcessor;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Component extends Material {

    public Component(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "component");
    }

}
