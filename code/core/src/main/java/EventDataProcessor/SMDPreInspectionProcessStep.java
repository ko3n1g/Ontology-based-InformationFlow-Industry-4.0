package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class SMDPreInspectionProcessStep extends ProcessStep {

    public SMDPreInspectionProcessStep(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager);
    }

    public OWLNamedIndividual create(Integer processStepId, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {

        FunctionalTask tasks = new FunctionalTask(manager, processStepId, 1);

        OWLNamedIndividual task1 = tasks.addAoiPreInspectionTask(manager, inputs, outputs, sensor);

        tasks.print();

        OWLNamedIndividual ps = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + processStepId));
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFunctionalTask, ps, task1);

        ontology.addAxiom(ax);

        return ps;
    }

}

