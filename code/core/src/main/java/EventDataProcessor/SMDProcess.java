package EventDataProcessor;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

public class SMDProcess extends Process {

    protected Integer num_steps = 5;

    protected List<IRI> inputs = new ArrayList<>();
    protected List<IRI> outputs = new ArrayList<>();

    public SMDProcess(OWLOntologyManager manager, Integer num_of_instances) throws OWLOntologyStorageException, OWLOntologyCreationException {
        super(manager);

        List<OWLNamedIndividual> processes = new ArrayList<>();

        for (int i=1; i<=num_of_instances; i++) {
            createSMDProcess(i);
            processes.add(dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + i)));

        }

        OWLDifferentIndividualsAxiom ax = dataFactory.getOWLDifferentIndividualsAxiom(processes);

        ontology.addAxiom(ax);
    }

    public Process createSMDProcess(Integer id) throws OWLOntologyCreationException, OWLOntologyStorageException {

        createMaterials(id);
        createObservations(id);

        // Todo:
        // Create new instance processSteps
        // Within processSteps, create namedIndividual smdPrepareBoardProcessStep
        // create axiom hasProcessStep

        ProcessStep processSteps = new ProcessStep(manager, id, num_steps);

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputs.add(setBoard);
        OWLNamedIndividual ps5 = processSteps.addSMDPostInspectionProcessStep(manager, inputs, outputs, sensor);

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputs.add(stickedBoard);
        outputs.add(setBoard);

        OWLNamedIndividual ps4 = processSteps.addSMDSetBoardProcessStep(manager, inputs, outputs, ps5);

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputs.add(stickedBoard);

        OWLNamedIndividual ps3 = processSteps.addSMDPreInspectionProcessStep(manager, inputs, outputs, sensor, ps4);

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputs.add(preparedBoard);
        inputs.addAll(components);
        outputs.add(stickedBoard);

        OWLNamedIndividual ps2 = processSteps.addSMDStickBoardProcessStep(manager, inputs, outputs, ps3);


        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        inputs.add(board);
        inputs.add(solderPaste);
        outputs.add(preparedBoard);

        OWLNamedIndividual ps1 = processSteps.addSMDPrepareBoardProcessStep(manager, inputs, outputs, ps2);


        processSteps.print();

        OWLDifferentIndividualsAxiom ax = dataFactory.getOWLDifferentIndividualsAxiom(ps1, ps2, ps3, ps4, ps5);


        OWLNamedIndividual smdProcess = dataFactory.getOWLNamedIndividual(getIRI(NS_individual, ontologyName + "#" + id));
        OWLObjectPropertyAssertionAxiom ax1 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasProcessStep, smdProcess, ps1);
        OWLObjectPropertyAssertionAxiom ax2 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasProcessStep, smdProcess, ps2);
        OWLObjectPropertyAssertionAxiom ax3 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasProcessStep, smdProcess, ps3);
        OWLObjectPropertyAssertionAxiom ax4 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasProcessStep, smdProcess, ps4);
        OWLObjectPropertyAssertionAxiom ax5 = dataFactory.getOWLObjectPropertyAssertionAxiom(hasProcessStep, smdProcess, ps5);

        ontology.addAxiom(ax1);
        ontology.addAxiom(ax2);
        ontology.addAxiom(ax3);
        ontology.addAxiom(ax4);
        ontology.addAxiom(ax5);

        if (id % 10 == 0)
            System.out.println(id + " :: Done");

        return this;
    }

}
