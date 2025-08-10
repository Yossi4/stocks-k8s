# Stock Portfolio Service on Kubernetes (Cloud Computing)

This repository contains the final Kubernetes-based implementation of a highly available, robust stock portfolio application. It 
builds upon previous Docker Compose versions by migrating all services to Kubernetes, leveraging its orchestration, scalability, 
and self-healing capabilities.

---

## Overview

This application consists of multiple microservices that together provide portfolio management functionality with persistent 
storage, reverse proxy routing, and load balancing:

- **Two instances of the Stocks Service** (`stocks1` and `stocks2`), each connected to its own MySQL database with persistent 
storage.  
- **Capital Gains Service**, which queries both stock services to compute capital gains. This service is stateless and does not 
store data persistently.  
- **NGINX Reverse Proxy**, which routes requests to appropriate services and implements weighted round-robin load balancing for 
`stocks1`.  
- Kubernetes features used include Deployments, Services, PersistentVolumeClaims, ConfigMaps, Health Checks, and Horizontal Pod 
Autoscalers.

---

## Features & Requirements

- **High Availability & Failover:**  
  Stocks services automatically restart on failure using Kubernetes' self-healing capabilities. The `/kill` endpoint can be used 
to test container failure and automatic restart.  
- **Persistence:**  
  Data is stored in MySQL databases backed by PersistentVolumeClaims to survive pod restarts and cluster node failures.  
- **Load Balancing:**  
  Weighted round-robin load balancing is implemented for the two replicas of `stocks1` service. For every 3 requests to 
`stocks1-a`, 1 request is routed to `stocks1-b`. All `stocks2` requests go to its single instance.  
- **NGINX Reverse Proxy:**  
  Routes GET requests on host port 80 for `/stocks1`, `/stocks2`, `/stocks1/{id}`, `/stocks2/{id}` to respective services.  
  It does not forward requests for `/stock-value/{id}` or `/portfolio-value`.  
- **Service Ports:**  
  - `stocks1` instances listen on separate container ports, mapped to host ports as configured.  
  - `stocks2` and `capital-gains` services run on their own ports.  
  - NGINX listens on port 80 on the host.  
- **Capital Gains Service:**  
  Supports GET `/capital-gains` endpoint, with optional query strings:  
  - `portfolio=stocks1` or `portfolio=stocks2` to filter by portfolio  
  - `numsharesgt=<int>` and `numshareslt=<int>` to filter by number of shares.  
- **Query String Filtering:**  
  Enables retrieval of capital gains for subsets of stocks matching filters (e.g., portfolio only, shares greater than X, etc.).  
- **Health Checks:**  
  Liveness and readiness probes configured on all services to monitor health and ensure proper load balancing and restart.  
- **Autoscaling:**  
  Horizontal Pod Autoscalers monitor CPU usage and scale the number of `stocks1` replicas accordingly.

---
