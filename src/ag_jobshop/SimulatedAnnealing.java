/*
Essa classe implementa as funções referentes aos procedimentos do Simulated Annealing
Maiores informações sobre o funcionamento estão nos comentários dos métodos.
Essa classe possui os seguintes métodos:
mutacaoSASimplificada realiza o simulated annealing porém com um cálculo de probabilidades
baseado em porcentagem.
mutacaoSA usa o cálculo tradicional que utiliza exponenciais para calcular a probabilidade
de aceite de uma resposta.
*/
package ag_jobshop;

import static java.lang.Math.exp;
import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class SimulatedAnnealing {
       public static char[] mutacaoSASimplificada(char[] individuoOriginal,String[][] dataset,
               int tam_Dataset_Linhas, String penalidade){
       /*
       Realiza uma mutação em um indivíduo utilizando o princípio do Simulated Annealing.
       A "Temperatura" se inicia em 80 sempre e o fator de resfriamento posteriormente
       será definido pelo usuário.
       */
       float temperatura = 80;
       float fatorResfriamento = 0.3f;
       Random randomizador = new Random();
       do{
           char[] aleatorio = Populacao.unicoIndividuoAleatorio(tam_Dataset_Linhas,dataset,penalidade);
           double fitOriginal = Fitness.calculaFitness(individuoOriginal,dataset);
           double fitNovo = Fitness.calculaFitness(aleatorio,dataset);
           if(fitNovo<fitOriginal)
               individuoOriginal = aleatorio.clone();
           else if((int)randomizador.nextInt(100)<temperatura)
               individuoOriginal = aleatorio.clone();
           temperatura = temperatura * fatorResfriamento;
       }while(temperatura>5);
       return individuoOriginal;
   }
   
   public static char[] mutacaoSA(char[] individuoOriginal, String[][] dataset, int tam_Dataset_Linhas,
                                    String penalidade){
       /*
       Realiza uma mutação em um indivíduo utilizando o princípio do Simulated Annealing.
       A "Temperatura" se inicia em 80 sempre e o fator de resfriamento posteriormente
       será definido pelo usuário. É uma "forma diferenciada" de se contar iterações. 
       Nesta versão do SA, o cálculo da probabilidade é feito usando o exponencial.
       */
       int temperatura = 80;
       int fatorResfriamento = 2;
       //Esse loop roda enquanto o sistema estiver "quente"
       do{
           char[] aleatorio = Populacao.unicoIndividuoAleatorio(tam_Dataset_Linhas,dataset,penalidade);
           double fitOriginal = Fitness.calculaFitness(individuoOriginal,dataset);
           double fitNovo = Fitness.calculaFitness(aleatorio,dataset);
           //Se o novo fitness é menor (logo, melhor) que o original, é substituído prontamente
           if(fitNovo<fitOriginal)
               individuoOriginal = aleatorio.clone();
           //Caso não seja, é feito o cálculo exponencial
           else if(exp((fitNovo-fitOriginal)/temperatura)<temperatura)
               individuoOriginal = aleatorio.clone();
           temperatura = temperatura - fatorResfriamento;
       }while(temperatura>5);
       return individuoOriginal;
   }

}
