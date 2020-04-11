package jobshop.encodings;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Schedule;

public class RessourceOrder extends Encoding {
    public final Task[][] resources;
    //public int[] nextToSet;
    public RessourceOrder(Instance instance) {
        super(instance);
        resources = new Task[instance.numMachines][instance.numJobs];
    }

    public Schedule toSchedule() {
        int[] nextFreeTimeResource = new int[instance.numMachines];
        int[] nextTask = new int[instance.numJobs];
        int[][] startTimes = new int[instance.numJobs][instance.numTasks];
        int nbNotScheduled = instance.numJobs * instance.numMachines;
        while (nbNotScheduled > 0) {
            for (int i = 0; i < instance.numMachines; i++) {
                for (Task t : resources[i]) {
                    if (t.task == nextTask[t.job]) {
                        int est = t.task == 0 ? 0 : startTimes[t.job][t.task - 1] + instance.duration(t.job, t.task - 1);
                        est = Math.max(est, nextFreeTimeResource[i]);
                        startTimes[t.job][t.task] = est;
                        nextFreeTimeResource[i] = est + instance.duration(t.job, t.task);
                        nextTask[t.job]++;
                        nbNotScheduled--;
                    }
                }
            }
        }
        return new Schedule(instance, startTimes);
    }

  /*  public static RessourceOrder fromSchedule(Schedule sched) {
        RessourceOrder res = new RessourceOrder(sched.pb);
        int machine;
        Task best[] = new Task[res.instance.numMachines];
        while (res.nextToSet[0] < res.instance.numJobs) {
            Arrays.fill(best, null);
            for (int i=0; i < res.instance.numJobs; i++) {
                for (int j=0; j < res.instance.numTasks; j++) {
                    machine = res.instance.machine(i, j);
                    if ((res.nextToSet[machine] == 0 || sched.startTime(res.resources[machine][res.nextToSet[machine]-1].job, res.resources[machine][res.nextToSet[machine]-1].task) < sched.startTime(i, j)) && (best[machine] == null || sched.startTime(best[machine].job, best[machine].task) > sched.startTime(i, j))) best[machine] = new Task(i, j);
                }
            }
            for (int i=0; i < res.instance.numMachines; i++) {
                res.resources[i][res.nextToSet[i]++] = best[i];
            }
        }
        return res;
    }*/

    @Override
    public String toString() {
        String str = new String();
        for (int i=0; i < instance.numMachines; i++) {
            str+="\n";
          for(int j=0;j<instance.numJobs;j++){
              str+=resources[i][j]+"   ";
          }
        }
        return str+"\n";
    }
}