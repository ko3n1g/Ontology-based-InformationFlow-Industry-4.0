package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

class Process extends Thing {

    protected OWLObjectProperty hasProcessStep = dataFactory.getOWLObjectProperty(getIRI(NS_gn, "hasProcessStep"));

    protected IRI board, solderPaste, preparedBoard, stickedBoard, setBoard, sensor;
    protected List<IRI> components, observations = new ArrayList<>();



    public Process(OWLOntologyManager manager) throws OWLOntologyCreationException {
        super(manager, "process");
    }


    protected void createMaterials(Integer processId) throws OWLOntologyCreationException, OWLOntologyStorageException {
        Material materials = new Material(manager, processId);
        IRI component;
        List<IRI> components = new ArrayList<>();

        for (int i=0; i<6; i++) {
            component = materials.addComponent();
            components.add(component);
        }

        board = materials.addBoard();
        solderPaste = materials.addSolderPaste();
        preparedBoard = materials.addPreparedBoard();
        stickedBoard = materials.addStickedBoard();
        setBoard = materials.addSetBoard();

        sensor = materials.addSensor();

        OWLDifferentIndividualsAxiom ax = dataFactory.getOWLDifferentIndividualsAxiom(
                dataFactory.getOWLNamedIndividual(components.get(0)),
                dataFactory.getOWLNamedIndividual(components.get(1)),
                dataFactory.getOWLNamedIndividual(components.get(2)),
                dataFactory.getOWLNamedIndividual(components.get(3)),
                dataFactory.getOWLNamedIndividual(components.get(4)),
                dataFactory.getOWLNamedIndividual(components.get(5)));

        ontology.addAxiom(ax);

        this.components = components;
        materials.print();
    }

    protected void createObservations(Integer processId) throws OWLOntologyCreationException, OWLOntologyStorageException {
        Observation observation = new Observation(manager, processId, sensor);

        for (IRI component : components) {
            observations.addAll(observation.create(component));
        }

        List<OWLNamedIndividual> observationsList = new ArrayList<>();
        for (IRI ob : observations) {
            observationsList.add(dataFactory.getOWLNamedIndividual(ob));
        }

        OWLDifferentIndividualsAxiom ax = dataFactory.getOWLDifferentIndividualsAxiom(observationsList);

        ontology.addAxiom(ax);

        observation.print();
    }

}
