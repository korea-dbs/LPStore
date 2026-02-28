# LPStore

This is a github repository of LPStore project, developed by researchers from Korea University, Hankuk University of Foreign Studies and Washington University.

#What Is LPStore?

LPStore is a new architecture for page-based database, dedicated for effective large-payload management.
It is based on three core features: Separated File Management, Large Payload Prefetching and Grouped Eviction.

- **Separated File Management**: Conventional databases does not distinguish large payload page and ordinary data. This lowers db file's spatial locality and degrades engine's overall performance. LPStore separates large payloads into different files.
- **Large Payload Prefetching**: For I/O requests on large payload pages, LPStore enables prefetch to manage all pages in one I/O. This miminize the number of I/O request and enables large payload's faster buffer caching.
- **Grouped Eviction** : For faster payload eviction, LPStore groups large payload chains into one, which minimizes I/O request on write eviction.

In this repository, you can get our prototype LPStore modules implemented on SQLite and LibSQL, as well as our tools used for analysis and evaluation.

##Our Repo's architecture
```
LPStore
├── evaluation
│   ├── app-tester
│   ├── band.py
│   ├── iologre_log.py
│   ├── queries
│   └── vdbe_parser.py
├── filetree.txt
├── LPAnalyzer
│   ├── api
│   ├── docker-compose.yml
│   ├── docs
│   ├── front
│   ├── grafana
│   └── prometheus
├── LPStore_Libsql
│   └── src
├── LPStore_SQLite
│   └── src
└── README.md
```


#LPStore Prototypes

Our project developed LPStore product with SQLite and LibSQL, which are lightweight database modules.
Although LPStore's architectures can be applied for regular page-based databases, these simple implementations would give users a glimpse of LPStore's efficiency, as well as its high applicability.

##How to Install
To install LPStore\_SQLite, substitute SQLite's src directory with this repository's LPstore\_SQLite/src directory.
After that, install SQLite as vanilla module.

To install LPStore\_Libsql, substitue LibSQL's src with LPStore\_Libsql directory, then install as vanilla modules.

### How to prepare DB with LPStore (with LPStore\_SQLite)
1. Prepare database 
   Generate a dump.sql file from the original SQLite (We'll reference it as 'vanilla') database that you want to use in LPStore environment.
```
vanilla dump > dump.sql
```

2. Prepare new db
```
sqlite3 [new DB file(.db)]
PRAGMA journal_mode = WAL;
.quit
```


3. Read dump from 1. 
```
cd [path to ovfl_sqlite bld dir]
./sqlite3 [new DB file(.db)]
.read dump.sql[Path to dump.sql]
```

The new.db now contains the same data as the vanilla database, but its structure has been modified to store overflow pages separately. All of the overflowed large payloads are stored in new.db-lp file.
To run regular queries, simply launch SQLite[with LPStore features] with the new.db, and execute queries in the same way as before.

#Analysis Tool: LPAnalyzer

LPAnalyzer is an SQLite Benchmark for specialized in Android Media Access Queries.
Developed by researchers from Hankuk University of Foreign Studies, Korea University and Samsung Electronics, this comprehensive tool reflects Android system's interaction with SQLite engine.
By leveraging media access queries collected from real Android applications, SAMA offers more accurate and thorough analysis to users.
LPAnalyzer also provides interactive web-based dashboards, enabling users to grasp SQLite's performance with intuitive graphs, as well as to compare results from various configurations.

## Features

- **Comprehensive Analysis**: Evaluates SQLite's performance in handling media access within Android, highlighting the implications of media files on database operations.
- **Interactive Dashboards**: Utilizes intuitive web-based dashboards for configuring benchmarks, executing queries, and analyzing results in real-time.
- **Real Application Queries**: Incorporates media access queries from representative Android applications, providing a realistic benchmarking scenario.

## Architecture

LPAnalyzer comprises two main components: a web-based dashboard and a benchmark backend. The dashboard facilitates configuration management, query execution, and interactive analysis. The backend handles API requests, executes commands on Android devices via adb, and collects performance metrics for analysis.

![dashboard](LPAnalyzer/docs/SAMBench_dashboard.png)

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
   git clone https://github.com/korea-dbs/LPStore.git
   cd sama
   ```

2. Change Env Value:

   ```sh
   cp .env.example .env
   vim .env
   ```
   - Fill in your Host IP address and Grafana login information.

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

- Once you opened api server successfully, visit this link and execute apis below

http://localhost:3000/api

```
PUT /setup/storage/generate-batch
PUT /setup/storage/push-scripts
PUT /setup/storage/push-query
```

- After you finished your settings, you can enjoy api features.

- We prepared media queries extracted from actual Android Apps in our evaluation/queries directory.

2. Setup Host Sqlite

- Modify your SQLite's Makefile, add the following to the CFLAGS:
```
CFLAGS =   -DVDBE_PROFILE -DSQLITE_DEBUG -DSQLITE_PERFORMANCE_TRACE
```
- Once you've finished this, compile the SQLite again.

3. Open Grafana 

- Access the link below and login to the Grafana server. After that, go to **[Dashboard]** to monitor your device.

http://localhost:3001

4. **[Optional]** If you are not accustomed to api pages, visit the link below to run frontend. This provides more intuitive UI for Benchmark evaluation.

http://localhost:3002


### Usage

- **Configuration Management**: Set up your benchmark configurations using the dashboard, specifying parameters such as media file types and system load.
- **Query Execution**: Execute media access queries on your Android device, modifying conditions and configurations as needed.
- **Interactive Analysis**: Analyze the benchmark results through the dashboard, comparing various performance metrics across different configurations.

## Demonstration

- LPAnalyzer was demonstrated using a Google Pixel 7. It showed capability to construct realistic experimental environments and evaluate SQLite's performance under diverse conditions. (i.e. media file types, storage fragmentation)

## License

This work is licensed under the MIT License. See [LICENSE](LICENSE) for more information.


## Acknowledgments

- This project is a collaborative effort between Hankuk University of Foreign Studies, Samsung Electronics and Korea University.
- Special thanks to all contributors and the open-source community for making this project possible.

## Contact

For any inquiries, please reach out to the email addresses down below.

## Contact

Jonghyeok Park jonghyeok_park@korea.ac.kr  

Dohwan Lee dohwan0123@hufs.ac.kr

Dongkyun Chung cdk6042@korea.ac.kr


