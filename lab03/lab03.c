#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <pthread.h>
#include "timer.h"

#define lli long long int
#define MAX_VALUE 1000000

int _vectorSize;
int _threadsUsed;

float* _vector;

typedef struct _Bounds {
    float min;
    float max;
    float calcTime;
} Bounds;

float min(float a, float b) {
	return a < b ? a : b;
}

float max(float a, float b) {
	return a > b ? a : b;
}

void init(int argc, char **argv)
{
    if (argc < 3) 
    {
        printf("Uso incorreto: digite \"%s <threads> <tamanho>\"\n", argv[0]);
        exit(-1);
    }

    _threadsUsed = atoi(argv[1]);
    _vectorSize = atoi(argv[2]);

    _vector = (float *) malloc(sizeof(float) * _vectorSize);

    if (_vector == NULL) 
    {
        printf("Memoria insuficiente.\n");
        exit(-1);
    }

    for(int i = 0; i < _vectorSize; i++)
    {
        _vector[i] = rand() % MAX_VALUE;
    }

}

Bounds* getDefaultBounds()
{
    Bounds* bounds = (Bounds *) malloc(sizeof(Bounds));

    if (bounds == NULL) 
    {
        printf("Memoria insuficiente.\n");
        exit(-1);
    }

    bounds->min = MAX_VALUE;
    bounds->max = -1;
    bounds->calcTime = 0;

    return bounds;
}

void* getBounds(void* args)
{
    lli startIndex = (lli) args;
	Bounds* bounds = getDefaultBounds();

	for (lli i = startIndex; i < _vectorSize; i += _threadsUsed) {
		bounds->min = min(bounds->min, _vector[i]);
		bounds->max = max(bounds->max, _vector[i]);
	}

	pthread_exit((void *) bounds);
}


Bounds* getBoundsConc() 
{
    double startTime, endTime;
    GET_TIME(startTime);

    pthread_t tids[_threadsUsed];

	for (lli i = 0; i < _threadsUsed; i++) 
    {
        tids[i] = i;
		if (pthread_create(&tids[i], NULL, getBounds, (void *) i)) 
        {
			fprintf(stderr, "Erro em pthread_create()\n");
			exit(-2);
		}
	}

    Bounds* bounds = getDefaultBounds();

	for (lli i = 0; i < _threadsUsed; i++) 
    {
        Bounds* threadBounds;
		if (pthread_join(tids[i], (void **) &threadBounds)) 
        {
			fprintf(stderr, "Erro em pthread_join()\n");
			exit(-2);
		}	

		bounds->min = min(bounds->min, threadBounds->min);
		bounds->max = max(bounds->max, threadBounds->max);

        free(threadBounds);
	}

    GET_TIME(endTime);
	bounds->calcTime = endTime - startTime;

	return bounds;
}

Bounds* getBoundsSeq() {
    double startTime, endTime;
    GET_TIME(startTime);

	Bounds* bounds = getDefaultBounds();
	
	for (lli i = 0; i < _vectorSize; i++) {
		bounds -> min = min(bounds -> min, _vector[i]);
		bounds -> max = max(bounds -> max, _vector[i]);
	}

    GET_TIME(endTime);
	bounds->calcTime = endTime - startTime;
    
	return bounds;	
}

void compare(Bounds* boundsConc, Bounds* boundsSeq)
{
    if(boundsConc->min != boundsSeq->min || boundsConc->max != boundsSeq->max)
    {
        printf("Erro ao comparar os valores.\n");
        exit(-3);
    }
    printf("Valores corretos.\nMin = %f\nMax = %f\n", boundsConc->min, boundsConc->max);
    printf("Tempo concorrente = %f\nTempo sequencial = %f\nRazao de aceleracao: %f\n", boundsConc->calcTime, boundsSeq->calcTime, boundsSeq->calcTime/boundsConc->calcTime);
}

int main(int argc, char **argv)
{
    init(argc, argv);

    Bounds* boundsConc = getBoundsConc();

    Bounds* boundsSeq = getBoundsSeq();

    compare(boundsConc, boundsSeq);
}