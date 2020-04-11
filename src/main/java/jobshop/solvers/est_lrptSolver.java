package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.RessourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class est_lrptSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {
        RessourceOrder sol= new RessourceOrder(instance);
        int duration[] = new int[instance.numJobs];
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        Vector<Task> readyTodo=new Vector<Task>();
        int startTime[] = new int[instance.numJobs];
        //Place where the next task can be done on the machine
        int next[]=new int[instance.numJobs];
        for(int job=0;job<instance.numJobs;job++)
            for(int task=0;task<instance.numMachines;task++)
            {
                int time = instance.duration(job,task);
                duration[job]+=time;
            }
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        while(nbTaskRemaining>0)
        {
            Task current = est_sptBest(readyTodo,startTime,duration);
            int machine = instance.machine(current.job,current.task);
            sol.resources[machine][next[machine]] = current;
            next[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
            startTime[current.job]+=instance.duration(current.job,current.task);
        }
        return new Result(instance,sol.toSchedule(),Result.ExitCause.Timeout);
    }
    Task est_sptBest(Vector<Task> T,int[] startTime, int[] duration){
        Task task=T.elementAt(0);
        Task t;
        for( int i=0;i<T.size();i++){
            t=T.elementAt(i);
            if(startTime[t.job]<startTime[task.job])
                task=t;
            else if (startTime[t.job]==startTime[task.job] && duration[t.job] > duration[task.job])
                task=t;
        }
        return task;
    }
}
