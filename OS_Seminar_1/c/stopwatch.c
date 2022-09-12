#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <math.h>

// Declare function msleep for sleeping in milliseconds
int msleep(unsigned int tms);

#define CHILD_ID 2;

int main(void)
{
  pthread_t id; // parent id

  int child_id = CHILD_ID;  // declare child thread id from defined value

  // Create child thread
  pthread_create(&id, NULL, stopwatch, &child_id);

  int* ptr;

  pthread_join(id, (void**)&ptr);
  exit(0)
}


void* stopwatch(void* child_id)
{
  float elapsed_time = 0;
  while(floor(elapsed_time) != 60)
  {
    msleep(10);
    elapsed_time += 0.01;
    printf("Stopwatch thread. Elapsed: %.2f seconds.", elapsed_time);
  }

  // exit thread referencing global id 'i'
  pthread_exit(&child_id);
}

int msleep(unsigned int tms) {
  return usleep(tms * 1000);
}
