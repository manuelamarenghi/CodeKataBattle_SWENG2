# CodeKataBattle
## Warning
We've provided this project with a an access token to a github account created exclusively for the purpose of this porject, you may be encountering some problems with creatinig github repositories. If you encounter any problems, modify the [GitHubService.java](./code/github-manager/src/main/java/ckb/GitHubManager/service/GitHubService.java) class and provide it with your own access token (you then need to rebuild the project as eplained below), the same problem can be experienced with the SMTP token used to send emails to users, save the mail-service [applicatoin.properties](./code/mail-service/src/main/resources/application.properties)

## About
The objective of this project is to apply in practice what is taught in the Software Engineering 2 course at Politecnico di Milano, with the purpose of becoming familiar with software engineering practices and able to address software engineering issues in a rigorous way. To this extent, the following problem is provided: <br> <br>
CodeKataBattle (CKB) is a new platform that helps students improve their software development skills by training with peers on code kata1 . Educators use the platform to challenge students by creating code kata battles in which teams of students can compete against each other, thus proving (and improving) their skills. <br>
A code kata battle is essentially a programming exercise in a programming language of choice (this implementation only supports C), The exercise includes a brief textual description and a software project with build automation scripts (e.g., a Gradle project in case of Java sources) that contains a set of test cases that the program must pass, but without the program implementation, students are asked to complete the project with their code. At the end of the battle, the platform assigns scores to groups to create a competition rank. <br>
Each battle created by an educator belongs to a specific tournament. Tournaments are created by an educator who can then grant to other colleagues the permission to create battles within the context of a specific tournament. After creation of a battle, students use the platform to form teams for that battle. In particular, each student can join a battle on his/her own or by inviting other students.
Students are asked to fork the GitHub repository of the code kata (automatically created by the application) and set up an automated workflow through GitHub Actions that informs the CKB platform (through proper API calls) as soon as students push a new commit into the main branch of their repository (due to not having access to a static ip address, this part can be simulated by artifically performing an API call using Postman or the curl command on a computer connected to the same LAN of the solution evaluation service). <br>
The score is a natural number between 0 and 100 determined by considering some mandatory factors evaluated in a fully automated way, and optional factors evaluated manually by educators:
* Mandatory automated evaluation:
  * functional aspects, measured in terms of number of test cases that pass out of all test cases (the higher the better);
  * timeliness, measured in terms of time passed between the registration deadline and the last commit (the lower the better);
  * quality level of the sources, extracted through static analysis tools that consider multiple aspects such as security, reliability, and maintainability.
* Optional manual evaluation:
  * personal score assigned by the educator, who checks and evaluates the work done by students (which will need to checkout the students repositories by looking at who forked the repository created by CKB)
 The CKB platform automatically updates the battle score of a team as soon as new push actions on GitHub are performed. So, both students and educators involved in the battle can see the current rank evolving during the battle. When the submission deadline expires, there is a consolidation stage in which, if manual evaluation is required, the educator uses the CKB platform to go through the sources produced by each team to assign his/her score. Once the consolidation stage finishes, all students participating in the battle are notified when the final battle rank becomes available.
<br>

Lastly, more documentation can be found the in the [DeliveryFolder](./DeliveryFolder/)

## How to install
### Backend
The only requirement to run the application backend is to have docker compose V2 installed on your system. The whole backend infrastructure can be easily and platform-independently installed and run by exploiting docker containers, in the code directory of the project, a [docker-compose.yml](./code/docker-compoe.yml) can be used to start the application both with official and unofficial images:
* Official images
  * To run the official images it’s sufficient to open a command line interface in the [code](./code/) directory and run the ```docker compose up``` command
  * After having run the command, docker will start pulling the official images of the application from DockerHub
  * After a few minutes, when all images have been downloaded, docker will run all images
  * As the application is starting, you can check which services have started by connecting to ```localhost:8761``` which is the address of the discovery server, once all the 6 services services and the api gateway are listed there, the backend is finally running
* Unofficial images
  * You can apply any modifications to the sources and build your own images by following the next few steps, which are supposed to be performed on a system that has a running version of docker and maven, all commands are to be run in the [code](./code/) directory
  * Build and package the application by running the ```mvn clean install -DskipTests``` command
  * If you can run bash scripts, run the [docker-build.sh](./code/docker-build.sh) script present in the code directory by running ```./docker-build.sh {your-test-tag}``` this will build all docker of the project’s images, and name them ```{your-test-tag}/{service-name}```
  * The only thing left is to actually run the new images! To do that you can run the ```ID={your-test-tag} docker compose up``` command in the [code](./code/) directory or modifying the docker compose file to user the images you’ve just built

### Frontend
To use the application, after having run the docker compose file, you need to host an http server in the web app directory to be able to fetch the necessary files, the following steps are to be run in a linux distribution, or a WSL instance
1. Run the ```sudo apt install npm``` command to download the Node Package Manager (if you do not have apt on your machine, substitute it with a package manager of your preference, e.g., yum, pacman, …)
2. After having installed npm, you’ll need to install the http-server command by running the ```sudo npm install --global http-server``` command
3. Now you are all set to host an http-server on your machine, go in the code/web-app directory of the project and run the ```http-server ./ -p 3000``` command
4. You can now finally search for ```localhost:3000``` in your browser to use the frontend of the application



## Group members

- [__Luca Cattani__](https://github.com/SigCatta)
- [__Tommaso Fellegara__](https://github.com/Felle33)
- [__Manuela Marenghi__](https://github.com/manuelamarenghi)
