#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>

#define NUM_THREADS 4

sem_t firstDone;
sem_t midProgress;

void *first (void *t) 
{
    printf("Seja bem-vindo!\n");
    sem_post(&firstDone);
    sem_post(&firstDone);
    pthread_exit(NULL);
}

void *midA (void *t) 
{
    sem_wait(&firstDone);
    printf("Fique a vontade.\n");
    sem_post(&midProgress);
    pthread_exit(NULL);
}

void *midB (void *t) 
{   
    sem_wait(&firstDone);
    printf("Sente-se por favor.\n");
    sem_post(&midProgress);
    pthread_exit(NULL);
}

void *last (void *t) 
{
    sem_wait(&midProgress);
    sem_wait(&midProgress);
    printf("Volte sempre!\n");
    pthread_exit(NULL);
}


void init()
{
    sem_init(&firstDone, 0, 0);
    sem_init(&midProgress, 0, 0);
}

void startThreads()
{
    pthread_t tids[NUM_THREADS];
    
    pthread_create(&tids[0], NULL, first, NULL);
    pthread_create(&tids[1], NULL, midA, NULL);
    pthread_create(&tids[2], NULL, midB, NULL);
    pthread_create(&tids[3], NULL, last, NULL);

    for (int i = 0; i < NUM_THREADS; i++) 
    {
        pthread_join(tids[i], NULL);
    }
}

void finalize()
{
    sem_destroy(&firstDone);
    sem_destroy(&midProgress);
}

int main(int argc, char *argv[]) 
{
    init();

    startThreads();
    
    finalize();
}