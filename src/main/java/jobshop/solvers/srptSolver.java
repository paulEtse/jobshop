package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class srptSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {
        ResourceOrder sol= new ResourceOrder(instance);
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        int remainingTime[] = new int[instance.numJobs];
        Vector<Task> readyTodo=new Vector<Task>();
        //Place where the next task can be done on the machine
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
            for(int task=0;task<instance.numMachines;task++)
            {
                int duration = instance.duration(job,task);
                remainingTime[job]+=duration;
            }
        }
        for(int i = nbTaskRemaining;i>0;i--){
            Task current = srptBest(readyTodo, remainingTime);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = current;
            sol.nextFreeSlot[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
            remainingTime[current.job]-=instance.duration(current.job,current.task);
        }
        return new Result(instance,sol.toSchedule(),Result.ExitCause.Timeout);
    }

    Task srptBest(Vector<Task> T, int[] remainingTime){
        Task task=T.elementAt(0);
            Task t;
            for( int i=1;i<T.size();i++){
                t=T.elementAt(i);
                if (remainingTime[t.job] < remainingTime[task.job])
                    task=t;
            }
            return task;
        }
}
