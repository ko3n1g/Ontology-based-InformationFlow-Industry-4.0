package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class SMDPrepareBoardProcessStep extends ProcessStep {

    public SMDPrepareBoardProcessStep(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager);
    }

    public OWLNamedIndividual create(Integer processStepId, List<IRI> inputs, List<IRI> outputs) throws OWLOntologyCreationException, OWLOntologyStorageException {

        FunctionalTask tasks = new FunctionalTask(manager, processStepId, 1);

        OWLNamedIndividual task1 = tasks.addApplySolderPasteTask(manager, inputs, outputs);

        tasks.print();

        OWLNamedIndividual ps = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + processStepId));
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFunctionalTask, ps, task1);

        ontology.addAxiom(ax);

        return ps;
    }

}
