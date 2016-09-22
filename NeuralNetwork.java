package Main;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class NeuralNetwork {

    //no more than 18
    public static interface Neuron {

        public void updateInfo(float bias, float[] weights, float threshold);

        public void setInputs(Neuron[] Inputs, float bias, float[] weights, float threshold);

        public float getOutput();

        public float getThreshold();

        public float getBias();

        public float[] getWeights();

    }

    public static class InputNeuron implements Neuron {

        float Input = 0;

        public InputNeuron(float input) {
            Input = input;
        }

        @Override
        public void updateInfo(float bias, float[] weights, float threshold) {

        }

        public void setInput(float input) {
            Input = input;
        }

        @Override
        public void setInputs(Neuron[] Inputs, float bias, float[] weights, float threshold) {

        }

        @Override
        public float getOutput() {
            return Input;
        }

        @Override
        public float getThreshold() {
            return 0;
        }

        @Override
        public float getBias() {
            return 0;
        }

        @Override
        public float[] getWeights() {
            return null;
        }

    }

    public static class Perceptron implements Neuron {

        Neuron[] Inputs = null;
        float bias = 0;
        float[] weights = null;
        float threshold = 0;

        public Perceptron(Neuron[] Inputs, float bias, float[] weights, float threshold) {
            this.Inputs = Inputs;
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        @Override
        public void updateInfo(float bias, float[] weights, float threshold) {
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        @Override
        public void setInputs(Neuron[] Inputs, float bias, float[] weights, float threshold) {
            this.Inputs = Inputs;
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        @Override
        public float getOutput() {

            float total = 0;

            for (int i = 0; i < Inputs.length; i++) {
                total += Inputs[i].getOutput() * weights[i];
            }

            return total + bias > threshold ? 1 : 0;
        }

        @Override
        public float getThreshold() {
            return threshold;
        }

        @Override
        public float getBias() {
            return bias;
        }

        @Override
        public float[] getWeights() {
            return weights;
        }

    }

    public static class SigmoidNeuron implements Neuron {

        Neuron[] Inputs = null;
        float bias = 0;
        float[] weights = null;
        float threshold = 0;

        public SigmoidNeuron(Neuron[] Inputs, float bias, float[] weights, float threshold) {
            this.Inputs = Inputs;
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        @Override
        public void updateInfo(float bias, float[] weights, float threshold) {
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        @Override
        public void setInputs(Neuron[] Inputs, float bias, float[] weights, float threshold) {
            this.Inputs = Inputs;
            this.bias = bias;
            this.weights = weights;
            this.threshold = threshold;
        }

        public static float S(float t) {
            return (float) (1f / (1f + Math.exp(-t)));
        }

        @Override
        public float getOutput() {

            float total = 0;

            for (int i = 0; i < Inputs.length; i++) {
                total += Inputs[i].getOutput() * weights[i];
            }

            return S(total);
        }

        @Override
        public float getThreshold() {
            return threshold;
        }

        @Override
        public float getBias() {
            return bias;
        }

        @Override
        public float[] getWeights() {
            return weights;
        }

    }

    public static class Layer {

        Neuron[] neurons = null;

        Layer parent = null;

        float defaultWeight, defaultBias, defaultThreshold;

        public boolean isInputLayer = false;

        public Layer(int length, Class<? extends Neuron> type, float defaultWeight, float defaultBias, float defaultThreshold) {
            neurons = (Neuron[]) Array.newInstance(type, length);
            this.defaultWeight = defaultWeight;
            this.defaultBias = defaultBias;
            this.defaultThreshold = defaultThreshold;

            for (int i = 0; i < length; i++) {
                try {
                    if (type == InputNeuron.class) {
                        neurons[i] = type.getConstructor(float.class).newInstance(0);
                    } else {
                        neurons[i] = type.getConstructor(Neuron[].class, float.class, float[].class, float.class).newInstance(null, 0, null, 0);
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    ex.printStackTrace();
                }
            }

        }

        /**
         * @param neuron neuron index
         * @param newVal new Value
         * @param type 0=bias, 1=threshold, 2+=weight(#)
         */
        public void tweakNeuron(int neuron, float newVal, int type) {
            Neuron n = neurons[neuron];
            switch (type) {
                case 0:
                    n.updateInfo(n.getBias(), n.getWeights(), newVal);
                    break;
                case 1:
                    n.updateInfo(n.getBias(), n.getWeights(), newVal);
                    break;
                default:
                    float[] weights = n.getWeights().clone();
                    weights[type - 2] = newVal;
                    n.updateInfo(n.getBias(), weights, n.getThreshold());
                    break;
            }

        }

        /**
         * See tweakNeuron
         */
        public void tweakNeuronDelta(int neuron, float delta, int type) {
            Neuron n = neurons[neuron];
            switch (type) {
                case 0:
                    n.updateInfo(n.getBias() + delta, n.getWeights(), n.getThreshold());
                    break;
                case 1:
                    n.updateInfo(n.getBias(), n.getWeights(), n.getThreshold() + delta);
                    break;
                default:
                    float[] weights = n.getWeights().clone();
                    System.out.println(weights[type - 2]);
                    weights[type - 2] = weights[type - 2] + delta;
                    System.out.println(weights[type - 2]);
                    n.updateInfo(n.getBias(), weights, n.getThreshold());
                    break;
            }

        }

        public void setInputLayer() {
            isInputLayer = true;
        }

        public void setParentLayer(Layer parent) {
            isInputLayer = false;
            this.parent = parent;

            float[] weights = new float[parent.neurons.length];

            for (int i = 0; i < parent.neurons.length; i++) {
                weights[i] = defaultWeight;
            }

            for (Neuron n : neurons) {
                if (n != null) {
                    n.setInputs(parent.neurons, defaultBias, weights, defaultThreshold);
                }
            }

        }

        public void initGaus(long l) {
            Random r = new Random(l);
            for (Neuron n : neurons) {
                float[] weights = new float[parent.neurons.length];
                for(int i = 0; i < parent.neurons.length; i++){
                    weights[i] = (float) r.nextGaussian();
                }
                n.updateInfo((float) r.nextGaussian(), weights, defaultThreshold);
            }
        }

        public void setNeuronInput(int neuron, float input) {
            if (isInputLayer) {
                ((InputNeuron) neurons[neuron]).setInput(input);
            }
        }

        public float getNeuronOutput(int neuron) {
            return neurons[neuron].getOutput();
        }

        public void setInput(IOVector v) {
            float[] data = v.getData();

            if (!isInputLayer) {
                return;
            }

            for (int i = 0; i < neurons.length; i++) {
                ((InputNeuron) neurons[i]).setInput(data[i]);
            }

        }

        public IOVector getOutput() {
            float[] v = new float[neurons.length];
            for (int i = 0; i < neurons.length; i++) {
                v[i] = neurons[i].getOutput();
            }

            return new IOVector(v);
        }

    }

    public static class IOVector {

        float[] vector = null;

        public IOVector(float[] vector) {
            this.vector = vector;
        }

        public float[] getData() {
            return vector;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < vector.length; i++) {
                sb.append(vector[i]).append(i + 1 != vector.length ? ", " : "");
            }

            return sb.toString();

        }

    }

    public static float C(IOVector Output, IOVector Goal) {

        float[] output = Output.getData();
        float[] goal = Goal.getData();
        float[] subbed = new float[output.length];

        for (int i = 0; i < output.length; i++) {
            subbed[i] = (float) Math.pow(Math.abs(output[i] - goal[i]), 2) / 2f;
        }

        float tot = 0;

        for (float t : subbed) {
            tot += t;
        }

        tot /= subbed.length;

        return tot;
    }

    public static void main(String[] args) {
        Layer input = new Layer(10, InputNeuron.class, 1, 0, 1);
        Layer hidden1 = new Layer(10, SigmoidNeuron.class, 1, 0, 1);
        Layer output = new Layer(3, SigmoidNeuron.class, 1, 0, 1);

        input.setInputLayer();
        hidden1.setParentLayer(input);
        output.setParentLayer(hidden1);

        hidden1.initGaus(50);
        output.initGaus(25);
        
        for(int i = 0; i < 10; i++)
            input.setNeuronInput(i, 1f);
        
        System.out.println(input.getOutput());
        
        System.out.println(hidden1.getOutput());
        
        System.out.println(output.getOutput());
        
        System.out.println(C(output.getOutput(), new IOVector(new float[]{10, 10, 10})));

    }

}
