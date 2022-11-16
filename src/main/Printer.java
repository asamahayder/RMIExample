package main;

import java.util.ArrayList;

public class Printer {

    private final String name;
    private final ArrayList<PrinterJob> jobs;

    private final ArrayList<PrinterJob> finishedJobs;

    private int jobIndex;

    Thread thread;

    public Printer(String name){
        jobs = new ArrayList<>();
        finishedJobs = new ArrayList<>();
        this.name = name;
        jobIndex = 0;
        thread = new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(20000);
                    if (jobs.size() > 0){
                        finishedJobs.add(jobs.get(0));
                        jobs.remove(0);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        thread.start();
    }

    public void addJob(String content){
        jobs.add(new PrinterJob(jobIndex, content));
        jobIndex++;

    }

    public boolean topQueue(int jobIndex){

        boolean jobFound = false;

        PrinterJob copy;
        for (PrinterJob j :
                jobs) {
            if (j.jobId == jobIndex){
                copy = j;
                jobs.remove(j);
                jobs.add(0, copy);
                jobFound = true;
            }
        }

        return jobFound;

    }

    public ArrayList<PrinterJob> getJobs(){
        return jobs;
    }

    public ArrayList<PrinterJob> getFinishedJobs(){
        return finishedJobs;
    }

    public String getName(){
        return this.name;
    }

    public void clearJobs(){
        this.jobs.clear();
    }

}

