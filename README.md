# SAMA_SQLiteOP

This is a github repository of SAMA, A SQLite project dedicated to analyze and optimize SQLite's Android Media Access Query Performance.
The repository is separated into two parts : SAMBench and SQLite-OP.

# Part 1. SAMBench


## Introduction

SAMBench is an SQLite Benchmark for specialized in Android Media Access Queries.
Developed by researchers from Hankuk University of Foreign Studies, Korea University and Samsung Electronics, this comprehensive tool reflects Android system's interaction with SQLite engine.
By leveraging media access queries collected from real Android applications, SAMBench offers more accurate and thorough analysis to users.
SAMBench also provides interactive web-based dashboards, enabling users to grasp SQLite's performance with intuitive graphs, as well as to compare results from various configurations.

## Features

- **Comprehensive Analysis**: Evaluates SQLite's performance in handling media access within Android, highlighting the implications of media files on database operations.
- **Interactive Dashboards**: Utilizes intuitive web-based dashboards for configuring benchmarks, executing queries, and analyzing results in real-time.
- **Real Application Queries**: Incorporates media access queries from representative Android applications, providing a realistic benchmarking scenario.

## Architecture

SAMBench comprises two main components: a web-based dashboard and a benchmark backend. The dashboard facilitates configuration management, query execution, and interactive analysis. The backend handles API requests, executes commands on Android devices via adb, and collects performance metrics for analysis.

![dashboard](sambench/docs/SAMBench_dashboard.png)

## Getting Started

### Prerequisites for Host Machine

- Ubuntu `20.04.6 LTS`
- [nodejs](https://github.com/nvm-sh/nvm#installing-and-updating) `18.0.0`
- [docker](./docs/docker-install.md) `24.0.7`
- [adb](https://developer.android.com/tools/sdkmanager) `35.0.0-11411520`
- [fastboot](https://developer.android.com/tools/sdkmanager) `35.0.0-11411520`
- sqlite3 `3.45.2`

### Prerequisites for Android Device

- Android device with at least Android version 13
- Android Must be rooted to run the benchmark

### Installation

1. Clone the repository and go to sambench directory:

   ```sh
   git clone https://github.com/korea-dbs/sama_sqliteop.git
   cd sambench
   ```

2. Change Env Value:

   ```sh
   cp .env.example .env
   vim .env
   ```
   Fill in your Host IP address and Grafana login information.

2. Start Infra Services:

   ```sh
   docker compose up -d
   ```
   
3. Start Api Server:

   ```sh
   cd api
   npm install -g yarn
   yarn install
   pm2 start yarn --name sambench -- start:dev --preserveWatchOutput
   ```

### Setup

1. Setup Enviornments

Once you opened api server successfully, visit this link and execute apis below

http://localhost:3000/api

```
PUT /setup/storage/generate-batch
PUT /setup/storage/push-scripts
PUT /setup/storage/push-query
```

After you finished your settings, you can enjoy api features.

Our qry_cpy directory provides sample queries for SAMBench. You can use other queries if you want.

2. Setup Host Sqlite

Modify your SQLite's Makefile, add the following to the CFLAGS:
```
CFLAGS =   -DVDBE_PROFILE -DSQLITE_DEBUG -DSQLITE_PERFORMANCE_TRACE
```
Once you've finished this, compile the SQLite again.

3. Open Grafana 

Access the link below and login to the Grafana server. After that, go to [Dashboard] to monitor your device.

http://localhost:3001

4. Run Dashboard

http://localhost:3002


### Usage

- **Configuration Management**: Set up your benchmark configurations using the dashboard, specifying parameters such as media file types and system load.
- **Query Execution**: Execute media access queries on your Android device, modifying conditions and configurations as needed.
- **Interactive Analysis**: Analyze the benchmark results through the dashboard, comparing various performance metrics across different configurations.

## Demonstration

SAMBench was demonstrated using a Google Pixel 7, showcasing its ability to construct realistic experimental environments and evaluate SQLite's performance under diverse conditions such as media file types and storage fragmentation.

## License

This work is licensed under the MIT License. See [LICENSE](LICENSE) for more information.

## Acknowledgments

- This project is a collaborative effort between Hankuk University of Foreign Studies and Samsung Electronics.
- Special thanks to all contributors and the open-source community for making this project possible.

## Contact

For any inquiries, please reach out to the authors through their provided email addresses.


# Part 2. SQLite-OP


SQLite-OP is an optimized version of SQLite, designed to handle overflow pages efficiently.
In this version, a specialized controller is implemented to manage overflow pages, aiming to improve performance for databases that contain a large number of such pages.

## Features

- **Large Payload Controller**:
  The Large Payload Controller enables the separation of regular pages and overflow pages, allowing them to be stored in different storage devices.
- **Wal Frame header**:
  The WAL frame header stores both the large payload page number and the regular database page number, and provides the correct mapping between them when the data is needed.



## Getting Started

### Build
1. copy sqlite-op/src directory

2. Install regular SQLite

3. Swap original SQLite's src directory with 2.

4. Configure SQLite with SQLite-OP's source data
```
mkdir bld && cd bld
../configure
```

5. Compile
```
make -j
sudo make install -j
```

### Run
1. Make Overflow DB
   Generate a dump.sql file from the original (vanilla) database that you want to separate into an overflow database.
```
vanilla_sqlite3[this sqlite engine is vanilla_sqlite] dump > dump.sql
```

2. Prepare new db
```
vanilla_sqlite3 [new DB file(.db)]
PRAGMA journal_mode = WAL;
.quit
```


3. Read dump from 1. 
```
cd [path to ovfl_sqlite bld dir]
./sqlite3 [new DB file(.db)]
.read dump.sql[Path to dump.sql]
```

The new.db now contains the same data as the vanilla database, but its structure has been modified to store overflow pages separately. 
To run regular queries, simply launch SQLite using ovfl_sqlite with the new.db file, and execute queries in the same way as before.


## Acknowledgements

- This project is a collaborative effort of Korea University and Hankuk University of Foreign Studies.

## Contact

Jonghyeok Park jonghyeok_park@korea.ac.kr  

Dohwan Lee dohwan0123@hufs.ac.kr

Dongkyun Chung cdk6042@korea.ac.kr


