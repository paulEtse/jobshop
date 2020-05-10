package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;

public class TabooSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {
        int dureeTaboo = 5;
        Taboo sTaboo = new Taboo(dureeTaboo,instance.numJobs,instance.numMachines);
        int kMax=10;
        ResourceOrder sInit = new ResourceOrder(new est_lrptSolver().solve(instance,deadline).schedule);
        ResourceOrder best = sInit;
        ResourceOrder s = sInit;
        int k=0;
        ResourceOrder sPrim;
        //
        while (k < kMax && deadline - System.currentTimeMillis() > 1){
            k++;
            /*==============*/
            sPrim=getBest(s,sTaboo,k);
            s = sPrim;
            if(sPrim.toSchedule().makespan()<best.toSchedule().makespan()){
                best=sPrim;
            }
            /*==============*/
        }
        return new Result(instance,best.toSchedule(),Result.ExitCause.Timeout);
    }

    ResourceOrder getBest(ResourceOrder s,Taboo sTaboo,int k){
        List<DescentSolver.Swap> swaps= new ArrayList();
        ResourceOrder current,currentBest=null;
        List<DescentSolver.Block> blocks = DescentSolver.blocksOfCriticalPath(s);
        for(DescentSolver.Block block: blocks) {
            swaps.addAll(DescentSolver.neighbors(block));
        }
        boolean first = true;
        for(int i=0;i<swaps.size();i++){
            current = s.copy();
            Task task1= s.tasksByMachine[swaps.get(i).machine][swaps.get(i).t1];
            Task task2= s.tasksByMachine[swaps.get(i).machine][swaps.get(i).t2];
            if(!sTaboo.isTaboo(task1,task2,k)){
                swaps.get(i).applyOn(current);

                //Gestion de la solution
                if(first)
                {
                    currentBest = current;
                    first = false;
                }
                else if (current.toSchedule().makespan() < currentBest.toSchedule().makespan()) {
                    currentBest=current;
                }

                //Gestion de Tabou
                if(current.toSchedule().makespan() < s.toSchedule().makespan()){
                    sTaboo.add(task1,task2,k);
                }
                else
                    sTaboo.add(task2,task1,k);
            }
            else{
                //solution tabou améliorant
                swaps.get(i).applyOn(current);
                if(first)
                {
                    currentBest = current;
                    first = false;
                }
                else if (current.toSchedule().makespan() < currentBest.toSchedule().makespan()) {
                    currentBest=current;
                }
            }
        }
        return currentBest;
    }
}
