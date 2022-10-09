#include <pthread.h>
#include <semaphore.h>
#include <stdio.h>
#include <unistd.h>
 
#define N 5

/****** Define the states that a philosopher can have ******/
#define THINKING 2
#define HUNGRY 1
#define EATING 0

/****** Declare LEFT and RIGHT since they depend on an array of length 5 and can not be >= 5 or < 0 ******/
#define LEFT (phnum + 4) % N
#define RIGHT (phnum + 1) % N
 
/****** Declare list of states and philosophers ******/
int state[N];
int philosophers[N] = { 0, 1, 2, 3, 4 };
 
sem_t mutex;
sem_t S[N];

/**** Attempt to make philosopher busy (state[phnum] = EATING) if the right and left sticks
      are available, and the philosopher is in HUNGRY state ****/
void check(int phnum)
{
    if (state[phnum] == HUNGRY
        && state[LEFT] != EATING
        && state[RIGHT] != EATING) {
            
        state[phnum] = EATING;
 
        sleep(2);
 
        printf("Philosopher %d takes fork %d and %d\n",
                      phnum + 1, LEFT + 1, phnum + 1);
 
        printf("Philosopher %d is eating\n", phnum + 1);
        sem_post(&S[phnum]);
    }
}
 
void pick_up_chopstick(int phnum)
{
    sem_wait(&mutex);
    state[phnum] = HUNGRY;

    printf("Philosopher %d is hungry\n", phnum + 1);
 
    check(phnum);
    sem_post(&mutex);
    sem_wait(&S[phnum]);
 
    sleep(1);
}
 
void put_down_chopstick(int phnum)
{
 
    sem_wait(&mutex);
 
    state[phnum] = THINKING;
 
    printf("Philosopher %d putting fork %d and %d down\n",
           phnum + 1, LEFT + 1, phnum + 1);
    printf("Philosopher %d is thinking\n", phnum + 1);
 
    check(LEFT);
    check(RIGHT);
 
    sem_post(&mutex);
}
 
void* philosopher(void* num)
{
 
    while (1) {
        int* i = num;
        sleep(1);
        pick_up_chopstick(*i);
        sleep(0);
        put_down_chopstick(*i);
    }
}
 
int main()
{
 
    int i;
    pthread_t thread_id[N];
 
    sem_init(&mutex, 0, 1);
 
    for (i = 0; i < N; i++)
 
        sem_init(&S[i], 0, 0);
 
    for (i = 0; i < N; i++) {
        pthread_create(&thread_id[i], NULL,
                       philosopher, &philosophers[i]);
        printf("Philosopher %d is thinking\n", i + 1);
    }
 
    for (i = 0; i < N; i++) pthread_join(thread_id[i], NULL);
 
    return 0;
}
