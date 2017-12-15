package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.List;

public class FunctionalTask extends Thing {

    protected OWLObjectProperty hasFunctionalTaskInput = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasFunctionalTaskInput"));
    protected OWLObjectProperty hasFunctionalTaskOutput = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasFunctionalTaskOutput"));

    protected Integer processStepId;
    protected Integer taskId;
    protected Integer num_tasks;
    protected IRI sensor;
    protected String taskType;

    public FunctionalTask(OWLOntologyManager manager, Integer processStepId, Integer num_tasks) throws OWLOntologyCreationException {
        super(manager, "functionalTask");
        this.processStepId = processStepId;
        this.taskId = (processStepId-1) * num_tasks + 1;
        this.num_tasks = num_tasks;
    }

    public FunctionalTask(OWLOntologyManager manager, String task) throws OWLOntologyCreationException {
        super(manager, task);
        this.taskType = task;

    }

    public OWLNamedIndividual add(FunctionalTask task, List<IRI> inputs, List<IRI> outputs) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLNamedIndividual ind;

        String NS = NS_sick;
        if (sensor == null) {
            ind = task.create(this.taskId, inputs, outputs, null);

        } else {
            ind = task.create(this.taskId, inputs, outputs, sensor);
            NS = NS_aoi;
            sensor = null;
        }

        String className = Character.toUpperCase(task.taskType.charAt(0)) + task.taskType.substring(1);
        OWLIndividualAxiom ax = dataFactory.getOWLClassAssertionAxiom(dataFactory.getOWLClass(getIRI(NS, className)), ind);
        this.ontology.addAxiom(ax);

        this.taskId++;
        return ind;
    }

    public OWLNamedIndividual addApplySolderPasteTask(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs) throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new ApplySolderPasteTask(manager), inputs, outputs);
    }

    public OWLNamedIndividual addApplyComponentsTask(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs) throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new ApplyComponentsTask(manager), inputs, outputs);
    }

    public OWLNamedIndividual addAoiPostInspectionTask(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.sensor = sensor;
        return add(new PostInspectionTask(manager), inputs, outputs);
    }

    public OWLNamedIndividual addAoiPreInspectionTask(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {
        this.sensor = sensor;
        return add(new PreInspectionTask(manager), inputs, outputs);
    }

    public OWLNamedIndividual addSetBoardTask(OWLOntologyManager manager, List<IRI> inputs, List<IRI> outputs) throws OWLOntologyCreationException, OWLOntologyStorageException {
        return add(new SetBoardTask(manager), inputs, outputs);
    }

    public OWLNamedIndividual create(Integer taskId, List<IRI> inputs, List<IRI> outputs, IRI sensor) throws OWLOntologyCreationException, OWLOntologyStorageException {

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

        this.print();

        return task;
    }
}