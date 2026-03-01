import sys

textname="test"

n=sys.argv[1]

testname="test"+n+".txt"

with open("samp.txt", "r", encoding="utf-8") as f:
    payload = f.read()

new_payload = payload * int(n)

with open(testname, "w", encoding="utf-8") as f2:
    f2.write(new_payload)
