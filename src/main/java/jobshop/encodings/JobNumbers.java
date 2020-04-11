package jobshop.encodings;

import jobshop.Encoding;
import jobshop.Instance;
import jobshop.Schedule;

import java.util.Arrays;

/** Représentation par numéro de job. */
public class JobNumbers extends Encoding {

    /** A numJobs * numTasks array containing the representation by job numbers. */
    public final int[] jobs;

    /** In case the encoding is only partially filled, indicates the index of first
     * element of `jobs` that has not been set yet. */
    public int nextToSet = 0;

    public JobNumbers(Instance instance) {
        super(instance);

        jobs = new int[instance.numJobs * instance.numMachines];
        Arrays.fill(jobs, -1);
    }

    @Override
    public Schedule toSchedule() {
        // time at which each machine is going to be freed
        int[] nextFreeTimeResource = new int[instance.numMachines];

        // for each job, the first task that has not yet been scheduled
        int[] nextTask = new int[instance.numJobs];

        // for each task, its start time
        int[][] startTimes = new int[instance.numJobs][instance.numTasks];

        // compute the earliest start time for every task of every job
        for(int job : jobs) {
            int task = nextTask[job];
            int machine = instance.machine(job, task);
            // earliest start time for this task
            int est = task == 0 ? 0 : startTimes[job][task-1] + instance.duration(job, task-1);
            est = Math.max(est, nextFreeTimeResource[machine]);

            startTimes[job][task] = est;
            nextFreeTimeResource[machine] = est + instance.duration(job, task);
            nextTask[job] = task + 1;
        }

        return new Schedule(instance, startTimes);
    }

    public static JobNumbers fromSchedule(Schedule sched) {
        JobNumbers job = new JobNumbers(sched.pb);
        int[] nextToSetJobs = new int[job.instance.numJobs];
        int schedSize = job.instance.numJobs * job.instance.numMachines;
        int best;
        while(schedSize > 0) {
            best = -1;
            for (int i=0; i < job.instance.numJobs; i++) {
                if (nextToSetJobs[i] < job.instance.numTasks) {
                    if (best == -1 || sched.startTime(best, nextToSetJobs[best]) > sched.startTime(i, nextToSetJobs[i])) best = i;
                }
            }
            job.jobs[job.nextToSet++] = best;
            nextToSetJobs[best]++;
            schedSize--;
        }
        return job;
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOfRange(jobs,0, nextToSet));
    }
}
