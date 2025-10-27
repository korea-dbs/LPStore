#!/bin/bash

# 파일 이름을 인자로 받아옴
file="$1"

# 인자가 비어있거나 파일이 존재하지 않으면 오류 메시지 출력
if [ -z "$file" ] || [ ! -f "$file" ]; then
    echo "사용법: $0 <파일명>"
    exit 1
fi

# f2fs.fibmap 명령을 사용하여 파일의 블록 매핑 정보를 가져옴
block_info=$(f2fs.fibmap "$file")

# 블록 정보를 출력하여 확인
#echo "블록 정보:"
#echo "$block_info"

# 최대 end_blk와 최소 start_blk를 계산하기 위해 awk 사용
max_end_blk=$(echo "$block_info" | awk 'NR>16 {if (max =="" || $3 > max) max=$3} END {print max}')
min_start_blk=$(echo "$block_info" | awk 'NR>16 {if (min == "" || $2 < min) min=$2} END {print min}')

# 결과 계산
if [ -z "$max_end_blk" ] || [ -z "$min_start_blk" ]; then
    echo "값을 가져오지 못했습니다. 결과를 계산할 수 없습니다."
    exit 1
fi

# 결과 출력
echo "max_end_blk: $max_end_blk"
echo "min_start_blk: $min_start_blk"

# 큰 숫자를 처리하기 위해 bc 사용
result=$(echo "$max_end_blk - $min_start_blk" | bc)

# 결과 출력
echo "max_endblk - min startblk = $result"