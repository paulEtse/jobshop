package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.RessourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class lrptSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {
        RessourceOrder sol= new RessourceOrder(instance);
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        int duration[] = new int[instance.numJobs];
        for(int job=0;job<instance.numJobs;job++)
            for(int task=0;task<instance.numMachines;task++)
            {
                int time = instance.duration(job,task);
                duration[job]+=time;
            }
        Vector<Task> readyTodo=new Vector<Task>();
        //Place where the next task can be done on the machine
        int next[]=new int[instance.numJobs];
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        while(nbTaskRemaining>0)
        {
            Task current = lrptBest(readyTodo, duration);
            int machine = instance.machine(current.job,current.task);
            sol.resources[machine][next[machine]] = current;
            next[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
        }
        return new Result(instance,sol.toSchedule(),Result.ExitCause.Timeout);
    }

    Task lrptBest(Vector<Task> T, int[] duration){
        Task task=T.elementAt(0);
        Task t;
        for( int i=1;i<T.size();i++){
            t=T.elementAt(i);
            if (duration[t.job] > duration[task.job])
                task=t;
        }
        return task;
    }
}
