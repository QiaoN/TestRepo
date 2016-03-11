# Virtual Grid System
This repo is using for Virtual Grid System lab exercise.  

The vision of a seamlessly shared infrastructure comprising heterogeneous resources to be used by multiple organizations and independent users alike, which was formulated in the beginning of the 1990s in the context of grids [4], has also been adopted by many of today’s commercial Infrastructure-as-a-Service clouds. Today, grid and cloud system designers often rely on multi-cluster distributed systems as infrastructure. Although many grids and clouds exist, key features of multi-cluster systems, such as sophisticated resource planning strategies and the adaptation of existing applications to the distributed conditions of (wide-area) multicluster systems, are still active research topics. Many of these features require new or more indepth understanding of the behavior of multi-cluster systems, but, because few such systems are available for exclusive access for research experiments, simulation remains an important research tool. Thus, companies and research labs may invest in in-house or market-provided products for multi-cluster system simulation. 

Your team (“you”) is hired by WantDS BV to develop their new multi-cluster simulator. Because the computational complexity of these simulators is high, and because the CTO of WantDS BV believes distributed systems offer an excellent performance-cost trade-off when running many simulations for many users, WantDS BV has decided to invest in the design and implementation of a distributed simulator of multi-cluster systems, the virtual grid system simulator (the VGS). You must design, develop, experiment with realistic scenarios, and report back on the feasibility of a distributed VGS.  

Many grids are created from sets of independent clusters, which each contain resources of two types: processors and a Resource Manager (RM). Such an RM receives requests (supposed to be sequential jobs that use a processor exclusively in this exercise) from its users, dispatches them on the cluster when a processor becomes idle, and maintains a job queue of waiting jobs.  

For grid operation, such a set of clusters needs a grid scheduler (GS), which enables load sharing among the clusters, i.e., the transfer of jobs from heavily loaded clusters to lightly loaded ones. A non-distributed version of a VGS (see Figure 1) has been implemented by a previous consultant of WantDS BV and is available for study.  

## Mandatory requirements 
You must design and implement a distributed (i.e., with multiple server nodes), replicated, and fault-tolerant version of the VGS system, and assess its properties. The VGS system should meet the following requirements:  

1. **System operation requirements**: When a request arrives in a cluster, it is put in the local RM’s job queue or sent to the GS. The GS also maintains a job queue, from which it sends jobs to the RMs. When an RM receives a job from the GS, it has to accept it, although possibly by adding it to a waiting queue. Each cluster has an unique identifier and a fixed number of nodes (its size, e.g., 32, 64, 128). Each job has an unique identifier and a fixed duration (its runtime on a single node), and records the identifier of the cluster where it originally arrives,. The GS may also load-balance across the simulated multi-cluster system, in which case each job will record all additional clusters to which it is sent to run.
2. **Fault tolerance**: Consider a simple failure model, in which the grid scheduler nodes may crash and restart. The VGS must by design be resilient against resource manager and grid scheduler node crashes. In addition, all grid and system events (e.g., job arrivals, job starts and completions, RM and GS node restarts) must be logged in the order they occur, on at least two grid scheduler nodes.
3. **Scalability requirements**: The properties of the VGS must be demonstrated when it contains 20 clusters with at least 1,000 nodes, 5 grid scheduler nodes, and when the workload consists of at least 10,000 jobs in total. To enable the study of realistic imbalance situations, the ratio of the numbers of jobs arriving at the most and least loaded cluster should be, for at least a significant part of the experiments, at least 5.

## Optional for bonus points
Additional features that you may want to consider for your system (pick at most two):  
1. **Realistic experiments**: by using workloads or parts of workloads taken from the Grid Workloads Archive [5].  
2. **Multiple consistency models**: analysis of the impact of the consistency model on performance.  
3. **Advanced fault-tolerance**: through tolerance for and analysis of the impact of multiple failures or other failure models than fail-stop on the VGS system.  
4. **Multi-tenancy**: support multiple users running simulations for the VGS system, through a queue of simulation jobs and adequate scheduling policies.  
5. **Repeatability**: making sure that the outcome of the simulations is always the same for the same input, through the use of priority numbers for concurrent simulated events [1, Section 3.9.2].  
6. **Benchmarking**: by running service workloads designed to stress particular elements of the system.  
7. **Portfolio scheduling**: by adapting the concept of portfolio scheduling [6] to the VGS system.
