package treegp.gp;

import com.google.gson.Gson;
import genetics.chromosome.Chromosome;
import genetics.utils.Observation;
import lombok.Getter;
import lombok.Setter;
import treegp.program.TreeNode;
import treegp.solver.TreeGP;

@Getter
@Setter
public class TGPChromosome extends Chromosome {

    private TreeNode root;
    private transient TreeGP manager;

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

    public double eval(Observation observation) {
        return root.eval(observation);
    }

    public TGPChromosome deserialization(String json) {
        return new Gson().fromJson(json, TGPChromosome.class);
    }

}

