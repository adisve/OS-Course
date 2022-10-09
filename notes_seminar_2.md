# Notes seminar 2

## Why is synchronization taught in OS


## Task 1 & 2
- Some of us had issues getting starvation for Task 1
- Pete solved the solution by implementing a queue for the readers and writers, with states of 0 and 1's
- We thought this semnar was a bit more challenging.
- All of us had similar solutions to the reader writer problem.
- For task 2 everyone used ReentrantLock to lock/unlock
- Pete implemented his own locking mechanism 

## Task 3
All of us had similar solutions since the circular wait problem can be solved easily by only allowing a left chopstick to be picked up if both of the chopsticks are available.

## Why is synchronziation taught in OS?
Concurrency is used in multicore processing systems and since atomic instructions are implemented, they need allocated resources to successfully execute.

## What is the difference between a mutex and a semaphore?
Mutex is a locking mechanism and semaphores are a signaling mechanism, mutexes handle locking and unlocking of access to a shared data object. Semaphores signal when a resource is ready for accessing or when a thread is executing or not.


Adam Holgersson, Hans Strömquist, Piotr Sajkowski, Rosita Hamidi