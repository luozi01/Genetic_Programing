package com.zluo.lgp.gp;

import com.zluo.ga.Generator;
import com.zluo.lgp.enums.LGPInitialization;
import com.zluo.lgp.program.Instruction;
import com.zluo.lgp.solver.LinearGP;

import java.util.ArrayList;
import java.util.List;

public class LGPGenerator implements Generator<LGPChromosome> {

    private LinearGP manager;

    public LGPGenerator(LinearGP manager) {
        this.manager = manager;
    }

    @Override
    public List<LGPChromosome> generate() {
        List<LGPChromosome> pop = new ArrayList<>();
        if (manager.getInitialization() == LGPInitialization.CONSTANT_LENGTH) {
            pop = initializeWithConstantLength();
        } else if (manager.getInitialization() == LGPInitialization.VARIABLE_LENGTH) {
            pop = initializeWithVariableLength();
        }
        return pop;
    }

    private List<LGPChromosome> initializeWithVariableLength() {
        List<LGPChromosome> list = new ArrayList<>();
        for (int i = 0; i < manager.getPopulationSize(); ++i) {
            int length = manager.getRandEngine().nextInt(manager.getPopInitMinProgramLength(),
                    manager.getPopInitMaxProgramLength());
            list.add(initialize(length));
        }
        return list;
    }

    private List<LGPChromosome> initializeWithConstantLength() {
        List<LGPChromosome> list = new ArrayList<>();
        for (int i = 0; i < manager.getPopulationSize(); ++i) {
            list.add(initialize(manager.getPopInitConstantProgramLength()));
        }
        return list;
    }

    private LGPChromosome initialize(int instructionCount) {
        LGPChromosome chromosome = new LGPChromosome(manager);
        chromosome.addConstant(manager.getConstantSet());

        final int registerCount = manager.getRegisterCount();
        chromosome.addRegister(registerCount);

        chromosome.addOperators(manager.getOperatorSet());

        for (int i = 0; i < instructionCount; ++i) {
            Instruction newInst = new Instruction();
            newInst.initialize(chromosome, manager.getRandEngine());
            chromosome.getInstructions().add(newInst);
        }
        return chromosome;
    }
}
