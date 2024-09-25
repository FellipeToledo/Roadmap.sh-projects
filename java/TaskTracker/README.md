
<h1 align="center">
  
  [Task Tracker](https://github.com/FellipeToledo/Roadmap.sh-projects/edit/main/java/TaskTracker)
 
</h1>

<h4 align="center">Simple Command Line Interface (CLI) <br> To track what you need to do.</h4>

<p align="center">
  <a href="#Key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> •
  <a href="#license">License</a>
</p>

![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/refs/heads/main/TaskTracker.gif)

## Key Features

* Add, Update, and Delete tasks
* Mark a task as in progress or done
* List all tasks
* List all tasks that are done
* List all tasks that are not done
* List all tasks that are in progress

## How To Use

To clone and run this application, you'll need [Git](https://git-scm.com) and [Maven](https://maven.apache.org/download.cgi) installed on your computer. From your command line:

```bash
# Clone this repository
$ git clone https://github.com/FellipeToledo/Roadmap.sh-projects.git

# Go into the repository
$ cd java/TaskTracker

# Install dependencies
$ mvn clean install

# Add a new task
$ mvn exec:java -D"exec.args"="add '<taskName>'"

# List all tasks
$ mvn exec:java -D"exec.args"="list"

# Update a task
$ mvn exec:java -D"exec.args"="update <taskId> done"

# Delete a task:
$ mvn exec:java -D"exec.args"="delete <taskId>"

```

## Credits

This project uses the following open source packages:

- [JUnit](https://junit.org/junit5/)
- [JSON-Java](https://stleary.github.io/JSON-java/index.html/)
- [Java.io](https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html)
- [Java.nio](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)
- [Java.util](https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html)
- [Java.time](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html)
- [Slf4j](https://www.slf4j.org/apidocs/org/slf4j/Logger.html)


## Related

[Roadmap.sh - TT](https://roadmap.sh/projects/task-tracker) &nbsp;&middot;&nbsp;

## You may also like...

- [TODO](TODO) - A ...TODO

## License

MIT

---

<div align="center">



 ![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/refs/heads/main/github-desktop.svg) [@FellipeToledo](https://github.com/FellipeToledo) &nbsp;&middot;&nbsp;   ![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/refs/heads/main/linkedin-outlined.svg) [@FellipeToledo](https://www.linkedin.com/in/fellipetoledo/) &nbsp;&middot;&nbsp;  

</div>
