# O2OA API {@o2version}

## 소개

* O2OA는 고도로 사용자 정의 가능한 기업용 오피스 플랫폼이며, 스크립트 언어로 플랫폼 기능을 확장할 수 있습니다.

* O2OA 프런트엔드 스크립트는 브라우저와 모바일 h5 페이지에서 실행되며, 사용 중인 브라우저의 Javascript 엔진을 기반으로 Javascript 문법을 사용합니다.

* O2OA 백엔드 스크립트는 서버에서 실행됩니다. V9.0.0 버전부터 GraalVM 기반으로 ECMAScript 2021 규격과 완전히 호환됩니다. 9.0.0(미포함) 이전에는 Nashorn 기반으로 ECMAScript 5.1 규격을 따릅니다. 이 API의 modules.server 장 외에도 백엔드 스크립트는 다음 글에서 설명하는 기능을 지원합니다: [https://www.o2oa.net/cms/service/531.html](https://www.o2oa.net/cms/service/531.html).

* 이 API는 O2OA 스크립트 작성 안내입니다. 스크립트로 플랫폼의 다양한 객체에 접근하고 조작할 수 있습니다.

## API 개요

### Namespace

* 플랫폼의 전역 네임스페이스

#### web
   {@o2IndexTable namespaces|o2category=web}


#### mobile
   * 모바일 H5 페이지는 web 장과 Class 장에서 설명한 API 외에 다음 API도 사용할 수 있습니다:
   
   {@o2IndexTable namespaces|o2category=mobile}

### Modules

* 스크립트용으로 캡슐화된 객체, 메서드, 클래스. this[moduleName]으로 호출합니다.

 
#### web
  * 프런트엔드 스크립트(브라우저/모바일 H5 페이지)에서 사용할 수 있는 객체, 메서드, 클래스.
  
   {@o2IndexTable modules|o2category=web}
 
  
#### server
 * 백엔드 스크립트(서버)에서 사용할 수 있는 객체, 메서드, 클래스.
    
##### common 공통 백엔드 api
{@o2IndexTable modules|o2category=server.common}

##### process 프로세스 백엔드 api
  {@o2IndexTable modules|o2category=server.process}
  
##### service 서비스 관리 api
  {@o2IndexTable modules|o2category=server.service}
 

### FormComponent

* FormComponent는 양식 페이지 컴포넌트로, 프런트엔드 스크립트에서 사용할 수 있습니다. `this.form.get("fieldId")`로 가져올 수 있습니다.

#### Process 프로세스 양식 컴포넌트
   {@o2IndexTable classes|o2category=FormComponents|o2range=Process}


#### CMS 콘텐츠 관리 양식 컴포넌트
   {@o2IndexTable classes|o2category=FormComponents|o2range=CMS}


#### Portal 포털 페이지 컴포넌트
   {@o2IndexTable classes|o2category=FormComponents|o2range=Portal}

### Query

* Query는 데이터 센터 앱이며, 이 장에서는 뷰, 조회 뷰, 가져오기 모델의 api를 다룹니다.

#### QueryView 뷰 클래스
   {@o2IndexTable classes|o2category=QueryView|o2range=QueryView}
   
#### QueryStatement 조회 뷰 클래스
   {@o2IndexTable classes|o2category=QueryStatement|o2range=QueryStatement}
   
#### QueryImporter 가져오기 모델 클래스
   {@o2IndexTable classes|o2category=QueryImporter|o2range=QueryImporter}
    
### 서비스

* 서비스는 O2OA 백엔드가 제공하는 restful 서비스 모음입니다.

<table>
    <tr>
        <td>x_processplatform_assemble_surface</td>
        <td>프로세스 플랫폼 관련 서비스</td>
    </tr>
    <tr>
         <td>x_portal_assemble_surface</td>
         <td>포털 플랫폼 관련 서비스</td>
     </tr>
    <tr>
         <td>x_cms_assemble_control</td>
         <td>콘텐츠 관리 플랫폼 관련 서비스</td>
     </tr>
    <tr>
         <td>x_query_assemble_surface</td>
         <td>데이터 플랫폼 관련 서비스</td>
     </tr>
    <tr>
         <td>x_organization_assemble_express</td>
         <td>조직 구조 관련 서비스</td>
     </tr>
    <tr>
         <td>x_file_assemble_control</td>
         <td>클라우드 파일 관련 서비스</td>
     </tr>
    <tr>
         <td>x_meeting_assemble_control</td>
         <td>회의 관리 관련 서비스</td>
     </tr>
     <tr>
          <td>x_bbs_assemble_control</td>
          <td>포럼 관련 서비스</td>
      </tr>
     <tr>
          <td>x_calendar_assemble_control</td>
          <td>일정 관리 관련 서비스</td>
      </tr>
     <tr>
          <td>x_hotpic_assemble_control</td>
          <td>핫 정보 관련 서비스</td>
      </tr>
     <tr>
          <td>x_mind_assemble_control</td>
          <td>마인드맵 모듈 관련 서비스</td>
      </tr>
     <tr>
          <td>x_organization_assemble_personal</td>
          <td>개인 설정 관련 서비스</td>
      </tr>
     <tr>
          <td>x_attendance_assemble_control</td>
          <td>근태 모듈 관련 서비스</td>
      </tr>
</table>

## 사용 범위

O2OA는 여러 위치에 스크립트 코드를 삽입해 플랫폼을 확장하고 사용자 정의 기능을 구현할 수 있습니다. 삽입 스크립트 코드는 프런트엔드 실행 코드와 서버 실행 코드로 나뉩니다.
 
 * 프런트엔드 스크립트 사용 범위:
 <table>
      <tr>
          <td>스크립트 라이브러리</td>
          <td>프로세스 플랫폼, 포털 플랫폼, 콘텐츠 관리 플랫폼에는 모두 스크립트 설계 요소가 있으며 여기서 스크립트 라이브러리를 만들 수 있습니다.</td>
      </tr>
      <tr>
           <td>양식, 페이지, 뷰, 조회 뷰, 가져오기 모델 및 컴포넌트 이벤트</td>
           <td>프로세스 플랫폼과 콘텐츠 플랫폼의 양식, 포털 플랫폼의 페이지, 데이터 플랫폼의 뷰와 조회 뷰에서 각 설계 컴포넌트는 DOM 객체 기본 이벤트와 O2 플랫폼 확장 이벤트를 포함한 여러 이벤트를 가집니다.</td>
       </tr>
      <tr>
           <td>양식, 페이지 편집 가능 컴포넌트 기본값</td>
           <td>프로세스 플랫폼과 콘텐츠 플랫폼의 양식 및 포털 플랫폼의 페이지에서 편집 가능 컴포넌트나 텍스트 컴포넌트의 기본값을 스크립트로 지정할 수 있습니다.</td>
       </tr>
      <tr>
           <td>양식 및 편집 가능 컴포넌트 검증</td>
           <td>프로세스 플랫폼과 콘텐츠 관리의 양식에서 편집 가능 필드는 스크립트로 유효성을 검증할 수 있으며, 통과하면 true를, 통과하지 못하면 안내 메시지를 반환합니다.</td>
       </tr>
      <tr>
           <td>프로세스 설계</td>
           <td>프로세스 설계는 주로 백엔드 스크립트이지만 예외가 있으며, 프로세스 경로 속성의 확장 탭과 선택 탭의 스크립트는 프런트엔드 스크립트를 사용합니다.</td>
       </tr>
  </table>
  
  * 백엔드 스크립트 사용 범위:
  <table>
       <tr>
           <td>스크립트 라이브러리</td>
           <td>프로세스 플랫폼, 포털 플랫폼, 콘텐츠 관리 플랫폼에는 모두 스크립트 설계 요소가 있으며 여기서 스크립트 라이브러리를 만들 수 있습니다.</td>
       </tr>
       <tr>
            <td>서비스 관리</td>
            <td>서비스 관리의 인터페이스와 예약 에이전트.</td>
        </tr>
       <tr>
            <td>프로세스 설계</td>
            <td>프로세스 속성의 모든 스크립트(프로세스 이벤트, 기한 스크립트 등), 프로세스 활동의 모든 스크립트(처리자, 열람 대기자, 열람자 스크립트, 분할 기준, 기한 스크립트, 활동 이벤트, 매개변수 스크립트, 응답 스크립트, 실행 스크립트 등), 프로세스 경로의 조건 스크립트.</td>
        </tr>
       <tr>
            <td>조회 설정</td>
            <td>조회 설정의 조회 구문과 총계 구문.</td>
        </tr>
       <tr>
            <td>뷰 열</td>
            <td>뷰 열의 표시 스크립트.</td>
        </tr>
   </table>
   

### 프런트엔드 스크립트 사용 범위 상세

#### 스크립트 라이브러리

프로세스 플랫폼, 포털 플랫폼, 콘텐츠 관리 플랫폼에는 모두 스크립트 설계 요소가 있으며 여기서 자신의 스크립트 라이브러리를 만들 수 있습니다. 아래 그림 참고:
![스크립트 라이브러리](img/home/script.png)
<br/><br/>

#### 양식, 페이지, 뷰, 조회 뷰 및 컴포넌트 이벤트

프로세스 플랫폼과 콘텐츠 플랫폼의 양식, 포털 플랫폼의 페이지, 데이터 플랫폼의 뷰와 조회 뷰에서 각 설계 컴포넌트는 DOM 객체 기본 이벤트와 O2 플랫폼 확장 이벤트를 포함한 여러 이벤트를 가집니다. 아래 그림 참고:
![이벤트의 스크립트](img/home/script_event.png)
<br/><br/>

#### 양식, 페이지 편집 가능 컴포넌트 기본값

프로세스 플랫폼과 콘텐츠 플랫폼의 양식 및 포털 플랫폼의 페이지에서 편집 가능 컴포넌트나 텍스트 컴포넌트의 기본값을 스크립트로 지정할 수 있습니다. 아래 그림 참고:
![이벤트의 스크립트](img/home/script_defaultvalue.png)
<br/><br/>

#### 양식, 페이지, 뷰, 조회 뷰의 일부 속성

프로세스 플랫폼과 콘텐츠 플랫폼의 양식, 포털 플랫폼의 페이지, 데이터 플랫폼의 뷰와 조회 뷰에서 많은 컴포넌트의 관련 속성을 스크립트로 정의할 수 있습니다.<br/>
예: 드롭다운, 단일·다중 선택 버튼의 선택 가능 값, 사용자 필드의 선택 범위, 구간 기준 등. 아래 그림 참고:<br/>
![속성의 스크립트](img/home/script_attribute.png)
<br/><br/>

#### 양식 및 편집 가능 컴포넌트 검증

프로세스 플랫폼과 콘텐츠 관리의 양식에서 편집 가능 필드는 스크립트로 유효성을 검증할 수 있으며, 통과하면 true를, 통과하지 못하면 안내 메시지를 반환합니다. 아래 그림 참고:
![스크립트](img/home/script_validation.png)
<br/><br/>

#### 프로세스 경로 속성의 확장 탭과 선택 탭 스크립트
![확장 탭 스크립트](img/home/script_route_extend.png)
![선택 탭 스크립트](img/home/script_route_select.png)
<br/><br/>


### 백엔드 스크립트 사용 범위 상세

#### 스크립트 라이브러리
프로세스 플랫폼, 포털 플랫폼, 콘텐츠 관리 플랫폼에는 모두 스크립트 설계 요소가 있으며 여기서 스크립트 라이브러리를 만들 수 있습니다.
![스크립트 라이브러리](img/home/server_script.png)
<br/><br/>

#### 서비스 관리
서비스 관리의 인터페이스와 예약 에이전트.
![예약 에이전트](img/home/server_agent.png)
![인터페이스](img/home/server_interface.png)
<br/><br/>

#### 프로세스 설계
프로세스 속성의 모든 스크립트(프로세스 이벤트, 기한 스크립트 등).
![프로세스 속성](img/home/server_process.png)

프로세스 활동의 모든 스크립트(처리자, 열람 대기자, 열람자 스크립트, 분할 기준, 기한 스크립트, 활동 이벤트, 매개변수 스크립트, 응답 스크립트, 실행 스크립트 등)
![프로세스 활동](img/home/server_process_activity.png)

프로세스 경로의 조건 스크립트.
![프로세스 경로](img/home/server_process_route.png)

#### 조회 설정
조회 설정의 조회 구문과 총계 구문.
![프로세스 경로](img/home/server_statement.png)

#### 뷰 열
뷰 열의 표시 스크립트로, this.value로 이 열의 값을, this.entry.data로 이 행의 데이터를 가져올 수 있습니다.
![뷰 열 표시 스크립트](img/home/server_view_column.png)

## 예제
* 스크립트를 작성하고 실행하는 방법을 보여 주는 간단한 예제입니다.
* 이 예제에서는 양식의 두 드롭다운이 설정 데이터에서 선택 가능 데이터를 가져오고 서로 연동되도록 구현합니다.
* 프로세스 앱 하나, 양식 하나, 데이터 사전 하나를 만들고 양식에 드롭다운 목록 두 개를 만듭니다.

1. 먼저 양식에 드롭다운 두 개를 만듭니다
<br/>
![드롭다운](img/home/example_1_1.png)
<br/><br/>

2. category1과 category2 드롭다운 속성에서 “스크립트”로 선택 가능 값을 편집하도록 선택합니다.
<br/>
category1 스크립트:
<br/>
![드롭다운](img/home/example_1_2_1.png)
<br/><br/>
category2 스크립트:
<br/>
![드롭다운](img/home/example_1_2_2.png)
<br/><br/>

3. category1과 category2 드롭다운의 선택 가능 값 스크립트는 다음과 같습니다:<br/><br/>
category1의 선택 가능 값 스크립트:
```
var dict = new this.Dict("category");   //이름이 category인 데이터 사전 가져오기 
var categoryList = dict.get();  //데이터 사전 가져오기
var options = Object.keys(categoryList);    //대분류를 가져와 options 변수에 할당
options.unshift("(대분류를 선택하세요)|");   //options 배열 맨 앞에 안내 항목 삽입, “”를 value로, “(대분류를 선택하세요)”를 text로 사용
return options; //목록을 반환해 목록 상자의 선택 가능 값으로 사용 
```    
category2의 선택 가능 값 스크립트:
```  
var dict = new this.Dict("category"); //이름이 category인 데이터 사전 가져오기 
var categoryList = dict.get(this.data.category1); //데이터 사전 가져오기, 드롭다운 category1의 값을 키로 하는 데이터 값(배열) 
return categoryList; //목록을 반환해 목록 상자의 선택 가능 값으로 사용 
```  

4. category1의 change 이벤트에 다음 코드를 추가합니다:
```  
//category2 드롭다운을 가져와 선택 항목 새로 고침
this.form.get("category2").resetOption();
```  
5. 데이터 사전을 아래 그림처럼 설계하고 이름은 "분류 설정", 별칭은 “category”로 지정합니다.
![드롭다운](img/home/example_1_5.png)

6. 양식을 미리 보면 대분류 드롭다운의 항목이 데이터 사전의 첫 단계 데이터이며, 대분류를 선택하면 소분류 드롭다운과 연동됩니다.