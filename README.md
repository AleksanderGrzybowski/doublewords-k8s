# Doublewords on Kubernetes

This is a funny project I wanted to do in order to get more experience with running CPU-intensive loads on Kubernetes. For the last few months I have a habit of noticing words that are made up from two separate words (example: lifeline, life-line). To get rid of this madness, I decided to design a solution that could find me all those words in Polish language, thus preventing my craziness.

The project is made in pure Java + Gradle for some automation, no frameworks. Depending on startup parameters, it works either as a:
* *worker* - piece of algorithmic code that downloads a list of Polish words, partitions it and finds all double words inside
* *server* - simple Spark-Java web service for storing found double words.

The algorithm used by *worker* is extremely lame and not optimized at all. If I wanted to get the answer fast, I'd probably use some prefix tree or something like that. But since I want to test CPU-intensive tasks on Kubernetes, that doesn't bother me too much.

Script `k8s/run.sh` can be used to schedule finding jobs on currently set up Kubernetes cluster. Feel free to tweak resource limits and number of jobs, but take note that finding algorithm is single-threaded anyway, so giving the pod more than 1 vCPU is probably a poor idea.

To run the project, no building is necessary, since everything is pushed to Dockerhub already, so:
* run the server at some publicly accessible server: ` d run --rm -e MODE=SERVER -p 8888:8888 -ti kelog/doublewords:latest`
* schedule Kubernetes jobs and watch: `cd k8s && ./run.sh`


