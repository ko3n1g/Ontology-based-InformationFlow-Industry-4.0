package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

public class ApplyComponentsTask extends FunctionalTask {

    public ApplyComponentsTask(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "applyComponentsTask");
    }

}
