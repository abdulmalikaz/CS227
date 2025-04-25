
import java.util.*;
// This class was done by abdulmalik 

public class Priorty {
	long time; // This is waiting time in queue set for each process    
    model m;
	Queue<PCB> FinishedQueue = new LinkedList<PCB>(); // PCB with waiting time , first response ,and turn around info 
    List<eachStep> eachStep = new LinkedList<>(); // Each PCB steps in the schdual algorithm to print gant chart
    public Priorty(model m) {
        this.m = m;
    }


	public void PQ() 
	{	
		clearFinishedQueue(); 
		clearEachSteps();

		// Insert all the values of ready queue into priortyqueue and change readyqueue pointer from linkedlist queue to priorty queue
//		Queue<PCB> temp = new PriorityQueue<>(new LinkedList<>(m.readyQueue));
//		m.readyQueue = temp;
// In Priorty.java, modify the queue conversion:

		// Abdulmalik
		//help fixing the problem of null pointer exception by checking then adding it
		Queue<PCB> temp = new PriorityQueue<>();
		for (PCB pcb : new LinkedList<>(m.readyQueue)) {
			if (pcb != null) {
				temp.add(pcb);
			}
		}
		m.readyQueue = temp;
		//
		 
		m.removeNullsFromReadyQueue();
 

		// Step 2: Move processes from Ready Queue to Priority Queue
		System.out.println("\nExecuting Non-Preemptive Priority Scheduling:");

		while (!m.CanFill()) {
			assignRequiredCountToAll(m.readyQueue);
			reduceRequiredCountToAll(m.readyQueue);
			//PCB process = m.pollReadyQueue();
			PCB process = m.readyQueue.poll();
			if (process == null) {

				try 
				{
					Thread.sleep(10); 
				} catch (Exception e) 
				{

				}

				continue;
			}
			// this sleep to wait for a process to load
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// assign for new pcb entered
			assignRequiredCountToAll(m.readyQueue);	

			// Set first response time (if not already set)
			if (process.getFirstResponseTime() == -1) {
				process.setFirstResponseTime( time);
			}

			// Calculate waiting time (total time spent before execution starts)
			process.setWaitingTime(time);

			// Execute process
			System.out.println("Executing Process ID: " + process.getId() + ", Priority: " + process.getPriority());
			System.out.println(
					"Process " + process.getId() + " executed from " + time + " to " + (time + process.getBurstTime()));
            eachStep.addLast(new eachStep(process.getId() , time, time + process.getBurstTime())); //For Gant Chart

			time += process.getBurstTime(); // Update total execution time
			process.setState(State.Terminated);
			process.setFinishTime(time); // Store finish time

			m.free(process);
			FinishedQueue.add(process);
		}
			m.readyQueue = new LinkedList<>();  // return readyqueue back to LinkedList 
	}
	// I want a methode that takes each process and assign RequiredCount to readyqueue size if it has dont change it if it doesnt has it value -1 
	private void assignRequiredCountToAll(Queue<PCB> q)
	{
		for (PCB pcb : q)
		{
			if (pcb == null) {
				
				try 
				{
					Thread.sleep(130); 
				} catch (Exception e) 
				{

				}

				continue;
			}
			if (pcb.getRequiredCount() == -1 ) // i used .size to make it is definitely will execute in #No iteration  
			{
				pcb.setRequiredCount(q.size());
			}
		}
	}
	// I want u to reduce each requiredCount -= 1 and if somone is negative prints it and say there was starvation and then use assignRequiredCountToAll to change its value 


	//Abdulmalik
	//Edited for the concurrent exception solved after this edit.
	// because when we do some operations in the original queue maybe there will be a concurrent exception because if this.


	private void reduceRequiredCountToAll(Queue<PCB> queue) {
		Queue<PCB> q = new PriorityQueue<>();
		List<PCB> processSnapshot = new ArrayList<>(queue.size());
		try {
			Thread.sleep(100);			
		} catch (Exception e) {

		}
		q.addAll(queue);
		for (PCB p : q)
		{
			if (p != null) {
				processSnapshot.add(p);
			}	
		}
		// Clear the original queue to rebuild it
		q.clear();

		// Get current system load for dynamic threshold calculation
		int systemLoad = processSnapshot.size();

		// Process each PCB in the snapshot
		for (PCB pcb : processSnapshot) {
			// Decrement the starvation counter
			int newRequiredCount = pcb.getRequiredCount() - 1;
			pcb.setRequiredCount(newRequiredCount);

			// Check for starvation condition
			if (newRequiredCount < 0) {
				handleStarvation(pcb, systemLoad);
			}

			// Return the PCB to the queue (whether modified or not)
			q.add(pcb);
		}
		queue = q;
	}

	private void handleStarvation(PCB pcb, int systemLoad) {
		// Starvation detected - print detailed alert
		System.out.printf(
				"STARVATION ALERT " +
						"  Process ID: %d" +
						"  Current Priority: %d " +
						"\n",
						//"  Waited for: %d cycles \n",
				pcb.getId(),
				pcb.getPriority()
				//Math.abs(pcb.getRequiredCount())
		);
	}

	// Helper method to determine how much to boost priority
//	private int getPriorityBoostAmount(int currentPriority) {
//		// Higher priority processes get smaller boosts
//		return currentPriority >= 6 ? 1 : 2;
//	}
//
//	// Helper method to calculate new starvation threshold
//	private int calculateNewThreshold(int systemLoad) {
//		// Base threshold + load factor
//		return Math.max(5, systemLoad * 2);
//	}
	// public void printPCBRequiredCounts(Queue<PCB> q) {
	// 	for (PCB pcb : q) {
	// 		System.out.println("PCB ID: " + pcb.getId() + ", Required Count: " + pcb.getRequiredCount());
	// 	}
	// }

	public void printGantChart(){
		RR.gantChart(eachStep);
	}
	public void clearFinishedQueue(){
		FinishedQueue.clear();
	}
	public void clearEachSteps(){
		eachStep.clear();
	}
	public Queue<PCB> getFinishedQueue() {
		return FinishedQueue;
	}
	public List<eachStep> getEachSteps() {
		return eachStep;
	}
	// Testing
	public static void main(String[] args) {
		// model m = new model(); // Create system model
        // m.print_Memory();

		// List<PCB> process = null;
        // try {
        //     process = readFile.read_returnPcbs("./job.txt");
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } // Read process data

		// for (int i = 0; i < process.size(); i++) {
		// 	m.createProcess(process.get(i)); // Add process to system
		// }
		// process = null; // Clear process list

		// while (!m.JobQueue.isEmpty()) {
		// 	m.load();
		// }

		// Priorty scheduler = new Priorty(m);
		// Queue<PCB> FinishedQueue = scheduler.PQ(m.readyQueue, m.readyQueue.size());

		// // Step 4: Print finished processes information
		// System.out.println("\nFinished Processes:");
		// for (PCB process1 : FinishedQueue) {
		// 	System.out.println("PID: " + process1.getId() + ", Waiting Time: " + process1.getWaitingTime()
		// 			+ ", First Response Time: " + process1.getFirstResponseTime() + ", Finish Time: "
		// 			+ process1.getFinishTime());
		// }
	}
}
