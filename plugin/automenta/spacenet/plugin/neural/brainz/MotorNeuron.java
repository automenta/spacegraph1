package automenta.spacenet.plugin.neural.brainz;

public class MotorNeuron implements AbstractNeuron {
    boolean firing;

    @Override
    public double getOutput() {
        return firing ? 1.0 : 0.0;
    }


}
