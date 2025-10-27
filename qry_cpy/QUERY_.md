All six queries in this directory is extracted from the Android applications.
Appilcation name is abbreviated into acronyms.

Since Media Access Queries does not use Complex JOIN method, all six query's execution plan is mainly about searching data from columns fitting with given WHERE clause. 

...
It can be classified into two categories: [OL] and [OH].

[OL] type queries are E, F, G and S. This type typically extracts data from designated columns of MediaDB.
This access pattern results does not require SQLite engine to traverse every field, resulting in less request of overflow page access.
Moreover, [OL] 
[OL] type queries can be effectively managed with SOMA's separation policy.
...

[OH] type queries are I and M. This type access every column data with MediaDB, including BLOB-type fields such as XMP.
This access pattern results in increased IO request to handle overflow page access.
Moreover, [OH] may require large temptable due to the amount of data retrieved.
[OH} type queries can be effectively managed with SOMA's IO Prefetching.
...
