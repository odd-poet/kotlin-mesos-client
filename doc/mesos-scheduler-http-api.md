# Schduler HTTP API

이 문서는 Mesos의 [Scheduler HTTP API] 문서를 요약한 문서다. 

Mesos scheduler는 2가지 방식으로 만들수 있다. 

1. `SchedulerDriver` C++ interface를 사용하는 방법
    - `SchedulerDriver`는 Mesos master와의 통신 세부를 다룬다. 
    - Scheduler 개발자는 `SchedulerDriver`에 callback을 등록함으로써 자신의 스케쥴링 로직을 구현할 수 있다. 
    - 새로운 resource offer를 받거나, 작업(task)의 상태를 업데이트 등의 이벤트가 이 callback으로 전달된다. 
    - `SchedulerDriver` interface가 C++로 작성되어 있으므로, 일반적으로 scheduler 개발자는 C++를 사용하거나,    
        C++ 바인딩된 언어를 사용해야 한다(e.g. jvm 언어라면 JNI를 통해서 사용)
2. 새로운 HTTP API를 사용하는 방법
    - HTTP API를 사용하면 native client library나 C++를 사용하지 않고 Mesos scheduler를 작성할 수 있다. 
    - custom scheduler는 Mesos master와 HTTP request를 통해 통신한다. 
    - 이론적으로는 HTTP API를 *직접* 사용할 수도 있지만, 대부분의 scheduler 개발자는 자신의 언어에 맞는 library를 사용하는 것이 낫다
    - [HTTP API client libraries](http://mesos.apache.org/documentation/latest/api-client-libraries/)


## Overview

스케쥴러는 master의 `/api/v1/scheduler` endpoint를 통해 Mesos와 통신한다. 이 endpoint는 JSON(`application/json`)이나 
Protobuf(`application/x-protobuf`)로 인코딩된 데이터를 `HTTP POST`로 받는다. 스케쥴러가 `/scheduler` endpoint에 보내는 최초 요청을 
`SUBSCRIBE`라고 부르며 응답 스트림이 결과로 반환된다('200 OK' status code에 `Transfer-Encoding: chunked` 형태).

**스케쥴러는 subscription connection을 가능한 오랫동안 유지해야 하며(네트웍, SW, HW의 오류가 없다면), 응답을 지속적으로 처리해야 한다.** 
즉, 연결 종료된 후에야 응답을 파싱할 수 있는 HTTP client libraries들은 사용할 수 없다. (chunk 단위로 parsing하고 처리할 수 있어야 한다)  


`/scheduler` endpoint에 대한 `SUBSCRIBE`가 아닌 요청(아래 `Calls` 섹션 참조)들은 subscription이 사용하는 연결과는 다른 연결을 통해서 
보내고 처리되어야 한다. 스케쥴러는 하나 이상의 HTTP 연결을 통해 요청을 보낼 수 있다. 

master는 비동기 처리를 필요로 하는 `HTTP POST` 요청에 대해서 `200 Accepted`로 응답한다. `202 Accepted` 응답은 요청이 접수 되었지만 
처리가 완료되지 않았음을 의미한다. 이러한 요청에 대한 비동기 응답은 long-live subscription connection(`SUBSCRIBE` 요청에 대한 응답 stream)을 통해 응답될 것이다.

master는 즉시 응답할 수 있는 `HTTP POST`요청에 대해서는 `200 OK` 응답을 한다. 응답 body는 *JSON*이나 *Protobuf*로 인코딩 된다. 
인코딩은 `Accept` 헤더에 따라 달라지며 기본 값은 *JSON*이다.     
 

## Calls

아래의 call들은 master에 의해 처리된다. 이 정보에 대한 형식적인 내용을 [scheduler.proto]를 참고 하라(공통 데이터들은 [mesos.proto]에 정의되어 있다). 
JSON 인코딩으로 call을 보낼때, 스케쥴러는 *raw bytes*는 *Base64*로 인코딩한 UTF-8 문자열로 보내야 한다. 
또한 `SUBSCRIBE`가 아닌 call들은 `Mesos-Stream-Id` 헤더를 포함해야 한다(`SUBSCRIBE` 섹션에서 설명한다). `SUBSCRIBE` call은 `Mesos-Stream-Id` 헤더를 
포함해서는 안된다. 

### RecordIO response format

`SUBSCRIBE` call로 리턴되는 응답은 *RecordIO* format으로 인코딩된다. 
recordIO 포맷에서 하나의 record는 bytes length와 개행문자, 그리고 data 형태(JSON or Protobuf)로 표현된다. 자세한 건 [RecordIO format] 문서를 참고할 것. 

### SUBSCRIBE

스케쥴러와 master가 통신을 시작하는 첫 단계가 `SUBSCRIBE` call이다. 이 call은 `/scheduler` 이벤트 스트림에 대한 구독(subscription)으로 생각할 수 있다. 

master를 구독(subscribe)하기 위해서 스케쥴러는 FrameworkInfo를 포함하는 `SUBSCIRBE` 메시지를 *HTTP POST*로 보낸다. 
`SUBSCIRBE`의 FrameworkInfo에 frameworkId가 없으면 master는 새로운 scheduler로 간주하고 frameworkId를 할당한다. HTTP 응답은 *RecordIO*의 스트림이며, 
이 이벤트 스트림의 첫 번째 이벤트는 `SUBSCRIBED` 이벤트다(**Event** 섹션 참조). 또한 응답에는 `Mesos-Stream-Id`헤더가 포함되며, 이 값은 master가 
scheduler instance를 식별하는데 사용된다. 이 stream Id 헤더는 다른 non-`SUBSCIRBE` call을 보낼 때 포함되어야 한다. 
`Mesos-Stream-Id`는 최대 128bytes 길이를 갖는다. 

```
SUBSCRIBE Request (JSON):

POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Accept: application/json
Connection: close

{
   "type"       : "SUBSCRIBE",
   "subscribe"  : {
      "framework_info"  : {
        "user" :  "foo",
        "name" :  "Example HTTP Framework",
        "roles": ["test"],
        "capabilities" : [{"type": "MULTI_ROLE"}]
      }
  }
}

SUBSCRIBE Response Event (JSON):
HTTP/1.1 200 OK

Content-Type: application/json
Transfer-Encoding: chunked
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

<event length>
{
 "type"         : "SUBSCRIBED",
 "subscribed"   : {
     "framework_id"               : {"value":"12220-3440-12532-2345"},
     "heartbeat_interval_seconds" : 15
  }
}
<more events>
```

반면, frameworkId가 설정되어 있을 경우, master는 이미 구독중인 scheduler가 연결이 끊긴 후 다시 연결을 시도하는 것으로 간주하며, 
`SUBSCRIBED` 이벤트를 응답한다. 이에 대한 자세한 사항은 **Disconnections** 섹션을 참고하라. 

NOTE: API의 예전 버전에서는 (re-)register callback에 MasterInfo가 포함되었다(현재 driver가 연결된 master 정보를 제공하기 위해서). 
하지만 새로운 API에서는 scheduler가 명시적으로 leading master를 구독하므로, 이 것은 더이상 필요하지 않다. 자세한 내용은 **Master Detection** 섹션을
참고하라. 

어떤 이유(e.g. invalid request)로는 구독(subscription)에 실패하면 HTTP 4xx과 에러메시지가 응답되며 연결은 끊긴다. 

scheduler는 `SUBSCRIBE` 요청을 보내고 `SUBSCRIBED` 응답 스트림을 받은 이후에는 그 `/scheduler` endpoint에 다른 HTTP 요청을 보낼 수 있다. 
subscription이 없는 상태에서 call을 보내면 *403 Forbidden*이 리턴될 것이다. HTTP 요청이 형식에 맞지 않으면(e.g. malformed HTTP headers) *400 Bad Request*를 리턴받는다. 

`Mesos-Stream-Id` 헤더는 `SUBSCRIBE` call에 포함되서는 안된다. master는 각 구독(subscription)에 대해 항상 새로운 uniq stream id를 제공할 것이다. 

### TEARDOWN

종료될 때 스케쥴러는 `TEARDOWN` call을 보낸다. mesos는 이 요청을 받으면 모든 executor을 shutdown할 것이다. 그리고 이 framework을 삭제하고 
이 스케쥴러와의 모든 연결을 닫을 것이다. 

```
TEARDOWN Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "TEARDOWN"
}

TEARDOWN Response:
HTTP/1.1 202 Accepted
```          

### ACCEPT

master에게 받은 offer를 받아들일 때 스케쥴러는 `ACCEPT` call을 보낸다. 

`ACCEPT` 요청은 scheduler가 offer에 대해 실해하길 원하는 operation을 포함한다. 

**주의**: scheduler가 offer에 대해 accepts나 declines로 응답 할 때까지 offer의 resource는 offer의 role과 framework에 할당된 것으로 간주한다. 
또한, `ACCEPT` call에서 사용되지 않은 offer의 resource들은 거절(decline) 된 것으로 간주되고 다른 framework에 다시 offer될 수 있는데, 
filter에 지정된 시간동안은 scheduler에 reoffer되지 않을 것이다. 하나 이상의 `ACCEPT` call에 같은 `OfferID`가 사용될 수 없다. 
Mesos에 새로운 기능이 추가될 때, 이 규칙은 변경될 수 있다. 

scheduler API는 resource가 거절(declined)된 것으로 간주하는 시간을 명시하기 위해 `Filters.refuse_seconds`를 사용한다. 
`filters`가 설정되지 않으면 [mesos.proto]에 정의되 기본 값(현재 `5.0`)이 사용된다. 
`Filters.refuse_seconds`의 상한은 31536000 sec(365일)이다. 

master는 `LAUNCH`, `LAUNCH_GROUP` operation에 대한 응답으로 task status update를 보낸다. 
다른 타입의 Operation에 대해서는, operation ID가 명시되어 있다면 master는 operation status update를 응답으로 보낸다. 

**주의**: operation이 [resource provider]가 제공한 resources에 영향을 줄 때만, operation ID를 명시할 수 있다. 

```
ACCEPT Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
   "framework_id":{
      "value":"12220-3440-12532-2345"
   },
   "type":"ACCEPT",
   "accept":{
      "offer_ids":[
         {
            "value":"12220-3440-12532-O12"
         }
      ],
      "operations":[
         {
            "type":"LAUNCH",
            "launch":{
               "task_infos":[
                  {
                     "name":"My Task",
                     "task_id":{"value":"12220-3440-12532-my-task"},
                     "agent_id":{"value":"12220-3440-12532-S1233"},
                     "executor":{
                        "command":{
                           "shell":true,
                           "value":"sleep 1000"
                        },
                        "executor_id":{"value":"12214-23523-my-executor"}
                     },
                     "resources":[
                        {
                           "allocation_info":{
                              "role":"engineering"
                           },
                           "name":"cpus",
                           "role":"*",
                           "type":"SCALAR",
                           "scalar":{
                              "value":1.0
                           }
                        },
                        {
                           "allocation_info":{
                              "role":"engineering"
                           },
                           "name":"mem",
                           "role":"*",
                           "type":"SCALAR",
                           "scalar":{
                              "value":128.0
                           }
                        }
                     ]
                  }
               ]
            }
         }
      ],
      "filters":{
         "refuse_seconds":5.0
      }
   }
}

ACCEPT Response:
HTTP/1.1 202 Accepted
```   

### DECLINE

scheduler가 명시적으로 offer를 거절할 때 `DECLINE` call을 보낸다. 이것은 `ACCEPT`와 동일하지만 operation이 없다. 

```
DECLINE Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "DECLINE",
  "decline"         : {
    "offer_ids" : [
                   {"value" : "12220-3440-12532-O12"},
                   {"value" : "12220-3440-12532-O13"}
                  ],
    "filters"   : {"refuse_seconds" : 5.0}
  }
}

DECLINE Response:
HTTP/1.1 202 Accepted

```

### REVIVE

다음 두 작업을 위해서 scheduler가 보내는 call이다. 

1. offer를 다시 받기 위해서 non-`SUPPRESS`ed 상태로 scheduler의 role을 바꾼다. 
2. `ACCEPT`나 `DECLINE`을 통해서 전에 설정된 모든 filters를 clear 한다. 

여긴 `SUPPRESS` 섹션과 [Using Resource Roles]를 참조할 것. (나는 왠지 안 쓸것 같은 기능이다)

```
REVIVE Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id" : {"value" : "12220-3440-12532-2345"},
  "type"         : "REVIVE",
  "revive"       : {"role": <one-of-the-subscribed-roles>}
}

REVIVE Response:
HTTP/1.1 202 Accepted
```

### KILL

특정 task를 kill하기 위해 scheduler는 `KILL` call을 보낸다. 
scheduler가 custom executor를 가진다면, kill은 executor에게 전달된다. 즉, 이 경우 
task를 kill하고 `TASK_KILLED`(or `TASK_FAILED`) update를 보내는 것도 executor가 해야 한다. 
mesos master나 agent가 kill 요청을 받았지만 task가 아직 executor에게 전달되기 전이라면, `TASK_KILLED`처리되고 
task는 executor에 전달되지 않는다. 

task가 어떤 task group에 속할 때, task를 kill하면 그 group의 모든 task들이 kill된다. 
mesos는 task 종료 상태를 받으면 task에 대한 resource를 해제한다. master가 알지 못하는 task인 경우에는 `TASK_LOST`로 처리된다.


```
KILL Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "KILL",
  "kill"            : {
    "task_id"   :  {"value" : "12220-3440-12532-my-task"},
    "agent_id"  :  {"value" : "12220-3440-12532-S1233"}
  }
}

KILL Response:
HTTP/1.1 202 Accepted
```    


### SHUTDOWN 

scheduler가 특정 custom executor를 shutdown시키려 할 때 `SHUTDOWN` call을 보낸다(NOTE: old api에 없는 새로운 call이다). 

executor는 shutdown 이벤트를 받으면 모든 task를 kill하고(그리고는 `TASK_KILLED` update를 보냄) 종료한다. 
executor가 timeout(agent flag `--executor_shutdown_grace_period`로 설정) 내에 종료되지 않으면 agent는 강제로 container를 destroy하고 active tasks를 `TASK_LOST`로 상태를 전이시킬 것이다.  


```
SHUTDOWN Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "SHUTDOWN",
  "shutdown"        : {
    "executor_id"   :  {"value" : "123450-2340-1232-my-executor"},
    "agent_id"      :  {"value" : "12220-3440-12532-S1233"}
  }
}

SHUTDOWN Response:
HTTP/1.1 202 Accepted
```

### ACKNOWLEDGE

scheduler는 status update에 대한 ACK로 `ACKNOWLEDGE` call를 보낸다. 
new API에서는 scheduler는 명시적으로 state update(`status.uuid`가 있음)를 받은 후 ack를 보낼 책임을 갖는다.
이 statue update는 scheduler가 ACK할 때까지 계속 보내질 것이다. 

scheduler는 `status.uuid`가 설정되지 않은 status update에 대해서는 ACK하면 안된다. `uuid`는 raw bytes를 Base64 encode한 값이다. 

```
ACKNOWLEDGE Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "ACKNOWLEDGE",
  "acknowledge"     : {
    "agent_id"  :  {"value" : "12220-3440-12532-S1233"},
    "task_id"   :  {"value" : "12220-3440-12532-my-task"},
    "uuid"      :  "jhadf73jhakdlfha723adf"
  }
}

ACKNOWLEDGE Response:
HTTP/1.1 202 Accepted
```

### ACKNOWLEDGE_OPERATION_STATUS

operation status update에 대한 ACK로써 scheduler는 `ACKNOWLEDGE_OPERATION_STATUS`를 보낸다. 
(`ACKNOWLEDGE`와 동일하니 내용은 스킵)

```
ACKNOWLEDGE_OPERATION_STATUS Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id": { "value": "12220-3440-12532-2345" },
  "type": "ACKNOWLEDGE_OPERATION_STATUS",
  "acknowledge_operation_status": {
    "agent_id": { "value": "12220-3440-12532-S1233" },
    "resource_provider_id": { "value": "12220-3440-12532-rp" },
    "uuid": "jhadf73jhakdlfha723adf",
    "operation_id": "73jhakdlfha723adf"
  }
}

ACKNOWLEDGE_OPERATION_STATUS Response:
HTTP/1.1 202 Accepted
```

### RECONCILE

scheduler는 끝나지 않은 task의 상태를 질의하기 위해서 `RECONCILE` call을 보낼 수 있다. 
`RECONCILE`을 받으면 master는 리스트에 있는 각 task에 대해 `UPDATE` event를 보낸다. 
Mesos가 알 지 못하는 task에 대해서는 `TASK_LOST` update를 리턴한다. 만약 task 리스트를 empty인 상태로 질의하면, 
mesos는 현재 알고 있는 모든 task에 대해서 `UPDATE` event를 보낼 것이다.

```
RECONCILE Request (JSON):
POST /api/v1/scheduler   HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "RECONCILE",
  "reconcile"       : {
    "tasks"     : [
                   { "task_id"  : {"value" : "312325"},
                     "agent_id" : {"value" : "123535"}
                   }
                  ]
  }
}

RECONCILE Response:
HTTP/1.1 202 Accepted
```   

### RECONCILE_OPERATIONS

끝나지 않는 operation의 상태 질의를 위해서 scheduler는 `RECONCILE_OPERATIONS` call을 보낼 수 있다. 
master는 각 operation의 상태를 포함하는 `RECONCILE_OPERATIONS`을 응답한다. operation 리스트를 empty인 상태로 질의하면
모든 operation에 대해 응답한다. 

```
RECONCILE_OPERATIONS Request (JSON):
POST /api/v1/scheduler   HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Accept: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id": { "value": "12220-3440-12532-2345" },
  "type": "RECONCILE_OPERATIONS",
  "reconcile_operations": {
    "operations": [
      {
        "operation_id": { "value": "312325" },
        "agent_id": { "value": "123535" }
      }
    ]
  }
}

RECONCILE_OPERATIONS Response:
HTTP/1.1 200 Accepted

Content-Type: application/json

{
  "type": "RECONCILE_OPERATIONS",
  "reconcile_operations": {
    "operation_statuses": [
      {
        "operation_id": { "value": "312325" },
        "state": "OPERATION_PENDING",
        "uuid": "adfadfadbhgvjayd23r2uahj"
      }
    ]
  }
}
```

### MESSAGE

executor에 임의의 binary data를 보내기 위해 scheduler는 `MESSAGE` call을 보낼 수 있다. 
mesos는 이 data를 변환(interpret)하지도 않고, executor에 메시지가 전달 되었음을 보장하지도 않는다. 
`data`는 base64로 인코딩된 raw bytes다.  

```
MESSAGE Request (JSON):
POST /api/v1/scheduler   HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "MESSAGE",
  "message"         : {
    "agent_id"       : {"value" : "12220-3440-12532-S1233"},
    "executor_id"    : {"value" : "my-framework-executor"},
    "data"           : "adaf838jahd748jnaldf"
  }
}

MESSAGE Response:
HTTP/1.1 202 Accepted
```

### REQUEST

master/allocator로 부터 resource를 요청하기 위해 `REQUEST` call을 보낼 수 있다. 
내장된 hierarchical allocator는 이 요청을 무시하지만, 다른 allocators(modules)는 이 것을 각자 방식으로 변환할 수 있다.  

```
Request (JSON):
POST /api/v1/scheduler   HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id"    : {"value" : "12220-3440-12532-2345"},
  "type"            : "REQUEST",
  "requests"        : [
      {
         "agent_id"       : {"value" : "12220-3440-12532-S1233"},
         "resources"      : {}
      }
  ]
}

REQUEST Response:
HTTP/1.1 202 Accepted
```


### SUPPRESS

어떤 role들에 대한 offer를 더이상 필요로 하지 않는 경우 scheduler는 `SUPPRESS` call을 보낼 수 있다. 
mesos master는 이 요청을 받으면, 주어진 role에 대한 offer를 framework에게 보내지 않게 된다. role을 명시하지 않으면, 
이 framework이 구독하는 모든 role이 숨겨진다(suppress). 

master는 suppress되지 않는 다른 구독 중인 role에 대해서는 여전히 offer를 보낸다. 또한, task에 대한 status update, executor, agent 등은 
이 call의 영향을 받지 않는다. 

만약 scheduler가 suppressed role에 대한 offer를 다시 받길 원하면, `REVIVE` call을 보내도록 하자. 

```
SUPPRESS Request (JSON):
POST /api/v1/scheduler  HTTP/1.1

Host: masterhost:5050
Content-Type: application/json
Mesos-Stream-Id: 130ae4e3-6b13-4ef4-baa9-9f2e85c3e9af

{
  "framework_id" : {"value" : "12220-3440-12532-2345"},
  "type"         : "SUPPRESS",
  "suppress"     : {"roles": <an-array-of-strings>}
}

SUPPRESS Response:
HTTP/1.1 202 Accepted
```


## Events

scheduler는 `/scheduler` endpoint에 대한 **persistent** connection을 유지해야 한다(`SUBSCRIBED` event를 받은 후에도).
이 HTTP 응답은 `Connection: keep-alive`, `Transfer-Encdoing: chunked` 헤더를 가지며 `Content-Length`헤더는 갖지 않는다. 
이 후 이 framework과 관련되는 event들이 mesos에서 생성되어 이 connection을 통해 streaming된다. master는 각 event를 [RecordIO format]으로 
인코드한다(`<LENGTH>\n<DATA>`의 형태). length는 64bit unsigned integer이고 text value로 인코드되며 절대 "0"이 될 수 없다. 
content encoding은 JSON과 Protobuf를 선택할 수 있는데 HTTP POST 요청의 `Accept`헤더에 따라 응답 포맷이 결정된다. 

이벤트 데이터는 [scheduler.proto]를 참고하라. 단, JSON encoded event에서 raw bytes는 Base64로 encoding된다.   


### SUBSCRIBED

scheduler가 `SUBSCRIBE` 요청을 보낼 때, master에서 최초로 보내지는 event다. 
위의 `SUBSCRIBE` 섹션을 보자. 

### OFFERS

framework에 제공할 수 있는 새로운 resource가 있을 때 master는 `OFFERS` 이벤트를 보낸다. 
각 offer는 한 agent의 resources set에 대응되며, framework이 구독하는 role 중 하나에 할당된다. 
scheduler가 offer에 대해서 `Accept`, `Decline` 하기 전까지 그 resource들은 scheduler에 할당(allocate)한 것으로 간주된다(offer가 폐기(rescind)되지 않는 한). 

```
OFFERS Event (JSON)

<event-length>
{
  "type"    : "OFFERS",
  "offers"  : [
    {
      "allocation_info": { "role": "engineering" },
      "id"             : {"value": "12214-23523-O235235"},
      "framework_id"   : {"value": "12124-235325-32425"},
      "agent_id"       : {"value": "12325-23523-S23523"},
      "hostname"       : "agent.host",
      "resources"      : [
                          {
                           "allocation_info": { "role": "engineering" },
                           "name"   : "cpus",
                           "type"   : "SCALAR",
                           "scalar" : {"value" : 2},
                           "role"   : "*"
                          }
                         ],
      "attributes"     : [
                          {
                           "name"   : "os",
                           "type"   : "TEXT",
                           "text"   : {"value" : "ubuntu16.04"}
                          }
                         ],
      "executor_ids"   : [
                          {"value" : "12214-23523-my-executor"}
                         ]
    }
  ]
}
``` 

### RESCIND

master는 특정 offer가 더이상 유효하지 않게 되어(e.g. offer에 대응되는 agent가 제거되었다던지) offer를 폐기해야 할 상황이 되면 
`RESCIND` event를 보낸다. 이 이후 scheduler의 `ACCEPT`/`DECLINE` call에서 이 offer는 유효하지 않게 될 것이다. 

```
RESCIND Event (JSON)

<event-length>
{
  "type"    : "RESCIND",
  "rescind" : {
    "offer_id"  : { "value" : "12214-23523-O235235"}
  }
}
```  
 
### UPDATE

executor, agent, master가 생성한 status update가 있으면 master는 `UPDATE` event를 보낸다. 
status update는 executor들이 그들이 관리하는 task의 상태와 믿을 수 있게 통신하기 위해서 사용되어야 한다. 
`TAKS_FINISHED`, `TASK_KILLED`, `TASK_FAILED`와 같은 중요한 terminal update들은 task가 종료되자 마자 executor에 의해 보내지기 때문에, 
master는 task에 할당된 resource를 해제할 수 있다. scheduler 역시 명시적으로 status update에 대한 ACK를 보내야 한다(안그러면 retry된다). 
이에 관해서는 Call의 `ACKNOWLEDGE`섹션을 보라. 

```
UPDATE Event (JSON)

<event-length>
{
  "type"    : "UPDATE",
  "update"  : {
    "status"    : {
        "task_id"   : { "value" : "12344-my-task"},
        "state"     : "TASK_RUNNING",
        "source"    : "SOURCE_EXECUTOR",
        "uuid"      : "adfadfadbhgvjayd23r2uahj",
        "bytes"     : "uhdjfhuagdj63d7hadkf"
      }
  }
}
``` 

### MESSAGE

executor가 생성한 message를 scheduler에 전달하기 위해서 master가 보내는 event다. 
이 메시지는 mesos에서 어떤 변환도 없이 scheduler에 그대로 전달된다(전달도 보장안함). 
이 메시지가 어떤 이유로든 drop되었을 때 retry 할 것인지는 executor에 달려있다.  


```
MESSAGE Event (JSON)

<event-length>
{
  "type"    : "MESSAGE",
  "message" : {
    "agent_id"      : { "value" : "12214-23523-S235235"},
    "executor_id"   : { "value" : "12214-23523-my-executor"},
    "data"          : "adfadf3t2wa3353dfadf"
  }
}
```

### FAILURE

cluster에서 agent가 제거될 때(e.g. health check 실패) 혹은 executor가 종료되었을 때 master는 `FAILURE` 메시지를 보낸다.
이 이벤트는   

[Scheduler HTTP API](http://mesos.apache.org/documentation/latest/scheduler-http-api/)
[scheduler.proto](https://github.com/apache/mesos/blob/master/include/mesos/v1/scheduler/scheduler.proto)
[mesos.proto](https://github.com/apache/mesos/blob/master/include/mesos/v1/mesos.proto)
[RecordIO format](http://mesos.apache.org/documentation/latest/recordio/)
[resource provider](http://mesos.apache.org/documentation/latest/csi/#resource-providers)
[MESOS-8194](https://issues.apache.org/jira/browse/MESOS-8371)
[Using Resource Roles](https://mesos.apache.org/documentation/latest/roles/)