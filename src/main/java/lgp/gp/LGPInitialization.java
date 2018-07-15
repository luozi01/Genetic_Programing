package lgp.gp;


import genetics.chromosome.Chromosome;
import genetics.interfaces.Initialization;
import lgp.program.Instruction;
import lgp.solver.LinearGP;

import java.util.ArrayList;
import java.util.List;

public class LGPInitialization implements Initialization {

    private final LinearGP manager;

    LGPInitialization(LinearGP manager) {
        this.manager = manager;
    }

    @Override
    public List<Chromosome> generate() {
        List<Chromosome> pop = new ArrayList<>();
        if (manager.getInitialization() == lgp.enums.LGPInitialization.CONSTANT_LENGTH) {
            pop = initializeWithConstantLength();
        } else if (manager.getInitialization() == lgp.enums.LGPInitialization.VARIABLE_LENGTH) {
            pop = initializeWithVariableLength();
        }
        return pop;
    }

    private List<Chromosome> initializeWithVariableLength() {
        List<Chromosome> list = new ArrayList<>();
        for (int i = 0; i < manager.getPopulationSize(); ++i) {
            int length = manager.getRandEngine().nextInt(manager.getPopInitMinProgramLength(),
                    manager.getPopInitMaxProgramLength());
            list.add(initialize(length));
        }
        return list;
    }

    private List<Chromosome> initializeWithConstantLength() {
        List<Chromosome> list = new ArrayList<>();
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
