/*
Essa classe realiza as operações que envolvem uma população, indivíduo ou conjunto
de indivíduos.
Maiores informações sobre o funcionamento estão nos comentários dentro dos métodos
*/
package ag_jobshop;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class Populacao {
    public static char[][] criaPopAleatoria(int iteracoes, int pctElite, char [][] populacaoAnterior,
                                int tamanhopop, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
        /* Função para alocação e criação de uma população. É uma das funções com mais argumentos,
        então faz-se necessário explicar um pouco sobre os mesmos:
        iteracoes: quantidade de iterações já ocorridas do algoritmo.
        pctElite: quantidade de membros da população que se referem à porcentagem dentro do elitismo
        populacaoAnterior: população da iteração anterior do AG, para fins de elitismo
        tamanhopop: tamanho da população
        tam_Dataset_Linhas: tamanho do dataset do problema
        dataset: o próprio dataset do problema, de onde serão tirados os indivíduos
        penalidade: a string definida como penalização para a escolha daquela linha para uma máquina em específico
        */
        
        //Instancia a população a ser retornada.Até o presente momento, o melhor modo de se representar a população foi com uma matriz de chars.
        char[][] populacao = new char[tamanhopop][tam_Dataset_Linhas];
        int posInicial;
        if(iteracoes != 1)
        {
            //Efetua o elitismo caso essa não seja a primeira execução da função
            for(int i=0;i<=pctElite;i++)
                populacao[i] = populacaoAnterior[i].clone();
            posInicial = pctElite + 1;
        }
        else
            posInicial = 0;
        
        //Aqui inicia-se o alocamento dos indivíduos na nova população
        for(int i = posInicial;i<tamanhopop;i++)
        {
            for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
            {
                //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
                char temp = MetodosAuxiliares.amostra(dataset[sequencia+1].clone(),penalidade);
                //Insere a mesma em sua respectiva coluna na população.
                populacao[i][sequencia] = temp;
            }
        }
        return populacao;
    }
    
    public static char[][] criaPopVNS(int iteracoes, int pctElite, char [][] populacaoAnterior,
                                int tamanhopop, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
        /*
        Esse método aloca uma população toda usando os princípios da Variable Neighborhood Search.
        O funcionamento básico é com ela sendo criadade forma aleatória, e depois disso seus indivíduos
        passarem pelo processo da busca por vizinhanças variáveis.
        */
        //Primeiramente cria a população aleatória
        char [][] populacao = Populacao.criaPopAleatoria(iteracoes, pctElite, populacaoAnterior, tamanhopop, tam_Dataset_Linhas, dataset, penalidade);
        //Aplica o VNS em cada um
        for(int i=0; i<tamanhopop;i++)
        {
            populacao[i] = VNS.mutacaoVNS(populacao[i], tam_Dataset_Linhas, dataset, penalidade);
        }
        return populacao;
    }
    
    public static char[][] criaPopGRASP(int iteracoes, int pctElite, char [][] populacaoAnterior,
                                int tamanhopop, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
        /*
        Esse método aloca uma população toda usando o Greedy Randomized Adaptive Search Procedures.
        Cada indivíduo é criado de forma aleatória para depois ser realizada um GRASP no mesmo,
        com o melhor sendo o escolhido para fazer parte da população.
        */
        //Cria a população de forma aleatória
        char [][] populacao = Populacao.criaPopAleatoria(iteracoes, pctElite, populacaoAnterior, tamanhopop, tam_Dataset_Linhas, dataset, penalidade);
        //Aplica o GRASP em cada um
        for(int i=0; i<tamanhopop;i++)
        {
            populacao[i] = GRASP.mutacaoGRASP(populacao[i], tam_Dataset_Linhas, dataset, penalidade);
        }
        return populacao;
    }
    
    public static char[][] criaPopSA(int iteracoes, int pctElite, char [][] populacaoAnterior,
                                int tamanhopop, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
        /*
        Esse método cria uma população toda usando o Simulated Annealing.
        Cada inviíduo é criado de forma aleatória, para depois ser aplicado o SA
        antes de ser inserido na população.
        */
        //Cria a população aleatoriamente
        char [][] populacao = Populacao.criaPopAleatoria(iteracoes, pctElite, populacaoAnterior, tamanhopop, tam_Dataset_Linhas, dataset, penalidade);
        //Aplica o GRASP em cada um
        for(int i=0; i<tamanhopop;i++)
        {
            populacao[i] = SimulatedAnnealing.mutacaoSA(populacao[i], dataset, tam_Dataset_Linhas, penalidade);
        }
        return populacao;
    }
    
    public static char[] unicoIndividuoAleatorio(int tam_Dataset_Linhas, String [][] dataset,String penalidade){
       /*Essa função instancia e devolve um único indivíduo feito aleatoriamente*/
       char[] novoAleatorio = new char[tam_Dataset_Linhas];
       for(int sequencia = 0; sequencia<tam_Dataset_Linhas;sequencia++)
          {
          //Escolhe aleatoriamente uma máquina dentre as possíveis para esse job
          char temp = MetodosAuxiliares.amostra(dataset[sequencia+1].clone(),penalidade);
          //Insere a mesma em sua respectiva coluna na população.
          novoAleatorio[sequencia] = temp;
          }
       return novoAleatorio;
   }
       
    public static char[] crossover(char[] pai1,char[] pai2, int tam_Dataset_Linhas){
        /*
        Função que realiza o crossover de dois indivíduos, no caso, duas linhas da Matriz População
        Nessa primeira tentativa, o crossover é realizado de forma comum, Uma vez que o crossover não
        altera a ordem de agendamento das tarefas, então não se faz necessário um crossover de permutação
        */
        Random randomizador = new Random();
        int pontoCross = randomizador.nextInt(tam_Dataset_Linhas);
        char[] filho = pai1;
//        for(int i=0;i<pontoCross;i++)
//            filho[i] = pai1[i];
        for(int i =pontoCross;i<pai2.length;i++)
            filho[i] = pai2[i];
        return filho;
    }   
    
    public static Fitness[] elitismo(Fitness[] valorFit){
        /*
        Esta função efetua o elitismo na população, ordenando o vetor de fitnesses
        Isso é feito simplesmente utilizando o método sort da classe Arrays, pricipalmente para fins de 
        organização de código. As demais operações referentes ao elitismo estão dentro do corpo do Algoritmo Genético.
        Também foi necessário sobrecarregar o comparador do método sort.
        */
        Arrays.sort(valorFit, (Fitness f1, Fitness f2) -> {
            if (f1.getFitness() > f1.getFitness()) return 1;
            if (f1.getFitness() < f2.getFitness()) return -1;
                return 0;
            });
        return valorFit;
    }
    
 }
