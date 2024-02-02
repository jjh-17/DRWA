# A708 Drwa
## 토론 게임 서비스

|팀원|Backend|Frontend|
|:---|:---:|:---:|
|김지환|:white_check_mark:|:white_check_mark:|
|곽민우|:white_check_mark:|[:white_check_mark:|
|이경태|:white_check_mark:|:white_check_mark:|
|이동현|:white_check_mark:|:white_check_mark:|
|지준호|:white_check_mark:|:white_check_mark:|
|차다운|:white_check_mark:|:white_check_mark:|

## 4주차
|할 일|월요일|화요일|수요일|목요일|금요일|
|:---|:---:|:---:|:---:|:---:|:---:|
|스크럼|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|[ ]|

### 랭킹 / 칭호 기능 백엔드
- 카테고리 별 랭킹 Redis 저장 및 조회 기능
- 칭호 저장

### 토론 방 생성 / 입장
- 방 생성 / 입장 시 백엔드 요청 및 Redis 통신
- 채널 생성은 아직 X

### 토론 진행
- 토론 시작 후 백엔드 요청 및 Redis 통신

### 전적 저장
- 토론 종료 후 Redis 를 통해 전적에 저장되는 정보만 DB에 저장
- 멤버 전적에 추가

### ElasticSearch 검색
- Elasticsearch의 nori분석기를 활용하여 검색어를 통해 redis에 있는 방 정보를 가져오기

### Frontend
- 메인페이지

### Infra
- Jenkins, nginx 연결 후 배포
