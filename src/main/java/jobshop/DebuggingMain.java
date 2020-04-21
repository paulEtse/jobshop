package jobshop;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import jobshop.solvers.DescentSolver;
import jobshop.solvers.srptSolver;

import java.io.IOException;
import java.nio.file.Paths;

public class DebuggingMain {

    public static void main(String[] args) {
        try {
            // load the aaa1 instance
            Instance instance = Instance.fromFile(Paths.get("instances/ft06"));

            // construit une solution dans la représentation par
            // numéro de jobs : [0 1 1 0 0 1]
            // Note : cette solution a aussi été vue dans les exercices (section 3.3)
            //        mais on commençait à compter à 1 ce qui donnait [1 2 2 1 1 2]
            /*JobNumbers enc = new JobNumbers(instance);
            enc = new JobNumbers(instance);
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 1;
            enc.jobs[enc.nextToSet++] = 1;
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 1;

            System.out.println("\nENCODING: " + enc);

            Schedule sched = enc.toSchedule();
            // TODO: make it print something meaningful
            // by implementing the toString() method
            System.out.println("SCHEDULE: " + sched);
            System.out.println("VALID: " + sched.isValid());
            System.out.println("MAKESPAN: " + sched.makespan());

*/
            int Dmax=0;
            for(int job=0;job<instance.numJobs;job++)
                for(int task=0;task<instance.numMachines;task++){
                    Dmax+=instance.duration(job,task);
                }
            System.out.println(Dmax);
            /*System.out.println("Test  2 ");
            ResourceOrder enc2=new ResourceOrder(instance);
            long dealine=11111;
            Result enc3= new DescentSolver().solve(instance,dealine);
            Schedule sched2 = enc2.toSchedule();
            System.out.println("schedule"+sched2);*/
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
