package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class ProcessStep extends Thing {

    protected OWLObjectProperty hasNextStep = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasNextStep_directly"));
    protected OWLObjectProperty hasFunctionalTask = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasFunctionalTask"));

    protected Integer processId;
    protected Integer processStepId;
    protected Integer num_steps;


    public ProcessStep(OWLOntologyManager manager, Integer processId, Integer num_steps) throws OWLOntologyCreationException, OWLOntologyStorageException {
        super(manager, "processStep");
        this.processId = processId;
        this.processStepId = (processId-1) * num_steps + 1;
        this.num_steps = num_steps;
    }

    public ProcessStep(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "processStep");

    }


    public OWLNamedIndividual addSMDPrepareBoardProcessStep(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, OWLNamedIndividual next) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = new SMDPrepareBoardProcessStep(manager).create(this.processStepId, inputs, outputs);
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasNextStep, ind, next);
        this.ontology.addAxiom(ax);
        this.processStepId++;
        return ind;
    }

    public OWLNamedIndividual addSMDStickBoardProcessStep(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, OWLNamedIndividual next) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = new SMDStickBoardProcessStep(manager).create(this.processStepId, inputs, outputs);
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasNextStep, ind, next);
        this.ontology.addAxiom(ax);
        this.processStepId++;
        return ind;
    }

    public OWLNamedIndividual addSMDPreInspectionProcessStep(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, IRI sensor, OWLNamedIndividual next) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = new SMDPreInspectionProcessStep(manager).create(this.processStepId, inputs, outputs, sensor);
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasNextStep, ind, next);
        this.ontology.addAxiom(ax);
        this.processStepId++;
        return ind;
    }

    public OWLNamedIndividual addSMDSetBoardProcessStep(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, OWLNamedIndividual next) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = new SMDSetBoardProcessStep(manager).create(this.processStepId, inputs, outputs);
        OWLObjectPropertyAssertionAxiom ax = dataFactory.getOWLObjectPropertyAssertionAxiom(hasNextStep, ind, next);
        this.ontology.addAxiom(ax);
        this.processStepId++;
        return ind;
    }

    public OWLNamedIndividual addSMDPostInspectionProcessStep(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind = new SMDPostInspectionProcessStep(manager).create(this.processStepId, inputs, outputs, sensor);
        this.processStepId++;
        return ind;
    }
}
