# LPStore: SQLite Optimization for Large Payalod Management

This is a github repository of LPStore project, developed by researchers from Korea University, Hankuk University of Foreign Studies and Washington University.

# What Is LPStore?

LPStore is a new architecture for page-based database, dedicated for effective large-payload management.
It is based on three core features: Separated File Management, Large Payload Prefetching and Grouped Eviction.

- **Separated File Management**: Conventional databases does not distinguish large payload page and ordinary data. This lowers db file's spatial locality and degrades engine's overall performance. LPStore separates large payloads into different files.
- **Large Payload Prefetching**: For I/O requests on large payload pages, LPStore enables prefetch to manage all pages in one I/O. This miminize the number of I/O request and enables large payload's faster buffer caching.
- **Grouped Eviction** : For faster payload eviction, LPStore groups large payload chains into one, which minimizes I/O request on write eviction.

In this repository, you can get our prototype LPStore modules implemented on SQLite and LibSQL, as well as our tools used for analysis and evaluation.

## Project Structure
```
LPStore
├── Evaluation
├── LPStore_Libsql
├── LPStore_SQLite
└── README.md
```

##Evaluation
*Evaluation* Directory contains tools developed to evaluate LPStore\_SQLite modules.

##LPStore Prototypes
Our project developed LPStore product with SQLite and LibSQL, which are lightweight database modules.
Although LPStore's architectures can be applied for regular page-based databases, these simple implementations would give users a glimpse of LPStore's efficiency, as well as its high applicability.

##How to Install
To install LPStore\_SQLite, substitute SQLite's src directory with this repository's *LPstore\_SQLite* directory.
After that, install SQLite as vanilla module.

To install LPStore\_Libsql, substitue LibSQL's src direcotry with *LPStore\_Libsql* directory, then install as vanilla modules.

##Setup Environements
- Ubuntu `20.04.6 LTS`
- sqlite3 `3.42.*`
- libsql `*.*.*`

### How to prepare DB with LPStore (with LPStore\_SQLite, LPStore\_Libsql)
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
cd [path to LPStore_SQLite build dir]
./sqlite3 [new DB file(.db)]
.read dump.sql[Path to dump.sql]
```

The new.db now contains the same data as the vanilla database, but its structure has been modified to store overflow pages separately. All of the overflowed large payloads are stored in new.db-lp file.
To run regular queries, simply launch SQLite[with LPStore features] with the new.db, and execute queries in the same way as before.

## License

This work is licensed under the MIT License. See [LICENSE](LICENSE) for more information.


## Acknowledgments

- This project is a collaborative effort between Hankuk University of Foreign Studies, Samsung Electronics and Korea University.
- Special thanks to all contributors and the open-source community for making this project possible.

## Contact

For any inquiries, please reach out to the email addresses down below.

## Contact

- Dohwan Lee dohwan0123@hufs.ac.kr
- Dongkyun Chung cdk6042@korea.ac.kr
- Jonggyu Park jonggyu@cs.washington.edu
- Jonghyeok Park jonghyeok_park@korea.ac.kr  


