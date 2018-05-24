package treegp.gp;

import genetics.utils.Observation;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.genetics.Chromosome;
import treegp.program.TreeNode;
import treegp.solver.TreeGP;

@Getter
@Setter
public class TGPChromosome extends Chromosome {

    private TreeNode root;
    private TreeGP manager;

    TGPChromosome(TreeNode root, TreeGP manager) {
        this.root = root;
        this.manager = manager;
    }

    public TGPChromosome makeCopy() {
        return new TGPChromosome(root.copy(), manager);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public double eval() {
        return root.eval();
    }

    @Override
    public double fitness() {
        double diff = 0;
        for (Observation o : manager.getTargets()) {
            for (int i = 0; i < o.inputCount(); i++) {
                manager.setVariable(o.getTextInput(i), o.getInput(i));
            }
            double targetValue = o.getOutput(0);
            double calculatedValue = root.eval();
            diff += Math.pow(targetValue - calculatedValue, 2);
        }
        return diff;
    }
}

