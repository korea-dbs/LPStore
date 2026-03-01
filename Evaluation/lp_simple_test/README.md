# LP\_simple\_test
This directory's tools are developed for simple test, in order to quantify large payload's impact on databases.

## Structure of this Directory
```
lp_simple_test
├── generators
├── query
├── README.md
├── texts
└── tree.txt
```

## Environment
We conduct this test on three different environments: SQLite(3.42.x), DuckDB(1.5.4) and MySQL(8.0.24).
All three databases use 4KB pages, and MySQL's buffer is fixed to 10% of 0-chain database's size.

We used python3.9 to generate payloads and databases.

## How to conduct test
1. Generating payloads
*texts* directory's samp.txt is a sample 4KB text payload.
Use copy.py program to increase payload size to multiple of 4KB.

```
python3 copy.py <multiplier>
```
2. Generating Databases

In *generators* directory, there are three python program that generates sample databases

The database is based on Android MediaDB format, filled with default values.
We substituted five column's name and content with sample payloads; one of them would be the payload we've made.

```
python3 <database_name>_generator.py <payload length>
```

3. Testing Database with sample Query

We prepared 1 LPN and 1 LPF query in *query* directory.
The queries has no WHERE or ORDER BY clause.

Use this sample query and estimate time to evaluate DB's performance.
