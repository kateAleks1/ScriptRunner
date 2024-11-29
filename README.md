# ScriptRunner
This project creates an execution plan for scripts with dependencies. Each script may have multiple dependencies that must be executed first. Using topological sorting, it generates a valid order for script execution, ensuring all dependencies are satisfied without circular references.


## Project Description

This project implements a mechanism for planning and executing scripts while respecting their dependencies. Each script can have dependencies, which are other scripts that need to be executed before it. The dependencies are represented as a list of `scriptId`s, indicating which other scripts must run before the current script.

The project solves the problem of creating an **execution plan** that runs all scripts in a correct order, ensuring that all dependencies are satisfied and scripts are executed in a logical sequence.

### How It Works
1. Each script in the database has a unique `scriptId` and a list of dependencies, which are other `scriptId`s that must be executed before the script itself.
2. The project implements an algorithm that analyzes all the scripts and creates an execution plan that respects these dependencies.
3. There are no circular dependencies — meaning that there's always a valid execution order for all scripts.
