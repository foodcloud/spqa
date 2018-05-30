# SPQA
## Scala/Play Quality Assurance  
Library of utilities support unit testing, supporting:
* Scala
* Play Framework
* Akka
* RabbitMq
* Databases

These utilities are intended to simplify the writing of unit tests for system using the above tech stack.

## Getting Started
### Prerequisites
SBT Build tools
Scala compiler
### Installation
Add the following to your SBT build file:
```
resolvers ++= Seq(
  "FoodCloud Repo" at "https://mymavenrepo.com/repo/epUeOhkU5rYeKp6iIU07/")
  
libraryDependencies += "org.foodcloud" %% "metro-spqa" % "0.1"
```

## The Utilties
### AMQP
Test utilities for working with AMQP messages queues
#### QPIDBroker
In-memory AMQP broker implemented with Apache Qpid
#### AmqpPublisher
Utility to publish messages to a queue
#### AmqpQueueListener
Utility to read messages from a queue

### HTTP
#### HttpStubServer
In-memory 
### Play Framework
#### MessageBuilder
#### TestLogger
#### TestCache


## Library structure

```
spqa/
+--build.sbt              The main sbt build script
+--src/                   All sources
   +---/main              
        +---/scala        Scala Standard Library
            +---/amqp     AMQP queue utilities
            +---/http     HTTP server utilities
            +---/play     Play framework utilities
+--test/                  The Scala test suite
   +---/resources         Sample configurations used by the tests
   +---/scala             Test sources
        +---/http         HTTP units tests / usage samples
        +---/queues       AMQP units tests / usage samples
```

## Copyright and licensing
#####Copyright 2018 © FoodCloud

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[Dependancy Licenses](https://github.com/foodcloud/spqa/metro-spqa-licenses.html)
