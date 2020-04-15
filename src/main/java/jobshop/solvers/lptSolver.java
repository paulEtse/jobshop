package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class lptSolver implements Solver {
    public Result solve(Instance instance, long deadline) {
        ResourceOrder sol= new ResourceOrder(instance);
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        Vector<Task> readyTodo=new Vector<Task>();
        //Place where the next task can be done on the machine
        int next[]=new int[instance.numJobs];
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        while(nbTaskRemaining>0)
        {
            Task current = lptBest(readyTodo,instance);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][next[machine]] = current;
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
    Task lptBest(Vector<Task> T,Instance instance){
        Task task=T.elementAt(0);
        Task t;
        for( int i=0;i<T.size();i++){
            t=T.elementAt(i);
            if (instance.duration(t.job,t.task) > instance.duration(task.job,task.task))
                task=t;
        }
        return task;
    }
}
