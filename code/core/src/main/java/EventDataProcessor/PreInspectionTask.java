package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class PreInspectionTask extends FunctionalTask {

    protected OWLObjectProperty implementedBy = dataFactory.getOWLObjectProperty(getIRI(NS_SSN, "implementedBy"));


    public PreInspectionTask(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "preInspectionTask");
    }

    public OWLNamedIndividual create(Integer taskId, List<IRI> inputs, List<IRI> outputs, IRI sens) throws OWLOntologyCreationException, OWLOntologyStorageException {


        OWLNamedIndividual task = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + taskId));


        for (IRI iri : inputs) {
            OWLNamedIndividual input = dataFactory.getOWLNamedIndividual(iri);
            OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFunctionalTaskInput, task, input);
            this.ontology.addAxiom(ax);

        }

        for (IRI iri : outputs) {
            OWLNamedIndividual output = dataFactory.getOWLNamedIndividual(iri);
            OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFunctionalTaskOutput, task, output);
            this.ontology.addAxiom(ax);

        }

        OWLNamedIndividual sensor = dataFactory.getOWLNamedIndividual(sens);
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(implementedBy, task, sensor);
        ontology.addAxiom(ax);

        this.print();


        return task;
    }
}
