//-----------------------------------------------------
// Title: Q1 - Delivary company
// Author: Abdallah Ghordlo and Hussein Abdikarim
// Description: In this question, we had to model for an online order delivery company.
// This class takes a txt-file as input and reads it into a String array. this String array later 
// saves these values into a customer array where it is used in the Courier finding average method.
//-----------------------------------------------------
import java.io.*;
import java.text.*;
import java.util.*;

public class Q1 {
	public static void main(String[] args) {
		//--------------------------------------------------------
		// Summary: By calling multiple methods, "main" first reads the txt-file, analyzes every 
		// line in that file and saves it in a String array, then it creats a heap and 
		// a customer array with size 12 from the txt. After that, it assigns the txt values 
		// into the customer array and prints them based on calculations within the method.
		// Precondition: A txt-file is provided.
		// Postcondition: The functions in the txt-file will be called using the given values.
		//--------------------------------------------------------
		Scanner input = new Scanner(System.in);
		System.out.println("Enter input filename:");
		String fileName = input.nextLine();
//		String fileName = "D:/UNI/Semester 3/CMPE 223/HW4/sampleinput1.txt";
		System.out.println("Enter the maximum average waiting time:");
		int avgTime = input.nextInt();
		String[] lines = readFile(fileName); 

		int numOfCustomers = Integer.valueOf(lines[0].trim()); // this line retrives the number '12' from the array.
		Heap heap = new Heap(numOfCustomers);
		Customer[] customers = new Customer[numOfCustomers];
		
		//Loop starts at 1 because the numOfCustomers was at 0.
		for(int i = 1, j = 0; i < lines.length; i++, j++) {
			Customer newCust = readCustomerInfo(lines[i]); // saves the customer info here
			customers[j] = newCust;
		}
		
		//Here we get the necessary number of couriers
		double tempAvgTime = avgTime + 1;
		int numOfCouriers = 0;
		DecimalFormat df = new DecimalFormat("#.#####");

		// this loop determines how many Couriers will be used, in order words, the average Couriers.
		while(tempAvgTime > avgTime) {
			tempAvgTime = minCourierNum(++numOfCouriers, heap, customers, false);
		}
		System.out.println("\nMinimum number of couriers required: "+numOfCouriers+"\n");
		System.out.println("Simulation with "+numOfCouriers+" Couriers:\n");
		
		// this is responsible for printing out the customers
		tempAvgTime = minCourierNum(numOfCouriers, heap, customers, true);

		// In here, if average time is for example, 3.0, it will just print 3.
		// else, print the double value eg. (3,888...).
		if(tempAvgTime % ((int)tempAvgTime) == 0)
			System.out.println("\nAverage waiting time: "+(int)tempAvgTime+" minutes");
		else
			System.out.println("\nAverage waiting time: "+Double.parseDouble(df.format(tempAvgTime))+" minutes");
		
	}

	private static double minCourierNum(int numOfCouriers, Heap heap, Customer[] customers, boolean printStatements) {
		//--------------------------------------------------------
		// Summary: This function returns the average time the deliveries take with the given number of 
		// couriers.
		// Precondition: The following parameters are provided: the number of couriers as an int, an instance 
		// of the Heap class which is used for
		// storing nodes that contain a key and an instance of Customer, an array of the customers 
		// (not organized according to priority), and a boolean which
		// is used to indicate whether the user wants the method to print lines that outline the process.
		// Postcondition: The average time the deliveries take with the given number of couriers is returned.
		// The method can also print lines thatoutline the process if the user desires.
		//--------------------------------------------------------
		int numOfCour = numOfCouriers, completedDeliveries = 0;
		Courier[] couriers = new Courier[numOfCour];
		for(int j = 0; j < couriers.length; j++) {
			Courier newCour = new Courier(j);
			couriers[j] = newCour;
		}
		
		int currTime = 1, iter = 0;
		int[] waitTimes = new int[customers.length];
		int waitTimesCounter = 0;
		//The variable currTime keeps increasing and the loops keeps iterating as long as there are customers waiting.
		while(completedDeliveries < customers.length) {
			//If there is a customer that ordered, they are added to the heap.
			if(customers[iter].getOrderTime() == currTime)
				heap.add(new Node(customers[iter]));
			
			//This loops refreshes the availability of the couriers
			for(int i = 0; i < numOfCour; i++) {
				if(!couriers[i].isAvailable())
					couriers[i].refreshAvailability(currTime);
			}
			
			//If there are any more customers that ordered in the same time, they'll be added without increasing the time.
			if(iter+1 != customers.length && customers[iter+1].getOrderTime() == currTime) {
				iter++;
				continue;
			}
			
			//Assigning the couriers to customers if available
			for(int i = 0; i < numOfCour; i++) {
				if(couriers[i].isAvailable()) {
					Node n = heap.remove();
					Customer priorityCustomer = n.getCustomer();
					if(printStatements)
						System.out.println("Courier "+i+" takes customer "+priorityCustomer.getId()+" at minute "+currTime+" (wait: "+(currTime-priorityCustomer.getOrderTime())+" mins)");
					waitTimes[waitTimesCounter++] = currTime-priorityCustomer.getOrderTime();
					couriers[i].setAvailable(false);
					couriers[i].setDeliveryFinishTime(currTime+priorityCustomer.getDeliveryTime());
					completedDeliveries++;
				}
			}
			currTime++;
		}
		double AvgTime = 0;
		for(int i: waitTimes)
			AvgTime += i;
		return AvgTime/customers.length;
	}
	private static Customer readCustomerInfo(String str) {
		//--------------------------------------------------------
		// Summary: This function takes in a String as input and and returns the customer from the lines of 
		// the String array.
		// Precondition: A String is provided as a parameter.
		// Postcondition: A Customer containing the id,regisYear and orderTime will be returned.
		//--------------------------------------------------------
		int id, regisYear, orderTime, deliveryTime;
		String[] info = str.split(" ");
		id = Integer.valueOf(info[0]);
		regisYear = Integer.valueOf(info[1]);
		orderTime = Integer.valueOf(info[2]);
		deliveryTime = Integer.valueOf(info[3]);
		int priority = 2022 - regisYear;
		
		return new Customer(id, regisYear, orderTime, deliveryTime, priority);
	}
	private static String[] readFile(String fileName) {
		//--------------------------------------------------------
		// Summary: This function takes in the file name, reads it, and returns a String Array 
		// containing the lines of the txt-file.
		// Precondition: A file name is provided in the parameter.
		// Postcondition: A String Array containing the lines will be returned.
		//--------------------------------------------------------
		try {
			File file = new File(fileName);
			Scanner scan = new Scanner(file);
			int length = 0;
			//First we get the number of lines in the txt-file.
			while(scan.hasNextLine()) {
				length++;
				scan.nextLine();
			}	
			
			String[] str = new String[length];
			scan = new Scanner(file);
			int i = 0;
			//Then the lines are added into a String array, which is later returned.
			while(scan.hasNextLine()) {
				str[i] = scan.nextLine();
				i++;
			}
			return str;
		}
		catch(Exception e) {
			System.out.println("File not found!");
			return null;
		}
	}
}
class Heap {
	private Node[] heap;
	private Node root;
	private int size;
	
	public Heap(int arrSize) {
		heap = new Node[arrSize];
		size = 0;
	}
	public void add(Node n) {
		//--------------------------------------------------------
		// Summary: This function takes a node as input and adds it to the last of the heap and 
		// heapifyUpper() is called.
		// the last value of the heap and heapifyLower() is called.
		// Precondition: root should not eqaul to null.
		// Postcondition: it adds a node to the heap and adjusts the heap upwards.
		//--------------------------------------------------------
		if(root == null)
			heap[0] = root = n;

		int i = size++;
		heap[i] = n;
		heapifyUpper(i);
		
	}
	public void heapifyUpper(int i) {
		//--------------------------------------------------------
		// Summary: This function takes in an int as index and compares it with the parent
		// if it is greater than the parent, we move it up and make it the parent.
		// Precondition: An int is provided as a parameter.
		// Postcondition: it adjusts the heap upwards based on the (priority) of the heap.
		//--------------------------------------------------------
		
		Node temp = heap[i];
		if(i > 0) {
			int parent = getParent(i);
			int iter = i;
			
			//If there is a parent and it is less than the child, we move the child up.
			while(parent >= 0 && heap[parent].getKey() < temp.getKey()) {
				heap[iter] = heap[parent];
				iter = parent;
				parent = getParent(parent);
			}
			
			heap[iter] = temp;
		}
	}
	public void heapifyLower(int i) {
		//--------------------------------------------------------
		// Summary: This function takes in an int as index and compares the parent
		// with it's children, if they are greater than the parent, we exchange the parent 
		// with the greater child.
		// Precondition: An int is provided as a parameter.
		// Postcondition: it adjusts the heap downwards based on the (priority) of the heap.
		//--------------------------------------------------------
		
		int iter = i;
		int left = getLeftChild(iter);
		int right = getRightChild(iter);
		
		//checking if the i'th node has children
		while(left != -1) {
			Node temp = heap[iter];
			
			//if the i'th node has two children
			if(right != -1) {
				//if the right child is bigger than both left child and the parent
				if(heap[right].getKey() > heap[left].getKey() && heap[right].getKey() >= heap[iter].getKey()) {
					heap[iter] = heap[right];
					iter = right;
				}
				
				//if the left child is bigger than both right child and the parent
				else if(heap[right].getKey() < heap[left].getKey() && heap[left].getKey() >= heap[iter].getKey()) {
					heap[iter] = heap[left];
					iter = left;
				}
				
				//if the parent is smaller than the children, and the children are equal, we just compare the waiting times and switch with the one that waited more.
				else if(heap[right].getKey() == heap[left].getKey() && heap[left].getKey() > heap[iter].getKey()) {
					if(heap[right].getCustomer().getOrderTime() < heap[left].getCustomer().getOrderTime()) {
						heap[iter] = heap[right];
						iter = right;
					}
					else  {
						heap[iter] = heap[left];
						iter = left;
					}					
				}
				
				//if the current node's key is equal the keys of both children.
				else if(heap[iter].getKey() == heap[left].getKey() && heap[left].getKey() == heap[right].getKey()) {
					//if the right child ordered before the parent and the left child.
					if(heap[right].getCustomer().getOrderTime() < heap[left].getCustomer().getOrderTime() 
							&& heap[right].getCustomer().getOrderTime() < heap[iter].getCustomer().getOrderTime()) {
						heap[iter] = heap[right];
						iter = right;
					}
					//if the left child ordered before the parent and the right child.
					else if(heap[right].getCustomer().getOrderTime() > heap[left].getCustomer().getOrderTime()
							&& heap[left].getCustomer().getOrderTime() < heap[iter].getCustomer().getOrderTime()){
						heap[iter] = heap[left];
						iter = left;
					}
					//else the parent ordered first, so break.
					else
						break;
				}
				//if the parent is bigger than the children
				else if(heap[iter].getKey() >= heap[left].getKey() && heap[iter].getKey() >= heap[right].getKey()) {
					System.out.print("hello");
					break;
				}
			
				heap[iter] = temp;
				left = getLeftChild(iter);
				right = getRightChild(iter);
				continue;
			}
			
			//if the i'th node has only a left child		
			//if left child is bigger than the parent
			if(heap[left].getKey() >= heap[iter].getKey()) {
				heap[iter] = heap[left];
				iter = left;
				heap[iter] = temp;	
			}
			//if there is only a left child, then we have reached the end of the heap, so we break.
			break;
		}
	}
	public Node remove() {
		//--------------------------------------------------------
		// Summary: This function removes the root of the heap and replaces it with 
		// the last value of the heap and heapifyLower() is called.
		// Precondition: root should not eqaul to null.
		// Postcondition: it removes the root and adjusts the heap downwards.
		//--------------------------------------------------------
		if(root == null)
			return null;
		
		Node temp = heap[0];
		heap[0] = heap[--size];
		heap[size+1] = null;
		
		heapifyLower(0);
		return temp;
	}
	public int getParent(int i) {
    //--------------------------------------------------------
		// Summary: This function takes in an int as index(index of child) 
		// and returns index of the parent.
		// Precondition: An int is passed as a parameter.
		// Postcondition: It returns the index of the parent.
		//-------------------------------------------------------- 
		if(i == 0)
			return -1;
		return (i-1)/2;
	}
	public int getLeftChild(int i) {
		//--------------------------------------------------------
		// Summary: This function takes in an int as index
		// and returns the position of the left child.
		// Precondition: An int is passed as a parameter.
		// Postcondition: It returns the index of the left child.
		//-------------------------------------------------------- 
		int ans = (i*2)+1;
		if(ans > size)
			return -1;
		return ans;
	}
	public int getRightChild(int i) {
	//--------------------------------------------------------
		// Summary: This function takes in an int as index
		// and returns the position of the right child.
		// Precondition: An int is passed as a parameter.
		// Postcondition: It returns the index of the right child.
		//-------------------------------------------------------- 
		int ans = (i*2)+2;
		if(ans > size)
			return -1;
		return ans;
	}
	public Node[] getHeap() {
		return heap;
	}
	public void setHeap(Node[] heap) {
		this.heap = heap;
	}
	public Node getRoot() {
		return root;
	}
	public void setRoot(Node root) {
		this.root = root;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}

class Node {
	private int key;
	private Customer customer;
	public Node(Customer cust) {
		this.key = cust.getPriority();
		customer = cust;
	}
	public int getKey() {
		return key;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public void setKey(int key) {
		this.key = key;
	}
	
}
class Courier {
	private boolean available;
	private int ID, deliveryFinishTime;
	public Courier(int iD) {
		this.available = true;
		ID = iD;
		deliveryFinishTime = 0;
	}
	
	public void refreshAvailability(int currTime) {
		if(currTime >= deliveryFinishTime)
			available = true;
	}
	
	public int getDeliveryFinishTime() {
		return deliveryFinishTime;
	}

	public void setDeliveryFinishTime(int deliveryFinishTime) {
		this.deliveryFinishTime = deliveryFinishTime;
	}

	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
}
class Customer {
	private int id, regisYear, orderTime, deliveryTime, priority;
	public Customer(int id, int regYear, int ordTime, int delivTime, int priority) {
		this.id = id;
		regisYear = regYear;
		orderTime = ordTime;
		deliveryTime = delivTime;
		this.priority = priority;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", regisYear=" + regisYear + ", orderTime=" + orderTime + ", deliveryTime="
				+ deliveryTime + "]";
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRegisYear() {
		return regisYear;
	}
	public void setRegisYear(int regisYear) {
		this.regisYear = regisYear;
	}
	public int getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(int orderTime) {
		this.orderTime = orderTime;
	}
	public int getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(int deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
}