package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.Vector;

public class Est_LrptSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {
        ResourceOrder sol= new ResourceOrder(instance);
        int remainingDuration[] = new int[instance.numJobs];
        int nbTaskRemaining=instance.numJobs*instance.numMachines;
        Vector<Task> readyTodo=new Vector<Task>();
        int startTimeOnJob[] = new int[instance.numJobs];
        //Place where the next task can be done on the machine
        int startTimeOnMachine[]= new int[instance.numMachines];
        //Place where the next task can be done on the machine
        for(int job=0;job<instance.numJobs;job++)
            for(int task=0;task<instance.numMachines;task++)
            {
                int time = instance.duration(job,task);
                remainingDuration[job]+=time;
            }
        for (int job = 0; job < instance.numJobs; job++) {
            readyTodo.add(new Task(job,0));
        }
        for(int i = nbTaskRemaining;i>0;i--){
            Task current = est_sptBest(readyTodo,startTimeOnJob,startTimeOnMachine,remainingDuration,instance);
            int machine = instance.machine(current.job,current.task);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = current;
            sol.nextFreeSlot[machine]++;
            readyTodo.remove(current);
            if(current.task<instance.numMachines-1)
            {
                readyTodo.add(new Task(current.job,current.task+1));
            }
            nbTaskRemaining--;
            remainingDuration[current.job]-=instance.duration(current.job,current.task);
            int start = Math.max(startTimeOnJob[current.job],startTimeOnMachine[machine]) + instance.duration(current.job,current.task);
            startTimeOnJob[current.job]=start;
            startTimeOnMachine[machine]=start;
        }
        return new Result(instance,sol.toSchedule(),Result.ExitCause.Timeout);
    }
    private static Task est_sptBest(Vector<Task> T,int[] startTimeOnJob,int[] startTimeOnMachine, int[] remaining,Instance instance){
        Task task=T.elementAt(0);
        int beginTask = Math.max(startTimeOnMachine[instance.machine(task.job,task.task)],startTimeOnJob[task.job]);
        Task t;
        for( int i=1;i<T.size();i++){
            t=T.elementAt(i);
            int beginT = Math.max(startTimeOnMachine[instance.machine(t.job,t.task)],startTimeOnJob[t.job]);
            if(beginT < beginTask || (beginT==beginTask && remaining[t.job] > remaining[task.job]))
            {
                task=t;
                beginTask=beginT;
            }
        }
        return task;
    }
}
