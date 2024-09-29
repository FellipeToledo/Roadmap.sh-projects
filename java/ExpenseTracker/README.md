
<h1 align="center">
  
  [Expense Tracker](https://github.com/FellipeToledo/Roadmap.sh-projects/tree/main/java/ExpenseTracker)
 
</h1>

<h4 align="center">Simple expense tracker application to manage your finances.</h4>

<p align="center">
  <a href="#Key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> •
  <a href="#license">License</a>
</p>

![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/c6c8e62520e32ee88d89648f2c034cba4f8af9c4/tenor.gif)

## Key Features

* Add, list, update and delete expense.
* Show a summary of all expenses.
* Show a summary of expenses for a specific month (of current year).
* Expense categories and allow users to filter expenses by category.

## How To Use

To clone and run this application, you'll need [Git](https://git-scm.com) and [Maven](https://maven.apache.org/download.cgi) installed on your computer. From your command line:

```bash
# Clone this repository
$ git clone https://github.com/FellipeToledo/Roadmap.sh-projects.git

# Go into the repository
$ cd java/ExpenseTracker

# Install dependencies
$ mvn clean package

# Display help
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -h

# Add an expense with a amount and description 
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -a <amount> <description> <category>

# Update an expense by id 
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -u <id> <amount> <description> <category>

# Delete an expense by id 
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -d <id>

# Show a summary of all expenses
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -s

# Show all expenses
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -l

# Show an expense summary for a specific month
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -m <specific month (1-12)>

# Filter expenses by category
$ java -jar target/ExpenseTracker-1.0-SNAPSHOT-jar-with-dependencies.jar -c <category>


```

## Credits

This project uses the following open source packages:

- [JCommander](https://jcommander.org/)
- [Gson](https://github.com/google/gson/)
- [Date](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html/)

## Related

[Roadmap.sh - ET](https://roadmap.sh/projects/expense-tracker) &nbsp;&middot;&nbsp;

## You may also like...

- [Roadmap.sh - TT](https://roadmap.sh/projects/task-tracker) &nbsp;&middot;&nbsp;  A Simple Command Line Interface (CLI)
  To track what you need to do.
- [Roadmap.sh - GHUA](https://roadmap.sh/projects/github-user-activity) &nbsp;&middot;&nbsp; A simple command line interface (CLI) to fetch the recent activity of a GitHub user and display it in the terminal.

## License

MIT

---

<div align="center">



 ![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/refs/heads/main/github-desktop.svg) [@FellipeToledo](https://github.com/FellipeToledo) &nbsp;&middot;&nbsp;   ![screenshot](https://raw.githubusercontent.com/FellipeToledo/files/refs/heads/main/linkedin-outlined.svg) [@FellipeToledo](https://www.linkedin.com/in/fellipetoledo/) &nbsp;&middot;&nbsp;  

</div>
