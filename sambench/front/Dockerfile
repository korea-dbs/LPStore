FROM nginx:stable-alpine

# React 앱의 빌드 결과물을 Nginx 서버의 서빙 디렉토리로 복사
COPY public /usr/share/nginx/html

# 기본 Nginx 설정을 사용하거나 커스텀 설정 파일을 복사
COPY nginx.conf /etc/nginx/nginx.conf

# Nginx 시작
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
