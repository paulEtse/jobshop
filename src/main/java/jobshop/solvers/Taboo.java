package jobshop.solvers;


import jobshop.encodings.Task;

import java.util.Vector;

public class Taboo {
    private Vector<Task> tasks;
    private int[][] matriceTaboo;
    private int dureeTaboo;
    DescentSolver.Swap swap;
    public Taboo(int dureeTaboo,int nbJob, int nbMachine){
        tasks = new Vector<Task>();
        matriceTaboo = new int [nbJob*nbMachine][nbJob*nbMachine];
        for(int job = 0;job < nbJob; job++)
            for(int task = 0;task <nbMachine; task++)
                tasks.add(new Task(job,task));
        this.dureeTaboo= dureeTaboo;
    }
    public void add(Task task1,Task task2, int k){
        matriceTaboo[tasks.indexOf(task1)][tasks.indexOf(task2)]= dureeTaboo+k;
    }
    public boolean isTaboo(Task task1,Task task2, int k){
        return k < matriceTaboo[tasks.indexOf(task1)][tasks.indexOf(task2)];
    }
}
