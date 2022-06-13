KC=	kotlinc
KFLAGS= -include-runtime
PROG= BuddySystem.jar
SRC= main.kt

all :
	$(KC) $(SRC) $(KFLAGS) -d $(PROG)

. PHONY : clean

clean:
	rm -rf BuddySystem.jar