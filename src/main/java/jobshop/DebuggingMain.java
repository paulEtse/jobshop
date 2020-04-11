package jobshop;

import jobshop.encodings.RessourceOrder;
import jobshop.encodings.Task;
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
            System.out.println("Test  2 ");
            RessourceOrder enc2=new RessourceOrder(instance);
            long dealine=11111;
            Result enc3= new srptSolver().solve(instance,dealine);
            enc2.resources[0][0]=new Task(0,0);
            enc2.resources[0][1]=new Task(1,1);
            enc2.resources[1][0]=new Task(0,1);
            enc2.resources[1][1]=new Task(1,0);
            enc2.resources[2][0]=new Task(0,2);
            enc2.resources[2][1]=new Task(1,2);
            Schedule sched2 = enc2.toSchedule();
            System.out.println("schedule"+sched2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
