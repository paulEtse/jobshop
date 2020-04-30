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
        int startTimeOnJob[] = new int[instance.numJobs];
        //Place where the next task can be done on the machine
        int startTimeOnMachine[]= new int[instance.numMachines];
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        for(int i = nbTaskRemaining;i>0;i--){
            Task current = est_sptBest(readyTodo,startTimeOnJob,startTimeOnMachine,instance);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = current;
            sol.nextFreeSlot[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
            int start = Math.max(startTimeOnJob[current.job],startTimeOnMachine[machine]);
            startTimeOnJob[current.job]=start + instance.duration(current.job,current.task);
            startTimeOnMachine[machine]=start + instance.duration(current.job,current.task);
        }
        return new Result(instance,sol.toSchedule(),Result.ExitCause.Timeout);
    }
     private static Task est_sptBest(Vector<Task> T, int[] startTimeOnJob,int[] startTimeOnMachine, Instance instance){
        Task task=T.elementAt(0);
        int beginTask = Math.max(startTimeOnMachine[instance.machine(task.job,task.task)],startTimeOnJob[task.job]);
        Task t;
        for( int i=1;i<T.size();i++){
            t=T.elementAt(i);
            int beginT = Math.max(startTimeOnMachine[instance.machine(t.job,t.task)],startTimeOnJob[t.job]);
            if(beginT<beginTask || (beginT==beginTask && instance.duration(t.job,t.task) < instance.duration(task.job,task.task)))
            {
                task=t;
                beginTask=beginT;
            }
        }
        return task;
    }
}
