# Replace 'gcd' with your %PROJECT-NAME%
project = gcd

# Toolchains and tools
MILL = ./../playground/mill

-include ./../playground/Makefile.include

# Targets
rtl: check-firtool ## Generates Verilog code from Chisel sources (output to ./generated_sv_dir)
	$(MILL) $(project).runMain gcd.gcd8

check: test
.PHONY: test
test: check-firtool ## Run Chisel tests
	$(MILL) $(project).test.testOnly gcd.GCDSpec
	@echo "The VCD file is generated in ./test_run_dir/testname directories."
