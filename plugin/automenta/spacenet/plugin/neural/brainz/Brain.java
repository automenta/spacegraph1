/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.plugin.neural.brainz;

import automenta.spacenet.var.Maths;
import java.util.ArrayList;
import java.util.List;

/**
 * java port of critterding's BRAINZ system
 */
public class Brain {
    // input / output accessor/mutators

    List<SenseNeuron> sense = new ArrayList();
    List<MotorNeuron> motor = new ArrayList();
    List<InterNeuron> neuron = new ArrayList();
    List<NeuronBuilder> neuronBuilders = new ArrayList();
//    // BUILD TIME
//    int minNeuronsAtBuildtime;  //    // min/max neurons at build time
//    int maxNeuronsAtBuildtime;  //    // min/max neurons at build time
//    int maxNeurons;     //    // absolute max neurons (mutation bound)
    double percentChanceInhibitoryNeuron;      // percent chance that when adding a new random neuron, it's inhibitory
    double percentChanceConsistentSynapses;    // synaptic consistancy, meaning all synapses of a neuron will be OR I OR E:  if set to 0, neurons will have mixed I and E synapses
    double percentChanceInhibitorySynapses;    //    // percent chance that when adding a new random neuron, it has inhibitory synapses
    double percentChanceMotorNeuron;   //    // percent chance that when adding a new random neuron, it has a motor function
    double percentChancePlasticNeuron; //    // percent chance that when adding a new random neuron, it is has synaptic plasticity
    double minPlasticityStrengthen;    //    // min/max synaptic plasticity strengthening factor
    double maxPlasticityStrengthen;    //    // min/max synaptic plasticity strengthening factor
    double minPlasticityWeaken;    //    // min/max synaptic plasticity weakening factor
    double maxPlasticityWeaken;    //    // min/max synaptic plasticity weakening factor
    double minFiringThreshold; //    // min/max firing threshold
    double maxFiringThreshold; //    // min/max firing threshold
    double maxDendridicBranches;   //    // max dendritic branches in a new neuron
    double percentChanceSensorySynapse;    //    // percent chance that a new synapse is connected to a sensor neuron
//    int minSynapsesAtBuildtime; //    // min/max synapses at build time
//    int maxSynapsesAtBuildtime; //    // min/max synapses at build time
    int minSynapses;    //    // absolute min/max synapses (mutation/plastic bounding)
    int maxSynapses;    //    // absolute min/max synapses (mutation/plastic bounding)
    double percentMutation;    //    // brain architecture mutation factor @ mutation time (%)
//    // INFO
//    // total neuron & connection keepers
    int totalNeurons;
    int totalSynapses;
//    // after every time instance, this will contain how many neurons where fired in that instant (energy usage help)
    int neuronsFired;
    int motorneuronsFired;
    private final int numNeurons;

    public Brain(int neurons, int minSynapses, int maxSynapses) {
        super();

        // build time defaults

        this.numNeurons = neurons;
        this.minSynapses = minSynapses;
        this.maxSynapses = maxSynapses;

        //totalNeurons = 0;
        //totalSynapses = 0;

//        minNeuronsAtBuildtime = 10;
//        maxNeuronsAtBuildtime = 150;

        // mutatables

//        minSynapsesAtBuildtime = 1;
//        maxSynapsesAtBuildtime = 50;

        percentChanceInhibitoryNeuron = 0.50;

        percentChanceConsistentSynapses = 0.50;

        percentChanceInhibitorySynapses = 0.50;

        percentChanceMotorNeuron = 0.50;

        percentChancePlasticNeuron = 0.50;

        minPlasticityStrengthen = 100;
        maxPlasticityStrengthen = 1000;
        minPlasticityWeaken = 1000;
        maxPlasticityWeaken = 10000;

        percentChanceSensorySynapse = 0.50;

        minFiringThreshold = 0.1;

        maxFiringThreshold = 1.0;

        maxDendridicBranches = 0.20;

        percentMutation = 0.01;


    }

    // RUN TIME
    public void clearInputs() {
        for (SenseNeuron sn : sense) {
            sn.senseInput = 0;
        }
    }

    public void clearOutputs() {
        // clear Motor Outputs
        for (MotorNeuron mn : motor) {
            mn.firing = false;
        }
    }

    public void forward() {
        // reset fired neurons counter
        neuronsFired = 0;
        motorneuronsFired = 0;

        clearOutputs();

        //place sensory information in receiving InterNeurons
//        for (SenseNeuron n : sense) {
//            n.receiver.potential = n.output;
//        }
        
        for (InterNeuron n : neuron) {
            n.forward();

            // if neuron fires
            if (n.nextOutput != 0) {
                neuronsFired++;
                //cerr << "neuron " << i << " fired " << n->waitoutput << endl;

                // motor neuron check & exec
                MotorNeuron mn = n.motor;
                if (mn != null) {
                    motorneuronsFired++;
                    mn.firing = true;
                    //cerr << "neuron " << i << " fired, motor is " << Neurons[i]->MotorFunc << " total now " << Outputs[Neurons[i]->MotorFunc]->output << endl;
                }
            }
        }

        // commit outputs at the end
        for (InterNeuron in : neuron) {
            in.output = in.nextOutput;
        }
    }

    public void forwardUntilAnswer() {
//        //		neuronsFired = 0;
//
//		// clear Motor Outputs
//		for ( unsigned int i=0; i < numberOfOutputs; i++ )
//			Outputs[i].output = false;
//
//		// clear Neurons
//		for ( unsigned int i=0; i < totalNeurons; i++ )
//		{
//			Neurons[i].output = 0;
//			Neurons[i].potential = 0.0f;
//		}
//
//		unsigned int counter = 0;
//		bool motorFired = false;
//
//		while ( counter < 1000 && !motorFired )
//		{
//			for ( unsigned int i=0; i < totalNeurons; i++ )
//			{
//				NeuronInterz* n = &Neurons[i];
//
//				n->process();
//
//				// if neuron fires
//				if ( n->waitoutput != 0 )
//				{
//					neuronsFired++;
//
//					// motor neuron check & exec
//					if ( n->isMotor )
//					{
//						motorFired = true;
//						*Outputs[n->motorFunc].output = true;
//						//cerr << "neuron " << i << " fired, motor is " << Neurons[i]->MotorFunc << " total now " << Outputs[Neurons[i]->MotorFunc]->output << endl;
//					}
//				}
//			}
//			// commit outputs at the end
//			for ( unsigned int i=0; i < totalNeurons; i++ ) Neurons[i].output = Neurons[i].waitoutput;
//
//			counter++;
//		}
    }

    MotorNeuron motor(int i) {
        return motor.get(i);
    }

    SenseNeuron sense(int i) {
        return sense.get(i);
    }

    public int getNumInputs() {
        return sense.size();
    }

    public int getNumOutputs() {
        return motor.size();
    }

    public MotorNeuron getRandomMotorNeuron() {
        return motor.get((int) Math.floor(Maths.random(0, motor.size())));
    }

    public InterNeuron getRandomInterNeuron() {
        return neuron.get((int) Math.floor(Maths.random(0, neuron.size())));
    }

    public SenseNeuron getRandomSenseNeuron() {
        return sense.get((int) Math.floor(Maths.random(0, sense.size())));
    }

    // build time functions
    public NeuronBuilder newRandomNeuronBuilder() {
        // new architectural neuron
        NeuronBuilder an = new NeuronBuilder();

        // is it inhibitory ?
        if (Math.random() <= percentChanceInhibitoryNeuron) {
            an.isInhibitory = true;
        } // if not, is it motor ?
        else if (Math.random() <= percentChanceMotorNeuron) {
            MotorNeuron mn = getRandomMotorNeuron();

            // check if motor already used
            boolean proceed = true;
            for (NeuronBuilder nb : neuronBuilders) {
                if (nb.motor == mn) {
                    proceed = false;
                    break;
                }
            }

            if (proceed) {
                an.motor = mn;
            }
        }

        // does it have synaptic plasticity ?
        if (Math.random() <= percentChancePlasticNeuron) {
            an.isPlastic = true;
            an.plasticityStrengthen = Maths.random(minPlasticityStrengthen, maxPlasticityStrengthen);
            an.plasticityWeaken = Maths.random(minPlasticityWeaken, maxPlasticityWeaken);
        }

        // does it have consistent synapses ?
        if (Math.random() <= percentChanceConsistentSynapses) {
            an.hasConsistentSynapses = true;

            // if so, does it have inhibitory synapses ?
            if (Math.random() <= percentChanceInhibitorySynapses) {
                an.hasInhibitorySynapses = true;
            }
        }

        // determine firing threshold
        if (an.motor != null) {
            an.firingThreshold = maxFiringThreshold;
        } else {
            an.firingThreshold = Maths.random(minFiringThreshold, maxFiringThreshold);
        }

        // determine dendritic branches
        if (an.motor != null) {
            an.maxDendridicBranches = maxDendridicBranches;
        } else {
            an.maxDendridicBranches = (int) Math.round(Maths.random(1, maxDendridicBranches));
        }

        // push it on the vector
        neuronBuilders.add(an);

        return an;
    }

    public SynapseBuilder newRandomSynapseBuilder(NeuronBuilder bn) {
        // new architectural synapse
        SynapseBuilder as = new SynapseBuilder();

        // is it connected to a sensor neuron ?
        // < 2 because if only 1 archneuron, it can't connect to other one
        if (Math.random() <= percentChanceSensorySynapse || neuronBuilders.size() < 2) {
            as.isSensorNeuron = true;

            // sensor neuron id synapse is connected to
            as.senseNeuron = getRandomSenseNeuron();
        } // if not determine inter neuron id
        else {
            // as in real life, neurons can connect to themselves
            //as.neuron = getRandomInterNeuron(); //randgen->Instance()->get( 0, ArchNeurons.size()-1 );
            as.senseNeuron = null;
        }

        // dendrite branch number
        as.dendriteBranches = (int) Math.round(Maths.random(0.01, bn.maxDendridicBranches));

        // synaptic weight
        if (bn.hasConsistentSynapses) {
            if (bn.hasInhibitorySynapses) {
                as.weight = -1.0f;
            } else {
                as.weight = 1.0f;
            }
        } else {
            if (Math.random() <= percentChanceInhibitorySynapses) {
                as.weight = -1.0f;
            } else {
                as.weight = 1.0f;
            }
        }

        bn.synapseBuilders.add(as);

        return as;
    }

    public MotorNeuron newOutput(/*bool* var, unsigned int id*/) {
        MotorNeuron m = new MotorNeuron();
        motor.add(m);
        return m;
    }

    public SenseNeuron newInput() {
        SenseNeuron s = new SenseNeuron();
        sense.add(s);
        return s;
    }

    public void removeObsoleteMotorsAndSensors() {
//		for ( int i = 0; i < (int)ArchNeurons.size(); i++ )
//		{
//			ArchNeuronz* an = &ArchNeurons[i];
//			// disable motor neurons
//			if ( an->isMotor )
//			{
//				if ( findMotorNeuron( an->motorID ) == -1 )
//				{
//					an->isMotor = false;
//				}
//			}
//
//			// disable sensor inputs
//			for ( int j = 0; j < (int)an->ArchSynapses.size(); j++ )
//			{
//				ArchSynapse* as = &an->ArchSynapses[j];
//				if ( as->isSensorNeuron )
//				{
//					if ( findSensorNeuron( as->neuronID ) == -1 )
//					{
//						an->ArchSynapses.erase( an->ArchSynapses.begin()+j );
//						j--;
//					}
//				}
//			}
//		}
    }

    public void buildArch() {
        // clear architecture by removing all architectural neurons
        neuronBuilders.clear();

        // determine number of neurons this brain will start with
        //int numNeurons = (int) Math.round(Maths.random(minNeuronsAtBuildtime, maxNeuronsAtBuildtime));

        // create the architectural neurons
        for (int i = 0; i < numNeurons; i++) {
            newRandomNeuronBuilder();
        }

        // create architectural synapses
        for (NeuronBuilder n : neuronBuilders) {
            // determine amount of synapses this neuron will start with
            int SynapseAmount = (int) Math.round(Maths.random(minSynapses, maxSynapses));

            // create the architectural neurons
            for (int j = 0; j < SynapseAmount; j++) {
                newRandomSynapseBuilder(n);
            }
        }
    }

    public void wireArch() {
        // clear everything
        neuron.clear();
        //sense.clear();
        //motor.clear();

        // we know the amount of neurons already, reset totalsynapses for the count later
        totalNeurons = neuronBuilders.size();
        totalSynapses = 0;


        // create all runtime neurons
        for (NeuronBuilder nb : neuronBuilders) {
            neuron.add(nb.newNeuron(maxSynapses));
        }

        // create their synapses & link them to their inputneurons
        for (InterNeuron n : neuron) {

            for (SynapseBuilder sb : n.synapseBuilders) {
                AbstractNeuron i;
                if (sb.isSensorNeuron) {
                    // sensor neuron id synapse is connected to
                    i = getRandomSenseNeuron();
                } // if not determine inter neuron id
                else {
                    // as in real life, neurons can connect to themselves
                    i = getRandomInterNeuron();
                }
                
                n.newSynapse(i, n.dendridicBranches, sb.weight);

            }

            // count connections
            totalSynapses += n.synapses.size();
        }

//			//cerr << "total neurons: " << totalNeurons << "total synapses: " << totalSynapses << endl;
    }

    public List<SenseNeuron> getSense() {
        return sense;
    }

    public List<MotorNeuron> getMotor() {
        return motor;
    }

    public List<InterNeuron> getNeuron() {
        return neuron;
    }

    public int getTotalNeurons() {
        return totalNeurons;
    }

    public int getTotalSynapses() {
        return totalSynapses;
    }
    
//		// load save architecture (serialize)
//			void			setArch(string* content);
//			string*			getArch();
//
//    // build commands
//    // functions
//    void copyFrom(const   Brainz& otherBrain);
//			void			mergeFrom(const Brainz& otherBrain1, const Brainz& otherBrain2);
}
