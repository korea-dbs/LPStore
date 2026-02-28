#About this Directory

This directory stores all queries we've extracted and gathered for our LPStore experiment.

The

All queries in media\_access directory are extracted from the Android applications.
Appilcations' name are abbreviated into acronyms.

Since Media Access Queries does not use Complex JOIN method, all six query's execution plan is mainly about searching data from columns fitting with given WHERE clause. 
Media Access Queries can be classified into two categories: [LP-None] and [LP-Full].

[LP-None] type queries are E, F, G and S. This type does not retrieve large payload column of the Android MediaDB filess.
[LP-None] pattern does not require SQLite to traverse all pages, resulting in low overflow page access.
[LP-None] does not require much temporary space to gather information either,
[LP-None] type queries can be effectively managed with LPStore's Separate File Managment policy.
...

[LP-Full] type queries are I and M. This type access every column data with MediaDB, including large payload data such as XMP.
[LP-Full]'s such behavior requires buffer retreival, increasing the number of I/O request on data page access.
Moreover, [LP-Full] may require large dataspace to allocate its massive record in temporary tables.
[LP-Full] type queries can be effectively managed with LPStore's IO Prefetching.
...

