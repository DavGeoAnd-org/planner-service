# planner-service

## Create repo in Github

* Create service from this template
* Find all instances of planner-service and update it to the repo name

## Create project local

* Clone repo locally
* Change service.context.path property in ServiceProperties.java
* Change service.namespace in otel.env files
* Update version in pom.xml
* Add the following env variables to the run configuration
    * JAVALIN_SERVICE_LOGGING - DEBUG
* Run maven install

## VPC

* Create
    * Resources to create: VPC and more
    * Enable Auto-generate
    * name: homeproject
    * Keep rest default

## Security Group - for otel-collector

* Create
    * Security group name: homeproject-ecs-otel_collector-security_group
    * Description: Security group for ecs otel-collector running in home project
    * VPC: Name of 'VPC'
    * Inbound rules:
        * Type: Custom TCP -- Port range: 4318 -- Source: Anywhere-IPv4
        * Type: Custom TCP -- Port range: 4318 -- Source: Anywhere-IPv6
        * Type: Custom TCP -- Port range: 13133 -- Source: Anywhere-IPv4
        * Type: Custom TCP -- Port range: 13133 -- Source: Anywhere-IPv6

## Security Group - for services load balancer

* Create
    * Security group name: homeproject-ecs-service-load_balancer-security_group
    * Description: Security group for ecs services load balancer running in home project
    * VPC: Name of 'VPC'
    * Inbound rules:
        * Type: HTTP -- Source: Anywhere-IPv4
        * Type: HTTP -- Source: Anywhere-IPv6

## Security Group - for ecs services

* Create
    * Security group name: homeproject-ecs-service-security_group
    * Description: Security group for ecs services running in home project
    * VPC: Name of 'VPC'
    * Inbound rules:
        * Type: Custom TCP -- Port range: 8080 -- Source: My IP
        * Type: Custom TCP -- Port range: 8080 -- Source: Custom -> Security group ID of 'Security Group - for services
          load balancer'

## Application Load Balancer

* Create
    * Load balancer name: homeproject-services-lb
    * Scheme: Internet-facing
    * Load balancer IP address type: IPv4
    * VPC: Name of 'VPC'
    * Mappings: all availability zones (public)
    * Security groups: Security group ID of 'Security Group - for services load balancer'
    * Default action: Create target group
        * Choose a target type: IP addresses
        * Target group name: http-homeproject-default-tg
        * Protocol : Port: HTTP: 80
        * VPC: Name of 'VPC'
        * Protocol version: HTTP1
        * Network: Name of 'VPC'
        * Enter an IPv4 address from a VPC subnet.: Remove

## S3

* Create
    * Bucket type: General purpose
    * Bucket name: homeproject-services-s3-bucket-396607284401
    * Create folder -> Folder name: planner-service
    * In planner-service folder
        * Create folder -> Folder name: [test|prod]
        * add .env files

## ECS Cluster

* Create
    * Cluster name: homeproject-services-[test|prod]-ecs-cluster
    * Default namespace: homeproject-[test|prod]-ecs-namespace
    * Infrastructure: AWS Fargate (serverless)

## ECS Task Definition - otel-collector-service

* Create
    * Task definition family: otel-collector-service-[test|prod]
    * Launch type: AWS Fargate
    * Task size:
        * CPU: .25 -- Memory: .5
    * Container - 1:
        * Name: otel-collector-service
        * Image URI: latest version
        * Port mappings:
            * Container port: 4317 -- Protocol: TCP -- App protocol: GRPC
            * Container port: 4318 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 13133 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 55678 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 55679 -- Protocol: TCP -- App protocol: HTTP
        * Log collection: disable log collection

## ECS Service - otel-collector-service

* Create
    * Deploy from ecs task definition
    * Existing cluster: Name of 'ECS Cluster'
    * Compute options: Capacity provider strategy
    * Service name: otel-collector-service
    * Desired tasks: 0
    * Service Connect:
        * Enable Use Service Connect
        * Service Connect configuration: Client and server
        * Namespace: Namespace of 'ECS Cluster'
        * Service Connect and discovery name configuration:
            * Port alias: otel-collector-service-4318-tcp -- Discovery: http_otel-collector-service_4318 -- DNS:
              otel-collector-service -- Port: 4318
        * Disable Use log collection
    * Networking:
        * VPC: Name of 'VPC'
        * Subnets: enable only public
        * Security group: Security group ID of 'Security Group - for otel-collector'

## ECS Task Definition - planner-service (create manually before first deployment)

* Create
    * Task definition family: planner-service-[test|prod]
    * Launch type: AWS Fargate
    * Task size:
        * CPU: .25 -- Memory: .5
    * Container - 1:
        * Name: planner-service
        * Image URI: use 'latest' for initial setup
        * Port mappings:
            * Container port: 8080 -- Protocol: TCP -- App protocol: HTTP
        * Environment variables:
            * Add from file: .env files from homeproject-services-s3-bucket-396607284401
        * Log collection: disable log collection

## ECS Service - planner-service (create manually before first deployment)

* Create
    * Deploy from ecs task definition
    * Existing cluster: Name of 'ECS Cluster'
    * Compute options: Capacity provider strategy
    * Service name: planner-service
    * Desired tasks: 0
    * Service Connect:
        * Enable Use Service Connect
        * Service Connect configuration: Client side only
        * Namespace: Namespace of 'ECS Cluster'
        * Disable Use log collection
    * Networking:
        * VPC: Name of 'VPC'
        * Subnets: enable only public
        * Security group: Security group ID of 'Security Group - for ecs services'
    * Load balancing (prod)
        * Enable Use load balancing
        * Load balancer type: Application Load Balancer
        * Application Load Balancer: Use an existing load balancer -> Name of 'Application Load Balancer'
        * Listener: Use an existing listener -> 80:HTTP
        * Target group:
            * Create new target group
            * Target group name: http-planner-service-tg
            * Path pattern: /planner/*
            * Evaluation order: 1 (or next available)
            * Health check path: /planner/admin/health