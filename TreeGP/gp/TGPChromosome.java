package com.zluo.tgp.gp;

import com.zluo.ga.Chromosome;
import com.zluo.tgp.program.TreeNode;
import com.zluo.tgp.solver.TreeGP;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class TGPChromosome implements Chromosome<TGPChromosome> {

    private TreeNode root;
    private TreeGP manager;

    TGPChromosome(TreeNode root, TreeGP manager) {
        this.root = root;
        this.manager = manager;
    }

    @Override
    public List<TGPChromosome> crossover(TGPChromosome chromosome, double uniformRate) {
        Crossover.apply(this, chromosome, manager);
        return new LinkedList<>(list(this, chromosome));
    }

    @Override
    public void mutate(double mutationRate) {
        if (mutationRate == 0) MicroMutation.apply(this, manager);
        else MacroMutation.apply(this, manager);
    }

    @Override
    public TGPChromosome makeCopy() {
        return new TGPChromosome(root.copy(), manager);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    @SafeVarargs
    private final <T> List<T> list(T... items) {
        List<T> list = new LinkedList<>();
        Collections.addAll(list, items);
        return list;
    }

    public double eval() {
        return root.eval();
    }
}

