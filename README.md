# Hazelcast sample

[공식문서](https://docs.hazelcast.com/imdg/latest/) 참고하여 hazelcast 샘플앱 작성(v4.2)

<br>

### [Topology](https://docs.hazelcast.com/imdg/latest/overview/topology) 
embed 또는 독립된 서버 두 가지 형태로 사용가능 한데, embed 인 경우 java application 만 가능 

<br>

### [Data partition](https://docs.hazelcast.com/imdg/latest/overview/data-partitioning)
* "node", "partition" 을 구분하기. node는 member와 같고, 하나의 member내에는 partition은 member 내 memory segment라고 보면 된다. 디폴트 최대값이 271개가 디폴트 세팅이라고 한다.
* "partition owner", "primary", "backups" 세 가지 구분하여 인식.
* "Partition Table" 의 존재
* 제일 중요한 것이 primary 만 있을때와, backups 가 등장하고 나서부터 각 노드가 갖는 파티션의 수가 줄어든다는 것이다.

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

### [Setting Up Clusters](https://docs.hazelcast.com/imdg/latest/clusters/setting-up-clusters)
* hazelcast 는 여러가지 Discovery Mechanisms 를 이용하여 cluster를 구성할 수 있는데, 어떤 방식으로 구성하든지간에 구성이 되고 나서는 각 member 들은 TCP/IP 통신으로 협력한다.
* Discovery Mechanisms 은 여러가지(AWS, GCP, Azuere, 쿠버네티스 등 방법이 많다)가 있는데 가장 대표적으로 TCP와 Multicast 가 있다.
  * [TCP](https://docs.hazelcast.com/imdg/latest/clusters/discovering-by-tcp) : TCP 로 구성하게 되면 모든 멤버의 IP 및 포트를 명시적으로 밝혀줘야 한다. 포트가 밝혀져있지 않으면 기본적으로 5701, 5702를 사용
  * [Multicast](https://docs.hazelcast.com/imdg/latest/clusters/discovering-by-multicast) :  [멀티캐스트](https://ko.wikipedia.org/wiki/%EB%A9%80%ED%8B%B0%EC%BA%90%EC%8A%A4%ED%8A%B8) 방식을 통해서 클러스터링한다. 이건 네트워크가 개방되어 있으면 좋지 않은 방식이다. 아무나 클러스터의 멤버로 합류가 가능하다. 그리고 UDP가 막힌 환경에서는 아예 사용이 불가.

<br>

### [Distributed Data Structures](https://docs.hazelcast.com/imdg/latest/data-structures/distributed-data-structures)
온갖 자료구조 다 사용 가능하다. 공식 문서에서 usage 까지 제공하고 있으니 보고 쓰자.

<br>

### [SQL](https://docs.hazelcast.com/imdg/latest/sql/distributed-sql)
SQL 도 제공한다. 신기하다. 정확히는 SQL을 제공한다기 보다는 SQL 처럼 질의하면 원하는 결과를 받을 수 있도록 내부에서 트랜스컴파일하는 뭔가를 처리해놨나보다.
```java
try (SqlResult result = hazelcastInstance.getSql().execute("SELECT name FROM emp WHERE age < ?", 30)) {
    for (SqlRow row : result) {
        String name = row.getObject(0);

        System.out.println(name);
    }
}
```

이런 느낌이다. 현재 BETA고 공지 없이 API가 바뀔 수 있다고 하니 아직 사용하기는 이른 것 같다.

<br>

### [Distributed Query](https://docs.hazelcast.com/imdg/latest/query/distributed-query)
클러스터에서 특정 정보를 찾을때, 모든 entry를 local에 가져와서 이를 iterate 하는데 이 방식이 공식문서에서는 비효율적이라고 하고 있다. 그래서 이를 해결하기 위해서 hazelcast는 분산 쿼리를 제공해서 클러스터에서 전체를 기져와서 로컬에서 탐색할 것이 아니라 아예 클러스터 내에서
원하는 결과를 바로 탐색할 수 있도록하는 인터페이스를 제공하는데 이를 "Distributed Query" 라고 말하고 있다.

작동원리는 이렇다. predicate 이 클러스터의 각 멤버에게 전달되고 이 predicate이 복호화되어 각 멤버들이 자체적으로 이 predicate에 따라 결과를 도출하고 이를 보내주면 도착한 각 결과들을 predicate requester가 merge 해서 single set으로 전달해 준다.
이게 좋은 이유는 클러스터를 구성하는 노드가 많아질 수록 각 노드가 갖고 있는 파티션의 수가 줄어들기 때문에 결과적으로 통으로 가져와서 로컬에서 탐색하는 것 보다 속도가 빠를 수 있다.

<br>

### [Transactions](https://docs.hazelcast.com/imdg/latest/transactions/transactions)
transaction context 를 생성해서 명시적으로 이 transaction context 를 코드로 '시작' 하고 처리할 것들을 처리하고 '커밋'한다. 이 사이에 발생하는 것들의 트랜잭션을 보장해 준다는 것. 두 가지 타입이 있다.
* ONE_PHASE : prepare 단계 없이 바로 처리한다. 그래서 conflict 가 나면 바로 롤백이 되고 conflict 감지가 안된다.
* TWO_PHASE : prepare 단계가 있다. 원하는 처리를 미리 prepare phase 에서 처리하고 문제가 없을때 commit 처리를 한다.

<br>

### [hazelcast clients](https://docs.hazelcast.com/imdg/latest/clients/hazelcast-clients)
java 부분에서 여러가지 client 설정들 볼 수 있으니 막히면 보자. aws도 있고 다양하게 소개해주고 있다. 리스너 같은건 업무 코드에서도 못본 것 같다.

<br>

### [Serialization](https://docs.hazelcast.com/imdg/latest/serialization/serialization)
serialize 시 [여러 인터페이스](https://docs.hazelcast.com/imdg/latest/serialization/comparing-interfaces) 를 명시할 수 있음. 

<br>

### [Management](https://docs.hazelcast.com/imdg/latest/management/management)
management center 말고 내부적으로 제공해주는 API 를 통해서 management 가 가능하다. 

<br>

### [Security](https://docs.hazelcast.com/imdg/latest/security/security-interceptor)
Interceptor 를 구현해서 처리 전 또는 후에 뭔갈 처리할 수 있다.(AOP 유사)
