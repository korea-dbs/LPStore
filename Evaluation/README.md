This directory gathers tools for our **LPStore** project evaluation.

#Structure of this Directory
```
Evaluation
├── app-tester
├── band.py
├── iologre_log.py
├── lp_simple_test
├── queries
├── README.md
└── vdbe_parser.py
```

#app-tester
This is a simple testing app that emulates Android OS's gallery applications.
The application calculates its total running in three different categories; DB execution, UI rendering, and other operations.

*app-tester* directory's app is targeted for Android 33, and compiled with v.11 jvm.
Minimal SDK is version 24.

##How to install app-tester
Once you build this app with android studio, install this into Android machine via ADB.

#lp\_simple\_test
This directory contains simple test kit that access large payload's impact on DuckDB, SQLite and MySQL.

#queries
All of the media queries we've extracted is located in this directory.

##other programs.
band.py is a python program to extract bandwidth performance data from linux strace result.
iologre\_log.py is a python program to extract average I/O latency, also from linux strace result.
vdbe\_parser.py parses CPU cycles from SQLite's vdbe\_profile.out file.

All three programs is executed with python3.9 engine. 
