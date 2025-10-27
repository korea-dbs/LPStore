#!/bin/sh

# dbname을 인자로 받습니다
dbname=$1

# f2fs.fibmap 명령어를 실행하고 결과의 줄 수를 계산합니다
line_count=$(f2fs.fibmap "$dbname" | wc -l)

# 16을 뺀 값을 계산합니다
result=$(expr $line_count - 16)

# 결과를 출력합니다
echo "$result"