
# initServer

## externalDataSources

get /jaxrs/externaldatasources/check
외부 데이터 소스를 설정할 수 있는지 확인

get /jaxrs/externaldatasources/list
사용할 수 있는 외부 데이터 소스 설정 예시 목록.

post /jaxrs/externaldatasources/set
외부 데이터 소스 설정.
{
"externalDataSources": [{
"enable": true,
"url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
"username": "sa",
"password": "password",
"includes": [],
"excludes": [],
"logLevel": "ERROR",
"autoCommit": false,
"schema": "X"
}]
}


post /jaxrs/externaldatasources/validate
데이터 소스에 연결할 수 있는지 확인.
{
"externalDataSources": [{
"enable": true,
"url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
"username": "sa",
"password": "password",
"includes": [],
"excludes": [],
"logLevel": "ERROR",
"autoCommit": false,
"schema": "X"
}]
}

get /jaxrs/externaldatasources/set/cancel
외부 데이터 소스 설정 취소.

## h2

get /jaxrs/h2/check
h2 서버 업그레이드 필요 여부 확인, jarVersion 프로그램 h2 버전, localRepositoryDataH2Version 로컬 파일 버전, dataBaseFileExists 데이터베이스 파일 존재 여부, needUpgrade 업그레이드 필요 여부

get /jaxrs/h2/upgrade
업그레이드 확인

get /jaxrs/h2/upgrade/cancel
업그레이드 취소

## restore

post /jaxrs/restore/upload
zip 형식 데이터 패키지 업로드, 필드 이름 file

get /jaxrs/restore/upload/cancel
데이터 가져오기 취소

## secret

get /jaxrs/secret/check
비밀번호가 비어 있는지 확인

post /jaxrs/secret/set
초기 관리자 비밀번호 설정 {"secret":"o2oa@2022"}

get /jaxrs/secret/cancel
초기 관리자 비밀번호 설정 취소

## server


get /jaxrs/server/execute
서버 작업 실행. 완료되면 init 서버를 멈추고 정상 기동한다.

get /jaxrs/server/execute/status
초기 서버 작업 실행 상태 조회 {"status":"waiting|running|success|failure", "messages":[""],"failureMessage":""}

get /jaxrs/server/stop
init 서버 중지
