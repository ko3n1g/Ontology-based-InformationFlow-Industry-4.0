package EventDataProcessor;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class StickedBoard extends Material {

    public StickedBoard(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "stickedBoard");
    }

    public OWLNamedIndividual create(Integer materialId) throws OWLOntologyCreationException, OWLOntologyStorageException {

        OWLNamedIndividual ind = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + materialId));


        return ind;
    }
}
