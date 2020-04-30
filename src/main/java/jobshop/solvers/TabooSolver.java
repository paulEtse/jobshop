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
        ResourceOrder current,currentBest;
        List<DescentSolver.Block> blocks = DescentSolver.blocksOfCriticalPath(s.copy());
        for(DescentSolver.Block block: blocks) {
            swaps.addAll(DescentSolver.neighbors(block));
        }
        currentBest = s.copy();
        for(int i=0;i<swaps.size();i++){
            current = s.copy();
            Task task1= s.tasksByMachine[swaps.get(i).machine][swaps.get(i).t1];
            Task task2= s.tasksByMachine[swaps.get(i).machine][swaps.get(i).t2];
            if(!sTaboo.isTaboo(task1,task2,k)){
                swaps.get(i).applyOn(current);
                if(current.toSchedule().makespan()<s.toSchedule().makespan()){
                    sTaboo.add(task2,task1,k);
                    if(current.toSchedule().makespan()<currentBest.toSchedule().makespan()){
                        currentBest=current;
                    }
                }
                else
                    sTaboo.add(task1,task2,k);
            }
            else{
                //solution tabou améliorant
                swaps.get(i).applyOn(current);
                if(current.toSchedule().makespan()<currentBest.toSchedule().makespan()){
                    currentBest=current;
                }
            }
        }
        return currentBest;
    }
/*
    ResourceOrder getBest(ResourceOrder s, Taboo sTaboo,int k){
        List<DescentSolver.Swap> swaps= new ArrayList();
        ResourceOrder currentBest=s.copy();
        Vector<DescentSolver.Swap> twinSwaps;
        int machine;
        List<DescentSolver.Block> blocks = DescentSolver.blocksOfCriticalPath(s.copy());
        for(DescentSolver.Block block: blocks) {
            twinSwaps=new Vector<DescentSolver.Swap>();
            twinSwaps.addAll(DescentSolver.neighbors(block));
            machine = twinSwaps.get(0).machine;
            if(twinSwaps.size()==2)
            {
                Task task1=s.tasksByMachine[machine][twinSwaps.get(0).t1];
                Task task2=s.tasksByMachine[machine][twinSwaps.get(0).t2];
                Task task3=s.tasksByMachine[machine][twinSwaps.get(1).t1];
                Task task4=s.tasksByMachine[machine][twinSwaps.get(1).t2];
                if(!sTaboo.isTaboo(task1,task2,k) && !sTaboo.isTaboo(task3,task4,k)) {
                    ResourceOrder sol1 = s.copy();
                    ResourceOrder sol2 = s.copy();
                    swaps.get(0).applyOn(sol1);
                    swaps.get(1).applyOn(sol2);
                    if(sol1.toSchedule().makespan()>sol2.toSchedule().makespan()){
                        currentBest=evaluateSol(currentBest,sol1);
                        sTaboo.add(s.tasksByMachine[machine][twinSwaps.get(1).t1],s.tasksByMachine[machine][twinSwaps.get(1).t2],k);
                    }
                    else{
                        currentBest=evaluateSol(currentBest,sol2);
                        sTaboo.add(s.tasksByMachine[machine][twinSwaps.get(0).t1],s.tasksByMachine[machine][twinSwaps.get(0).t2],k);
                    }
                }
                else if(!sTaboo.isTaboo(task1,task2,k)){
                    ResourceOrder sol = s.copy();
                    swaps.get(0).applyOn(sol);
                    currentBest=evaluateSol(currentBest,sol);
                }
                // !sTabou.isTaboo(task3,task4)
                else {
                    ResourceOrder sol = s.copy();
                    swaps.get(1).applyOn(sol);
                    currentBest=evaluateSol(currentBest,sol);
                }
            }
            //twin.size() == 1 and not taboo
            else if(sTaboo.isTaboo(s.tasksByMachine[machine][twinSwaps.get(0).t1],s.tasksByMachine[machine][twinSwaps.get(0).t2],k)){
                ResourceOrder sol = s.copy();
                twinSwaps.get(0).applyOn(sol);
                evaluateSol(currentBest,sol);
            }
        }
        return currentBest;
    }
 */
  /*  ResourceOrder evaluateSol(ResourceOrder best, ResourceOrder sol){
        return best.toSchedule().makespan()>sol.toSchedule().makespan()?best:sol;
    }
   ResourceOrder getBest(ResourceOrder s,Vector<ResourceOrder> sTabou){
        ResourceOrder currentBest=null;
        List<DescentSolver.Block> blocks = DescentSolver.blocksOfCriticalPath(s.copy());
        for(DescentSolver.Block block: blocks){
            List<DescentSolver.Swap> swaps = DescentSolver.neighbors(block);
            currentBest = s.copy();
            swaps.get(0).applyOn(currentBest);
            for(int i=1;i<swaps.size();i++){
                ResourceOrder current = s.copy();
                swaps.get(i).applyOn(current);
                if(!sTabou.contains(current) && current.toSchedule().makespan()<currentBest.toSchedule().makespan()){
                    currentBest=current;
                }
            }
        }
        return currentBest;
    }*/
}
