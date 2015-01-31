The theme is a large-scale statistics gathering and analysis tool in scalable virtualized environments with an emphasis on resource scheduling.

The goal of this application is:
1. Practice with virtualized environment, managing, load balancing and testing VM.
2. Apply open source tools like Logstash or Scribe
3. Understanding the need of gathering and analyze for large data
4. Visualize the collected data using the tools like google charts

This is a simple DRS (Distributed Resource Scheduler) and DPM (Distributed Power Management) function, and large-scale statistics gathering and analysis in scalable virtualized environments. Specific areas of interest include health models for multi-tier applications, VM and host performance, and detection of anomalies.


1. Simple DRS and DPM implementation
a) DRS1, Initial placement: Create 2 vHosts, each running 2 VMs. Each VM should run some programs such as like Prime95 or Folding@Home to keep a vCPU busy. Create a new VM and place it among one of the two existing vHosts. Placement is based on CPU load. (capture before and after screenshots) b) DRS2, Load balancing: Create new vHost and re-balance the vmload among 3 vHosts. (create multiple VMs and load unbalance for setup, capture before and after screenshots) c) DPV: Terminate VMs until you reach lower threshold (typically average 30% cpu load), then DPM kicks in by consolidating VMs into two vHosts and shut down one vHost. Repeat the same until you reach one vHost. (capture before and after screenshots)

2. Framework
Build a framework for large scale metrics data collection and analysis and display of the collected and analyzed data.
The types of operations that the framework will have are:
1. performing collection of system data (metrics, logs) to identify workloads on system elements (jobs, hosts, guests etc) as you have done in project 1 and store them in noSQL Database such as Cassandra or Mongo DB. Data rate can reach 100,000 per minutes. Monitor Hosts and collect the following metrics (Per Guest Level CPU, Memory, Threads, I/O, VMotion)
2. Applying higher-level processing (average per 5 mins intervals, etc) on collected system data to generate abstract views of the system. You can store them in Relational DB such as MySQL, Postgress, BerkeleyDB.
3. Presenting and/or visualizing the outcomes of the above steps in a simple manner.
4. Experiment security options in ESXi such as installing security option for ESXi and experiment with various security options, collect statistics, analyze performance measurements, and report the results.

3. Design Components
The system will have at the minimum the following components:
1. One or more agent processes sitting at the ESXi kernel level that collect and send monitoring data to the collector
2. In order to measure health of application across multiple tiers, access to packets is required.
3. Collector Process that will Poll, Process and Archive collected data
4. Analysis module to process the collected data
5. A Supervisor module that can configure/ manage/ control the various agents
6. A simple visualization Tool for above data


4. Configurations
1. List of agents and locations
2. Environment and configurations for Collectors, port numbers for agents, locations to store data, Polling intervals, etc Startup and Shutdown
3. Agents and Collector can be started independently
4. Start and stop scripts can also be used to manage them independently
5. Simulate work load - There needs to be a way to stimulate activity. This could involve some free apps that are designed to stress-test a real CPU, like Prime95 or Folding@Home to keep a vCPU busy. The students could start up one or more instances of these apps and try to force VMware to auto-migrate to another Host with more CPU resources. (Most of these are all platform: Windows, Linux, Mac.)

1. Agents will collect the configured metrics and commit as local record tables on each host, every 5 seconds
2. Pollers poll the agents and get record tables back via suitable Transport (TCP/ UDP).
3. After collection is successful, data records will be archived and the engines will be notified.
4. These engines will then analyze and reduce data, and create second set of tables that has analyzed values
5. Hourly Analyzed Record Rollup Process will kick in every hour for rolling up data into 24 sets
6. Daily Analyzed Records Rollup. 24 hour Process will kick in for rolling up data from the 24 sets into daily sets
7. Purge and Cleanup
8. Visualization widgets will run on top of the collected and analyzed data. E.g. Per VM and System wide Histograms, Heat Maps etc, all with events coalesced by time, allowing superimpositions. Latency of Tiered applications co-hosted with per VM Guest/ Network statistics etc
SQL DB
Scribe agents

