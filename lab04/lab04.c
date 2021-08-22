#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#define NUM_THREADS 4

int x = 0;
pthread_mutex_t mutex;
pthread_cond_t firstDone;
pthread_cond_t midProgress;

void *first (void *t) 
{
    pthread_mutex_lock(&mutex);
    //
        printf("Seja bem-vindo!\n");
        x+= 0b1;
        pthread_cond_broadcast(&firstDone);
    //
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}

void *midA (void *t) 
{
    pthread_mutex_lock(&mutex);
    //
        if (x & 0b1 == 0) 
            pthread_cond_wait(&firstDone, &mutex);

        printf("Fique a vontade.\n");
        x+= 0b10;
        pthread_cond_broadcast(&midProgress);
    //
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}

void *midB (void *t) 
{   
    pthread_mutex_lock(&mutex);
    //
        if (x & 0b1 == 0) 
            pthread_cond_wait(&firstDone, &mutex);

        printf("Sente-se por favor.\n");
        x+= 0b100;
        pthread_cond_broadcast(&midProgress);
    //
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}

void *last (void *t) 
{
    pthread_mutex_lock(&mutex);
    //
        while (x & 0b111 != 0b111) 
            pthread_cond_wait(&midProgress, &mutex);
        
        printf("Volte sempre!\n");
        x+= 0b1000;
    //
    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}


void init()
{
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init (&firstDone, NULL);
    pthread_cond_init (&midProgress, NULL);
}

void startThreads()
{
    pthread_t tids[NUM_THREADS];
    
    pthread_create(&tids[0], NULL, first, NULL);
    pthread_create(&tids[1], NULL, midA, NULL);
    pthread_create(&tids[2], NULL, midB, NULL);
    pthread_create(&tids[3], NULL, last, NULL);

    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(tids[i], NULL);
    }
}

void finalize()
{
    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&firstDone);
    pthread_cond_destroy(&midProgress);
}

int main(int argc, char *argv[]) 
{
    init();

    startThreads();
    
    finalize();
}