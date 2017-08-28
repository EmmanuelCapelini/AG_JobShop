/*
Essa classe implementa os métodos referentes ao princípio do GRASP - Greedy
Randomized Adaptive Search Procedures
Maiores detalhes sobre o funcionamento estão nos comentários dos métodos.
*/
package ag_jobshop;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class GRASP {
    public static char[] mutacaoGRASP(char[] individuoOriginal, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
       /*
       Esse método realiza a mutação por meio da heurística Greedy Randomized Adaptive Search Procedure
       Ele da mesma forma que no início do VNS, instancia um número predefinido 
       de indivíduos a partir de mutações simples do indivíduo a ser mutado. Após isso,
       calcula o melhor fitness e adiciona à Restricted Canditate List todos os que forem até uma
       porcentagem do mesmo. Após isso, escolhe um aleatoriamente da RCL.
       O melhor ao fim das iterações é retornado.
       */
       
       //Tamanho do conjunto de soluções e estruturas para armazená-los.
       int tamConjunto = 50; 
       Random randomizador = new Random();
       char[][] conjuntoSolucoes = new char[tamConjunto][];
       char[] melhorAnterior = individuoOriginal.clone();
       //Variáveis de controle para as iterações
       int estagnadas = 0;
       double melhorFitness;
       double taxaAceite = 0.8; //De 0 a 1
       //O código é executado até que bata a quantidade máxima de iterações sem melhora
       do
       {
           //Define valor altíssimo para o melhor Fitness
           melhorFitness = Double.MAX_VALUE;
           //Cria um novo conjunto de soluções
           for(int i = 0; i < tamConjunto; i++)
            {
                conjuntoSolucoes[i] = Mutacao.mutacao(individuoOriginal, tam_Dataset_Linhas, dataset, penalidade);
            }
           //Encontra o melhor fitness desse conjunto
           for(int i = 0; i < tamConjunto; i++)
           {
               if(Fitness.calculaFitness(conjuntoSolucoes[i],dataset)<melhorFitness)
                   melhorFitness = Fitness.calculaFitness(conjuntoSolucoes[i],dataset);
           }
           //Define o parâmetro "alpha" que define o valor mínimo de um fit para ser adicionado à RCL
           double alpha = melhorFitness * taxaAceite;
           //Instancia a Restricted Candidate List e adiciona os individuos pertinentes a ela
           ArrayList<char[]> restrictedCandidateList = new ArrayList();
           for(int i = 0; i < tamConjunto; i++)
           {
               if(Fitness.calculaFitness(conjuntoSolucoes[i],dataset) > alpha)
                   restrictedCandidateList.add(conjuntoSolucoes[i]);
           }
           //Define um número aleatório e escolhe na lista o indivíduo com esse índice
           int indiceRandom = randomizador.nextInt(restrictedCandidateList.size());
           char[] temp = restrictedCandidateList.get(indiceRandom);
           //Se esse indivíduo apresenta melhora, substitui o melhor com ele. Ao contrário, aumenta o contador de estagnação
           if(Fitness.calculaFitness(temp,dataset)<Fitness.calculaFitness(melhorAnterior,dataset))
           {
               melhorAnterior = temp;
               estagnadas = 0;
           }
           else
               estagnadas++;
       }while (estagnadas <=5);
       return melhorAnterior;
   }
}
