package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DescentSolver implements Solver {

    /* * A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    static class Block {
        /** machine on which the block is identified */
        final int machine;
        /** index of the first task of the block */
        final int firstTask;
        /** index of the last task of the block */
        final int lastTask;

        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swam with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    static class Swap {
        // machine on which to perform the swap
        final int machine;
        // index of one task to be swapped
        final int t1;
        // index of the other task to be swapped
        final int t2;

        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            this.t1 = t1;
            this.t2 = t2;
        }

        /** Apply this swap on the given resource order, transforming it into a new solution. */
        public void applyOn(ResourceOrder order) {
            Task aux = order.tasksByMachine[this.machine][this.t1];
            order.tasksByMachine[this.machine][this.t1]= order.tasksByMachine[this.machine][this.t2];
            order.tasksByMachine[this.machine][this.t2]= aux;
        }
    }


    @Override
    public Result solve(Instance instance, long deadline) {
        ResourceOrder best = new ResourceOrder(new est_lrptSolver().solve(instance,deadline).schedule);
        //ResourceOrder best = new ResourceOrder(new Random_greedySolver().solve(instance,deadline).schedule);
        boolean change = true;
        ResourceOrder currentBest;
        //&& deadline - System.currentTimeMillis() > 1
        while (change ){
            change=false;
            /*==============*/
            currentBest=getBestNeighbour(best);
            /*==============*/
            if(currentBest.toSchedule().makespan()<best.toSchedule().makespan()){
                best=currentBest;
                change=true;
            }
        }
        return new Result(instance,best.toSchedule(),Result.ExitCause.Timeout);
    }
    ResourceOrder getBestNeighbour(ResourceOrder s){
        List<Swap> swaps= new ArrayList();

        ResourceOrder current,currentBest;
        List<Block> blocks = blocksOfCriticalPath(s.copy());
        for(Block block: blocks) {
            swaps.addAll(neighbors(block));
        }
        currentBest = s.copy();
        for(int i=0;i<swaps.size();i++){
            current = s.copy();
            swaps.get(i).applyOn(current);
            if(current.toSchedule().makespan()<currentBest.toSchedule().makespan()){
                currentBest=current;
            }
        }
        return currentBest;
    }
    /* Returns a list of all blocks of the critical path. */
    static List<Block> blocksOfCriticalPath(ResourceOrder order) {
        List<Block> blocks = new Vector<Block>();
        Block b;
        int machine=0;
        List<Task> criticalPath = order.toSchedule().criticalPath();
        if(blocks.size()>0)
            machine=order.instance.machine(criticalPath.get(0).job,criticalPath.get(0).task);
        int j,i=0;
        Task t;
        while(i<criticalPath.size())
        {
            t=criticalPath.get(i);
            machine = order.instance.machine(t.job,t.task);
            j=i+1;
            while(j<criticalPath.size()&& order.instance.machine(criticalPath.get(j).job,criticalPath.get(j).task)==machine){
                j++;
            }
            if(j>i+1){
                int x=indexOfTask(order.tasksByMachine[machine],criticalPath.get(i));
                int y=indexOfTask(order.tasksByMachine[machine],criticalPath.get(j-1));
                blocks.add(new Block(machine,x,y));
            }
            i=j;
        }
        return  blocks;
    }
    static int indexOfTask(Task[] Tab,Task t){
        boolean find = false;
        int k=0;
        while(k <Tab.length && !find)
        {
            if(Tab[k].equals(t)){
                find = true;
            }
            else
                k++;
        }
        return k;
    }
    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    static List<Swap> neighbors(Block block) {
        Task aux,t1,t,t2;
        Vector<Swap> swaps = new Vector<Swap>();
        swaps.add(new Swap(block.machine,block.firstTask,block.firstTask+1));
        Swap s;
        if(block.lastTask>block.firstTask+1)
        {
            swaps.add(new Swap(block.machine,block.lastTask-1,block.lastTask));
        }
        return  swaps;
    }

}