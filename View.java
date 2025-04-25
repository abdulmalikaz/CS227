import java.util.InputMismatchException;
import java.util.Scanner;

public class View 
{
    Scanner scanner = new Scanner(System.in);
    
    model m = new model(2048); // Here to change Memory 
    
    String fileName = "src/job.txt"; //This is File Name
    
    //Thread LoadThread = new Thread(new MyRunnable(ThreadState.LoadToJobQueue, m));               
    Thread LoadThread = new Thread(new MyRunnable(ThreadState.LoadToJobQueueWithComments, m));  //this same as a above but with comments memory full .. 

    boolean AutoFill = true; 
    boolean WaitUntilFinishReading = true ;

    performance p;
    performance p2;
    performance p3;
    performance all = new performance();

    public void menu()
    {
        // Read and Create a process from a file
        runReadThreadIfQueuesEmpty();
            
        //Auto Load Thread from job Queue to ready Queue
        LoadThread.setDaemon(true); // if main dies it dies 
        LoadThread.start();
        System.out.println("Starting Loading...");   

        int option = 0;
        do
        {
            display_mainMenu();
            try { 
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid option.");
                scanner.nextLine(); // Clear the buffer
                continue;
            }
            System.out.println();
            if (option != -1 && option != 4)
                runReadThreadIfQueuesEmpty();
            HoldUntilFinish();
            System.out.println();
            try {
                Thread.sleep(125);
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (option) 
            {
                case 1: 
                    //FCFS
                    FCFS fcfs = new FCFS(m);
                
                    Thread runFCFS = new Thread(new MyRunnable(ThreadState.Execute_FCFS, m,fcfs));
                    runFCFS.start();
                    try {
                        runFCFS.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                     p = new performance(Algorithm.FCFS,fcfs.getFinishedQueue());
                    System.out.println(p); // Print performance of a algorithm such as AvgTurnaround , AvgWaitingTime..

                break;

                case 2: //Priorty 
                    Priorty pq = new Priorty(m);
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }
                    Thread runPQ = new Thread(new MyRunnable(ThreadState.Execute_PQ, m,pq));
                    runPQ.start();
                    try {
                        runPQ.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                     p2 = new performance(Algorithm.Priority,pq.getFinishedQueue());
                    System.out.println(p2); // Print performance of a algorithm such as AvgTurnaround , AvgWaitingTime..
                break;

                case 3://Rouund Roubin
                    //getting quantum time
                    System.out.println("Enter the quantum time: ");
                    int quantum = 0;
                    do
                    {
                        try
                        {
                            int tmp;
                            System.out.print("-->");
                            quantum = scanner.nextInt();
                            System.out.println();                   
                        }
                        catch(InputMismatchException e)
                        {
                            scanner.next();
                            System.out.println("Please enter a valid number");
                            System.out.println();
                        }
                    }while (quantum <= 0);
                    
                    
                    RR rr = new RR(m);
                    Thread runRR = new Thread(new MyRunnable(ThreadState.Execute_RR, m,rr,quantum));
                    runRR.start();
                    try {
                        runRR.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                     p3 = new performance(Algorithm.Round_Robin, rr.getFinishedQueue());
                    System.out.println(p3); // Print performance of a algorithm such as AvgTurnaround , AvgWaitingTime..
                    
                    
                break;
 
                case 4://Print best
                //RUN 1 ,2 ,3 AND THEN USE PERFORMANCE
                int ch =0;
                    do{
                        System.out.println("To get best info try using all algorithm because performance depends on them");
                        System.out.println();
                        System.out.println("1- Best TurnAroundTime");
                        System.out.println("2- Best AvgWaitingTime");
                        System.out.println("3- Best FirstResponseTime");
                        System.out.println("4- Best FinishResponseTime");
                        System.out.println("-1 Exit ");
                        System.out.println();
                        if (p == null){System.out.println("Warning You Didnt try using FCFS ");}
                        if (p2 == null){System.out.println("Warning You Didnt try using Priorty ");}
                        if (p3 == null){System.out.println("Warning You Didnt try using Round Roubin ");}

                        try
                        {
                            System.out.print("--> ");
                            ch = scanner.nextInt();
                            System.out.println();
                            if(ch ==1 )
                                all.BetterPerformanceAt(p,p2,p3,status.TurnAroundTime);
                            else if (ch ==2 )
                                all.BetterPerformanceAt(p,p2,p3,status.WaitingTime);
                           else if(ch ==3 )
                                all.BetterPerformanceAt(p,p2,p3,status.FirstResponseTime);
                            else if (ch ==4 )
                                all.BetterPerformanceAt(p,p2,p3,status.FinishResponseTime);
                            else {
                                System.out.println("Wrong number please enter a valid number");
                                continue;
                            }
                        }
                        catch (InputMismatchException e )
                        {
                            System.out.println("Invalid input. Please enter a valid option.");
                            scanner.nextLine();
                            continue;

                        }
                        catch (Exception e )
                        {
                            System.out.println("Use All function then try again ");
                            ch = -1 ;
                        }

                    }while(ch != -1);

                    break;
                
                case -1:
                    System.out.println("Exiting...");
                    LoadThread.interrupt();
                break;

                default:
                    System.out.println("Invalid input. Please enter a valid option.");
                break;
            }
        }while(option != -1);
    }

    public void display_mainMenu()
    {
        System.out.println();
        System.out.println( "* * * * * * * * * * * * * * *");
        System.out.println("*          Welcome          *");
        System.out.println("*  Please select an option: *");
        System.out.println("*    1.FCFS                 *"); // process
        System.out.println("*    2.Priorty              *");
        System.out.println("*    3.Round Roubin         *");
        System.out.println("*    4.Best Status          *");
        System.out.println("*    -1.Exit                *");
        System.out.println( "* * * * * * * * * * * * * * *");
        System.out.println();
        System.out.print("-->");
    }
    private void runReadThreadIfQueuesEmpty() {
        if (AutoFill && m.CanFill()) {
            WaitUntilFinishReading = true;
            System.out.println("Start Reading file");
            try {
                Thread readThread = new Thread(new MyRunnable(ThreadState.read, m, fileName));
                readThread.start();
                readThread.join();
            } catch(Exception e) {
                e.printStackTrace();
            } 
            System.out.println("Job Queue Fulled");
            WaitUntilFinishReading = false;
        }
    }
    private void HoldUntilFinish()
    {
        while(WaitUntilFinishReading)
        {
            ;
        }
    }





    public static void main(String[] args) {
        View v = new View();
        v.menu();
    }
}
