# Hazelcast sample

[공식문서](https://docs.hazelcast.com/imdg/latest/) 참고하여 hazelcast 샘플앱 작성(v4.2)

<br>

### [Topology](https://docs.hazelcast.com/imdg/latest/overview/topology) 
embed 또는 독립된 서버 두 가지 형태로 사용가능 한데, embed 인 경우 java application 만 가능 

<br>

### [Data partition](https://docs.hazelcast.com/imdg/latest/overview/data-partitioning)
* "partition owner", "primary partition", "backups" 세 가지 구분하여 인식.
* "Partition Table" 의 존재

<br>

### [Install & Upgrade](https://docs.hazelcast.com/imdg/latest/installation/installing-using-maven)
all 의 의미는 member 로서의 라이브러리와 Java application 이 client로서 member에 접근하기 위한 라이브러리가 모두 있다는 뜻

<br>

### [Starting the Members and Clients](https://docs.hazelcast.com/imdg/latest/starting-members-clients)
어떤 코드가 member를 만들고 어떤 코드가 Client를 만드는지 명확히 이해가 필요

<br>

### [Configuration Options](https://docs.hazelcast.com/imdg/latest/configuration/understanding-configuration)
static, dynamic 두 가지가 있다. static은 runtime 전에 모든 것을 설정하고 더 이상 변경이 없는 것이고 dynamic 은 런타임중에 data structure 의 변경이 가능하다는 차이가 있음.
sample app 에서는 Programmatic Configuration 방식을 이용(=API를 이용)해서 구성할 예정. API 를 이용해서 설정 정보가 담겨있는 xml 또는 yml 파일 경로를 잡아줄 수도 있는데,
그렇게 하지 않고 제어하고자 하는 설정 항목별로 모두 일일이 API 로 컨트롤 하는 방식으로 구현할 예정이다.

<br>

### 
