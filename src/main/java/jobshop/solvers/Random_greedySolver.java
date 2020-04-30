package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Random;
import java.util.Vector;

public class Random_greedySolver implements Solver {
    Random generator = new Random(0);
    @Override
    public Result solve(Instance instance, long deadline) {

        ResourceOrder best = new ResourceOrder(instance);
        for(int job=0;job<instance.numJobs;job++){
            for(int task=0;task<instance.numTasks;task++)
                best.tasksByMachine[instance.machine(job,task)][best.nextFreeSlot[instance.machine(job,task)]++]=new Task(job,task);
        }
        Schedule bestSche = best.toSchedule();
        Schedule solSche;
        while(deadline - System.currentTimeMillis() > 1){
            ResourceOrder sol= new ResourceOrder(instance);
            int nextTaskOnMachine[] = new int[instance.numMachines];
            Vector<Task> readyTodo=new Vector<Task>();
            for (int job = 0; job < instance.numJobs; job++) {
                readyTodo.add(new Task(job,0));
            }
            int nbTaskRemaining=instance.numJobs*instance.numMachines;
            for(int i = nbTaskRemaining;i>0;i--){
                int randomIndexToSwap = generator.nextInt(readyTodo.size());
                Task current=  readyTodo.get(randomIndexToSwap);
                int machine = instance.machine(current.job,current.task);
                sol.tasksByMachine[machine][nextTaskOnMachine[machine]] = current;
                nextTaskOnMachine[machine]++;
                readyTodo.remove(current);
                if(current.task<instance.numMachines-1)
                {
                    readyTodo.add(new Task(current.job,current.task+1));
                }
            }
            solSche = sol.toSchedule();
            if(solSche.makespan()<bestSche.makespan()){
                bestSche = solSche;
            }
        }
        return new Result(instance,bestSche,Result.ExitCause.Blocked);
    }
}
