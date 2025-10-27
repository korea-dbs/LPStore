#!/bin/sh

# 파일 경로와 출력 파일을 인자로 받습니다
file_path=$1
output_file=$2

# awk를 사용하여 차이를 계산하고 출력
f2fs.fibmap "$file_path" | awk '
BEGIN {
    OFS = "\t"
    previous_end_blk = 0
    first_line = 1
    skip_lines = 1
}
/^file_pos/ {
    skip_lines = 0
    next
}
skip_lines == 0 {
    if (first_line) {
        # 첫 번째 줄의 end_blk을 초기값으로 설정
        previous_end_blk = $3
        first_line = 0
        next
    }
    # 첫 번째 줄을 제외한 나머지 줄에서 차이를 계산
    difference = $2 - previous_end_blk
    print difference
    # 현재 줄의 end_blk을 previous_end_blk로 업데이트
    previous_end_blk = $3
}' > "$output_file"