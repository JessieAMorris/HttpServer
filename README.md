#HttpServer
This is an example HttpServer. It was created by Jessie A. Morris <jessie@jessieamorris.com>.

## Startup
To get started, import the project and run it. It will start a web server on your localhost port 8888. You can view this file by going to [localhost:8888/README.md](http://localhost:8888/README.md).

## Custom Handlers
Create your own implementation of IHttpHandler and pass it to the HttpServer constructor. Further documentation can be found in [/docs](docs/).

## Benchmarking
I ran some benchmarks using AB (Apache Benchmark). The results follow:

```
Jessies-MBP:~ jessie$ ab -n 16000 -c 10 -s 10 http://localhost:8888/README.md
This is ApacheBench, Version 2.3 <$Revision: 1663405 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1600 requests
Completed 3200 requests
Completed 4800 requests
Completed 6400 requests
Completed 8000 requests
Completed 9600 requests
Completed 11200 requests
Completed 12800 requests
Completed 14400 requests
Completed 16000 requests
Finished 16000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8888

Document Path:          /README.md
Document Length:        16 bytes

Concurrency Level:      10
Time taken for tests:   9.722 seconds
Complete requests:      16000
Failed requests:        0
Non-2xx responses:      16000
Total transferred:      1632000 bytes
HTML transferred:       256000 bytes
Requests per second:    1645.69 [#/sec] (mean)
Time per request:       6.076 [ms] (mean)
Time per request:       0.608 [ms] (mean, across all concurrent requests)
Transfer rate:          163.93 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    4  47.5      2    2004
Processing:     0    2   1.2      2      15
Waiting:        0    2   1.1      2      15
Total:          1    6  47.6      4    2007

Percentage of the requests served within a certain time (ms)
  50%      4
  66%      5
  75%      6
  80%      6
  90%      7
  95%      8
  98%      9
  99%     11
 100%   2007 (longest request)
Jessies-MBP:~ jessie$ 
```

While these results are biased since they're running on localhost, a 99 percentile result of 11 milliseconds is pretty good. The longest time spent was in the "Connect" phase of which the kernel/JVM handles, so there's not too much I can do there to improve it.

## Limitations
There are some limitations. Right now there are no timeouts or other similar protections. The server also only supports GET and POST methods.
