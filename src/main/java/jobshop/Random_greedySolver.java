package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Random;

public class Random_greedySolver implements Solver {
    Random generator = new Random(0);
    @Override
    public Result solve(Instance instance, long deadline) {
        ResourceOrder sol = new ResourceOrder(instance);
        for(int job=0;job < instance.numJobs;job++)
            for(int task = 0; task < instance.numMachines;task++)
            {
                int machine=instance.machine(job,task);
                sol.tasksByMachine[machine][sol.nextFreeSlot[machine]++]=new Task(job,task);
            }
        ResourceOrder s;
        Schedule best = sol.toSchedule();
        boolean t = true;
        while (deadline - System.currentTimeMillis()>1){
            s = getRandomSol(sol.copy(),generator);
            try {
                if( s.toSchedule().makespan() < best.makespan())
                    best=s.toSchedule();
            }
            catch (Exception e){
                //Not realisable
                //System.out.println("poblème");
            }
        }
        return new Result(instance,best, Result.ExitCause.Blocked);
    }
    ResourceOrder getRandomSol(ResourceOrder s,Random rand){
        for(int machine= 0;machine<s.instance.numMachines;machine++){
            for (int i = 0; i < s.instance.numJobs; i++) {
                int randomIndexToSwap = rand.nextInt(s.instance.numJobs);
                Task Temp = s.tasksByMachine[machine][randomIndexToSwap];
                s.tasksByMachine[machine][randomIndexToSwap] = s.tasksByMachine[machine][i];
                s.tasksByMachine[machine][i] = Temp;
            }
        }
        return s;
    }
}
