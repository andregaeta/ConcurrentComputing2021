#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include "timer.h"

const int _INIT_VALUE;

int _matrixSize;
int _threadsUsed;

float* _inputMatrix;
float* _outputMatrix;

double _totalTime = 0.0;

void init(int argc, char **argv) 
{
    if (argc < 3) 
    {
        printf("Uso incorreto: digite \"%s <threads> <tamanho>\"\n", argv[0]);
        exit(-1);
    }

    _threadsUsed = atoi(argv[1]);
    _matrixSize = atoi(argv[2]);
    
    _inputMatrix = (float *) malloc(sizeof(float) * _matrixSize * _matrixSize);
    _outputMatrix = (float *) malloc(sizeof(float) * _matrixSize * _matrixSize);

    if (_inputMatrix == NULL || _outputMatrix == NULL) 
    {
        printf("Memoria insuficiente.\n");
        exit(-2);
    }

    for (int i = 0; i < _matrixSize; i++)
    {
        for (int j = 0; j < _matrixSize; j++)
        {
            _inputMatrix[i * _matrixSize + j] = _INIT_VALUE;
        }
    }
}

void* multiplyMatrix(void* arg) 
{
    int startLine = *((int *) arg);

    for (int i = startLine; i < _matrixSize; i += _threadsUsed)
    {
        for (int j = 0; j < _matrixSize; j++)
        {
            for (int k = 0; k < _matrixSize; k++)
            {
                float value = _inputMatrix[i * _matrixSize + k] * _inputMatrix[k * _matrixSize + j];
                _outputMatrix[i * _matrixSize + j] += value;
            }
        }
    }

    pthread_exit(NULL);
}

void createThreads() 
{
    pthread_t tids[_threadsUsed];
    int args[_threadsUsed];

    for (int i = 0; i < _threadsUsed; i++) 
    {
        tids[i] = i;
        args[i] = i;

        if (pthread_create(&tids[i], NULL, multiplyMatrix,(void *) &args[i])) 
        {
            printf("Erro em pthread_create().\n");
            exit(-1);
        }
    }

    for (int i = 0; i < _threadsUsed; i++)
    {
        if (pthread_join(tids[i], NULL)) 
        {
            printf("Erro em pthread_join().\n");
            exit(-1);
        }
    }
}


void verifyMatrix() 
{
    float expectedOutput = _INIT_VALUE * _INIT_VALUE * _matrixSize;

    for (int i = 0; i < _matrixSize; i++)
    {
        for (int j = 0; j < _matrixSize; j++)
        {
            if (_outputMatrix[i * _matrixSize + j] != expectedOutput) 
            {
                printf("Erro encontrado na matriz[%d][%d]\nSaida atual: %f\nSaida esperada: %f\n", (i+1), (j+1), _outputMatrix[i * _matrixSize + j], expectedOutput);
                exit(-3);
            }
        }
    }

    printf("Nenhum erro encontrado na verificacao da matrix.\n");
}

double computeTime(double startTime, double endTime)
{
    double deltaTime = endTime - startTime;
    _totalTime += deltaTime;

    return deltaTime;
}

void timedInit(int argc, char **argv)
{
    double startTime = 0.0;
    double endTime = 0.0;

    GET_TIME(startTime)
    init(argc, argv);
    GET_TIME(endTime);
    
    printf("(a) init() = %f\n", computeTime(startTime, endTime));
}

void timedCreateThreads()
{
    double startTime = 0.0;
    double endTime = 0.0;

    GET_TIME(startTime);
    createThreads();
    GET_TIME(endTime);

    printf("(b) createThreads() = %f\n", computeTime(startTime, endTime));
}

void timedFinalize()
{
    double startTime = 0.0;
    double endTime = 0.0;

    GET_TIME(startTime);
    verifyMatrix();
    free(_inputMatrix);
    free(_outputMatrix);
    GET_TIME(endTime);

    printf("(c) finalize = %f\n", computeTime(startTime, endTime));
}

int main(int argc, char **argv) 
{
    timedInit(argc, argv);

    timedCreateThreads();

    timedFinalize();

    printf("Total = %f\n", _totalTime);

    return 0;
}
