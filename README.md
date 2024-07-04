Chisel Project Template
=======================

Use this project as a starting point for your project. This project uses [playground](https://github.com/morphingmachines/playground.git) as a library. `playground` and `this template` directories should be at the same level, as shown below.  
```
  workspace
  |-- playground
  |-- chisel-template
```
Make sure that you have a working [playground](https://github.com/morphingmachines/playground.git) project before proceeding further. And donot rename/modify `playground` directory structure.

## A Working Chisel3 Environment
#### Download the code
Clone this repository into the same directory that contains `playground` and delete `.git` directory.
```bash
$ git clone https://github.com/morphingmachines/chisel-template.git
$ rm -rf .git
```
### Is it working ?
To known you have all dependencies met for a chisel3 development environment, run the included test.
```bash
$ cd chisel-template
$ make check
```
This should create `test_run_dir` directory that includes `DecoupledGcd.sv` and `DecoupledGcd.vcd` files. If you see those two files then you have a working chisel3 development environment.

## Make your own Chisel3 project
TODO

1. Update project name in `build.sc` and `Makefile`.
1. Update `moduleDeps` as per your project requirements in `build.sc`.
1. Update `testOnly` and `runMain` class names in the `Makefile`
## Good To Know
#### Format you scala code using scalafmt and scalafix
```bash
make lint
```
#### Starting a scala console in a mill project
```bash
make console
```

#### Multiple `main` objects that extends `App`
To generate multiple rtl with different configuration using the same generator, define a `main` object for each configuration as shown in `src/main/scala/gcd/Top.scala`. And using `runMain` explicitly select the configuration. For example, this `chisel-template` project generates rtl code for two configurations of GCD using the commands shown below.
```bash
./mill gcd.runMain gcd.gcd8
./mill gcd.runMain gcd.gcd16
```
### Invoke a specific test specification
Use `testOnly` to invoke a specific test specification from multiple test specs.
```bash
./mill gcd.test.testOnly gcd.GCDSpec
```

## Chisel Learning Resources

- [Chisel Book](https://github.com/schoeberl/chisel-book)
- [Chisel Documentation](https://www.chisel-lang.org/chisel3/)
- [Chisel API](https://www.chisel-lang.org/api/latest/index.html)




