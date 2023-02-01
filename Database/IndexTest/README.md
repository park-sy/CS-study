# 인덱스


### 인덱스(Index)란 무엇인가?
- 정의
    - 검색속도를 높이기 사용하는 하나의 기술
    - 인덱스란 데이터를 빠르게 검색할 수 있게 해주는 객체.
    - 레코드 검색 시 Full Scan하는게 아닌 Index파일을 검색하여 속도를 빠르게 도움을 주는 기술
        - DBMS 에서 인덱스는 데이터의 저장 성능을 희생하고 그 대신 데이터의 읽기 속도를 높이는 기능
- 선택도와 카디널리티
    - **인덱스의 선택도**
        - 선택도란, 전체 레코드 중에서 조건절에 의해 선택될 것으로 예상되는 레코드의 비율
        - 선택도 = 카디널리티 / 총 레코드 수
            - *Selectivity = Cardinality / Total Number Of Records*
            - 선택도가 **낮을수록** 인덱스의 후보가 되기 좋다.
            - 선택도가 높다면 테이블 엑세스만 많이 발생하기 때문
    - 카디널리티
        - 기수성(Cardinality), 특정 데이터 집합의 유니크(Unique)한 값의 개수
        - *Cardinality = Distinct Value 개수 = select count(distinct (column)) from table*
### 인덱스의 원리
- 테이블 생성 시 3가지 파일이 생성
    - FRM( 테이블 구조 저장 파일 )
    - MYD( 실제 데이터 파일 )
    - MYI ( Index 정보 파일 )
- INDEX를 해당 컬럼에 주게 되면 초기 TABLE 생성 시 만들어진 MYI에 해당 컬럼을 따로 인덱싱하여 MYI 파일에 입력
    - 물론 INDEX를 사용 안 할 시에는 MYI파일은 비어 있음
- 그래서 사용자가 SELECT 쿼리로 INDEX가 사용하는 쿼리를 사용 시 해당 TABLE을 검색하는 것이 아니라 빠른 TREE로 정리해둔 MYI파일의 내용을 검색
    - 모든 데이터를 검색하는 것보다 컬럼의 값과 해당 레코드가 저장된 주소를 키와 값의 쌍으로 만들어두었기에 훨씬 빠른 성능으로 접근 가능
- **SELECT 문 수행 시 ( name = 'turtle'인 대상 탐색 )**
    - 서버 프로세스가 DB Buffer Cache에 name이 'turtle'인 정보가 있는지 확인
    - Buffer에 없다면, 하드디스크에서 turtle 정보를 가진 블록을 복 Buffer Cache로 복사한 후, 특정 데이터 출력
    - Index가 없는 경우, 데이터 파일 블록 전체를 Cache로 복사 후 찾음
        - 풀스캔
    - Index가 있는 경우, where 절에 Index 컬럼이 있는지 확인 후, 인덱스에서 turtle이 어떤 'ROW ID'를 가진 지 찾아서 해당 블록만 Cache에 복사
- **Insert 문 수행 시**
    - 기존 Block에 여유가 없을 때, 새로운 Block을 할당 받은 후, Key를 옮긴다.
    - 이 때 해당 key값들은 DML이 블로킹되고, lock이 걸린다.
        - INSERT 의 경우 INDEX 에 대한 데이터도 추가해야 하므로 그만큼 성능에 손실이 따른다.
        - index split이 발생할 수도 있음
            - 인덱스의 Block들이 하나에서 두 개로 나누어지는 현상
            - **index split은 새로운 블록을 할당 받고 key를 옮기는 복잡한 작업을 수행**
            - **index split이 이루어지는 동안 해당 블록에 대해 키 값이 변경되면 안되므로 DML이 블로킹**
- **Delete문 수행 시**
    - Data는 삭제되어서 그 공간에 다른 Data가 사용 가능하지만, Inde는 Data는 지워지지 않고 "사용 안됨"표시만 생성
    - DELETE 의 경우 INDEX 에 존재하는 값은 삭제하지 않고 사용 안한다는 표시로 남게 됨
    - 즉 row 의 수는 그대로이며 실제 데이터는 10 만건인데 데이터가 100 만건 있는 결과를 낳을 수도 있음
- **Update 수행 시**
    - Index는 Delete -> Insert 작업으로 수행( index에선 update가 없다.)
    - UPDATE 의 경우는 INSERT 의 경우, DELETE 의 경우의 문제점을 동시에 수반
    - **이전 데이터가 삭제되고 그 자리에 새 데이터가 들어오는 개념.** 
    - 즉, 변경 전 데이터는 삭제되지 않고 insert 로 인한 split 도 발생

### 인덱스 자료구조 : RDBMS에서는 B+ Tree를 사용
- B+-Tree 인덱스 알고리즘
    - 일반적으로 사용되는 인덱스 알고리즘은 B+-Tree 알고리즘
    - B+ -Tree 인덱스는 칼럼의 값을 변형하지 않고(사실 값의 앞부분만 잘라서 관리한다.), 원래의 값을 이용해 인덱싱하는 알고리즘
    - **Q) B + - 차이?**
        ![image](https://user-images.githubusercontent.com/53611554/215512680-6d6b1efc-828a-4fc2-a80a-5a5cb3a28a04.png)
        
        - key 데이터만 담음. 하나의 노드에 더 많은 key들을 담을 수 있기에 트리의 높이는 더 낮아짐.(cache hit를 높일 수 있음)
        - B tree는 모든 노드에 레코드 포인터를 가지지만, B+ Tree는 오직 리프 노드에서만 레코드 포인터를 가지며 리프 노드에 모든 키들이 존재해야 함(부모노드에도 존재)
            - 모든 리프노드는 왼쪽에서 오른쪽으로 연결되어 있음(리프는 더블 연결리스트 활용, 부모 자식은 연결리스트)
- Index 자료구조
    - Hash 인덱스 알고리즘
        - 칼럼의 값으로 해시 값을 계산해서 인덱싱하는 알고리즘으로 매우 빠른 검색을 지원
        - 특정 문자로 시작하는 값으로 검색을 하는 전방 일치와 같이 값의 일부만으로 검색하고자 할 때는 해시 인덱스를 사용할 수 없음 
        - 주로 메모리 기반의 데이터베이스에서 많이 사용
        - **Q) 왜 index 를 생성하는데 b-tree 를 사용하는가?**
            - 동등 연산(=)에 특화된 해시테이블은 데이터베이스의 자료구조로 적합하지 않으며. where 조건절에 동등뿐만 아니라 대소 비교도 자주 나옴
            - 또한 값을 변형해서 인덱싱하므로, 전방(Prefix) 일치와 같이 값의 일부만 검색하고자 할 때는 해시 인덱스를 사용할 수 없음

### Primary Index vs Secondary Index  
- 클러스터드 인덱스는 테이블의 **PK에 대해서만 적용되는 내용, 즉 프라이머리 키 값이 비슷한 레코드끼리 묶어서 저장하는 것을 클러스터드 인덱스**라고 표현
    - 클러스터(Cluster)란 여러 개를 하나로 묶는다는 의미로 주로 사용
        - 인덱스에서 클러스터드는 비슷한 것들을 묶어서 저장하는 형태로 구현되는데, 이는 주로 비슷한 값들을 동시에 조회하는 경우가 많다는 점에서 착안
    - 기본키 설정시 자동으로 만들어지고, 테이블 자체가 인덱스이므로 인덱스 페이지가 없음
    - 클러스터드 인덱스는 테이블 당 한 개만 생성할 수 있음 (PK에 대해서만 적용되기 때문)
- 비슷한 값들은 물리적으로 인접한 장소에 저장되어 있는 데이터들을 말함
    - 클러스터드 인덱스에서는 PK 값에 의해 레코드의 저장 위치가 결정되며 PK 값이 변경되면 그 레코드의 물리적인 저장 위치 또한 변경되어야 함
    - 그렇기 때문에 PK를 신중하게 결정하고 클러스터드 인덱스를 사용해야 함
- 이에 반해 Non 클러스터드 인덱스는 테이블 당 여러 개를 생성할 수 있다.
    
    ![image](https://user-images.githubusercontent.com/53611554/215512582-cfee4828-07a9-403d-a103-dc27fd6dee98.png)

    
- **클러스터링 인덱스에서 PK가 존재하지 않는다면**
    - NOT NULL 옵션의 유니크 인덱스 중에서 첫 번째 인덱스를 클러스터링 키로 선택
    - 자동으로 유니크한 값을 가지도록 증가되는 컬럼을 내부적으로 추가한 후, 그것을 클러스터링 키로 선택
        - InnoDB 스토리지 엔진이 내부적으로 레코드의 일련번호 컬럼을 생성하는데, 해당 컬럼은 쿼리 문장에 명시적으로 사용할 수 없으며 사용자에게 노출되지도 않음
        - 그래서 아무런 의미가 없는 값이 되므로 되도록 PK를 명시적으로 생성하는 것을 권장

### Composite Index
- Composite Index : 2개 이상의 컬럼을 합쳐서 생성한 인덱스
- 인덱스로 설정하는 필드의 속성이 중요
    - title, author 이 순서로 인덱스를 설정한다면 title 을 search 하는 경우, index 를 생성한 효과를 볼 수 있지만, author 만으로 search 하는 경우, index 를 생성한 것이 소용이 없어짐
- **Composite Index 전략**
    - 데이터가 많고 조건에 걸리는 컬럼들이 많은 경우에 사용
        - 복합 인덱스는 테이블 데이터를 기준으로 새로운 인덱스 공간을 생성 및 데이터를 정렬하기 때문에 공간 사용량이 증가
    - **조회 시, 인덱스 선두 컬럼이 조건절에 없으면 옵티마이저는 먼저 Table Full Scan 방식을 고려한다.**
        - 즉 복합 인덱스에서 선행 인덱스를 넣어주지 않으면 풀스캔과 흡사
        - Equal 연산이 아닌 검색 조건이 들어오는 경우(범위 연산), 처리 범위가 크게 증가하여 효율이 크게 저하될 수 있음
    - 조회 조건에 포함되어 있는지가 중요할 뿐, **반드시** 인덱스 순서와 조회 칼럼 순서를 지킬 필요는 없음
- 사용하는 경우
    - where절에서 and 조건으로 자주 결합되어 사용되면서 각각의 분포도 보다 두 개 이상의 컬럼이 결합될 때 분포도가 좋아지는 컬럼
    - 다른 테이블과 조인의 연결고리로 자주 사용되는 컬럼
    - order by에서 자주 사용되는 컬럼들

### 인덱스 주의사항 및 실제 사용
- 인덱스를 추가할지 말지는 데이터의 저장 속도를 어디까지 희생할 수 있는지, 읽기 속도를 얼마나 더 빠르게 만들 수 있는지의 여부에 따라 결정
    - 레코드 100만건이 있을 때, Full Table Scan을 한 후 50만건을 버릴지, 인덱스를 통해 50만건만 읽을지 판단
    - 일반적으로 인덱스를 통해 1건 읽는 비용 = 직접 읽는 비용 x 4~5
    - 읽어야할 레크도 건수가 전체 테이블의 20~25%넘기면 FTS가 더 효율적
- 카디널리티와 분포도를 고려하여 인덱스 사용
    - 카디널리티가 작은 속성(값의 range가 적은)은 인덱스를 읽고 다시 한 번 디스크 I/O 가 발생하기 때문에 그 만큼 비효율적
    - 분포도가 높은 칼럼에 인덱스 적용
    - 아래 쿼리의 결과가 레코드 한 개라고 가정.

        ```sql
        SELECT * FROM tb_test WHERE country='KOREA' and city='SEOUL';
        ```
        - 케이스 A
            - country 칼럼의 유니크한 레코드 개수가 10개 → 분포도 0.1%
            - 위 쿼리를 실행하면 평균 1,000건의 데이터가 조회된다. 따라서, 999건의 불필요한 데이터를 읽게 된다.
        - 케이스 B
            - country 칼럼의 유니크한 레코드 개수가 1000개 → 분포도 10%
            - 위 쿼리를 실행하면 평균 10건의 데이터가 조회된다. 따라서, 9건의 불필요한 데이터만 읽게 된다.
    - 중복되는 데이터가 전체의 15%를 넘어가는 칼럼은 인덱스를 설정하지 않는 것이 좋음
- 인덱스가 full table scan보다 늦어지는 경우도 있음
    - Full Scan일 경우, 특정 주소에 접근하는 방식이 아니라 블록별로 모두 순차적으로 접근 
    - 메모리에 적재해야할 것이 많아지긴 하지만 순차적으로 접근하기 때문에 접근비용이 감소
        - 인덱스는 랜덤엑세스
    - 또한 인덱스를 사용(Index Range Scan)할 경우 Single Block I/O 방식으로 블록을 읽음 
    - 캐시에서 블록을 찾지 못하면 매번 레코드하나를 가져오는 동안 잠을자는 I/O 매커니즘
- **인덱스를 잡을 때 키의 길이는 짧을 수록 좋음**
    - InnoDB(MySQL)에서 디스크에 데이터를 저장하는 가장 기본단위를 페이지라 하며, 인덱스 역시 페이지 단위로 관리
    - 인덱스가 저장되는 페이지는 16KB 크기로 고정됨
    - 키의 크기가 길 수록 페이지에 저장되는 인덱스의 양은 줄어들고, 페이지에 저장된 인덱스의 양이 줄어들 수록, 조회시 더 많은 페이지를 조회
    - 키의 길이는 B tree의 높이를 결정하는 주요 요소
- Join과 Index
    - Join시 Outer Talbe(Driving Table)과 Inner Table(Driven Table)을 탐색하며 O(m*n)의 시간복잡도가 발생
    - 수행시간을 줄이기 위해서 Driving Table의 크기를 줄이거나 Inner Table에 대해 인덱스 생성
    - Inner Table에 대하여 어떤 인덱스를 생성하면 Inner Table의 모든 레코드를 스캔할 필요가 없어짐
- 다중 칼럼 인덱스를 사용(결합/복합 인덱스)
    - 다중칼럼 인덱스에서 중요한 것은 N 번째 칼럼은 N-1번째 칼럼에 의존해서 정렬한다는 것
    - 뒤의 칼럼이 빠르더라도 앞의 칼럼이 더 느리다면 뒤쪽에 위치하기 때문이 **칼럼의 순서**가 중요
    - 동등비교인지, 범위 조건인지에 따라 인덱스 칼럼의 활용 형태가 달라지며 효율 또한 달라짐
    ```sql
    SELECT * FROM dept_emp WHERE dept_no='d002' AND emp_no >= 1014;
    (dept_no, emp_no)와 (emp_no, dept_no) 두 가지의 경우
    전자는 dept_no='d002' AND emp_no >= 1014를 만족하는 레코드를 찾은 후에 dept_no가 d002가 아닐 때까지 인덱스를 스캔하면 됨
    후자는 dept_no='d002' AND emp_no >= 1014를 만족하는 레코드를 찾은 후에 그 이후의 모든 레코드에 대하여 dept_no가 d002인지 비교하는 작업을 수행
    ```
    - 다중 칼럼 인덱스 대신 각각의 인덱스를 생성하면 옵티마이저가 어떤 인덱스를 탐색하는 것인지 판단하기 때문에 성능이 저하됨
- 다중 칼럼 인덱스 사용시 주의점
    -  인덱스는 Prefix를 기준으로 생성이되기 때문에, WHERE 절이 다중 칼럼 인덱스를 이용하도록 하려면 비교구문의 칼럼들이 다중 칼럼 인덱스에 대하여 Prefix를 이루어야함
    -  즉, 뒷부분 일치 형태의 문자열 비교는 인덱스 사용이 불가능
    -  (col1, col2, col3)으로 인덱스가 이루어진 경우, WHERE 절의 비교 순서는 (col1, col2, col3), (col1, col2)와 같아야 함
        -  (col1, col3) 이나 (col2, col3)은 인덱스를 사용할 수 없음
- 클러스터링 테이블 사용시 주의점
```프라이머리 키값에 의해 레코드의 저장 위치가 결정된다. → 프라이머리 키 값이 변경되면 레코드의 저장 위치도 바뀐다.```
    - **클러스터 인덱스 키의 크기**
        - 클러스터 테이블의 경우 모든 보조 인덱스가 프라이머리 키 값을 포함한다.
        - 따라서 프라이머리 키의 크기가 커지면 보조 인덱스도 자동으로 크기가 커진다.
        - 일반적으로 하나의 테이블에 보조 인덱스가 4~5개가 생성되는데 이 경우 보조 인덱스 전체 크기가 급격하게 증가한다.
        - InnoDB 테이블의 프라이머리 키는 신중하게 선택해야 한다.
    - **가능하면 프라이머리 키는 AUTO-INCREMENT보다 업무적인 칼럼으로**
        - 클러스터링 테이블에서 프라이머리 키를 이용한 검색은 클러스터링되지 않는 테이블이 비해 검색이 매우 빠르다.
        - 프라이머리 키는 의미만큼이나 중요한 역할을 하고 검색에서 빈번하게 사용되기 때문에 그 크기가 크더라도 업무적으로 해당 레코드를 대표할 수 있는 칼럼이 있다면 프라이머리 키로 사용하는 것이 좋다.
    - **프라이머리 키는 반드시 명시할 것**
        - 프라이머리 키가 없는 테이블이라면 AUTO_INCREMENT 칼럼을 이용해서라도 프라이머리 키를 설정하는 것이 좋다.
        - InnoDB 엔진은 디폴트로 클러스터링 테이블을 만들기 때문에 프라이머리 키를 정의하지 않으면 내부적으로 자동 증가 칼럼을 추가한다.
        - 즉 프라이머리 키를 정의 하지 않는 경우와 AUTO_INCREMENT 칼럼을 생성하는 경우의 성능은 동일하다.
        - 이런 경우 업무적으로 프로그래머가 확인할 수 있는 프라이머리 키를 설정하는 것이 좋다.
    - **AUTO-INCREMENT 칼럼을 인조 식별자로 설정**
        - 프라이머리 키가 다중 칼럼인 경우, 보조 인덱스가 필요하지 않다면 그대로 사용해도 좋다.
        - 하지만 보조 인덱스도 필요하고 프라이머리 키의 크기도 크다면, AUTO-INCREMENT 칼럼을 추가하고 이를 프라이머리 키로 이용하는 것이 좋다.
        - 위와 같이 프라이머리 키 대신 사용하기 위해 인위적으로 추가된 프라이머리 키를 인조 식별자(Surrogate Key)라고 한다.
- 순차 I/O & 랜덤 I/O
    - 랜덤 I/O는 3개의 페이지를 디스크에 기록하기 위해 **3번** 시스템콜을 요청한다.
        - 헤드가 3번 움직인다.
    - 위 경우, 순차 I/O가 랜덤 I/O보다 3배 빠르다.
    - 여러 번 쓰기, 읽기를 요청하는 랜덤 I/O 작업의 부하가 훨씬 크다. 
    - 데이터베이스 대부분의 작업은 작은 데이터를 빈번히 읽고 쓰기 때문에 MySQL 서버에는 그룹 커밋이나 바이너리 로그 버퍼 또는 InnoDB 로그 버퍼 등의 기능이 내장되어 있다.
    - 일반적으로 쿼리를 튜닝하는 것은 랜덤 I/O 자체를 줄여주는 것이 목적이다. 랜덤 I/O를 줄인다는 것은 쿼리를 처리하는 데 꼭 필요한 데이터만 읽도록 하는 것이다. 
    - (쿼리 튜닝을 통해 랜덤 I/O를 순차 I/O로 바꿔서 실행하는 방법은 많지 않다.)
    
```
    💡 인덱스 레인지 스캔은 데이터를 읽기 위해 주로 랜덤 I/O를 사용하며, 풀 테이블 스캔은 순차 I/O를 사용한다. 
    그래서 큰 테이블의 레코드 대부분을 읽는 작업에서는 인덱스를 사용하지 않고 풀 테이블 스캔을 사용하도록 유도하는 경우가 있다. 
    순차 I/O가 랜덤 I/O보다 훨씬 빨리 많은 레코드를 읽어올 수 있기 때문이고 이러한 형태는 OLTP 성격의 웹 서비스보다 데이터 웨어하우스나 통계 작업에서 자주 사용된다.
```


### 인덱스 Test
- 목표
    - cardinaliry에 따른 index scan 검색 row 비교
    - 여러 조건으로 결합된 where문에서 index 선택
    - 결합 인덱스 순서에 따른 scan 검색 row 비교
    - where or 에서 index 비교
    - 인덱스 크기 및 전후 insert / delete 시간 비교
    옵티마이저에 의한 Duration 시간 측정은 제외함(추후 옵티마이저 Test에서 비교 예정)
- Test 환경
    - 100만건 데이터 기준으로 진행
    - Table
        |Column|Type|Cardinality|
        |------|---|---|
        |ID|INT|1,000,000|
        |GENDER|String|2|
        |MBTI|String|16|
        |AGE|INT|100|
        |REGION|INT|1,000|
        |JOB|INT|10,000|
        |INTEREST|INT|100,000|
        |INCOME|INT|250,000|
        |ASSETS|INT|500,000|
    - 1000개 이상의 데이터는 편의상 Int로 진행
#### 단일 where '=' 비교
- 결과
    - Cardinality가 높아질수록 index로 인한 효과가 커짐
        - index를 통해 구분되는 column의 개수가 많아지기 때문    
    - 쿼리 결과로 선택되는 row의 수가 적을수록(range가 적을수록) 인덱스 효과가 커짐
        - 선택되는 범위가 넓을수록 index scan 보다 full table scan이 효과적이게 됨
- Test
    - index 적용 전 row 탐색 : 996355 
    - MBTI (C : 16)
        ```sql
        Query                                       -- Full scan 검색 row 수: 996355
        select * from user where mbti = 'ISFP';     -- 검색 row 수: 126355
        ```

    - age(C : 100)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where age = 27;              -- 검색 row : 17938
        select * from user where age between 20 and 29; -- 검색 row : 189496
        ```
    - age(C : 100)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where age = 27;              -- 검색 row : 17938
        select * from user where age between 20 and 29; -- 검색 row : 189496
        ```

    - region(C : 1000)
        ```sql
        Query                                       -- Full scan 검색 row 수: 996355
        select * from user where region = 900;      -- 검색 row : 978
        select * from user where region > 900;      -- 검색 row : 193734
        ```
    - job(C : 10000)
        ```sql
        Query                                       -- Full scan 검색 row 수: 996355
        select * from user where job = 1000;        -- 검색 row : 85 
        select * from user where job < 1000;        -- 검색 row : 205852 
        ```
    - interest(C : 100000)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where interest = 10000;      -- 검색 row : 11
        select * from user where interest < 10000;      -- 검색 row : 203944
        ```
    - interest(C : 100000)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where interest = 10000;      -- 검색 row : 1 
        select * from user where interest < 10000;      -- 검색 row : 189496
        ```
    - income(C : 250000)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where income = 100000;       -- 검색 row : 1
        select * from user where income < 100000;       -- 검색 row : 79842
        ```
    - assets(C : 500000)
        ```sql
        Query                                           -- Full scan 검색 row 수: 996355
        select * from user where assets = 100000;       -- 검색 row : 1
        select * from user where assets < 100000;       -- 검색 row : 39692
        ```

#### 여러개 where and 비교
- 결과 
    - 여러 개의 조건이 and로 결합된 where문의 경우 가장 cardinality가 큰 column에 index를 거는 것이 효과적
    - cardinality가 매우 작은 column에 index를 걸 경우 full table scan보다 느려짐
    - index가 걸려있는 column에 조건이 range로 검색되는 경우 검색 속도가 저하될 수 있음
        - 해당 range에 들어가는 모든 row에 대해 나머지 쿼리를 검색하기 때문 
- Test
    ```sql
    select * from user where gender = 'male' and age = 27 and mbti = 'ISFP' and region > 900;
    -- Full scan 검색 row 수: 996355
    ```
    - gender (C : 2) - 검색 row :498177
    - mbti (C : 16) 121684
    - age (C : 100) 17938
    - region (C : 1000) 985

#### 결합 인덱스 비교
- 결과 
    - 쿼리의 우선순위에 맞춰 결합 인덱스 순서를 짜는 것이 중요
        - 우선순위 : cardinality ↑ > cardinaliry ↓ > range 탐색
        - range 탐색이 결합 인덱스 앞에 올 경우 range를 모두 range 단일 column 인덱스와 비교시 큰 차이가 없음
            -  (age, gender, mbti) : 172876 / age : 189496
- Test
    ```sql
    select * from user where gender = 'male' and age between 20 and 29 and mbti = 'ISFP' and region = 902;
    -- Full scan 검색 row 수: 996355
    ```
    - (gender, age, mbti) 85998
    - (age, gender, mbti) 172876
    - (mbti,age, gender) 10640
    - (mbti,gender, age) 3100
#### Where 'or' 비교
- 결과
    - or에 포함되는 모든 조건 column에 index가 걸려있다면 index_merge 실행
        - 만약 column이 두개라면 각각에 해당하는 column을 index scan하고 그 결과를 Union
        - 이러한 과정때문에 full table scan이 더 빠른 경우가 많음
    - 하나라도 걸려있지 않다면 index는 적용되지 않고 full scan
- Test
    ```sql
    select * from user where age = 27 or mbti = 'ISFP';
    ```
    - age index full scan
    - age / mbti index - 139622
#### 인덱스 크기
![image](https://user-images.githubusercontent.com/53611554/216095368-008b9c16-1a8c-48f2-b4ea-d4d65797d7f3.png)  


#### 인덱스 전후 insert / delete 시간 차이
- insert
![image](https://user-images.githubusercontent.com/53611554/216095434-bb0f8270-76eb-4fed-8537-47045e045483.png)
![image](https://user-images.githubusercontent.com/53611554/216095452-9e8e6ef9-48c9-4ba0-8c56-19647ecad78c.png)

- delete
![image](https://user-images.githubusercontent.com/53611554/216095497-eebd1dae-4411-4d7b-a2b6-506e3e2b5b72.png)
![image](https://user-images.githubusercontent.com/53611554/216095506-62fdf5b2-b9b6-495a-8ef5-60a0c0dcc38d.png)

