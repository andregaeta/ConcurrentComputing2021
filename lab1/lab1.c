#include <stdio.h>
#include <stdlib.h> 
#include <pthread.h>

#define VECTOR_SIZE 10000
#define NUM_THREADS 2

typedef struct _VectorSquareParams {
    int startIndex;
    int endIndex;
} VectorSquareParams;

//variaveis globais
int vector[VECTOR_SIZE];
int initialVector[VECTOR_SIZE];
VectorSquareParams params[NUM_THREADS];
pthread_t tid[NUM_THREADS];

//inicializa o vetor com numeros aleatorios de 1 a 9
void initVector(){
    for(int i = 0; i < VECTOR_SIZE; i++){
        initialVector[i] = rand() % 9 + 1;
        vector[i] = initialVector[i];
    }
}

//calcula os parametros que definem os index de cada thread
void initParams(){
    int range = VECTOR_SIZE / NUM_THREADS;

    for(int i = 0; i < NUM_THREADS; i++){
        params[i].startIndex = i*range;
        params[i].endIndex = (i+1) * range;
    }
    params[NUM_THREADS - 1].endIndex = VECTOR_SIZE;
}

//imprime o vetor
void printVector(){
    for(int i = 0; i < VECTOR_SIZE; i++){
        printf("%d\n", vector[i]);
    }
}

//eleva os valores do vetor ao quadrado
void* squareVector(void* arg){
    VectorSquareParams params = *(VectorSquareParams*) arg;

    for(int i = params.startIndex; i < params.endIndex; i++){
        vector[i] = vector[i] * vector[i];
    }
    pthread_exit(NULL);
}

//cria as threads secundarias
void createThreads(){
    for (int i = 0; i < NUM_THREADS; i++){
        if(pthread_create(&tid[i], NULL, squareVector, (void*) &params[i]))
            printf("Erro em pthread_create\n");
    }

    for (int i = 0; i < NUM_THREADS; i++){
        if(pthread_join(tid[i], NULL))
            printf("Erro em pthread_join\n");
    }
}

//verifica se o vetor foi elevado ao quadrado corretamente
void verifyVector(){
    int vectorIsCorrect = 1;
    for(int i = 0; i < VECTOR_SIZE; i++){
        if(vector[i] != initialVector[i] * initialVector[i]){
            vectorIsCorrect = 0;
            printf("Erro encontrado na posicao [%d]:\nnumero ao quadrado = %d\nnumero inicial = %d\n", i, vector[i], initialVector[i]);
        }
    }

    if(vectorIsCorrect){
        printf("Nenhum erro foi encontrado na checagem do vetor.\n");
    }
}

int main(){

    initVector();

    initParams();

    createThreads();

    //printVector();

    verifyVector();

    pthread_exit(NULL);
}
