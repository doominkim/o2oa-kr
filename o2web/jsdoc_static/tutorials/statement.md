## 문법
### 1. JPQL 문법
 조회 구문은 JPA JPQL 구문을 지원합니다. 다음 구문은 시스템 테이블 Task에서 결재 대기를 가져옵니다:
```sql
SELECT o FROM Task o WHERE o.person = :person
```
![JPQL](img/tutorial/statement/jpql.png)
JPQL 공식 사이트: [https://www.objectdb.com/java/jpa/query/jpql/structure](https://www.objectdb.com/java/jpa/query/jpql/structure)<br />
중국어 소개: [https://www.codercto.com/a/4338.html](https://www.codercto.com/a/4338.html)

### 2. 네이티브 SQL 문법
v8.0 이후 버전에서는 조회 구문이 네이티브 SQL 문법을 지원합니다. 다음 구문은 시스템 테이블 Task에서 결재 대기를 가져옵니다:
```sql
SELECT * FROM Task WHERE person = :person
```
![SQL](img/tutorial/statement/sql.png)
w3schoole 사이트: [https://www.w3schools.com/sql/default.asp](https://www.w3schools.com/sql/default.asp)<br />
중국어 사이트: [https://www.runoob.com/sql/sql-tutorial.html](https://www.runoob.com/sql/sql-tutorial.html)


### 3. 차이점
| **차이 항목** | **JPQL** | **SQL** | **설명** |
| --- | --- | --- | --- |
| 시스템 테이블명 | Task TaskCompleted Read ReadCompleted Work WorkCompleted Review Document | PP_C_TASK PP_C_TASKCOMPLETED PP_C_READ PP_C_READCOMPLETED PP_C_WORK PP_C_WORKCOMPLETED PP_C_REVIEW CMS_DOCUMENT | 결재 대기 처리 완료 열람 대기 열람 완료 프로세스 인스턴스 완료된 프로세스 인스턴스 열람 가능 콘텐츠 관리 문서 |
| 데이터 테이블명(직접 생성 테이블) | TableName | QRY_DYN_TABLENAME | 데이터 테이블 생성 시 이름을 “TableName”으로 설정했다면 SQL에서는 "QRY_DYN_" 접두사에 이름의 대문자를 붙입니다. |
| 필드명 | id | xid | 필드 id를 예로 들면, SQL의 필드는 "x" 접두사에 필드명을 붙입니다. |
| 값 | `SELECT o FROM Task o WHERE o.person = '홍길동'` 또는 `SELECT o FROM Task o WHERE o.person = "홍길동"` | `SELECT * FROM PP_C_TASK WHERE person = '홍길동'` | JPQL 값에는 작은따옴표와 큰따옴표를 모두 쓸 수 있고, SQL은 작은따옴표만 쓸 수 있습니다. 모두 작은따옴표 사용을 권장합니다. |


### 
## where 절 매개변수 전달
### 1. 콜론으로 동적 매개변수 전달
조회 구문의 where 절 값은 json으로 전달할 수 있습니다. 예:<br />조회 구문 설계:
```sql
select o from tableName o where o.name=:n
```
조회 구문 서비스를 호출할 때 json 전달:
```json
{
    "n" : "zhangsan"
}
```
시스템이 최종적으로 백엔드에서 이 설계를 바탕으로 다음 구문을 조합합니다:
```sql
select o from tableName o where o.name='zhangsan'
```
아래 그림 참고:<br />
![콜론으로 동적 매개변수 전달](img/tutorial/statement/para1.png)
JPQL 구문의 동적 매개변수 전달은 다음 링크에서 확인할 수 있습니다: https://www.objectdb.com/java/jpa/query/parameter
### 2. 물음표와 숫자로 동적 매개변수 전달
V8.0에서 플랫폼은 물음표와 숫자 형식으로 where 절 값을 전달하는 기능을 지원하며, 콜론 동적 매개변수 전달과 비슷하게 동작합니다. 예:<br />조회 구문 설계:
```sql
select o from tableName o where o.name= ?1
```
조회 구문 서비스를 호출할 때 json 전달:
```json
{
    "?1" : "zhangsan"
}
```
시스템이 최종적으로 백엔드에서 이 설계를 바탕으로 다음 구문을 조합합니다:
```sql
select o from tableName o where o.name='zhangsan'
```
아래 그림 참고:<br />
![물음표와 숫자로 동적 매개변수 전달](img/tutorial/statement/para2.png)
### 3. 기본 매개변수
시스템에는 몇 가지 기본 매개변수가 있으며, 시스템이 자동으로 값을 할당합니다.

| **기본 매개변수** | **의미** |
| --- | --- |
| person | 현재 사용자 |
| identityList | 현재 사용자 신분 목록 |
| unitList | 현재 사용자의 직속 조직 |
| unitAllList | 현재 사용자가 속한 모든 조직 |
| groupList | 현재 사용자가 속한 그룹 |
| roleList | 현재 사용자가 가진 역할 |

다음과 같은 구문이 있다면:
```json
select o from Task o where o.person = :person
```
매개변수 `:person`은 현재 사용자입니다.<br />v8.0 이전에는 외부에서 매개변수 {person: ""}를 전달하면 됩니다.<br />v8.0 이후에는 시스템이 이 기본 매개변수를 자동으로 해석하므로 전달할 필요가 없습니다.<br />현재 사용자가 `홍길동@zhangsan@P`이면 시스템이 해석 후 다음과 같이 자동 조합합니다:
```json
select o from Task o where o.person = "홍길동@zhangsan@P"
```

## 기타 주의 사항

### 1. 날짜 형식 작성법
sql에서는 날짜 형식을 텍스트로 쓰면 됩니다. 예: "2019-12-31", "23:59:59", "2020-01-03 13:59:59".<br />
jpql에서는 날짜 형식에 특별한 작성법이 있으며 형식은 다음과 같습니다:<br />
Date - {d 'yyyy-mm-dd'} - for example: {d '2019-12-31'}<br />
Time - {t 'hh:mm:ss'} - for example: {t '23:59:59'}<br />
Timestamp(DateTime) - {ts 'yyyy-mm-dd hh:mm:ss'} - for example: {ts '2020-01-03 13:59:59'}
<br />예시는 아래 그림과 같습니다:<br />
![날짜 형식](img/tutorial/statement/datetime.png)
### 2. like 작성법
연산자가 like(모호 조회)이면 값은 "%{value}%"입니다.
