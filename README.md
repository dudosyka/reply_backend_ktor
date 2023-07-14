# Reply ktor microservices API

## Startup

### Dev
#### Run docker compose

You can do it with your command line

> cd ./docker <br>
> docker compose up

Or with Idea configuration **Run Configurations --> Run docker dev**

That command will start two containers mariadb and consul server and **exposed 8500** port (for consul) and **3306** port (for mariadb) <br>
_Note: to edit the default mariadb configuration like login, pw etc. go to ./docker/mariadb/Dockerfile_

<br>

#### Add necessary configuration
You need to add Jwt and MariaDB configurations to _./libs/main/resources/configuration.conf_

<br>

#### Run gateway

You can run it with gradle from prompt:

> gradle gateway-service:run

Or with idea from **Run Configuration** menu

<br>

#### Run other microservices you need

You can run it with gradle from prompt:

> gradle <service-name>:run

Or with idea from <b>Run Configuration</b> menu


## Modules

- Server engine <b>Netty</b>
- Dependency manager <b>Gradle\Kotlin</b>
- Discovery server <b>Consul</b>
- Database <b>MariaDB</b>
- ORM <b>Exposed</b>


## Project structure

- Structure is from based gradle multi project 
- Microservices located at the root under the appropriate names 
- Libs module is configuration module also here dto classes are located 
- Jwt configuration should be past in configuration.conf file at the libs module resources 
- Build logic and dependency implementation located in build-logic module 