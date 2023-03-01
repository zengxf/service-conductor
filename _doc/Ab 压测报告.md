## Ab 压测报告
### 参考
- https://www.jianshu.com/p/43d04d8baaf7

### 版本
- `Apache-httpd-2.4.52`

### 环境
- 联想笔记本 E15：`I7-1165G7@2.8GHz C4 T8 16G 512G`
- 编排服务与下流服务是同一电脑上运行

### 命令
- 简单测试： `ab -n 1 -c 1 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002`
  - `-n`表示**请求数**，`-c`表示**并发数**
- 并发`10`，测试`100`次： `ab -n 100 -c 10 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002`

### 配置及压测报告
#### 资源使用情况
- CPU 使用率：`24% ~ 60%`
- 内存使用：`0.1Gb ~ 0.95Gb`
- 线程数最大 `97`

#### c2 m4 - 10 并发 100 请求
```yaml
- sign: localhost:9999 # 独立配置
  core-size: 2
  max-size: 4
  queue-size: 4
```
- **出现请求拒绝**
- 测试报告：
```shell
> ab -n 100 -c 10 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient).....done


Server Software:
Server Hostname:        127.0.0.1
Server Port:            9020

Document Path:          /composer/abTest/zxf-test-002
Document Length:        651 bytes

Concurrency Level:      10
Time taken for tests:   1.489 seconds
Complete requests:      100
Failed requests:        82
   (Connect: 0, Receive: 0, Length: 82, Exceptions: 0)
Total transferred:      693408 bytes
HTML transferred:       682908 bytes
Requests per second:    67.14 [#/sec] (mean)
Time per request:       148.937 [ms] (mean)
Time per request:       14.894 [ms] (mean, across all concurrent requests)
Transfer rate:          454.66 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.6      0       5
Processing:    20  134 148.1     69     601
Waiting:       20  132 148.3     65     601
Total:         21  134 148.1     70     601

Percentage of the requests served within a certain time (ms)
  50%     70
  66%    117
  75%    180
  80%    253
  90%    371
  95%    482
  98%    567
  99%    601
 100%    601 (longest request)
```

#### c20 m40 - 10 并发 100 请求
```yaml
- sign: localhost:9999 # 独立配置
  core-size: 20
  max-size: 40
  queue-size: 400
```
- **请求无拒绝**
- 测试报告：
```shell
> ab -n 100 -c 10 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient).....done


Server Software:
Server Hostname:        127.0.0.1
Server Port:            9020

Document Path:          /composer/abTest/zxf-test-002
Document Length:        651 bytes

Concurrency Level:      10
Time taken for tests:   2.153 seconds
Complete requests:      100
Failed requests:        0
Total transferred:      75600 bytes
HTML transferred:       65100 bytes
Requests per second:    46.44 [#/sec] (mean)
Time per request:       215.335 [ms] (mean)
Time per request:       21.534 [ms] (mean, across all concurrent requests)
Transfer rate:          34.29 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.7      0       4
Processing:    97  201 103.6    165     557
Waiting:       97  198 102.9    158     556
Total:         98  201 103.5    165     558

Percentage of the requests served within a certain time (ms)
  50%    165
  66%    200
  75%    229
  80%    260
  90%    349
  95%    484
  98%    532
  99%    558
 100%    558 (longest request)
```

#### c20 m40 - 10 并发 1000 请求
- `yaml` 同上
- **请求无拒绝**
- 测试报告：
```shell
> ab -n 1000 -c 10 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Completed 900 requests
Completed 1000 requests
Finished 1000 requests


Server Software:
Server Hostname:        127.0.0.1
Server Port:            9020

Document Path:          /composer/abTest/zxf-test-002
Document Length:        651 bytes

Concurrency Level:      10
Time taken for tests:   12.196 seconds
Complete requests:      1000
Failed requests:        9
   (Connect: 0, Receive: 0, Length: 9, Exceptions: 0)
Total transferred:      755721 bytes
HTML transferred:       650721 bytes
Requests per second:    82.00 [#/sec] (mean)
Time per request:       121.958 [ms] (mean)
Time per request:       12.196 [ms] (mean, across all concurrent requests)
Transfer rate:          60.51 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.5      0       5
Processing:    70  120  41.2    111     517
Waiting:       67  118  40.8    109     517
Total:         70  120  41.2    111     518

Percentage of the requests served within a certain time (ms)
  50%    111
  66%    120
  75%    128
  80%    134
  90%    152
  95%    181
  98%    241
  99%    305
 100%    518 (longest request)
```

#### c20 m40 - 20 并发 10000 请求
- `yaml` 同上
- **请求无拒绝**
- 测试报告：
```shell
> ab -n 10000 -c 20 -m POST http://127.0.0.1:9020/composer/abTest/zxf-test-002
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking 127.0.0.1 (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:
Server Hostname:        127.0.0.1
Server Port:            9020

Document Path:          /composer/abTest/zxf-test-002
Document Length:        651 bytes

Concurrency Level:      20
Time taken for tests:   109.876 seconds
Complete requests:      10000
Failed requests:        115
   (Connect: 0, Receive: 0, Length: 115, Exceptions: 0)
Total transferred:      7556435 bytes
HTML transferred:       6506435 bytes
Requests per second:    91.01 [#/sec] (mean)
Time per request:       219.753 [ms] (mean)
Time per request:       10.988 [ms] (mean, across all concurrent requests)
Transfer rate:          67.16 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.5      0      15
Processing:    70  218 157.7    161    2345
Waiting:       69  216 157.7    159    2343
Total:         70  219 157.8    162    2345

Percentage of the requests served within a certain time (ms)
  50%    162
  66%    192
  75%    222
  80%    258
  90%    453
  95%    521
  98%    702
  99%    812
 100%   2345 (longest request)
```