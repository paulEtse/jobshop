package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class est_sptSolver implements Solver {
    public Result solve(Instance instance, long deadline) {
        ResourceOrder sol= new ResourceOrder(instance);
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        Vector<Task> readyTodo=new Vector<Task>();
        int startTime[] = new int[instance.numJobs];
        //Place where the next task can be done on the machine
        int next[]=new int[instance.numJobs];
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        while(nbTaskRemaining>0)
        {
            Task current = est_sptBest(readyTodo,startTime,instance);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][next[machine]] = current;
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


    public static ResourceOrder getSol(Instance instance, long deadline) {
        ResourceOrder sol= new ResourceOrder(instance);
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        Vector<Task> readyTodo=new Vector<Task>();
        int startTime[] = new int[instance.numJobs];
        //Place where the next task can be done on the machine
        int next[]=new int[instance.numJobs];
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        while(nbTaskRemaining>0)
        {
            Task current = est_sptBest(readyTodo,startTime,instance);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][next[machine]] = current;
            next[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
            startTime[current.job]+=instance.duration(current.job,current.task);
        }
        return sol;
    }

     static Task est_sptBest(Vector<Task> T, int[] startTime, Instance instance){
        Task task=T.elementAt(0);
        Task t;
        for( int i=0;i<T.size();i++){
            t=T.elementAt(i);
            if(startTime[t.job]<startTime[task.job])
                task=t;
            else if (startTime[t.job]==startTime[task.job] && instance.duration(t.job,t.task) < instance.duration(task.job,task.task))
                task=t;
        }
        return task;
    }
}
