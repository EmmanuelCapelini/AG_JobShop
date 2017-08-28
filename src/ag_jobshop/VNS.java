/*
Essa classe realiza os processos relativos à VNS - Variable Neighborhood Search
Maiores informações sobre o funcionamento estão especificadas no corpo dos métodos
Essa classe possui dois métodos:
mutacaoVNSBasica() onde o VNS é executado apenas uma vez 
e
mutacaoVNS() onde ele é executado de forma iterativa.
A maior parte dos argumentos em ambas as funções se deve ao fato da função de mutação
básica as pedir.
*/
package ag_jobshop;

import java.util.Random;

/**
 *
 * @author emmanuel
 */
public class VNS {
       private char[] mutacaoVNSBasica(char[] individuoOriginal, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
       /*
       Realiza uma mutação em um indivíduo usando o princípio da VNS: Variable Neighborhood Search.
       Por ora o tamanho de vizinhaças exploradas é fixo, mas poderá ser alterado para 
       o usuário setar.
       Como entende-se por "vizinhança" qualquer alteração a um indivíduo, ela 
       é composta nessa versão por um conjunto de mutações simples ao indivíduo original.
       */
       int tamanhoVizinhanca = 50;
       char[][] neighborhood = new char[tamanhoVizinhanca][];
       //As "tamanhoVizinhanca" linhas da vizinhança são instanciadas com uma 
       //mutação simples do indivíduo em cada uma. Problema: podem ocorrer repetições.
       for(int i = 0;i<tamanhoVizinhanca;i++)
       {
           neighborhood[i] = Mutacao.mutacao(individuoOriginal,tam_Dataset_Linhas,dataset,penalidade).clone();
       }
       //Aqui são instanciadas variáveis de controle para a seleção do melhor indivíduo da VNS
       double melhorFit = Double.MAX_VALUE;
       int posicaoMelhorVizinho = 0;
       for(int i = 0; i< tamanhoVizinhanca; i++)
       {
           double fitTemporario = Fitness.calculaFitness(neighborhood[i].clone(),dataset);
           if(fitTemporario<melhorFit)
           {
               melhorFit = fitTemporario;
               posicaoMelhorVizinho = i;
           }
       }
       return neighborhood[posicaoMelhorVizinho].clone();
   }
   
   private char[] mutacaoVNS(char[] individuoOriginal, int tam_Dataset_Linhas, String[][] dataset, String penalidade){
       /*
       Realiza uma mutação em um indivíduo usando o princípio da VNS: Variable Neighborhood Search.
       Por ora o tamanho de vizinhaças exploradas é fixo, mas poderá ser alterado para 
       o usuário setar.
       Como entende-se por "vizinhança" qualquer alteração a um indivíduo, ela 
       é composta nessa versão por um conjunto de mutações simples ao indivíduo original.
       Nesta versão da VNS, são feitas iterações sucessivas até que uma estagnação
       da melhora (quando houver) ocorra.
       */
       //Conjuntos de vizinhança e seu tamanho.
       int tamanhoVizinhanca = 50;
       char[][] neighborhood = new char[tamanhoVizinhanca][];
       char[] melhorAtual = individuoOriginal.clone();
       //Aqui são instanciadas variáveis de controle para a seleção do melhor indivíduo da VNS
       double melhorFit;
       int posicaoMelhorVizinho = 0;
       int iteracoesEstagnadas = 0;
       do{
           melhorFit = Double.MAX_VALUE;
           //As "tamanhoVizinhanca" linhas da vizinhança são instanciadas com uma 
           //mutação simples do indivíduo em cada uma. Problema: podem ocorrer repetições.
           for(int i = 0;i<tamanhoVizinhanca;i++)
           {
                neighborhood[i] = Mutacao.mutacao(individuoOriginal,tam_Dataset_Linhas,dataset,penalidade).clone();
           }
           for(int i = 0; i< tamanhoVizinhanca; i++)
           {
                double fitTemporario = Fitness.calculaFitness(neighborhood[i].clone(),dataset);
                if(fitTemporario<melhorFit)
                {
                    melhorFit = fitTemporario;
                    posicaoMelhorVizinho = i;
                }
           }
           if(melhorFit> Fitness.calculaFitness(melhorAtual, dataset))
           {
               melhorAtual = neighborhood[posicaoMelhorVizinho].clone();
               iteracoesEstagnadas = 0;
           }
           else
               iteracoesEstagnadas++;
       }while(iteracoesEstagnadas<=5);
       return melhorAtual;
   }

}
